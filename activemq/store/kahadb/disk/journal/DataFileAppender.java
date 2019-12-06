// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.journal;

import java.util.concurrent.atomic.AtomicReference;
import org.apache.activemq.store.kahadb.disk.util.LinkedNodeList;
import org.slf4j.LoggerFactory;
import java.util.zip.Checksum;
import org.apache.activemq.util.RecoverableRandomAccessFile;
import java.util.zip.Adler32;
import org.apache.activemq.store.kahadb.disk.util.DataByteArrayOutputStream;
import java.io.InterruptedIOException;
import org.apache.activemq.util.ByteSequence;
import java.util.concurrent.CountDownLatch;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;

class DataFileAppender implements FileAppender
{
    private static final Logger logger;
    protected final Journal journal;
    protected final Map<Journal.WriteKey, Journal.WriteCommand> inflightWrites;
    protected final Object enqueueMutex;
    protected WriteBatch nextWriteBatch;
    protected boolean shutdown;
    protected IOException firstAsyncException;
    protected final CountDownLatch shutdownDone;
    protected int maxWriteBatchSize;
    protected final boolean syncOnComplete;
    protected boolean running;
    private Thread thread;
    int statIdx;
    int[] stats;
    
    public DataFileAppender(final Journal dataManager) {
        this.enqueueMutex = new Object();
        this.shutdownDone = new CountDownLatch(1);
        this.statIdx = 0;
        this.stats = new int[DataFileAppender.maxStat];
        this.journal = dataManager;
        this.inflightWrites = this.journal.getInflightWrites();
        this.maxWriteBatchSize = this.journal.getWriteBatchSize();
        this.syncOnComplete = this.journal.isEnableAsyncDiskSync();
    }
    
    @Override
    public Location storeItem(final ByteSequence data, final byte type, final boolean sync) throws IOException {
        final int size = data.getLength() + 5;
        final Location location = new Location();
        location.setSize(size);
        location.setType(type);
        final Journal.WriteCommand write = new Journal.WriteCommand(location, data, sync);
        final WriteBatch batch = this.enqueue(write);
        location.setLatch(batch.latch);
        if (sync) {
            try {
                batch.latch.await();
            }
            catch (InterruptedException e) {
                throw new InterruptedIOException();
            }
            final IOException exception = batch.exception.get();
            if (exception != null) {
                throw exception;
            }
        }
        return location;
    }
    
    @Override
    public Location storeItem(final ByteSequence data, final byte type, final Runnable onComplete) throws IOException {
        final int size = data.getLength() + 5;
        final Location location = new Location();
        location.setSize(size);
        location.setType(type);
        final Journal.WriteCommand write = new Journal.WriteCommand(location, data, onComplete);
        final WriteBatch batch = this.enqueue(write);
        location.setLatch(batch.latch);
        return location;
    }
    
    private WriteBatch enqueue(final Journal.WriteCommand write) throws IOException {
        synchronized (this.enqueueMutex) {
            if (this.shutdown) {
                throw new IOException("Async Writter Thread Shutdown");
            }
            if (!this.running) {
                this.running = true;
                (this.thread = new Thread() {
                    @Override
                    public void run() {
                        DataFileAppender.this.processQueue();
                    }
                }).setPriority(10);
                this.thread.setDaemon(true);
                this.thread.setName("ActiveMQ Data File Writer");
                this.thread.start();
                this.firstAsyncException = null;
            }
            if (this.firstAsyncException != null) {
                throw this.firstAsyncException;
            }
            while (true) {
                while (this.nextWriteBatch != null) {
                    if (this.nextWriteBatch.canAppend(write)) {
                        this.nextWriteBatch.append(write);
                        if (!write.sync) {
                            this.inflightWrites.put(new Journal.WriteKey(write.location), write);
                        }
                        return this.nextWriteBatch;
                    }
                    try {
                        while (this.nextWriteBatch != null) {
                            final long start = System.currentTimeMillis();
                            this.enqueueMutex.wait();
                            if (DataFileAppender.maxStat > 0) {
                                DataFileAppender.logger.info("Watiting for write to finish with full batch... millis: " + (System.currentTimeMillis() - start));
                            }
                        }
                    }
                    catch (InterruptedException e) {
                        throw new InterruptedIOException();
                    }
                    if (this.shutdown) {
                        throw new IOException("Async Writter Thread Shutdown");
                    }
                }
                DataFile file = this.journal.getCurrentWriteFile();
                if (file.getLength() > this.journal.getMaxFileLength()) {
                    file = this.journal.rotateWriteFile();
                }
                this.nextWriteBatch = this.newWriteBatch(write, file);
                this.enqueueMutex.notifyAll();
                continue;
            }
        }
    }
    
