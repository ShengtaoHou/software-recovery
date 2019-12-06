// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.journal;

import java.util.zip.Checksum;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.util.RecoverableRandomAccessFile;
import java.util.zip.Adler32;
import java.io.IOException;
import org.apache.activemq.store.kahadb.disk.util.DataByteArrayOutputStream;

class CallerBufferingDataFileAppender extends DataFileAppender
{
    final DataByteArrayOutputStream[] cachedBuffers;
    volatile byte flip;
    
    @Override
    protected DataFileAppender.WriteBatch newWriteBatch(final Journal.WriteCommand write, final DataFile file) throws IOException {
        return new WriteBatch(file, file.getLength(), write);
    }
    
    private void initBuffer(final DataByteArrayOutputStream buff) throws IOException {
        buff.reset();
        buff.write(Journal.BATCH_CONTROL_RECORD_HEADER);
        buff.writeInt(0);
        buff.writeLong(0L);
    }
    
    public CallerBufferingDataFileAppender(final Journal dataManager) {
        super(dataManager);
        this.cachedBuffers = new DataByteArrayOutputStream[] { new DataByteArrayOutputStream(this.maxWriteBatchSize), new DataByteArrayOutputStream(this.maxWriteBatchSize) };
        this.flip = 1;
    }
    
    @Override
    protected void processQueue() {
        DataFile dataFile = null;
        RecoverableRandomAccessFile file = null;
        WriteBatch wb = null;
        try {
            while (true) {
                Object o = null;
                synchronized (this.enqueueMutex) {
                    while (this.nextWriteBatch == null) {
                        if (this.shutdown) {
                            return;
                        }
                        this.enqueueMutex.wait();
                    }
                    o = this.nextWriteBatch;
                    this.nextWriteBatch = null;
                    this.enqueueMutex.notifyAll();
                }
                wb = (WriteBatch)o;
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
                final DataByteArrayOutputStream buff = wb.buff;
                final boolean forceToDisk = wb.forceToDisk;
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
                if (CallerBufferingDataFileAppender.maxStat > 0) {
                    if (this.statIdx < CallerBufferingDataFileAppender.maxStat) {
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
                        System.err.println("Ave writeSize: " + all / CallerBufferingDataFileAppender.maxStat);
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
    
    private boolean appendToBuffer(final Journal.WriteCommand write, final DataByteArrayOutputStream buff) throws IOException {
        buff.writeInt(write.location.getSize());
        buff.writeByte(write.location.getType());
        buff.write(write.data.getData(), write.data.getOffset(), write.data.getLength());
        return write.sync | (this.syncOnComplete && write.onComplete != null);
    }
    
    public class WriteBatch extends DataFileAppender.WriteBatch
    {
        DataByteArrayOutputStream buff;
        private boolean forceToDisk;
        
        public WriteBatch(final DataFile dataFile, final int offset, final Journal.WriteCommand write) throws IOException {
            super(dataFile, offset);
            final DataByteArrayOutputStream[] cachedBuffers = CallerBufferingDataFileAppender.this.cachedBuffers;
            final CallerBufferingDataFileAppender this$ = CallerBufferingDataFileAppender.this;
            final byte flip = (byte)(this$.flip ^ 0x1);
            this$.flip = flip;
            CallerBufferingDataFileAppender.this.initBuffer(this.buff = cachedBuffers[flip]);
            this.append(write);
        }
        
        @Override
        public void append(final Journal.WriteCommand write) throws IOException {
            super.append(write);
            this.forceToDisk |= CallerBufferingDataFileAppender.this.appendToBuffer(write, this.buff);
        }
    }
}
