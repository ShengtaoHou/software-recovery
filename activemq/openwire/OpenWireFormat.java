// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire;

import java.lang.reflect.Method;
import java.io.DataInput;
import org.apache.activemq.util.ByteSequenceData;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.util.ByteSequence;
import java.util.HashMap;
import org.apache.activemq.command.WireFormatInfo;
import org.apache.activemq.util.DataByteArrayInputStream;
import org.apache.activemq.util.DataByteArrayOutputStream;
import org.apache.activemq.command.DataStructure;
import java.util.Map;
import org.apache.activemq.wireformat.WireFormat;

public final class OpenWireFormat implements WireFormat
{
    public static final int DEFAULT_VERSION = 6;
    public static final int DEFAULT_WIRE_VERSION = 10;
    public static final long DEFAULT_MAX_FRAME_SIZE = Long.MAX_VALUE;
    static final byte NULL_TYPE = 0;
    private static final int MARSHAL_CACHE_SIZE = 16383;
    private static final int MARSHAL_CACHE_FREE_SPACE = 100;
    private DataStreamMarshaller[] dataMarshallers;
    private int version;
    private boolean stackTraceEnabled;
    private boolean tcpNoDelayEnabled;
    private boolean cacheEnabled;
    private boolean tightEncodingEnabled;
    private boolean sizePrefixDisabled;
    private long maxFrameSize;
    private short nextMarshallCacheIndex;
    private short nextMarshallCacheEvictionIndex;
    private Map<DataStructure, Short> marshallCacheMap;
    private DataStructure[] marshallCache;
    private DataStructure[] unmarshallCache;
    private DataByteArrayOutputStream bytesOut;
    private DataByteArrayInputStream bytesIn;
    private WireFormatInfo preferedWireFormatInfo;
    
    public OpenWireFormat() {
        this(6);
    }
    
    public OpenWireFormat(final int i) {
        this.maxFrameSize = Long.MAX_VALUE;
        this.marshallCacheMap = new HashMap<DataStructure, Short>();
        this.marshallCache = null;
        this.unmarshallCache = null;
        this.bytesOut = new DataByteArrayOutputStream();
        this.bytesIn = new DataByteArrayInputStream();
        this.setVersion(i);
    }
    
    @Override
    public int hashCode() {
        return this.version ^ (this.cacheEnabled ? 268435456 : 536870912) ^ (this.stackTraceEnabled ? 16777216 : 33554432) ^ (this.tightEncodingEnabled ? 1048576 : 2097152) ^ (this.sizePrefixDisabled ? 65536 : 131072);
    }
    
    public OpenWireFormat copy() {
        final OpenWireFormat answer = new OpenWireFormat(this.version);
        answer.stackTraceEnabled = this.stackTraceEnabled;
        answer.tcpNoDelayEnabled = this.tcpNoDelayEnabled;
        answer.cacheEnabled = this.cacheEnabled;
        answer.tightEncodingEnabled = this.tightEncodingEnabled;
        answer.sizePrefixDisabled = this.sizePrefixDisabled;
        answer.preferedWireFormatInfo = this.preferedWireFormatInfo;
        return answer;
    }
    
    @Override
    public boolean equals(final Object object) {
        if (object == null) {
            return false;
        }
        final OpenWireFormat o = (OpenWireFormat)object;
        return o.stackTraceEnabled == this.stackTraceEnabled && o.cacheEnabled == this.cacheEnabled && o.version == this.version && o.tightEncodingEnabled == this.tightEncodingEnabled && o.sizePrefixDisabled == this.sizePrefixDisabled;
    }
    
    @Override
    public String toString() {
        return "OpenWireFormat{version=" + this.version + ", cacheEnabled=" + this.cacheEnabled + ", stackTraceEnabled=" + this.stackTraceEnabled + ", tightEncodingEnabled=" + this.tightEncodingEnabled + ", sizePrefixDisabled=" + this.sizePrefixDisabled + ", maxFrameSize=" + this.maxFrameSize + "}";
    }
    
