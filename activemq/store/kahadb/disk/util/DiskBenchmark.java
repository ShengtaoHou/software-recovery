// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.util;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.io.File;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;

public class DiskBenchmark
{
    private static final boolean SKIP_METADATA_UPDATE;
    boolean verbose;
    int bs;
    long size;
    long sampleInterval;
    
    public DiskBenchmark() {
        this.bs = 4096;
        this.size = 524288000L;
        this.sampleInterval = 10000L;
    }
    
    public static void main(String[] args) {
        final DiskBenchmark benchmark = new DiskBenchmark();
        args = CommandLineSupport.setOptions(benchmark, args);
        final ArrayList<String> files = new ArrayList<String>();
        if (args.length == 0) {
            files.add("disk-benchmark.dat");
        }
        else {
            files.addAll(Arrays.asList(args));
        }
        for (final String f : files) {
            try {
                final File file = new File(f);
                if (file.exists()) {
                    System.out.println("File " + file + " allready exists, will not benchmark.");
                }
                else {
                    System.out.println("Benchmarking: " + file.getCanonicalPath());
                    final Report report = benchmark.benchmark(file);
                    file.delete();
                    System.out.println(report.toString());
                }
            }
            catch (Throwable e) {
                if (benchmark.verbose) {
                    System.out.println("ERROR:");
                    e.printStackTrace(System.out);
                }
                else {
                    System.out.println("ERROR: " + e);
                }
            }
        }
    }
    
    public Report benchmark(final File file) throws IOException {
        final Report rc = new Report();
        final byte[] data = new byte[this.bs];
        for (int i = 0; i < data.length; ++i) {
            data[i] = (byte)(97 + i % 26);
        }
        rc.size = data.length;
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.setLength(this.size);
        long start = System.currentTimeMillis();
        long now = System.currentTimeMillis();
        int ioCount = 0;
        while (now - start <= this.sampleInterval) {
            raf.seek(0L);
            for (long j = 0L; j + data.length < this.size; j += data.length) {
                raf.write(data);
                ++ioCount;
                now = System.currentTimeMillis();
                if (now - start > this.sampleInterval) {
                    break;
                }
            }
            raf.getChannel().force(!DiskBenchmark.SKIP_METADATA_UPDATE);
        }
        raf.getChannel().force(!DiskBenchmark.SKIP_METADATA_UPDATE);
        raf.close();
        now = System.currentTimeMillis();
        rc.size = data.length;
        rc.writes = ioCount;
        rc.writeDuration = now - start;
        raf = new RandomAccessFile(file, "rw");
        start = System.currentTimeMillis();
        now = System.currentTimeMillis();
        ioCount = 0;
        while (now - start <= this.sampleInterval) {
            for (long j = 0L; j + data.length < this.size; j += data.length) {
                raf.seek(j);
                raf.write(data);
                raf.getChannel().force(false);
                ++ioCount;
                now = System.currentTimeMillis();
                if (now - start > this.sampleInterval) {
                    break;
                }
            }
        }
        raf.close();
        now = System.currentTimeMillis();
        rc.syncWrites = ioCount;
        rc.syncWriteDuration = now - start;
        raf = new RandomAccessFile(file, "rw");
        start = System.currentTimeMillis();
        now = System.currentTimeMillis();
        ioCount = 0;
        while (now - start <= this.sampleInterval) {
            raf.seek(0L);
            for (long j = 0L; j + data.length < this.size; j += data.length) {
                raf.seek(j);
                raf.readFully(data);
                ++ioCount;
                now = System.currentTimeMillis();
                if (now - start > this.sampleInterval) {
                    break;
                }
            }
        }
        raf.close();
        rc.reads = ioCount;
        rc.readDuration = now - start;
        return rc;
    }
    
    public boolean isVerbose() {
        return this.verbose;
    }
    
    public void setVerbose(final boolean verbose) {
        this.verbose = verbose;
    }
    
