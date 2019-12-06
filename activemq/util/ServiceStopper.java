// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.List;
import org.apache.activemq.Service;

public class ServiceStopper
{
    private Throwable firstException;
    
    public void stop(final Service service) {
        try {
            if (service != null) {
                service.stop();
            }
        }
        catch (Exception e) {
            this.onException(service, e);
        }
    }
    
    public void run(final Callback stopClosure) {
        try {
            stopClosure.execute();
        }
        catch (Throwable e) {
            this.onException(stopClosure, e);
        }
    }
    
    public void stopServices(final List services) {
        for (final Service service : services) {
            this.stop(service);
        }
    }
    
    public void onException(final Object owner, final Throwable e) {
        this.logError(owner, e);
        if (this.firstException == null) {
            this.firstException = e;
        }
    }
    
    public void throwFirstException() throws Exception {
        if (this.firstException == null) {
            return;
        }
        if (this.firstException instanceof Exception) {
            final Exception e = (Exception)this.firstException;
            throw e;
        }
        if (this.firstException instanceof RuntimeException) {
            final RuntimeException e2 = (RuntimeException)this.firstException;
            throw e2;
        }
        throw new RuntimeException("Unknown type of exception: " + this.firstException, this.firstException);
    }
    
    protected void logError(final Object service, final Throwable e) {
        final Logger log = LoggerFactory.getLogger(service.getClass());
        log.error("Could not stop service: " + service + ". Reason: " + e, e);
    }
}
