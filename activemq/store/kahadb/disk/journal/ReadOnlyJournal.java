// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.journal;

import java.io.IOException;
import java.util.List;
import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;
import java.io.FilenameFilter;
import java.io.File;
import java.util.ArrayList;

public class ReadOnlyJournal extends Journal
{
    private final ArrayList<File> dirs;
    
    public ReadOnlyJournal(final ArrayList<File> dirs) {
        this.dirs = dirs;
    }
    
    @Override
    public synchronized void start() throws IOException {
        if (this.started) {
            return;
        }
        this.started = true;
        final ArrayList<File> files = new ArrayList<File>();
        for (final File d : this.dirs) {
            final File directory = d;
            final File[] f = d.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(final File dir, final String n) {
                    return dir.equals(d) && n.startsWith(ReadOnlyJournal.this.filePrefix);
                }
            });
            for (int i = 0; i < f.length; ++i) {
                files.add(f[i]);
            }
        }
        for (final File file : files) {
            try {
                final String n = file.getName();
                final String numStr = n.substring(this.filePrefix.length(), n.length());
                final int num = Integer.parseInt(numStr);
                final DataFile dataFile = new ReadOnlyDataFile(file, num, this.preferedFileLength);
                this.fileMap.put(dataFile.getDataFileId(), dataFile);
                this.totalLength.addAndGet(dataFile.getLength());
            }
            catch (NumberFormatException ex) {}
        }
        final List<DataFile> list = new ArrayList<DataFile>(this.fileMap.values());
        Collections.sort(list);
        for (final DataFile df : list) {
            this.dataFiles.addLast(df);
            this.fileByFileMap.put(df.getFile(), df);
        }
    }
    
    @Override
    public synchronized void close() throws IOException {
        if (!this.started) {
            return;
        }
        this.accessorPool.close();
        this.fileMap.clear();
        this.fileByFileMap.clear();
        this.started = false;
    }
    
    public Location getFirstLocation() throws IllegalStateException, IOException {
        if (this.dataFiles.isEmpty()) {
            return null;
        }
        final DataFile first = this.dataFiles.getHead();
        final Location cur = new Location();
        cur.setDataFileId(first.getDataFileId());
        cur.setOffset(0);
        cur.setSize(0);
        return this.getNextLocation(cur);
    }
    
    @Override
    public synchronized boolean delete() throws IOException {
        throw new RuntimeException("Cannot delete a ReadOnlyAsyncDataManager");
    }
}
