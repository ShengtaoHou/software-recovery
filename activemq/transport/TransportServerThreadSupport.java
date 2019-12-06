// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

import org.slf4j.LoggerFactory;
import org.apache.activemq.util.ServiceStopper;
import java.net.URI;
import org.slf4j.Logger;

public abstract class TransportServerThreadSupport extends TransportServerSupport implements Runnable
{
    private static final Logger LOG;
    private boolean daemon;
    private boolean joinOnStop;
    private Thread runner;
    private long stackSize;
    
    public TransportServerThreadSupport(final URI location) {
        super(location);
        this.daemon = true;
        this.joinOnStop = true;
    }
    
    public boolean isDaemon() {
        return this.daemon;
    }
    
    public void setDaemon(final boolean daemon) {
        this.daemon = daemon;
    }
    
    public boolean isJoinOnStop() {
        return this.joinOnStop;
    }
    
    public void setJoinOnStop(final boolean joinOnStop) {
        this.joinOnStop = joinOnStop;
    }
    
    @Override
    protected void doStart() throws Exception {
        TransportServerThreadSupport.LOG.info("Listening for connections at: " + this.getConnectURI());
        (this.runner = new Thread(null, this, "ActiveMQ Transport Server: " + this.toString(), this.stackSize)).setDaemon(this.daemon);
        this.runner.setPriority(9);
        this.runner.start();
    }
    
    @Override
    protected void doStop(final ServiceStopper stopper) throws Exception {
        if (this.runner != null && this.joinOnStop) {
            this.runner.join();
            this.runner = null;
        }
    }
    
    public long getStackSize() {
        return this.stackSize;
    }
    
    public void setStackSize(final long stackSize) {
        this.stackSize = stackSize;
    }
    
    static {
        LOG = LoggerFactory.getLogger(TransportServerThreadSupport.class);
    }
}
