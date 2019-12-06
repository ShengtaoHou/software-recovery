// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store;

import org.slf4j.LoggerFactory;
import org.apache.activemq.util.ServiceStopper;
import java.io.IOException;
import org.apache.activemq.util.LockFile;
import org.slf4j.Logger;
import java.io.File;
import org.apache.activemq.broker.AbstractLocker;

public class SharedFileLocker extends AbstractLocker
{
    public static final File DEFAULT_DIRECTORY;
    private static final Logger LOG;
    private LockFile lockFile;
    protected File directory;
    
    public SharedFileLocker() {
        this.directory = SharedFileLocker.DEFAULT_DIRECTORY;
    }
    
    public void doStart() throws Exception {
        if (this.lockFile == null) {
            final File lockFileName = new File(this.directory, "lock");
            this.lockFile = new LockFile(lockFileName, true);
            if (this.failIfLocked) {
                this.lockFile.lock();
            }
            else {
                boolean locked = false;
                while (!this.isStopped() && !this.isStopping()) {
                    try {
                        this.lockFile.lock();
                        locked = true;
                    }
                    catch (IOException e) {
                        SharedFileLocker.LOG.info("Database " + lockFileName + " is locked... waiting " + this.lockAcquireSleepInterval / 1000L + " seconds for the database to be unlocked. Reason: " + e);
                        try {
                            Thread.sleep(this.lockAcquireSleepInterval);
                        }
                        catch (InterruptedException ex) {}
                        continue;
                    }
                    break;
                }
                if (!locked) {
                    throw new IOException("attempt to obtain lock aborted due to shutdown");
                }
            }
        }
    }
    
    @Override
    public boolean keepAlive() {
        return this.lockFile != null && this.lockFile.keepAlive();
    }
    
    public void doStop(final ServiceStopper stopper) throws Exception {
        this.lockFile.unlock();
        this.lockFile = null;
    }
    
    public File getDirectory() {
        return this.directory;
    }
    
    public void setDirectory(final File directory) {
        this.directory = directory;
    }
    
    @Override
    public void configure(final PersistenceAdapter persistenceAdapter) throws IOException {
        this.setDirectory(persistenceAdapter.getDirectory());
    }
    
    static {
        DEFAULT_DIRECTORY = new File("KahaDB");
        LOG = LoggerFactory.getLogger(SharedFileLocker.class);
    }
}