    @Override
    public int getVersion() {
        return this.version;
    }
    
    @Override
    public synchronized ByteSequence marshal(final Object command) throws IOException {
        if (this.cacheEnabled) {
            this.runMarshallCacheEvictionSweep();
        }
        ByteSequence sequence = null;
        int size = 1;
        if (command != null) {
            final DataStructure c = (DataStructure)command;
            final byte type = c.getDataStructureType();
            final DataStreamMarshaller dsm = this.dataMarshallers[type & 0xFF];
            if (dsm == null) {
                throw new IOException("Unknown data type: " + type);
            }
            if (this.tightEncodingEnabled) {
                final BooleanStream bs = new BooleanStream();
                size += dsm.tightMarshal1(this, c, bs);
                size += bs.marshalledSize();
                this.bytesOut.restart(size);
                if (!this.sizePrefixDisabled) {
                    this.bytesOut.writeInt(size);
                }
                this.bytesOut.writeByte(type);
                bs.marshal(this.bytesOut);
                dsm.tightMarshal2(this, c, this.bytesOut, bs);
                sequence = this.bytesOut.toByteSequence();
            }
            else {
                this.bytesOut.restart();
                if (!this.sizePrefixDisabled) {
                    this.bytesOut.writeInt(0);
                }
                this.bytesOut.writeByte(type);
                dsm.looseMarshal(this, c, this.bytesOut);
                sequence = this.bytesOut.toByteSequence();
                if (!this.sizePrefixDisabled) {
                    size = sequence.getLength() - 4;
                    final int pos = sequence.offset;
                    ByteSequenceData.writeIntBig(sequence, size);
                    sequence.offset = pos;
                }
            }
        }
        else {
            this.bytesOut.restart(5);
            this.bytesOut.writeInt(size);
            this.bytesOut.writeByte(0);
            sequence = this.bytesOut.toByteSequence();
        }
        return sequence;
    }
    
    @Override
    public synchronized Object unmarshal(final ByteSequence sequence) throws IOException {
        this.bytesIn.restart(sequence);
        if (!this.sizePrefixDisabled) {
            final int size = this.bytesIn.readInt();
            if (sequence.getLength() - 4 != size) {}
            if (size > this.maxFrameSize) {
                throw new IOException("Frame size of " + size / 1048576 + " MB larger than max allowed " + this.maxFrameSize / 1048576L + " MB");
            }
        }
        final Object command = this.doUnmarshal(this.bytesIn);
        return command;
    }
    
    @Override
    public synchronized void marshal(final Object o, final DataOutput dataOut) throws IOException {
        if (this.cacheEnabled) {
            this.runMarshallCacheEvictionSweep();
        }
        int size = 1;
        if (o != null) {
            final DataStructure c = (DataStructure)o;
            final byte type = c.getDataStructureType();
            final DataStreamMarshaller dsm = this.dataMarshallers[type & 0xFF];
            if (dsm == null) {
                throw new IOException("Unknown data type: " + type);
            }
            if (this.tightEncodingEnabled) {
                final BooleanStream bs = new BooleanStream();
                size += dsm.tightMarshal1(this, c, bs);
                size += bs.marshalledSize();
                if (!this.sizePrefixDisabled) {
                    dataOut.writeInt(size);
                }
                dataOut.writeByte(type);
                bs.marshal(dataOut);
                dsm.tightMarshal2(this, c, dataOut, bs);
            }
            else {
                DataOutput looseOut = dataOut;
                if (!this.sizePrefixDisabled) {
                    this.bytesOut.restart();
                    looseOut = this.bytesOut;
                }
                looseOut.writeByte(type);
                dsm.looseMarshal(this, c, looseOut);
                if (!this.sizePrefixDisabled) {
                    final ByteSequence sequence = this.bytesOut.toByteSequence();
                    dataOut.writeInt(sequence.getLength());
                    dataOut.write(sequence.getData(), sequence.getOffset(), sequence.getLength());
                }
            }
        }
        else {
            if (!this.sizePrefixDisabled) {
                dataOut.writeInt(size);
            }
            dataOut.writeByte(0);
        }
    }
    
