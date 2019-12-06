// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.page;

import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;

public class Page<T>
{
    public static final int PAGE_HEADER_SIZE = 21;
    public static final byte PAGE_FREE_TYPE = 0;
    public static final byte PAGE_PART_TYPE = 1;
    public static final byte PAGE_END_TYPE = 2;
    long pageId;
    byte type;
    long txId;
    int checksum;
    long next;
    T data;
    
    public Page() {
        this.type = 0;
    }
    
    public Page(final long pageId) {
        this.type = 0;
        this.pageId = pageId;
    }
    
    public Page<T> copy(final Page<T> other) {
        this.pageId = other.pageId;
        this.txId = other.txId;
        this.type = other.type;
        this.next = other.next;
        this.data = other.data;
        return this;
    }
    
    Page<T> copy() {
        return (Page<T>)new Page().copy(this);
    }
    
    void makeFree(final long txId) {
        this.type = 0;
        this.txId = txId;
        this.data = null;
        this.next = 0L;
    }
    
    public void makePagePart(final long next, final long txId) {
        this.type = 1;
        this.next = next;
        this.txId = txId;
    }
    
    public void makePageEnd(final long size, final long txId) {
        this.type = 2;
        this.next = size;
        this.txId = txId;
    }
    
    void write(final DataOutput os) throws IOException {
        os.writeByte(this.type);
        os.writeLong(this.txId);
        os.writeLong(this.next);
        os.writeInt(this.checksum);
    }
    
    void read(final DataInput is) throws IOException {
        this.type = is.readByte();
        this.txId = is.readLong();
        this.next = is.readLong();
        this.checksum = is.readInt();
    }
    
    public long getPageId() {
        return this.pageId;
    }
    
    public long getTxId() {
        return this.txId;
    }
    
    public T get() {
        return this.data;
    }
    
    public void set(final T data) {
        this.data = data;
    }
    
    public short getType() {
        return this.type;
    }
    
    public long getNext() {
        return this.next;
    }
    
    @Override
    public String toString() {
        return "[Page:" + this.getPageId() + ", type: " + this.type + "]";
    }
    
    public int getChecksum() {
        return this.checksum;
    }
    
    public void setChecksum(final int checksum) {
        this.checksum = checksum;
    }
}
