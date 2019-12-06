// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

public abstract class TransportThreadSupport extends TransportSupport implements Runnable
{
    private boolean daemon;
    private Thread runner;
    private long stackSize;
    
    public boolean isDaemon() {
        return this.daemon;
    }
    
    public void setDaemon(final boolean daemon) {
        this.daemon = daemon;
    }
    
    @Override
    protected void doStart() throws Exception {
        (this.runner = new Thread(null, this, "ActiveMQ Transport: " + this.toString(), this.stackSize)).setDaemon(this.daemon);
        this.runner.start();
    }
    
    public long getStackSize() {
        return this.stackSize;
    }
    
    public void setStackSize(final long stackSize) {
        this.stackSize = stackSize;
    }
}