    @Override
    public Object unmarshal(final DataInput dis) throws IOException {
        final DataInput dataIn = dis;
        if (!this.sizePrefixDisabled) {
            final int size = dis.readInt();
            if (size > this.maxFrameSize) {
                throw new IOException("Frame size of " + size / 1048576 + " MB larger than max allowed " + this.maxFrameSize / 1048576L + " MB");
            }
        }
        return this.doUnmarshal(dataIn);
    }
    
    public int tightMarshal1(final Object o, final BooleanStream bs) throws IOException {
        int size = 1;
        if (o != null) {
            final DataStructure c = (DataStructure)o;
            final byte type = c.getDataStructureType();
            final DataStreamMarshaller dsm = this.dataMarshallers[type & 0xFF];
            if (dsm == null) {
                throw new IOException("Unknown data type: " + type);
            }
            size += dsm.tightMarshal1(this, c, bs);
            size += bs.marshalledSize();
        }
        return size;
    }
    
    public void tightMarshal2(final Object o, final DataOutput ds, final BooleanStream bs) throws IOException {
        if (this.cacheEnabled) {
            this.runMarshallCacheEvictionSweep();
        }
        if (o != null) {
            final DataStructure c = (DataStructure)o;
            final byte type = c.getDataStructureType();
            final DataStreamMarshaller dsm = this.dataMarshallers[type & 0xFF];
            if (dsm == null) {
                throw new IOException("Unknown data type: " + type);
            }
            ds.writeByte(type);
            bs.marshal(ds);
            dsm.tightMarshal2(this, c, ds, bs);
        }
    }
    
    @Override
    public void setVersion(final int version) {
        final String mfName = "org.apache.activemq.openwire.v" + version + ".MarshallerFactory";
        Class mfClass;
        try {
            mfClass = Class.forName(mfName, false, this.getClass().getClassLoader());
        }
        catch (ClassNotFoundException e) {
            throw (IllegalArgumentException)new IllegalArgumentException("Invalid version: " + version + ", could not load " + mfName).initCause(e);
        }
        try {
            final Method method = mfClass.getMethod("createMarshallerMap", OpenWireFormat.class);
            this.dataMarshallers = (DataStreamMarshaller[])method.invoke(null, this);
        }
        catch (Throwable e2) {
            throw (IllegalArgumentException)new IllegalArgumentException("Invalid version: " + version + ", " + mfName + " does not properly implement the createMarshallerMap method.").initCause(e2);
        }
        this.version = version;
    }
    
    public Object doUnmarshal(final DataInput dis) throws IOException {
        final byte dataType = dis.readByte();
        if (dataType == 0) {
            return null;
        }
        final DataStreamMarshaller dsm = this.dataMarshallers[dataType & 0xFF];
        if (dsm == null) {
            throw new IOException("Unknown data type: " + dataType);
        }
        final Object data = dsm.createObject();
        if (this.tightEncodingEnabled) {
            final BooleanStream bs = new BooleanStream();
            bs.unmarshal(dis);
            dsm.tightUnmarshal(this, data, dis, bs);
        }
        else {
            dsm.looseUnmarshal(this, data, dis);
        }
        return data;
    }
    
    public int tightMarshalNestedObject1(final DataStructure o, final BooleanStream bs) throws IOException {
        bs.writeBoolean(o != null);
        if (o == null) {
            return 0;
        }
        if (o.isMarshallAware()) {
            final ByteSequence sequence = null;
            bs.writeBoolean(sequence != null);
            if (sequence != null) {
                return 1 + sequence.getLength();
            }
        }
        final byte type = o.getDataStructureType();
        final DataStreamMarshaller dsm = this.dataMarshallers[type & 0xFF];
        if (dsm == null) {
            throw new IOException("Unknown data type: " + type);
        }
        return 1 + dsm.tightMarshal1(this, o, bs);
    }
    
