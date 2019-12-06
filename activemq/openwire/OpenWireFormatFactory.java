// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire;

import org.apache.activemq.command.WireFormatInfo;
import org.apache.activemq.wireformat.WireFormat;
import org.apache.activemq.wireformat.WireFormatFactory;

public class OpenWireFormatFactory implements WireFormatFactory
{
    private int version;
    private boolean stackTraceEnabled;
    private boolean tcpNoDelayEnabled;
    private boolean cacheEnabled;
    private boolean tightEncodingEnabled;
    private boolean sizePrefixDisabled;
    private long maxInactivityDuration;
    private long maxInactivityDurationInitalDelay;
    private int cacheSize;
    private long maxFrameSize;
    private String host;
    
    public OpenWireFormatFactory() {
        this.version = 10;
        this.stackTraceEnabled = true;
        this.tcpNoDelayEnabled = true;
        this.cacheEnabled = true;
        this.tightEncodingEnabled = true;
        this.maxInactivityDuration = 30000L;
        this.maxInactivityDurationInitalDelay = 10000L;
        this.cacheSize = 1024;
        this.maxFrameSize = Long.MAX_VALUE;
        this.host = null;
    }
    
    @Override
    public WireFormat createWireFormat() {
        final WireFormatInfo info = new WireFormatInfo();
        info.setVersion(this.version);
        try {
            info.setStackTraceEnabled(this.stackTraceEnabled);
            info.setCacheEnabled(this.cacheEnabled);
            info.setTcpNoDelayEnabled(this.tcpNoDelayEnabled);
            info.setTightEncodingEnabled(this.tightEncodingEnabled);
            info.setSizePrefixDisabled(this.sizePrefixDisabled);
            info.setMaxInactivityDuration(this.maxInactivityDuration);
            info.setMaxInactivityDurationInitalDelay(this.maxInactivityDurationInitalDelay);
            info.setCacheSize(this.cacheSize);
            info.setMaxFrameSize(this.maxFrameSize);
            if (this.host != null) {
                info.setHost(this.host);
            }
        }
        catch (Exception e) {
            final IllegalStateException ise = new IllegalStateException("Could not configure WireFormatInfo");
            ise.initCause(e);
            throw ise;
        }
        final OpenWireFormat f = new OpenWireFormat(this.version);
        f.setMaxFrameSize(this.maxFrameSize);
        f.setPreferedWireFormatInfo(info);
        return f;
    }
    
    public boolean isStackTraceEnabled() {
        return this.stackTraceEnabled;
    }
    
    public void setStackTraceEnabled(final boolean stackTraceEnabled) {
        this.stackTraceEnabled = stackTraceEnabled;
    }
    
    public boolean isTcpNoDelayEnabled() {
        return this.tcpNoDelayEnabled;
    }
    
    public void setTcpNoDelayEnabled(final boolean tcpNoDelayEnabled) {
        this.tcpNoDelayEnabled = tcpNoDelayEnabled;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public void setVersion(final int version) {
        this.version = version;
    }
    
    public boolean isCacheEnabled() {
        return this.cacheEnabled;
    }
    
    public void setCacheEnabled(final boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }
    
    public boolean isTightEncodingEnabled() {
        return this.tightEncodingEnabled;
    }
    
    public void setTightEncodingEnabled(final boolean tightEncodingEnabled) {
        this.tightEncodingEnabled = tightEncodingEnabled;
    }
    
    public boolean isSizePrefixDisabled() {
        return this.sizePrefixDisabled;
    }
    
    public void setSizePrefixDisabled(final boolean sizePrefixDisabled) {
        this.sizePrefixDisabled = sizePrefixDisabled;
    }
    
    public long getMaxInactivityDuration() {
        return this.maxInactivityDuration;
    }
    
    public void setMaxInactivityDuration(final long maxInactivityDuration) {
        this.maxInactivityDuration = maxInactivityDuration;
    }
    
    public int getCacheSize() {
        return this.cacheSize;
    }
    
    public void setCacheSize(final int cacheSize) {
        this.cacheSize = cacheSize;
    }
    
    public long getMaxInactivityDurationInitalDelay() {
        return this.maxInactivityDurationInitalDelay;
    }
    
    public void setMaxInactivityDurationInitalDelay(final long maxInactivityDurationInitalDelay) {
        this.maxInactivityDurationInitalDelay = maxInactivityDurationInitalDelay;
    }
    
    public long getMaxFrameSize() {
        return this.maxFrameSize;
    }
    
    public void setMaxFrameSize(final long maxFrameSize) {
        this.maxFrameSize = maxFrameSize;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public void setHost(final String host) {
        this.host = host;
    }
}