    protected WriteBatch newWriteBatch(final Journal.WriteCommand write, final DataFile file) throws IOException {
        return new WriteBatch(file, file.getLength(), write);
    }
    
    @Override
    public void close() throws IOException {
        synchronized (this.enqueueMutex) {
            if (!this.shutdown) {
                this.shutdown = true;
                if (this.running) {
                    this.enqueueMutex.notifyAll();
                }
                else {
                    this.shutdownDone.countDown();
                }
            }
        }
        try {
            this.shutdownDone.await();
        }
        catch (InterruptedException e) {
            throw new InterruptedIOException();
        }
    }
    
    protected void processQueue() {
        DataFile dataFile = null;
        RecoverableRandomAccessFile file = null;
        WriteBatch wb = null;
        try {
            final DataByteArrayOutputStream buff = new DataByteArrayOutputStream(this.maxWriteBatchSize);
            while (true) {
                synchronized (this.enqueueMutex) {
                    while (this.nextWriteBatch == null) {
                        if (this.shutdown) {
                            return;
                        }
                        this.enqueueMutex.wait();
                    }
                    wb = this.nextWriteBatch;
                    this.nextWriteBatch = null;
                    this.enqueueMutex.notifyAll();
                }
                if (dataFile != wb.dataFile) {
                    if (file != null) {
                        file.setLength(dataFile.getLength());
                        dataFile.closeRandomAccessFile(file);
                    }
                    dataFile = wb.dataFile;
                    file = dataFile.openRandomAccessFile();
                    if (file.length() < this.journal.preferedFileLength) {
                        file.setLength(this.journal.preferedFileLength);
                    }
                }
                Journal.WriteCommand write = wb.writes.getHead();
                buff.reset();
                buff.writeInt(Journal.BATCH_CONTROL_RECORD_SIZE);
                buff.writeByte(2);
                buff.write(Journal.BATCH_CONTROL_RECORD_MAGIC);
                buff.writeInt(0);
                buff.writeLong(0L);
                boolean forceToDisk = false;
                while (write != null) {
                    forceToDisk |= (write.sync | (this.syncOnComplete && write.onComplete != null));
                    buff.writeInt(write.location.getSize());
                    buff.writeByte(write.location.getType());
                    buff.write(write.data.getData(), write.data.getOffset(), write.data.getLength());
                    write = write.getNext();
                }
                final ByteSequence sequence = buff.toByteSequence();
                buff.reset();
                buff.skip(5 + Journal.BATCH_CONTROL_RECORD_MAGIC.length);
                buff.writeInt(sequence.getLength() - Journal.BATCH_CONTROL_RECORD_SIZE);
                if (this.journal.isChecksum()) {
                    final Checksum checksum = new Adler32();
                    checksum.update(sequence.getData(), sequence.getOffset() + Journal.BATCH_CONTROL_RECORD_SIZE, sequence.getLength() - Journal.BATCH_CONTROL_RECORD_SIZE);
                    buff.writeLong(checksum.getValue());
                }
                file.seek(wb.offset);
                if (DataFileAppender.maxStat > 0) {
                    if (this.statIdx < DataFileAppender.maxStat) {
                        this.stats[this.statIdx++] = sequence.getLength();
                    }
                    else {
                        long all = 0L;
                        while (this.statIdx > 0) {
                            final long n = all;
                            final int[] stats = this.stats;
                            final int statIdx = this.statIdx - 1;
                            this.statIdx = statIdx;
                            all = n + stats[statIdx];
                        }
                        DataFileAppender.logger.info("Ave writeSize: " + all / DataFileAppender.maxStat);
                    }
                }
                file.write(sequence.getData(), sequence.getOffset(), sequence.getLength());
                final ReplicationTarget replicationTarget = this.journal.getReplicationTarget();
                if (replicationTarget != null) {
                    replicationTarget.replicate(wb.writes.getHead().location, sequence, forceToDisk);
                }
                if (forceToDisk) {
                    file.sync();
                }
                final Journal.WriteCommand lastWrite = wb.writes.getTail();
                this.journal.setLastAppendLocation(lastWrite.location);
                this.signalDone(wb);
            }
        }
        catch (IOException e) {
            DataFileAppender.logger.info("Journal failed while writing at: " + wb.offset);
            synchronized (this.enqueueMutex) {
                this.firstAsyncException = e;
                if (wb != null) {
                    wb.exception.set(e);
                    wb.latch.countDown();
                }
                if (this.nextWriteBatch != null) {
                    this.nextWriteBatch.exception.set(e);
                    this.nextWriteBatch.latch.countDown();
                }
            }
        }
        catch (InterruptedException ex) {}
        finally {
            try {
                if (file != null) {
                    dataFile.closeRandomAccessFile(file);
                }
            }
            catch (Throwable t) {}
            this.shutdownDone.countDown();
            this.running = false;
        }
    }
    
