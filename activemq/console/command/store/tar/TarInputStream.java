// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.command.store.tar;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

public class TarInputStream extends FilterInputStream
{
    private static final int SMALL_BUFFER_SIZE = 256;
    private static final int BUFFER_SIZE = 8192;
    private static final int LARGE_BUFFER_SIZE = 32768;
    private static final int BYTE_MASK = 255;
    protected boolean debug;
    protected boolean hasHitEOF;
    protected long entrySize;
    protected long entryOffset;
    protected byte[] readBuf;
    protected TarBuffer buffer;
    protected TarEntry currEntry;
    protected byte[] oneBuf;
    
    public TarInputStream(final InputStream is) {
        this(is, 10240, 512);
    }
    
    public TarInputStream(final InputStream is, final int blockSize) {
        this(is, blockSize, 512);
    }
    
    public TarInputStream(final InputStream is, final int blockSize, final int recordSize) {
        super(is);
        this.buffer = new TarBuffer(is, blockSize, recordSize);
        this.readBuf = null;
        this.oneBuf = new byte[1];
        this.debug = false;
        this.hasHitEOF = false;
    }
    
    public void setDebug(final boolean debug) {
        this.debug = debug;
        this.buffer.setDebug(debug);
    }
    
    @Override
    public void close() throws IOException {
        this.buffer.close();
    }
    
    public int getRecordSize() {
        return this.buffer.getRecordSize();
    }
    
    @Override
    public int available() throws IOException {
        if (this.entrySize - this.entryOffset > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int)(this.entrySize - this.entryOffset);
    }
    
    @Override
    public long skip(final long numToSkip) throws IOException {
        final byte[] skipBuf = new byte[8192];
        long skip;
        int numRead;
        for (skip = numToSkip; skip > 0L; skip -= numRead) {
            final int realSkip = (int)((skip > skipBuf.length) ? skipBuf.length : skip);
            numRead = this.read(skipBuf, 0, realSkip);
            if (numRead == -1) {
                break;
            }
        }
        return numToSkip - skip;
    }
    
    @Override
    public boolean markSupported() {
        return false;
    }
    
    @Override
    public void mark(final int markLimit) {
    }
    
    @Override
    public void reset() {
    }
    
    public TarEntry getNextEntry() throws IOException {
        if (this.hasHitEOF) {
            return null;
        }
        if (this.currEntry != null) {
            long numToSkip = this.entrySize - this.entryOffset;
            if (this.debug) {
                System.err.println("TarInputStream: SKIP currENTRY '" + this.currEntry.getName() + "' SZ " + this.entrySize + " OFF " + this.entryOffset + "  skipping " + numToSkip + " bytes");
            }
            while (numToSkip > 0L) {
                final long skipped = this.skip(numToSkip);
                if (skipped <= 0L) {
                    throw new RuntimeException("failed to skip current tar entry");
                }
                numToSkip -= skipped;
            }
            this.readBuf = null;
        }
        final byte[] headerBuf = this.buffer.readRecord();
        if (headerBuf == null) {
            if (this.debug) {
                System.err.println("READ NULL RECORD");
            }
            this.hasHitEOF = true;
        }
        else if (this.buffer.isEOFRecord(headerBuf)) {
            if (this.debug) {
                System.err.println("READ EOF RECORD");
            }
            this.hasHitEOF = true;
        }
        if (this.hasHitEOF) {
            this.currEntry = null;
        }
        else {
            this.currEntry = new TarEntry(headerBuf);
            if (this.debug) {
                System.err.println("TarInputStream: SET CURRENTRY '" + this.currEntry.getName() + "' size = " + this.currEntry.getSize());
            }
            this.entryOffset = 0L;
            this.entrySize = this.currEntry.getSize();
        }
        if (this.currEntry != null && this.currEntry.isGNULongNameEntry()) {
            final StringBuffer longName = new StringBuffer();
            final byte[] buf = new byte[256];
            int length = 0;
            while ((length = this.read(buf)) >= 0) {
                longName.append(new String(buf, 0, length));
            }
            this.getNextEntry();
            if (this.currEntry == null) {
                return null;
            }
            if (longName.length() > 0 && longName.charAt(longName.length() - 1) == '\0') {
                longName.deleteCharAt(longName.length() - 1);
            }
            this.currEntry.setName(longName.toString());
        }
        return this.currEntry;
    }
    
    @Override
    public int read() throws IOException {
        final int num = this.read(this.oneBuf, 0, 1);
        return (num == -1) ? -1 : (this.oneBuf[0] & 0xFF);
    }
    
    @Override
    public int read(final byte[] buf, int offset, int numToRead) throws IOException {
        int totalRead = 0;
        if (this.entryOffset >= this.entrySize) {
            return -1;
        }
        if (numToRead + this.entryOffset > this.entrySize) {
            numToRead = (int)(this.entrySize - this.entryOffset);
        }
        if (this.readBuf != null) {
            final int sz = (numToRead > this.readBuf.length) ? this.readBuf.length : numToRead;
            System.arraycopy(this.readBuf, 0, buf, offset, sz);
            if (sz >= this.readBuf.length) {
                this.readBuf = null;
            }
            else {
                final int newLen = this.readBuf.length - sz;
                final byte[] newBuf = new byte[newLen];
                System.arraycopy(this.readBuf, sz, newBuf, 0, newLen);
                this.readBuf = newBuf;
            }
            totalRead += sz;
            numToRead -= sz;
            offset += sz;
        }
        while (numToRead > 0) {
            final byte[] rec = this.buffer.readRecord();
            if (rec == null) {
                throw new IOException("unexpected EOF with " + numToRead + " bytes unread");
            }
            int sz2 = numToRead;
            final int recLen = rec.length;
            if (recLen > sz2) {
                System.arraycopy(rec, 0, buf, offset, sz2);
                System.arraycopy(rec, sz2, this.readBuf = new byte[recLen - sz2], 0, recLen - sz2);
            }
            else {
                sz2 = recLen;
                System.arraycopy(rec, 0, buf, offset, recLen);
            }
            totalRead += sz2;
            numToRead -= sz2;
            offset += sz2;
        }
        this.entryOffset += totalRead;
        return totalRead;
    }
    
    public void copyEntryContents(final OutputStream out) throws IOException {
        final byte[] buf = new byte[32768];
        while (true) {
            final int numRead = this.read(buf, 0, buf.length);
            if (numRead == -1) {
                break;
            }
            out.write(buf, 0, numRead);
        }
    }
}
