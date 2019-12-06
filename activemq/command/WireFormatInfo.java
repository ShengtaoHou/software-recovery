// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import org.apache.activemq.state.CommandVisitor;
import org.fusesource.hawtbuf.UTF8Buffer;
import java.util.Arrays;
import java.io.OutputStream;
import java.io.DataOutputStream;
import org.apache.activemq.util.ByteArrayOutputStream;
import org.apache.activemq.wireformat.WireFormat;
import org.apache.activemq.util.MarshallingSupport;
import java.io.InputStream;
import java.io.DataInputStream;
import org.apache.activemq.util.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Collections;
import java.io.IOException;
import java.util.Map;
import org.apache.activemq.util.ByteSequence;

public class WireFormatInfo implements Command, MarshallAware
{
    public static final byte DATA_STRUCTURE_TYPE = 1;
    private static final int MAX_PROPERTY_SIZE = 4096;
    private static final byte[] MAGIC;
    protected byte[] magic;
    protected int version;
    protected ByteSequence marshalledProperties;
    protected transient Map<String, Object> properties;
    private transient Endpoint from;
    private transient Endpoint to;
    
    public WireFormatInfo() {
        this.magic = WireFormatInfo.MAGIC;
    }
    
    @Override
    public byte getDataStructureType() {
        return 1;
    }
    
    @Override
    public boolean isWireFormatInfo() {
        return true;
    }
    
    @Override
    public boolean isMarshallAware() {
        return true;
    }
    
    public byte[] getMagic() {
        return this.magic;
    }
    
