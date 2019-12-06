// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.apache.activemq.Service;

public abstract class ServiceSupport implements Service
{
    private static final Logger LOG;
    private AtomicBoolean started;
    private AtomicBoolean stopping;
    private AtomicBoolean stopped;
    private List<ServiceListener> serviceListeners;
    
    public ServiceSupport() {
        this.started = new AtomicBoolean(false);
        this.stopping = new AtomicBoolean(false);
        this.stopped = new AtomicBoolean(false);
        this.serviceListeners = new CopyOnWriteArrayList<ServiceListener>();
    }
    
    public static void dispose(final Service service) {
        try {
            service.stop();
        }
        catch (Exception e) {
            ServiceSupport.LOG.debug("Could not stop service: " + service + ". Reason: " + e, e);
        }
    }
    
    @Override
    public void start() throws Exception {
        if (this.started.compareAndSet(false, true)) {
            boolean success = false;
            this.stopped.set(false);
            try {
                this.preStart();
                this.doStart();
                success = true;
            }
            finally {
                this.started.set(success);
            }
            for (final ServiceListener l : this.serviceListeners) {
                l.started(this);
            }
        }
    }
    
    @Override
    public void stop() throws Exception {
        if (this.stopped.compareAndSet(false, true)) {
            this.stopping.set(true);
            final ServiceStopper stopper = new ServiceStopper();
            try {
                this.doStop(stopper);
            }
            catch (Exception e) {
                stopper.onException(this, e);
            }
            finally {
                this.postStop(stopper);
            }
            this.stopped.set(true);
            this.started.set(false);
            this.stopping.set(false);
            for (final ServiceListener l : this.serviceListeners) {
                l.stopped(this);
            }
            stopper.throwFirstException();
        }
    }
    
    public boolean isStarted() {
        return this.started.get();
    }
    
    public boolean isStopping() {
        return this.stopping.get();
    }
    
    public boolean isStopped() {
        return this.stopped.get();
    }
    
    public void addServiceListener(final ServiceListener l) {
        this.serviceListeners.add(l);
    }
    
    public void removeServiceListener(final ServiceListener l) {
        this.serviceListeners.remove(l);
    }
    
    protected void postStop(final ServiceStopper stopper) throws Exception {
    }
    
    protected abstract void doStop(final ServiceStopper p0) throws Exception;
    
    protected void preStart() throws Exception {
    }
    
    protected abstract void doStart() throws Exception;
    
    static {
        LOG = LoggerFactory.getLogger(ServiceSupport.class);
    }
}
