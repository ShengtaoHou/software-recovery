// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.journal;

import org.apache.activemq.util.IOHelper;
import java.io.IOException;
import org.apache.activemq.util.RecoverableRandomAccessFile;
import org.apache.activemq.store.kahadb.disk.util.SequenceSet;
import java.io.File;
import org.apache.activemq.store.kahadb.disk.util.LinkedNode;

public class DataFile extends LinkedNode<DataFile> implements Comparable<DataFile>
{
    protected final File file;
    protected final Integer dataFileId;
    protected volatile int length;
    protected final SequenceSet corruptedBlocks;
    
    DataFile(final File file, final int number, final int preferedSize) {
        this.corruptedBlocks = new SequenceSet();
        this.file = file;
        this.dataFileId = number;
        this.length = (int)(file.exists() ? file.length() : 0L);
    }
    
    public File getFile() {
        return this.file;
    }
    
    public Integer getDataFileId() {
        return this.dataFileId;
    }
    
    public synchronized int getLength() {
        return this.length;
    }
    
    public void setLength(final int length) {
        this.length = length;
    }
    
    public synchronized void incrementLength(final int size) {
        this.length += size;
    }
    
    @Override
    public synchronized String toString() {
        return this.file.getName() + " number = " + this.dataFileId + " , length = " + this.length;
    }
    
    public synchronized RecoverableRandomAccessFile openRandomAccessFile() throws IOException {
        return new RecoverableRandomAccessFile(this.file.getCanonicalPath(), "rw");
    }
    
    public synchronized void closeRandomAccessFile(final RecoverableRandomAccessFile file) throws IOException {
        file.close();
    }
    
    public synchronized boolean delete() throws IOException {
        return this.file.delete();
    }
    
    public synchronized void move(final File targetDirectory) throws IOException {
        IOHelper.moveFile(this.file, targetDirectory);
    }
    
    public SequenceSet getCorruptedBlocks() {
        return this.corruptedBlocks;
    }
    
    @Override
    public int compareTo(final DataFile df) {
        return this.dataFileId - df.dataFileId;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean result = false;
        if (o instanceof DataFile) {
            result = (this.compareTo((DataFile)o) == 0);
        }
        return result;
    }
    
    @Override
    public int hashCode() {
        return this.dataFileId;
    }
}