    public void setMagic(final byte[] magic) {
        this.magic = magic;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public void setVersion(final int version) {
        this.version = version;
    }
    
    public ByteSequence getMarshalledProperties() {
        return this.marshalledProperties;
    }
    
    public void setMarshalledProperties(final ByteSequence marshalledProperties) {
        this.marshalledProperties = marshalledProperties;
    }
    
    @Override
    public Endpoint getFrom() {
        return this.from;
    }
    
    @Override
    public void setFrom(final Endpoint from) {
        this.from = from;
    }
    
    @Override
    public Endpoint getTo() {
        return this.to;
    }
    
    @Override
    public void setTo(final Endpoint to) {
        this.to = to;
    }
    
    public Object getProperty(final String name) throws IOException {
        if (this.properties == null) {
            if (this.marshalledProperties == null) {
                return null;
            }
            this.properties = this.unmarsallProperties(this.marshalledProperties);
        }
        return this.properties.get(name);
    }
    
    public Map<String, Object> getProperties() throws IOException {
        if (this.properties == null) {
            if (this.marshalledProperties == null) {
                return (Map<String, Object>)Collections.EMPTY_MAP;
            }
            this.properties = this.unmarsallProperties(this.marshalledProperties);
        }
        return Collections.unmodifiableMap((Map<? extends String, ?>)this.properties);
    }
    
    public void clearProperties() {
        this.marshalledProperties = null;
        this.properties = null;
    }
    
    public void setProperty(final String name, final Object value) throws IOException {
        this.lazyCreateProperties();
        this.properties.put(name, value);
    }
    
    protected void lazyCreateProperties() throws IOException {
        if (this.properties == null) {
            if (this.marshalledProperties == null) {
                this.properties = new HashMap<String, Object>();
            }
            else {
                this.properties = this.unmarsallProperties(this.marshalledProperties);
                this.marshalledProperties = null;
            }
        }
    }
    
    private Map<String, Object> unmarsallProperties(final ByteSequence marshalledProperties) throws IOException {
        return MarshallingSupport.unmarshalPrimitiveMap(new DataInputStream(new ByteArrayInputStream(marshalledProperties)), 4096);
    }
    
    @Override
    public void beforeMarshall(final WireFormat wireFormat) throws IOException {
        if (this.marshalledProperties == null && this.properties != null) {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final DataOutputStream os = new DataOutputStream(baos);
            MarshallingSupport.marshalPrimitiveMap(this.properties, os);
            os.close();
            this.marshalledProperties = baos.toByteSequence();
        }
    }
    
    @Override
    public void afterMarshall(final WireFormat wireFormat) throws IOException {
    }
    
    @Override
    public void beforeUnmarshall(final WireFormat wireFormat) throws IOException {
    }
    
    @Override
    public void afterUnmarshall(final WireFormat wireFormat) throws IOException {
    }
    
    public boolean isValid() {
        return this.magic != null && Arrays.equals(this.magic, WireFormatInfo.MAGIC);
    }
    
    @Override
    public void setResponseRequired(final boolean responseRequired) {
    }
    
    public boolean isCacheEnabled() throws IOException {
        return Boolean.TRUE == this.getProperty("CacheEnabled");
    }
    
    public void setCacheEnabled(final boolean cacheEnabled) throws IOException {
        this.setProperty("CacheEnabled", cacheEnabled ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public boolean isStackTraceEnabled() throws IOException {
        return Boolean.TRUE == this.getProperty("StackTraceEnabled");
    }
    
    public void setStackTraceEnabled(final boolean stackTraceEnabled) throws IOException {
        this.setProperty("StackTraceEnabled", stackTraceEnabled ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public boolean isTcpNoDelayEnabled() throws IOException {
        return Boolean.TRUE == this.getProperty("TcpNoDelayEnabled");
    }
    
    public void setTcpNoDelayEnabled(final boolean tcpNoDelayEnabled) throws IOException {
        this.setProperty("TcpNoDelayEnabled", tcpNoDelayEnabled ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public boolean isSizePrefixDisabled() throws IOException {
        return Boolean.TRUE == this.getProperty("SizePrefixDisabled");
    }
    
    public void setSizePrefixDisabled(final boolean prefixPacketSize) throws IOException {
        this.setProperty("SizePrefixDisabled", prefixPacketSize ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public boolean isTightEncodingEnabled() throws IOException {
        return Boolean.TRUE == this.getProperty("TightEncodingEnabled");
    }
    
    public void setTightEncodingEnabled(final boolean tightEncodingEnabled) throws IOException {
        this.setProperty("TightEncodingEnabled", tightEncodingEnabled ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public String getHost() throws IOException {
        final UTF8Buffer buff = (UTF8Buffer)this.getProperty("Host");
        if (buff == null) {
            return null;
        }
        return buff.toString();
    }
    
    public void setHost(final String hostname) throws IOException {
        this.setProperty("Host", hostname);
    }
    
    public long getMaxInactivityDuration() throws IOException {
        final Long l = (Long)this.getProperty("MaxInactivityDuration");
        return (l == null) ? 0L : l;
    }
    
    public void setMaxInactivityDuration(final long maxInactivityDuration) throws IOException {
        this.setProperty("MaxInactivityDuration", new Long(maxInactivityDuration));
    }
    
    public long getMaxInactivityDurationInitalDelay() throws IOException {
        final Long l = (Long)this.getProperty("MaxInactivityDurationInitalDelay");
        return (l == null) ? 0L : l;
    }
    
    public void setMaxInactivityDurationInitalDelay(final long maxInactivityDurationInitalDelay) throws IOException {
        this.setProperty("MaxInactivityDurationInitalDelay", new Long(maxInactivityDurationInitalDelay));
    }
    
    public long getMaxFrameSize() throws IOException {
        final Long l = (Long)this.getProperty("MaxFrameSize");
        return (l == null) ? 0L : l;
    }
    
    public void setMaxFrameSize(final long maxFrameSize) throws IOException {
        this.setProperty("MaxFrameSize", new Long(maxFrameSize));
    }
    
    public int getCacheSize() throws IOException {
        final Integer i = (Integer)this.getProperty("CacheSize");
        return (i == null) ? 0 : i;
    }
    
    public void setCacheSize(final int cacheSize) throws IOException {
        this.setProperty("CacheSize", new Integer(cacheSize));
    }
    
    @Override
    public Response visit(final CommandVisitor visitor) throws Exception {
        return visitor.processWireFormat(this);
    }
    
    @Override
    public String toString() {
        Map<String, Object> p = null;
        try {
            p = this.getProperties();
        }
        catch (IOException ex) {}
        return "WireFormatInfo { version=" + this.version + ", properties=" + p + ", magic=" + this.toString(this.magic) + "}";
    }
    
    private String toString(final byte[] data) {
        final StringBuffer sb = new StringBuffer();
        sb.append('[');
        for (int i = 0; i < data.length; ++i) {
            if (i != 0) {
                sb.append(',');
            }
            sb.append((char)data[i]);
        }
        sb.append(']');
        return sb.toString();
    }
    
    @Override
    public void setCommandId(final int value) {
    }
    
    @Override
    public int getCommandId() {
        return 0;
    }
    
    @Override
    public boolean isResponseRequired() {
        return false;
    }
    
    @Override
    public boolean isResponse() {
        return false;
    }
    
    @Override
    public boolean isBrokerInfo() {
        return false;
    }
    
    @Override
    public boolean isMessageDispatch() {
        return false;
    }
    
    @Override
    public boolean isMessage() {
        return false;
    }
    
    @Override
    public boolean isMessageAck() {
        return false;
    }
    
    @Override
    public boolean isMessageDispatchNotification() {
        return false;
    }
    
    @Override
    public boolean isShutdownInfo() {
        return false;
    }
    
    @Override
    public boolean isConnectionControl() {
        return false;
    }
    
    public void setCachedMarshalledForm(final WireFormat wireFormat, final ByteSequence data) {
    }
    
    public ByteSequence getCachedMarshalledForm(final WireFormat wireFormat) {
        return null;
    }
    
    static {
        MAGIC = new byte[] { 65, 99, 116, 105, 118, 101, 77, 81 };
    }
}