    public void tightMarshalNestedObject2(final DataStructure o, final DataOutput ds, final BooleanStream bs) throws IOException {
        if (!bs.readBoolean()) {
            return;
        }
        final byte type = o.getDataStructureType();
        ds.writeByte(type);
        if (o.isMarshallAware() && bs.readBoolean()) {
            throw new IOException("Corrupted stream");
        }
        final DataStreamMarshaller dsm = this.dataMarshallers[type & 0xFF];
        if (dsm == null) {
            throw new IOException("Unknown data type: " + type);
        }
        dsm.tightMarshal2(this, o, ds, bs);
    }
    
    public DataStructure tightUnmarshalNestedObject(final DataInput dis, final BooleanStream bs) throws IOException {
        if (!bs.readBoolean()) {
            return null;
        }
        final byte dataType = dis.readByte();
        final DataStreamMarshaller dsm = this.dataMarshallers[dataType & 0xFF];
        if (dsm == null) {
            throw new IOException("Unknown data type: " + dataType);
        }
        final DataStructure data = dsm.createObject();
        if (data.isMarshallAware() && bs.readBoolean()) {
            dis.readInt();
            dis.readByte();
            final BooleanStream bs2 = new BooleanStream();
            bs2.unmarshal(dis);
            dsm.tightUnmarshal(this, data, dis, bs2);
        }
        else {
            dsm.tightUnmarshal(this, data, dis, bs);
        }
        return data;
    }
    
    public DataStructure looseUnmarshalNestedObject(final DataInput dis) throws IOException {
        if (!dis.readBoolean()) {
            return null;
        }
        final byte dataType = dis.readByte();
        final DataStreamMarshaller dsm = this.dataMarshallers[dataType & 0xFF];
        if (dsm == null) {
            throw new IOException("Unknown data type: " + dataType);
        }
        final DataStructure data = dsm.createObject();
        dsm.looseUnmarshal(this, data, dis);
        return data;
    }
    
    public void looseMarshalNestedObject(final DataStructure o, final DataOutput dataOut) throws IOException {
        dataOut.writeBoolean(o != null);
        if (o != null) {
            final byte type = o.getDataStructureType();
            dataOut.writeByte(type);
            final DataStreamMarshaller dsm = this.dataMarshallers[type & 0xFF];
            if (dsm == null) {
                throw new IOException("Unknown data type: " + type);
            }
            dsm.looseMarshal(this, o, dataOut);
        }
    }
    
    public void runMarshallCacheEvictionSweep() {
        while (this.marshallCacheMap.size() > this.marshallCache.length - 100) {
            this.marshallCacheMap.remove(this.marshallCache[this.nextMarshallCacheEvictionIndex]);
            this.marshallCache[this.nextMarshallCacheEvictionIndex] = null;
            ++this.nextMarshallCacheEvictionIndex;
            if (this.nextMarshallCacheEvictionIndex >= this.marshallCache.length) {
                this.nextMarshallCacheEvictionIndex = 0;
            }
        }
    }
    
    public Short getMarshallCacheIndex(final DataStructure o) {
        return this.marshallCacheMap.get(o);
    }
    
    public Short addToMarshallCache(final DataStructure o) {
        final short nextMarshallCacheIndex = this.nextMarshallCacheIndex;
        this.nextMarshallCacheIndex = (short)(nextMarshallCacheIndex + 1);
        final short i = nextMarshallCacheIndex;
        if (this.nextMarshallCacheIndex >= this.marshallCache.length) {
            this.nextMarshallCacheIndex = 0;
        }
        if (this.marshallCacheMap.size() < this.marshallCache.length) {
            this.marshallCache[i] = o;
            final Short index = new Short(i);
            this.marshallCacheMap.put(o, index);
            return index;
        }
        return new Short((short)(-1));
    }
    
    public void setInUnmarshallCache(final short index, final DataStructure o) {
        if (index == -1) {
            return;
        }
        this.unmarshallCache[index] = o;
    }
    
    public DataStructure getFromUnmarshallCache(final short index) {
        return this.unmarshallCache[index];
    }
    
