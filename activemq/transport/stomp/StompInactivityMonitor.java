// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.stomp;

import org.slf4j.LoggerFactory;
import org.apache.activemq.command.WireFormatInfo;
import java.io.IOException;
import org.apache.activemq.wireformat.WireFormat;
import org.apache.activemq.transport.Transport;
import org.slf4j.Logger;
import org.apache.activemq.transport.AbstractInactivityMonitor;

public class StompInactivityMonitor extends AbstractInactivityMonitor
{
    private static final Logger LOG;
    private boolean isConfigured;
    
    public StompInactivityMonitor(final Transport next, final WireFormat wireFormat) {
        super(next, wireFormat);
        this.isConfigured = false;
    }
    
    public void startMonitoring() throws IOException {
        this.isConfigured = true;
        this.startMonitorThreads();
    }
    
    @Override
    protected void processInboundWireFormatInfo(final WireFormatInfo info) throws IOException {
    }
    
    @Override
    protected void processOutboundWireFormatInfo(final WireFormatInfo info) throws IOException {
    }
    
    @Override
    protected boolean configuredOk() throws IOException {
        if (!this.isConfigured) {
            return false;
        }
        StompInactivityMonitor.LOG.debug("Stomp Inactivity Monitor read check interval: {}ms, write check interval: {}ms", (Object)this.getReadCheckTime(), this.getWriteCheckTime());
        return this.getReadCheckTime() >= 0L && this.getWriteCheckTime() >= 0L;
    }
    
    static {
        LOG = LoggerFactory.getLogger(StompInactivityMonitor.class);
    }
}
