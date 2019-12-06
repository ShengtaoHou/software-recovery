// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v3;

import org.apache.activemq.util.ByteSequence;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.openwire.BooleanStream;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.DataStructure;
import java.lang.reflect.Constructor;
import org.apache.activemq.openwire.DataStreamMarshaller;

public abstract class BaseDataStreamMarshaller implements DataStreamMarshaller
{
    public static final Constructor STACK_TRACE_ELEMENT_CONSTRUCTOR;
    
    @Override
    public abstract byte getDataStructureType();
    
    @Override
    public abstract DataStructure createObject();
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        return 0;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
    }
    
    public int tightMarshalLong1(final OpenWireFormat wireFormat, final long o, final BooleanStream bs) throws IOException {
        if (o == 0L) {
            bs.writeBoolean(false);
            bs.writeBoolean(false);
            return 0;
        }
        if ((o & 0xFFFFFFFFFFFF0000L) == 0x0L) {
            bs.writeBoolean(false);
            bs.writeBoolean(true);
            return 2;
        }
        if ((o & 0xFFFFFFFF00000000L) == 0x0L) {
            bs.writeBoolean(true);
            bs.writeBoolean(false);
            return 4;
        }
        bs.writeBoolean(true);
        bs.writeBoolean(true);
        return 8;
    }
    
    public void tightMarshalLong2(final OpenWireFormat wireFormat, final long o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        if (bs.readBoolean()) {
            if (bs.readBoolean()) {
                dataOut.writeLong(o);
            }
            else {
                dataOut.writeInt((int)o);
            }
        }
        else if (bs.readBoolean()) {
            dataOut.writeShort((int)o);
        }
    }
    
    public long tightUnmarshalLong(final OpenWireFormat wireFormat, final DataInput dataIn, final BooleanStream bs) throws IOException {
        if (bs.readBoolean()) {
            if (bs.readBoolean()) {
                return dataIn.readLong();
            }
            return this.toLong(dataIn.readInt());
        }
        else {
            if (bs.readBoolean()) {
                return this.toLong(dataIn.readShort());
            }
            return 0L;
        }
    }
    
    protected long toLong(final short value) {
        final long answer = value;
        return answer & 0xFFFFL;
    }
    
    protected long toLong(final int value) {
        final long answer = value;
        return answer & 0xFFFFFFFFL;
    }
    
    protected DataStructure tightUnmarsalNestedObject(final OpenWireFormat wireFormat, final DataInput dataIn, final BooleanStream bs) throws IOException {
        return wireFormat.tightUnmarshalNestedObject(dataIn, bs);
    }
    
    protected int tightMarshalNestedObject1(final OpenWireFormat wireFormat, final DataStructure o, final BooleanStream bs) throws IOException {
        return wireFormat.tightMarshalNestedObject1(o, bs);
    }
    
    protected void tightMarshalNestedObject2(final OpenWireFormat wireFormat, final DataStructure o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        wireFormat.tightMarshalNestedObject2(o, dataOut, bs);
    }
    
    protected DataStructure tightUnmarsalCachedObject(final OpenWireFormat wireFormat, final DataInput dataIn, final BooleanStream bs) throws IOException {
        if (!wireFormat.isCacheEnabled()) {
            return wireFormat.tightUnmarshalNestedObject(dataIn, bs);
        }
        if (bs.readBoolean()) {
            final short index = dataIn.readShort();
            final DataStructure object = wireFormat.tightUnmarshalNestedObject(dataIn, bs);
            wireFormat.setInUnmarshallCache(index, object);
            return object;
        }
        final short index = dataIn.readShort();
        return wireFormat.getFromUnmarshallCache(index);
    }
    
    protected int tightMarshalCachedObject1(final OpenWireFormat wireFormat, final DataStructure o, final BooleanStream bs) throws IOException {
        if (!wireFormat.isCacheEnabled()) {
            return wireFormat.tightMarshalNestedObject1(o, bs);
        }
        final Short index = wireFormat.getMarshallCacheIndex(o);
        bs.writeBoolean(index == null);
        if (index == null) {
            final int rc = wireFormat.tightMarshalNestedObject1(o, bs);
            wireFormat.addToMarshallCache(o);
            return 2 + rc;
        }
        return 2;
    }
    
    protected void tightMarshalCachedObject2(final OpenWireFormat wireFormat, final DataStructure o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        if (wireFormat.isCacheEnabled()) {
            final Short index = wireFormat.getMarshallCacheIndex(o);
            if (bs.readBoolean()) {
                dataOut.writeShort(index);
                wireFormat.tightMarshalNestedObject2(o, dataOut, bs);
            }
            else {
                dataOut.writeShort(index);
            }
        }
        else {
            wireFormat.tightMarshalNestedObject2(o, dataOut, bs);
        }
    }
    
    protected Throwable tightUnmarsalThrowable(final OpenWireFormat wireFormat, final DataInput dataIn, final BooleanStream bs) throws IOException {
        if (bs.readBoolean()) {
            final String clazz = this.tightUnmarshalString(dataIn, bs);
            final String message = this.tightUnmarshalString(dataIn, bs);
            final Throwable o = this.createThrowable(clazz, message);
            if (wireFormat.isStackTraceEnabled()) {
                if (BaseDataStreamMarshaller.STACK_TRACE_ELEMENT_CONSTRUCTOR != null) {
                    final StackTraceElement[] ss = new StackTraceElement[dataIn.readShort()];
                    for (int i = 0; i < ss.length; ++i) {
                        try {
                            ss[i] = BaseDataStreamMarshaller.STACK_TRACE_ELEMENT_CONSTRUCTOR.newInstance(this.tightUnmarshalString(dataIn, bs), this.tightUnmarshalString(dataIn, bs), this.tightUnmarshalString(dataIn, bs), dataIn.readInt());
                        }
                        catch (IOException e) {
                            throw e;
                        }
                        catch (Throwable t) {}
                    }
                    o.setStackTrace(ss);
                }
                else {
                    final short size = dataIn.readShort();
                    for (int i = 0; i < size; ++i) {
                        this.tightUnmarshalString(dataIn, bs);
                        this.tightUnmarshalString(dataIn, bs);
                        this.tightUnmarshalString(dataIn, bs);
                        dataIn.readInt();
                    }
                }
                o.initCause(this.tightUnmarsalThrowable(wireFormat, dataIn, bs));
            }
            return o;
        }
        return null;
    }
    
    private Throwable createThrowable(final String className, final String message) {
        try {
            final Class clazz = Class.forName(className, false, BaseDataStreamMarshaller.class.getClassLoader());
            final Constructor constructor = clazz.getConstructor(String.class);
            return constructor.newInstance(message);
        }
        catch (Throwable e) {
            return new Throwable(className + ": " + message);
        }
    }
    
    protected int tightMarshalThrowable1(final OpenWireFormat wireFormat, final Throwable o, final BooleanStream bs) throws IOException {
        if (o == null) {
            bs.writeBoolean(false);
            return 0;
        }
        int rc = 0;
        bs.writeBoolean(true);
        rc += this.tightMarshalString1(o.getClass().getName(), bs);
        rc += this.tightMarshalString1(o.getMessage(), bs);
        if (wireFormat.isStackTraceEnabled()) {
            rc += 2;
            final StackTraceElement[] stackTrace = o.getStackTrace();
            for (int i = 0; i < stackTrace.length; ++i) {
                final StackTraceElement element = stackTrace[i];
                rc += this.tightMarshalString1(element.getClassName(), bs);
                rc += this.tightMarshalString1(element.getMethodName(), bs);
                rc += this.tightMarshalString1(element.getFileName(), bs);
                rc += 4;
            }
            rc += this.tightMarshalThrowable1(wireFormat, o.getCause(), bs);
        }
        return rc;
    }
    
    protected void tightMarshalThrowable2(final OpenWireFormat wireFormat, final Throwable o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        if (bs.readBoolean()) {
            this.tightMarshalString2(o.getClass().getName(), dataOut, bs);
            this.tightMarshalString2(o.getMessage(), dataOut, bs);
            if (wireFormat.isStackTraceEnabled()) {
                final StackTraceElement[] stackTrace = o.getStackTrace();
                dataOut.writeShort(stackTrace.length);
                for (int i = 0; i < stackTrace.length; ++i) {
                    final StackTraceElement element = stackTrace[i];
                    this.tightMarshalString2(element.getClassName(), dataOut, bs);
                    this.tightMarshalString2(element.getMethodName(), dataOut, bs);
                    this.tightMarshalString2(element.getFileName(), dataOut, bs);
                    dataOut.writeInt(element.getLineNumber());
                }
                this.tightMarshalThrowable2(wireFormat, o.getCause(), dataOut, bs);
            }
        }
    }
    
    protected String tightUnmarshalString(final DataInput dataIn, final BooleanStream bs) throws IOException {
        if (!bs.readBoolean()) {
            return null;
        }
        if (bs.readBoolean()) {
            final int size = dataIn.readShort();
            final byte[] data = new byte[size];
            dataIn.readFully(data);
            return new String(data, 0);
        }
        return dataIn.readUTF();
    }
    
    protected int tightMarshalString1(final String value, final BooleanStream bs) throws IOException {
        bs.writeBoolean(value != null);
        if (value == null) {
            return 0;
        }
        final int strlen = value.length();
        int utflen = 0;
        final char[] charr = new char[strlen];
        int c = 0;
        boolean isOnlyAscii = true;
        value.getChars(0, strlen, charr, 0);
        for (int i = 0; i < strlen; ++i) {
            c = charr[i];
            if (c >= 1 && c <= 127) {
                ++utflen;
            }
            else if (c > 2047) {
                utflen += 3;
                isOnlyAscii = false;
            }
            else {
                isOnlyAscii = false;
                utflen += 2;
            }
        }
        if (utflen >= 32767) {
            throw new IOException("Encountered a String value that is too long to encode.");
        }
        bs.writeBoolean(isOnlyAscii);
        return utflen + 2;
    }
    
    protected void tightMarshalString2(final String value, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        if (bs.readBoolean()) {
            if (bs.readBoolean()) {
                dataOut.writeShort(value.length());
                dataOut.writeBytes(value);
            }
            else {
                dataOut.writeUTF(value);
            }
        }
    }
    
    protected int tightMarshalObjectArray1(final OpenWireFormat wireFormat, final DataStructure[] objects, final BooleanStream bs) throws IOException {
        if (objects != null) {
            int rc = 0;
            bs.writeBoolean(true);
            rc += 2;
            for (int i = 0; i < objects.length; ++i) {
                rc += this.tightMarshalNestedObject1(wireFormat, objects[i], bs);
            }
            return rc;
        }
        bs.writeBoolean(false);
        return 0;
    }
    
    protected void tightMarshalObjectArray2(final OpenWireFormat wireFormat, final DataStructure[] objects, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        if (bs.readBoolean()) {
            dataOut.writeShort(objects.length);
            for (int i = 0; i < objects.length; ++i) {
                this.tightMarshalNestedObject2(wireFormat, objects[i], dataOut, bs);
            }
        }
    }
    
    protected int tightMarshalConstByteArray1(final byte[] data, final BooleanStream bs, final int i) throws IOException {
        return i;
    }
    
    protected void tightMarshalConstByteArray2(final byte[] data, final DataOutput dataOut, final BooleanStream bs, final int i) throws IOException {
        dataOut.write(data, 0, i);
    }
    
    protected byte[] tightUnmarshalConstByteArray(final DataInput dataIn, final BooleanStream bs, final int i) throws IOException {
        final byte[] data = new byte[i];
        dataIn.readFully(data);
        return data;
    }
    
    protected int tightMarshalByteArray1(final byte[] data, final BooleanStream bs) throws IOException {
        bs.writeBoolean(data != null);
        if (data != null) {
            return data.length + 4;
        }
        return 0;
    }
    
    protected void tightMarshalByteArray2(final byte[] data, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        if (bs.readBoolean()) {
            dataOut.writeInt(data.length);
            dataOut.write(data);
        }
    }
    
    protected byte[] tightUnmarshalByteArray(final DataInput dataIn, final BooleanStream bs) throws IOException {
        byte[] rc = null;
        if (bs.readBoolean()) {
            final int size = dataIn.readInt();
            rc = new byte[size];
            dataIn.readFully(rc);
        }
        return rc;
    }
    
    protected int tightMarshalByteSequence1(final ByteSequence data, final BooleanStream bs) throws IOException {
        bs.writeBoolean(data != null);
        if (data != null) {
            return data.getLength() + 4;
        }
        return 0;
    }
    
    protected void tightMarshalByteSequence2(final ByteSequence data, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        if (bs.readBoolean()) {
            dataOut.writeInt(data.getLength());
            dataOut.write(data.getData(), data.getOffset(), data.getLength());
        }
    }
    
    protected ByteSequence tightUnmarshalByteSequence(final DataInput dataIn, final BooleanStream bs) throws IOException {
        final ByteSequence rc = null;
        if (bs.readBoolean()) {
            final int size = dataIn.readInt();
            final byte[] t = new byte[size];
            dataIn.readFully(t);
            return new ByteSequence(t, 0, size);
        }
        return rc;
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
    }
    
    public void looseMarshalLong(final OpenWireFormat wireFormat, final long o, final DataOutput dataOut) throws IOException {
        dataOut.writeLong(o);
    }
    
    public long looseUnmarshalLong(final OpenWireFormat wireFormat, final DataInput dataIn) throws IOException {
        return dataIn.readLong();
    }
    
    protected DataStructure looseUnmarsalNestedObject(final OpenWireFormat wireFormat, final DataInput dataIn) throws IOException {
        return wireFormat.looseUnmarshalNestedObject(dataIn);
    }
    
    protected void looseMarshalNestedObject(final OpenWireFormat wireFormat, final DataStructure o, final DataOutput dataOut) throws IOException {
        wireFormat.looseMarshalNestedObject(o, dataOut);
    }
    
    protected DataStructure looseUnmarsalCachedObject(final OpenWireFormat wireFormat, final DataInput dataIn) throws IOException {
        if (!wireFormat.isCacheEnabled()) {
            return wireFormat.looseUnmarshalNestedObject(dataIn);
        }
        if (dataIn.readBoolean()) {
            final short index = dataIn.readShort();
            final DataStructure object = wireFormat.looseUnmarshalNestedObject(dataIn);
            wireFormat.setInUnmarshallCache(index, object);
            return object;
        }
        final short index = dataIn.readShort();
        return wireFormat.getFromUnmarshallCache(index);
    }
    
    protected void looseMarshalCachedObject(final OpenWireFormat wireFormat, final DataStructure o, final DataOutput dataOut) throws IOException {
        if (wireFormat.isCacheEnabled()) {
            Short index = wireFormat.getMarshallCacheIndex(o);
            dataOut.writeBoolean(index == null);
            if (index == null) {
                index = wireFormat.addToMarshallCache(o);
                dataOut.writeShort(index);
                wireFormat.looseMarshalNestedObject(o, dataOut);
            }
            else {
                dataOut.writeShort(index);
            }
        }
        else {
            wireFormat.looseMarshalNestedObject(o, dataOut);
        }
    }
    
    protected Throwable looseUnmarsalThrowable(final OpenWireFormat wireFormat, final DataInput dataIn) throws IOException {
        if (dataIn.readBoolean()) {
            final String clazz = this.looseUnmarshalString(dataIn);
            final String message = this.looseUnmarshalString(dataIn);
            final Throwable o = this.createThrowable(clazz, message);
            if (wireFormat.isStackTraceEnabled()) {
                if (BaseDataStreamMarshaller.STACK_TRACE_ELEMENT_CONSTRUCTOR != null) {
                    final StackTraceElement[] ss = new StackTraceElement[dataIn.readShort()];
                    for (int i = 0; i < ss.length; ++i) {
                        try {
                            ss[i] = BaseDataStreamMarshaller.STACK_TRACE_ELEMENT_CONSTRUCTOR.newInstance(this.looseUnmarshalString(dataIn), this.looseUnmarshalString(dataIn), this.looseUnmarshalString(dataIn), dataIn.readInt());
                        }
                        catch (IOException e) {
                            throw e;
                        }
                        catch (Throwable t) {}
                    }
                    o.setStackTrace(ss);
                }
                else {
                    final short size = dataIn.readShort();
                    for (int i = 0; i < size; ++i) {
                        this.looseUnmarshalString(dataIn);
                        this.looseUnmarshalString(dataIn);
                        this.looseUnmarshalString(dataIn);
                        dataIn.readInt();
                    }
                }
                o.initCause(this.looseUnmarsalThrowable(wireFormat, dataIn));
            }
            return o;
        }
        return null;
    }
    
    protected void looseMarshalThrowable(final OpenWireFormat wireFormat, final Throwable o, final DataOutput dataOut) throws IOException {
        dataOut.writeBoolean(o != null);
        if (o != null) {
            this.looseMarshalString(o.getClass().getName(), dataOut);
            this.looseMarshalString(o.getMessage(), dataOut);
            if (wireFormat.isStackTraceEnabled()) {
                final StackTraceElement[] stackTrace = o.getStackTrace();
                dataOut.writeShort(stackTrace.length);
                for (int i = 0; i < stackTrace.length; ++i) {
                    final StackTraceElement element = stackTrace[i];
                    this.looseMarshalString(element.getClassName(), dataOut);
                    this.looseMarshalString(element.getMethodName(), dataOut);
                    this.looseMarshalString(element.getFileName(), dataOut);
                    dataOut.writeInt(element.getLineNumber());
                }
                this.looseMarshalThrowable(wireFormat, o.getCause(), dataOut);
            }
        }
    }
    
    protected String looseUnmarshalString(final DataInput dataIn) throws IOException {
        if (dataIn.readBoolean()) {
            return dataIn.readUTF();
        }
        return null;
    }
    
    protected void looseMarshalString(final String value, final DataOutput dataOut) throws IOException {
        dataOut.writeBoolean(value != null);
        if (value != null) {
            dataOut.writeUTF(value);
        }
    }
    
    protected void looseMarshalObjectArray(final OpenWireFormat wireFormat, final DataStructure[] objects, final DataOutput dataOut) throws IOException {
        dataOut.writeBoolean(objects != null);
        if (objects != null) {
            dataOut.writeShort(objects.length);
            for (int i = 0; i < objects.length; ++i) {
                this.looseMarshalNestedObject(wireFormat, objects[i], dataOut);
            }
        }
    }
    
    protected void looseMarshalConstByteArray(final OpenWireFormat wireFormat, final byte[] data, final DataOutput dataOut, final int i) throws IOException {
        dataOut.write(data, 0, i);
    }
    
    protected byte[] looseUnmarshalConstByteArray(final DataInput dataIn, final int i) throws IOException {
        final byte[] data = new byte[i];
        dataIn.readFully(data);
        return data;
    }
    
    protected void looseMarshalByteArray(final OpenWireFormat wireFormat, final byte[] data, final DataOutput dataOut) throws IOException {
        dataOut.writeBoolean(data != null);
        if (data != null) {
            dataOut.writeInt(data.length);
            dataOut.write(data);
        }
    }
    
    protected byte[] looseUnmarshalByteArray(final DataInput dataIn) throws IOException {
        byte[] rc = null;
        if (dataIn.readBoolean()) {
            final int size = dataIn.readInt();
            rc = new byte[size];
            dataIn.readFully(rc);
        }
        return rc;
    }
    
    protected void looseMarshalByteSequence(final OpenWireFormat wireFormat, final ByteSequence data, final DataOutput dataOut) throws IOException {
        dataOut.writeBoolean(data != null);
        if (data != null) {
            dataOut.writeInt(data.getLength());
            dataOut.write(data.getData(), data.getOffset(), data.getLength());
        }
    }
    
    protected ByteSequence looseUnmarshalByteSequence(final DataInput dataIn) throws IOException {
        ByteSequence rc = null;
        if (dataIn.readBoolean()) {
            final int size = dataIn.readInt();
            final byte[] t = new byte[size];
            dataIn.readFully(t);
            rc = new ByteSequence(t, 0, size);
        }
        return rc;
    }
    
    static {
        Constructor constructor = null;
        try {
            constructor = StackTraceElement.class.getConstructor(String.class, String.class, String.class, Integer.TYPE);
        }
        catch (Throwable t) {}
        STACK_TRACE_ELEMENT_CONSTRUCTOR = constructor;
    }
}
