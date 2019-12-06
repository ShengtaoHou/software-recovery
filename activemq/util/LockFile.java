// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import java.nio.channels.OverlappingFileLockException;
import java.util.Date;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.io.File;

public class LockFile
{
    private static final boolean DISABLE_FILE_LOCK;
    private final File file;
    private FileLock lock;
    private RandomAccessFile readFile;
    private int lockCounter;
    private final boolean deleteOnUnlock;
    
    public LockFile(final File file, final boolean deleteOnUnlock) {
        this.file = file;
        this.deleteOnUnlock = deleteOnUnlock;
    }
    
    public synchronized void lock() throws IOException {
        if (LockFile.DISABLE_FILE_LOCK) {
            return;
        }
        if (this.lockCounter > 0) {
            return;
        }
        IOHelper.mkdirs(this.file.getParentFile());
        synchronized (LockFile.class) {
            if (System.getProperty(this.getVmLockKey()) != null) {
                throw new IOException("File '" + this.file + "' could not be locked as lock is already held for this jvm.");
            }
            System.setProperty(this.getVmLockKey(), new Date().toString());
        }
        try {
            if (this.lock == null) {
                this.readFile = new RandomAccessFile(this.file, "rw");
                IOException reason = null;
                try {
                    this.lock = this.readFile.getChannel().tryLock(0L, Math.max(1L, this.readFile.getChannel().size()), false);
                }
                catch (OverlappingFileLockException e) {
                    reason = IOExceptionSupport.create("File '" + this.file + "' could not be locked.", e);
                }
                catch (IOException ioe) {
                    reason = ioe;
                }
                if (this.lock != null) {
                    ++this.lockCounter;
                    System.setProperty(this.getVmLockKey(), new Date().toString());
                }
                else {
                    this.closeReadFile();
                    if (reason != null) {
                        throw reason;
                    }
                    throw new IOException("File '" + this.file + "' could not be locked.");
                }
            }
        }
        finally {
            synchronized (LockFile.class) {
                if (this.lock == null) {
                    System.getProperties().remove(this.getVmLockKey());
                }
            }
        }
    }
    
    public void unlock() {
        if (LockFile.DISABLE_FILE_LOCK) {
            return;
        }
        --this.lockCounter;
        if (this.lockCounter != 0) {
            return;
        }
        if (this.lock != null) {
            try {
                this.lock.release();
                System.getProperties().remove(this.getVmLockKey());
            }
            catch (Throwable t) {}
            this.lock = null;
        }
        this.closeReadFile();
        if (this.deleteOnUnlock) {
            this.file.delete();
        }
    }
    
    private String getVmLockKey() throws IOException {
        return this.getClass().getName() + ".lock." + this.file.getCanonicalPath();
    }
    
    private void closeReadFile() {
        if (this.readFile != null) {
            try {
                this.readFile.close();
            }
            catch (Throwable t) {}
            this.readFile = null;
        }
    }
    
    public boolean keepAlive() {
        return this.lock != null && this.lock.isValid() && this.file.exists();
    }
    
    static {
        DISABLE_FILE_LOCK = Boolean.getBoolean("java.nio.channels.FileLock.broken");
    }
}