    protected void signalDone(final WriteBatch wb) {
        for (Journal.WriteCommand write = wb.writes.getHead(); write != null; write = write.getNext()) {
            if (!write.sync) {
                this.inflightWrites.remove(new Journal.WriteKey(write.location));
            }
            if (write.onComplete != null) {
                try {
                    write.onComplete.run();
                }
                catch (Throwable e) {
                    DataFileAppender.logger.info("Add exception was raised while executing the run command for onComplete", e);
                }
            }
        }
        wb.latch.countDown();
    }
    
    static {
        logger = LoggerFactory.getLogger(DataFileAppender.class);
    }
    
    public static class WriteKey
    {
        private final int file;
        private final long offset;
        private final int hash;
        
        public WriteKey(final Location item) {
            this.file = item.getDataFileId();
            this.offset = item.getOffset();
            this.hash = (int)((long)this.file ^ this.offset);
        }
        
        @Override
        public int hashCode() {
            return this.hash;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof WriteKey) {
                final WriteKey di = (WriteKey)obj;
                return di.file == this.file && di.offset == this.offset;
            }
            return false;
        }
    }
    
    public class WriteBatch
    {
        public final DataFile dataFile;
        public final LinkedNodeList<Journal.WriteCommand> writes;
        public final CountDownLatch latch;
        protected final int offset;
        public int size;
        public AtomicReference<IOException> exception;
        
        public WriteBatch(final DataFile dataFile, final int offset) {
            this.writes = new LinkedNodeList<Journal.WriteCommand>();
            this.latch = new CountDownLatch(1);
            this.size = Journal.BATCH_CONTROL_RECORD_SIZE;
            this.exception = new AtomicReference<IOException>();
            this.dataFile = dataFile;
            this.offset = offset;
            this.dataFile.incrementLength(Journal.BATCH_CONTROL_RECORD_SIZE);
            this.size = Journal.BATCH_CONTROL_RECORD_SIZE;
            DataFileAppender.this.journal.addToTotalLength(Journal.BATCH_CONTROL_RECORD_SIZE);
        }
        
        public WriteBatch(final DataFileAppender this$0, final DataFile dataFile, final int offset, final Journal.WriteCommand write) throws IOException {
            this(this$0, dataFile, offset);
            this.append(write);
        }
        
        public boolean canAppend(final Journal.WriteCommand write) {
            final int newSize = this.size + write.location.getSize();
            return newSize < DataFileAppender.this.maxWriteBatchSize && this.offset + newSize <= DataFileAppender.this.journal.getMaxFileLength();
        }
        
        public void append(final Journal.WriteCommand write) throws IOException {
            this.writes.addLast(write);
            write.location.setDataFileId(this.dataFile.getDataFileId());
            write.location.setOffset(this.offset + this.size);
            final int s = write.location.getSize();
            this.size += s;
            this.dataFile.incrementLength(s);
            DataFileAppender.this.journal.addToTotalLength(s);
        }
    }
}
