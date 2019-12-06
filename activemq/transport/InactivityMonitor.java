// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import org.apache.activemq.wireformat.WireFormat;
import org.apache.activemq.command.WireFormatInfo;
import org.slf4j.Logger;

public class InactivityMonitor extends AbstractInactivityMonitor
{
    private static final Logger LOG;
    private WireFormatInfo localWireFormatInfo;
    private WireFormatInfo remoteWireFormatInfo;
    private boolean ignoreRemoteWireFormat;
    private boolean ignoreAllWireFormatInfo;
    
    public InactivityMonitor(final Transport next, final WireFormat wireFormat) {
        super(next, wireFormat);
        this.ignoreRemoteWireFormat = false;
        this.ignoreAllWireFormatInfo = false;
        if (this.wireFormat == null) {
            this.ignoreAllWireFormatInfo = true;
        }
    }
    
    @Override
    protected void processInboundWireFormatInfo(final WireFormatInfo info) throws IOException {
        IOException error = null;
        this.remoteWireFormatInfo = info;
        try {
            this.startMonitorThreads();
        }
        catch (IOException e) {
            error = e;
        }
        if (error != null) {
            this.onException(error);
        }
    }
    
    @Override
    protected void processOutboundWireFormatInfo(final WireFormatInfo info) throws IOException {
        this.localWireFormatInfo = info;
        this.startMonitorThreads();
    }
    
    @Override
    protected synchronized void startMonitorThreads() throws IOException {
        if (this.isMonitorStarted()) {
            return;
        }
        final long readCheckTime = this.getReadCheckTime();
        if (readCheckTime > 0L) {
            this.setWriteCheckTime(this.writeCheckValueFromReadCheck(readCheckTime));
        }
        super.startMonitorThreads();
    }
    
    private long writeCheckValueFromReadCheck(final long readCheckTime) {
        return (readCheckTime > 3L) ? (readCheckTime / 3L) : readCheckTime;
    }
    
    @Override
    protected boolean configuredOk() throws IOException {
        boolean configured = false;
        if (this.ignoreAllWireFormatInfo) {
            configured = true;
        }
        else if (this.localWireFormatInfo != null && this.remoteWireFormatInfo != null) {
            if (!this.ignoreRemoteWireFormat) {
                if (InactivityMonitor.LOG.isDebugEnabled()) {
                    InactivityMonitor.LOG.debug("Using min of local: " + this.localWireFormatInfo + " and remote: " + this.remoteWireFormatInfo);
                }
                final long readCheckTime = Math.min(this.localWireFormatInfo.getMaxInactivityDuration(), this.remoteWireFormatInfo.getMaxInactivityDuration());
                final long writeCheckTime = this.writeCheckValueFromReadCheck(readCheckTime);
                this.setReadCheckTime(readCheckTime);
                this.setInitialDelayTime(Math.min(this.localWireFormatInfo.getMaxInactivityDurationInitalDelay(), this.remoteWireFormatInfo.getMaxInactivityDurationInitalDelay()));
                this.setWriteCheckTime(writeCheckTime);
            }
            else {
                if (InactivityMonitor.LOG.isDebugEnabled()) {
                    InactivityMonitor.LOG.debug("Using local: " + this.localWireFormatInfo);
                }
                final long readCheckTime = this.localWireFormatInfo.getMaxInactivityDuration();
                final long writeCheckTime = this.writeCheckValueFromReadCheck(readCheckTime);
                this.setReadCheckTime(readCheckTime);
                this.setInitialDelayTime(this.localWireFormatInfo.getMaxInactivityDurationInitalDelay());
                this.setWriteCheckTime(writeCheckTime);
            }
            configured = true;
        }
        return configured;
    }
    
    public boolean isIgnoreAllWireFormatInfo() {
        return this.ignoreAllWireFormatInfo;
    }
    
    public void setIgnoreAllWireFormatInfo(final boolean ignoreAllWireFormatInfo) {
        this.ignoreAllWireFormatInfo = ignoreAllWireFormatInfo;
    }
    
    public boolean isIgnoreRemoteWireFormat() {
        return this.ignoreRemoteWireFormat;
    }
    
    public void setIgnoreRemoteWireFormat(final boolean ignoreRemoteWireFormat) {
        this.ignoreRemoteWireFormat = ignoreRemoteWireFormat;
    }
    
    static {
        LOG = LoggerFactory.getLogger(InactivityMonitor.class);
    }
}
