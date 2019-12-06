// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

import org.slf4j.LoggerFactory;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ExecutorService;
import org.apache.activemq.util.ThreadPoolUtils;
import org.apache.activemq.util.ServiceStopper;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import org.apache.activemq.store.PersistenceAdapter;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ScheduledFuture;
import org.slf4j.Logger;
import org.apache.activemq.util.ServiceSupport;

public abstract class LockableServiceSupport extends ServiceSupport implements Lockable, BrokerServiceAware
{
    private static final Logger LOG;
    boolean useLock;
    Locker locker;
    long lockKeepAlivePeriod;
    private ScheduledFuture<?> keepAliveTicket;
    private ScheduledThreadPoolExecutor clockDaemon;
    protected BrokerService brokerService;
    
    public LockableServiceSupport() {
        this.useLock = true;
        this.lockKeepAlivePeriod = 0L;
    }
    
    public abstract void init() throws Exception;
    
    @Override
    public void setUseLock(final boolean useLock) {
        this.useLock = useLock;
    }
    
    public boolean isUseLock() {
        return this.useLock;
    }
    
    @Override
    public void setLocker(final Locker locker) throws IOException {
        (this.locker = locker).setLockable(this);
        if (this instanceof PersistenceAdapter) {
            this.locker.configure((PersistenceAdapter)this);
        }
    }
    
    public Locker getLocker() throws IOException {
        if (this.locker == null) {
            this.setLocker(this.createDefaultLocker());
        }
        return this.locker;
    }
    
    @Override
    public void setLockKeepAlivePeriod(final long lockKeepAlivePeriod) {
        this.lockKeepAlivePeriod = lockKeepAlivePeriod;
    }
    
    @Override
    public long getLockKeepAlivePeriod() {
        return this.lockKeepAlivePeriod;
    }
    
    public void preStart() throws Exception {
        this.init();
        if (this.useLock) {
            if (this.getLocker() == null) {
                LockableServiceSupport.LOG.warn("No locker configured");
            }
            else {
                this.getLocker().start();
                if (this.lockKeepAlivePeriod > 0L) {
                    this.keepAliveTicket = this.getScheduledThreadPoolExecutor().scheduleAtFixedRate(new Runnable() {
                        @Override
                        public void run() {
                            LockableServiceSupport.this.keepLockAlive();
                        }
                    }, this.lockKeepAlivePeriod, this.lockKeepAlivePeriod, TimeUnit.MILLISECONDS);
                }
            }
        }
    }
    
    public void postStop(final ServiceStopper stopper) throws Exception {
        if (this.useLock) {
            if (this.keepAliveTicket != null) {
                this.keepAliveTicket.cancel(false);
                this.keepAliveTicket = null;
            }
            if (this.locker != null) {
                this.getLocker().stop();
            }
            ThreadPoolUtils.shutdown(this.clockDaemon);
        }
    }
    
    protected void keepLockAlive() {
        boolean stop = false;
        try {
            final Locker locker = this.getLocker();
            if (locker != null && !locker.keepAlive()) {
                stop = true;
            }
        }
        catch (SuppressReplyException e) {
            LockableServiceSupport.LOG.warn("locker keepAlive resulted in", e);
        }
        catch (IOException e2) {
            LockableServiceSupport.LOG.warn("locker keepAlive resulted in", e2);
        }
        if (stop) {
            this.stopBroker();
        }
    }
    
    protected void stopBroker() {
        LockableServiceSupport.LOG.error("{}, no longer able to keep the exclusive lock so giving up being a master", this.brokerService.getBrokerName());
        try {
            if (this.brokerService.isRestartAllowed()) {
                this.brokerService.requestRestart();
            }
            this.brokerService.stop();
        }
        catch (Exception e) {
            LockableServiceSupport.LOG.warn("Failure occurred while stopping broker");
        }
    }
    
    public ScheduledThreadPoolExecutor getScheduledThreadPoolExecutor() {
        if (this.clockDaemon == null) {
            this.clockDaemon = new ScheduledThreadPoolExecutor(5, new ThreadFactory() {
                @Override
                public Thread newThread(final Runnable runnable) {
                    final Thread thread = new Thread(runnable, "ActiveMQ Lock KeepAlive Timer");
                    thread.setDaemon(true);
                    return thread;
                }
            });
        }
        return this.clockDaemon;
    }
    
    @Override
    public void setBrokerService(final BrokerService brokerService) {
        this.brokerService = brokerService;
    }
    
    public BrokerService getBrokerService() {
        return this.brokerService;
    }
    
    static {
        LOG = LoggerFactory.getLogger(LockableServiceSupport.class);
    }
}