    public void setStackTraceEnabled(final boolean b) {
        this.stackTraceEnabled = b;
    }
    
    public boolean isStackTraceEnabled() {
        return this.stackTraceEnabled;
    }
    
    public boolean isTcpNoDelayEnabled() {
        return this.tcpNoDelayEnabled;
    }
    
    public void setTcpNoDelayEnabled(final boolean tcpNoDelayEnabled) {
        this.tcpNoDelayEnabled = tcpNoDelayEnabled;
    }
    
    public boolean isCacheEnabled() {
        return this.cacheEnabled;
    }
    
    public void setCacheEnabled(final boolean cacheEnabled) {
        if (cacheEnabled) {
            this.marshallCache = new DataStructure[16383];
            this.unmarshallCache = new DataStructure[16383];
        }
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
    
    public void setSizePrefixDisabled(final boolean prefixPacketSize) {
        this.sizePrefixDisabled = prefixPacketSize;
    }
    
    public void setPreferedWireFormatInfo(final WireFormatInfo info) {
        this.preferedWireFormatInfo = info;
    }
    
    public WireFormatInfo getPreferedWireFormatInfo() {
        return this.preferedWireFormatInfo;
    }
    
    public long getMaxFrameSize() {
        return this.maxFrameSize;
    }
    
    public void setMaxFrameSize(final long maxFrameSize) {
        this.maxFrameSize = maxFrameSize;
    }
    
    public void renegotiateWireFormat(final WireFormatInfo info) throws IOException {
        if (this.preferedWireFormatInfo == null) {
            throw new IllegalStateException("Wireformat cannot not be renegotiated.");
        }
        this.setVersion(this.min(this.preferedWireFormatInfo.getVersion(), info.getVersion()));
        info.setVersion(this.getVersion());
        this.setMaxFrameSize(this.min(this.preferedWireFormatInfo.getMaxFrameSize(), info.getMaxFrameSize()));
        info.setMaxFrameSize(this.getMaxFrameSize());
        info.setStackTraceEnabled(this.stackTraceEnabled = (info.isStackTraceEnabled() && this.preferedWireFormatInfo.isStackTraceEnabled()));
        info.setTcpNoDelayEnabled(this.tcpNoDelayEnabled = (info.isTcpNoDelayEnabled() && this.preferedWireFormatInfo.isTcpNoDelayEnabled()));
        info.setCacheEnabled(this.cacheEnabled = (info.isCacheEnabled() && this.preferedWireFormatInfo.isCacheEnabled()));
        info.setTightEncodingEnabled(this.tightEncodingEnabled = (info.isTightEncodingEnabled() && this.preferedWireFormatInfo.isTightEncodingEnabled()));
        info.setSizePrefixDisabled(this.sizePrefixDisabled = (info.isSizePrefixDisabled() && this.preferedWireFormatInfo.isSizePrefixDisabled()));
        if (this.cacheEnabled) {
            int size = Math.min(this.preferedWireFormatInfo.getCacheSize(), info.getCacheSize());
            info.setCacheSize(size);
            if (size == 0) {
                size = 16383;
            }
            this.marshallCache = new DataStructure[size];
            this.unmarshallCache = new DataStructure[size];
            this.nextMarshallCacheIndex = 0;
            this.nextMarshallCacheEvictionIndex = 0;
            this.marshallCacheMap = new HashMap<DataStructure, Short>();
        }
        else {
            this.marshallCache = null;
            this.unmarshallCache = null;
            this.nextMarshallCacheIndex = 0;
            this.nextMarshallCacheEvictionIndex = 0;
            this.marshallCacheMap = null;
        }
    }
    
    protected int min(final int version1, final int version2) {
        if ((version1 < version2 && version1 > 0) || version2 <= 0) {
            return version1;
        }
        return version2;
    }
    
    protected long min(final long version1, final long version2) {
        if ((version1 < version2 && version1 > 0L) || version2 <= 0L) {
            return version1;
        }
        return version2;
    }
}