    public int getBs() {
        return this.bs;
    }
    
    public void setBs(final int bs) {
        this.bs = bs;
    }
    
    public long getSize() {
        return this.size;
    }
    
    public void setSize(final long size) {
        this.size = size;
    }
    
    public long getSampleInterval() {
        return this.sampleInterval;
    }
    
    public void setSampleInterval(final long sampleInterval) {
        this.sampleInterval = sampleInterval;
    }
    
    static {
        SKIP_METADATA_UPDATE = Boolean.getBoolean("org.apache.activemq.file.skipMetadataUpdate");
    }
    
    public static class Report
    {
        public int size;
        public int writes;
        public long writeDuration;
        public int syncWrites;
        public long syncWriteDuration;
        public int reads;
        public long readDuration;
        
        @Override
        public String toString() {
            return "Writes: \n  " + this.writes + " writes of size " + this.size + " written in " + this.writeDuration / 1000.0 + " seconds.\n  " + this.getWriteRate() + " writes/second.\n  " + this.getWriteSizeRate() + " megs/second.\n\nSync Writes: \n  " + this.syncWrites + " writes of size " + this.size + " written in " + this.syncWriteDuration / 1000.0 + " seconds.\n  " + this.getSyncWriteRate() + " writes/second.\n  " + this.getSyncWriteSizeRate() + " megs/second.\n\nReads: \n  " + this.reads + " reads of size " + this.size + " read in " + this.readDuration / 1000.0 + " seconds.\n  " + this.getReadRate() + " writes/second.\n  " + this.getReadSizeRate() + " megs/second.\n\n";
        }
        
        private float getWriteSizeRate() {
            float rc = (float)this.writes;
            rc *= this.size;
            rc /= 1048576.0f;
            rc /= (float)(this.writeDuration / 1000.0);
            return rc;
        }
        
        private float getWriteRate() {
            float rc = (float)this.writes;
            rc /= (float)(this.writeDuration / 1000.0);
            return rc;
        }
        
        private float getSyncWriteSizeRate() {
            float rc = (float)this.syncWrites;
            rc *= this.size;
            rc /= 1048576.0f;
            rc /= (float)(this.syncWriteDuration / 1000.0);
            return rc;
        }
        
        private float getSyncWriteRate() {
            float rc = (float)this.syncWrites;
            rc /= (float)(this.syncWriteDuration / 1000.0);
            return rc;
        }
        
        private float getReadSizeRate() {
            float rc = (float)this.reads;
            rc *= this.size;
            rc /= 1048576.0f;
            rc /= (float)(this.readDuration / 1000.0);
            return rc;
        }
        
        private float getReadRate() {
            float rc = (float)this.reads;
            rc /= (float)(this.readDuration / 1000.0);
            return rc;
        }
        
        public int getSize() {
            return this.size;
        }
        
        public void setSize(final int size) {
            this.size = size;
        }
        
        public int getWrites() {
            return this.writes;
        }
        
        public void setWrites(final int writes) {
            this.writes = writes;
        }
        
        public long getWriteDuration() {
            return this.writeDuration;
        }
        
        public void setWriteDuration(final long writeDuration) {
            this.writeDuration = writeDuration;
        }
        
        public int getSyncWrites() {
            return this.syncWrites;
        }
        
        public void setSyncWrites(final int syncWrites) {
            this.syncWrites = syncWrites;
        }
        
        public long getSyncWriteDuration() {
            return this.syncWriteDuration;
        }
        
        public void setSyncWriteDuration(final long syncWriteDuration) {
            this.syncWriteDuration = syncWriteDuration;
        }
        
        public int getReads() {
            return this.reads;
        }
        
        public void setReads(final int reads) {
            this.reads = reads;
        }
        
        public long getReadDuration() {
            return this.readDuration;
        }
        
        public void setReadDuration(final long readDuration) {
            this.readDuration = readDuration;
        }
    }
}
