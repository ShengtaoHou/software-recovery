// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import javax.jms.MessageNotWriteableException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import javax.jms.MessageFormatException;
import org.fusesource.hawtbuf.UTF8Buffer;
import java.io.InputStream;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.util.JMSExceptionSupport;
import java.io.DataInputStream;
import java.util.zip.InflaterInputStream;
import org.apache.activemq.util.ByteArrayInputStream;
import org.apache.activemq.ActiveMQConnection;
import java.io.OutputStream;
import org.apache.activemq.util.MarshallingSupport;
import java.io.DataOutputStream;
import java.util.zip.DeflaterOutputStream;
import org.apache.activemq.util.ByteArrayOutputStream;
import javax.jms.JMSException;
import java.io.IOException;
import org.apache.activemq.wireformat.WireFormat;
import java.io.ObjectStreamException;
import java.util.HashMap;
import java.util.Map;
import javax.jms.MapMessage;

public class ActiveMQMapMessage extends ActiveMQMessage implements MapMessage
{
    public static final byte DATA_STRUCTURE_TYPE = 25;
    protected transient Map<String, Object> map;
    
    public ActiveMQMapMessage() {
        this.map = new HashMap<String, Object>();
    }
    
    private Object readResolve() throws ObjectStreamException {
        if (this.map == null) {
            this.map = new HashMap<String, Object>();
        }
        return this;
    }
    
    @Override
    public Message copy() {
        final ActiveMQMapMessage copy = new ActiveMQMapMessage();
        this.copy(copy);
        return copy;
    }
    
    private void copy(final ActiveMQMapMessage copy) {
        this.storeContent();
        super.copy(copy);
    }
    
    @Override
    public void beforeMarshall(final WireFormat wireFormat) throws IOException {
        super.beforeMarshall(wireFormat);
        this.storeContent();
    }
    
    @Override
    public void clearMarshalledState() throws JMSException {
        super.clearMarshalledState();
        this.map.clear();
    }
    
    @Override
    public void storeContentAndClear() {
        this.storeContent();
        this.map.clear();
    }
    
