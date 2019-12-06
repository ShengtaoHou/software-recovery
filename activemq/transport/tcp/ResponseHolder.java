// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.tcp;

import org.apache.activemq.command.Response;

public class ResponseHolder
{
    protected Response response;
    protected Object lock;
    protected boolean notified;
    
    public ResponseHolder() {
        this.lock = new Object();
    }
    
    public void setResponse(final Response r) {
        synchronized (this.lock) {
            this.response = r;
            this.notified = true;
            this.lock.notify();
        }
    }
    
    public Response getResponse() {
        return this.getResponse(0);
    }
    
    public Response getResponse(final int timeout) {
        synchronized (this.lock) {
            if (!this.notified) {
                try {
                    this.lock.wait(timeout);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return this.response;
    }
    
    public void close() {
        synchronized (this.lock) {
            this.notified = true;
            this.lock.notifyAll();
        }
    }
}