    @Override
    public void storeContent() {
        try {
            if (this.getContent() == null && !this.map.isEmpty()) {
                OutputStream os;
                final ByteArrayOutputStream bytesOut = (ByteArrayOutputStream)(os = new ByteArrayOutputStream());
                final ActiveMQConnection connection = this.getConnection();
                if (connection != null && connection.isUseCompression()) {
                    this.compressed = true;
                    os = new DeflaterOutputStream(os);
                }
                final DataOutputStream dataOut = new DataOutputStream(os);
                MarshallingSupport.marshalPrimitiveMap(this.map, dataOut);
                dataOut.close();
                this.setContent(bytesOut.toByteSequence());
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void loadContent() throws JMSException {
        try {
            if (this.getContent() != null && this.map.isEmpty()) {
                final ByteSequence content = this.getContent();
                InputStream is = new ByteArrayInputStream(content);
                if (this.isCompressed()) {
                    is = new InflaterInputStream(is);
                }
                final DataInputStream dataIn = new DataInputStream(is);
                this.map = MarshallingSupport.unmarshalPrimitiveMap(dataIn);
                dataIn.close();
            }
        }
        catch (IOException e) {
            throw JMSExceptionSupport.create(e);
        }
    }
    
    @Override
    public byte getDataStructureType() {
        return 25;
    }
    
    @Override
    public String getJMSXMimeType() {
        return "jms/map-message";
    }
    
    @Override
    public void clearBody() throws JMSException {
        super.clearBody();
        this.map.clear();
    }
    
    @Override
    public boolean getBoolean(final String name) throws JMSException {
        this.initializeReading();
        final Object value = this.map.get(name);
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            return (boolean)value;
        }
        if (value instanceof UTF8Buffer) {
            return Boolean.valueOf(value.toString());
        }
        if (value instanceof String) {
            return Boolean.valueOf(value.toString());
        }
        throw new MessageFormatException(" cannot read a boolean from " + value.getClass().getName());
    }
    
    @Override
    public byte getByte(final String name) throws JMSException {
        this.initializeReading();
        final Object value = this.map.get(name);
        if (value == null) {
            return 0;
        }
        if (value instanceof Byte) {
            return (byte)value;
        }
        if (value instanceof UTF8Buffer) {
            return Byte.valueOf(value.toString());
        }
        if (value instanceof String) {
            return Byte.valueOf(value.toString());
        }
        throw new MessageFormatException(" cannot read a byte from " + value.getClass().getName());
    }
    
    @Override
    public short getShort(final String name) throws JMSException {
        this.initializeReading();
        final Object value = this.map.get(name);
        if (value == null) {
            return 0;
        }
        if (value instanceof Short) {
            return (short)value;
        }
        if (value instanceof Byte) {
            return (short)value;
        }
        if (value instanceof UTF8Buffer) {
            return Short.valueOf(value.toString());
        }
        if (value instanceof String) {
            return Short.valueOf(value.toString());
        }
        throw new MessageFormatException(" cannot read a short from " + value.getClass().getName());
    }
    
    @Override
    public char getChar(final String name) throws JMSException {
        this.initializeReading();
        final Object value = this.map.get(name);
        if (value == null) {
            throw new NullPointerException();
        }
        if (value instanceof Character) {
            return (char)value;
        }
        throw new MessageFormatException(" cannot read a short from " + value.getClass().getName());
    }
    
    @Override
    public int getInt(final String name) throws JMSException {
        this.initializeReading();
        final Object value = this.map.get(name);
        if (value == null) {
            return 0;
        }
        if (value instanceof Integer) {
            return (int)value;
        }
        if (value instanceof Short) {
            return (int)value;
        }
        if (value instanceof Byte) {
            return (int)value;
        }
        if (value instanceof UTF8Buffer) {
            return Integer.valueOf(value.toString());
        }
        if (value instanceof String) {
            return Integer.valueOf(value.toString());
        }
        throw new MessageFormatException(" cannot read an int from " + value.getClass().getName());
    }
    
    @Override
    public long getLong(final String name) throws JMSException {
        this.initializeReading();
        final Object value = this.map.get(name);
        if (value == null) {
            return 0L;
        }
        if (value instanceof Long) {
            return (long)value;
        }
        if (value instanceof Integer) {
            return (long)value;
        }
        if (value instanceof Short) {
            return (long)value;
        }
        if (value instanceof Byte) {
            return (long)value;
        }
        if (value instanceof UTF8Buffer) {
            return Long.valueOf(value.toString());
        }
        if (value instanceof String) {
            return Long.valueOf(value.toString());
        }
        throw new MessageFormatException(" cannot read a long from " + value.getClass().getName());
    }
    
    @Override
    public float getFloat(final String name) throws JMSException {
        this.initializeReading();
        final Object value = this.map.get(name);
        if (value == null) {
            return 0.0f;
        }
        if (value instanceof Float) {
            return (float)value;
        }
        if (value instanceof UTF8Buffer) {
            return Float.valueOf(value.toString());
        }
        if (value instanceof String) {
            return Float.valueOf(value.toString());
        }
        throw new MessageFormatException(" cannot read a float from " + value.getClass().getName());
    }
    
    @Override
    public double getDouble(final String name) throws JMSException {
        this.initializeReading();
        final Object value = this.map.get(name);
        if (value == null) {
            return 0.0;
        }
        if (value instanceof Double) {
            return (double)value;
        }
        if (value instanceof Float) {
            return (float)value;
        }
        if (value instanceof UTF8Buffer) {
            return Float.valueOf(value.toString());
        }
        if (value instanceof String) {
            return Float.valueOf(value.toString());
        }
        throw new MessageFormatException(" cannot read a double from " + value.getClass().getName());
    }
    
    @Override
    public String getString(final String name) throws JMSException {
        this.initializeReading();
        final Object value = this.map.get(name);
        if (value == null) {
            return null;
        }
        if (value instanceof byte[]) {
            throw new MessageFormatException("Use getBytes to read a byte array");
        }
        return value.toString();
    }
    
    @Override
    public byte[] getBytes(final String name) throws JMSException {
        this.initializeReading();
        final Object value = this.map.get(name);
        if (value instanceof byte[]) {
            return (byte[])value;
        }
        throw new MessageFormatException(" cannot read a byte[] from " + value.getClass().getName());
    }
    
    @Override
    public Object getObject(final String name) throws JMSException {
        this.initializeReading();
        Object result = this.map.get(name);
        if (result instanceof UTF8Buffer) {
            result = result.toString();
        }
        return result;
    }
    
    @Override
    public Enumeration<String> getMapNames() throws JMSException {
        this.initializeReading();
        return Collections.enumeration(this.map.keySet());
    }
    
    protected void put(final String name, final Object value) throws JMSException {
        if (name == null) {
            throw new IllegalArgumentException("The name of the property cannot be null.");
        }
        if (name.length() == 0) {
            throw new IllegalArgumentException("The name of the property cannot be an emprty string.");
        }
        this.map.put(name, value);
    }
    
    @Override
    public void setBoolean(final String name, final boolean value) throws JMSException {
        this.initializeWriting();
        this.put(name, value ? Boolean.TRUE : Boolean.FALSE);
    }
    
    @Override
    public void setByte(final String name, final byte value) throws JMSException {
        this.initializeWriting();
        this.put(name, value);
    }
    
    @Override
    public void setShort(final String name, final short value) throws JMSException {
        this.initializeWriting();
        this.put(name, value);
    }
    
    @Override
    public void setChar(final String name, final char value) throws JMSException {
        this.initializeWriting();
        this.put(name, value);
    }
    
    @Override
    public void setInt(final String name, final int value) throws JMSException {
        this.initializeWriting();
        this.put(name, value);
    }
    
    @Override
    public void setLong(final String name, final long value) throws JMSException {
        this.initializeWriting();
        this.put(name, value);
    }
    
    @Override
    public void setFloat(final String name, final float value) throws JMSException {
        this.initializeWriting();
        this.put(name, new Float(value));
    }
    
    @Override
    public void setDouble(final String name, final double value) throws JMSException {
        this.initializeWriting();
        this.put(name, new Double(value));
    }
    
    @Override
    public void setString(final String name, final String value) throws JMSException {
        this.initializeWriting();
        this.put(name, value);
    }
    
    @Override
    public void setBytes(final String name, final byte[] value) throws JMSException {
        this.initializeWriting();
        if (value != null) {
            this.put(name, value);
        }
        else {
            this.map.remove(name);
        }
    }
    
    @Override
    public void setBytes(final String name, final byte[] value, final int offset, final int length) throws JMSException {
        this.initializeWriting();
        final byte[] data = new byte[length];
        System.arraycopy(value, offset, data, 0, length);
        this.put(name, data);
    }
    
    @Override
    public void setObject(final String name, final Object value) throws JMSException {
        this.initializeWriting();
        if (value != null) {
            if (!(value instanceof byte[])) {
                this.checkValidObject(value);
            }
            this.put(name, value);
        }
        else {
            this.put(name, null);
        }
    }
    
    @Override
    public boolean itemExists(final String name) throws JMSException {
        this.initializeReading();
        return this.map.containsKey(name);
    }
    
    private void initializeReading() throws JMSException {
        this.loadContent();
    }
    
    private void initializeWriting() throws MessageNotWriteableException {
        this.checkReadOnlyBody();
        this.setContent(null);
    }
    
    @Override
    public void compress() throws IOException {
        this.storeContent();
        super.compress();
    }
    
    @Override
    public String toString() {
        return super.toString() + " ActiveMQMapMessage{ theTable = " + this.map + " }";
    }
    
    public Map<String, Object> getContentMap() throws JMSException {
        this.initializeReading();
        return this.map;
    }
}
