// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import java.io.BufferedInputStream;
import org.apache.activemq.util.ByteSequence;
import javax.jms.MessageNotReadableException;
import org.apache.activemq.ActiveMQConnection;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;
import org.apache.activemq.util.ByteArrayInputStream;
import java.util.zip.DeflaterOutputStream;
import java.io.DataInput;
import org.apache.activemq.util.MarshallingSupport;
import java.io.EOFException;
import org.apache.activemq.util.JMSExceptionSupport;
import javax.jms.MessageFormatException;
import javax.jms.MessageEOFException;
import java.io.IOException;
import javax.jms.JMSException;
import java.io.DataInputStream;
import org.apache.activemq.util.ByteArrayOutputStream;
import java.io.DataOutputStream;
import javax.jms.StreamMessage;

public class ActiveMQStreamMessage extends ActiveMQMessage implements StreamMessage
{
    public static final byte DATA_STRUCTURE_TYPE = 27;
    protected transient DataOutputStream dataOut;
    protected transient ByteArrayOutputStream bytesOut;
    protected transient DataInputStream dataIn;
    protected transient int remainingBytes;
    
    public ActiveMQStreamMessage() {
        this.remainingBytes = -1;
    }
    
    @Override
    public Message copy() {
        final ActiveMQStreamMessage copy = new ActiveMQStreamMessage();
        this.copy(copy);
        return copy;
    }
    
    private void copy(final ActiveMQStreamMessage copy) {
        this.storeContent();
        super.copy(copy);
        copy.dataOut = null;
        copy.bytesOut = null;
        copy.dataIn = null;
    }
    
    @Override
    public void onSend() throws JMSException {
        super.onSend();
        this.storeContent();
    }
    
    @Override
    public void storeContent() {
        if (this.dataOut != null) {
            try {
                this.dataOut.close();
                this.setContent(this.bytesOut.toByteSequence());
                this.bytesOut = null;
                this.dataOut = null;
            }
            catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
    }
    
    @Override
    public byte getDataStructureType() {
        return 27;
    }
    
    @Override
    public String getJMSXMimeType() {
        return "jms/stream-message";
    }
    
    @Override
    public void clearBody() throws JMSException {
        super.clearBody();
        this.dataOut = null;
        this.dataIn = null;
        this.bytesOut = null;
        this.remainingBytes = -1;
    }
    
    @Override
    public boolean readBoolean() throws JMSException {
        this.initializeReading();
        try {
            this.dataIn.mark(10);
            final int type = this.dataIn.read();
            if (type == -1) {
                throw new MessageEOFException("reached end of data");
            }
            if (type == 1) {
                return this.dataIn.readBoolean();
            }
            if (type == 9) {
                return Boolean.valueOf(this.dataIn.readUTF());
            }
            if (type == 0) {
                this.dataIn.reset();
                throw new NullPointerException("Cannot convert NULL value to boolean.");
            }
            this.dataIn.reset();
            throw new MessageFormatException(" not a boolean type");
        }
        catch (EOFException e) {
            throw JMSExceptionSupport.createMessageEOFException(e);
        }
        catch (IOException e2) {
            throw JMSExceptionSupport.createMessageFormatException(e2);
        }
    }
    
    @Override
    public byte readByte() throws JMSException {
        this.initializeReading();
        try {
            this.dataIn.mark(10);
            final int type = this.dataIn.read();
            if (type == -1) {
                throw new MessageEOFException("reached end of data");
            }
            if (type == 2) {
                return this.dataIn.readByte();
            }
            if (type == 9) {
                return Byte.valueOf(this.dataIn.readUTF());
            }
            if (type == 0) {
                this.dataIn.reset();
                throw new NullPointerException("Cannot convert NULL value to byte.");
            }
            this.dataIn.reset();
            throw new MessageFormatException(" not a byte type");
        }
        catch (NumberFormatException mfe) {
            try {
                this.dataIn.reset();
            }
            catch (IOException ioe) {
                throw JMSExceptionSupport.create(ioe);
            }
            throw mfe;
        }
        catch (EOFException e) {
            throw JMSExceptionSupport.createMessageEOFException(e);
        }
        catch (IOException e2) {
            throw JMSExceptionSupport.createMessageFormatException(e2);
        }
    }
    
    @Override
    public short readShort() throws JMSException {
        this.initializeReading();
        try {
            this.dataIn.mark(17);
            final int type = this.dataIn.read();
            if (type == -1) {
                throw new MessageEOFException("reached end of data");
            }
            if (type == 4) {
                return this.dataIn.readShort();
            }
            if (type == 2) {
                return this.dataIn.readByte();
            }
            if (type == 9) {
                return Short.valueOf(this.dataIn.readUTF());
            }
            if (type == 0) {
                this.dataIn.reset();
                throw new NullPointerException("Cannot convert NULL value to short.");
            }
            this.dataIn.reset();
            throw new MessageFormatException(" not a short type");
        }
        catch (NumberFormatException mfe) {
            try {
                this.dataIn.reset();
            }
            catch (IOException ioe) {
                throw JMSExceptionSupport.create(ioe);
            }
            throw mfe;
        }
        catch (EOFException e) {
            throw JMSExceptionSupport.createMessageEOFException(e);
        }
        catch (IOException e2) {
            throw JMSExceptionSupport.createMessageFormatException(e2);
        }
    }
    
    @Override
    public char readChar() throws JMSException {
        this.initializeReading();
        try {
            this.dataIn.mark(17);
            final int type = this.dataIn.read();
            if (type == -1) {
                throw new MessageEOFException("reached end of data");
            }
            if (type == 3) {
                return this.dataIn.readChar();
            }
            if (type == 0) {
                this.dataIn.reset();
                throw new NullPointerException("Cannot convert NULL value to char.");
            }
            this.dataIn.reset();
            throw new MessageFormatException(" not a char type");
        }
        catch (NumberFormatException mfe) {
            try {
                this.dataIn.reset();
            }
            catch (IOException ioe) {
                throw JMSExceptionSupport.create(ioe);
            }
            throw mfe;
        }
        catch (EOFException e) {
            throw JMSExceptionSupport.createMessageEOFException(e);
        }
        catch (IOException e2) {
            throw JMSExceptionSupport.createMessageFormatException(e2);
        }
    }
    
    @Override
    public int readInt() throws JMSException {
        this.initializeReading();
        try {
            this.dataIn.mark(33);
            final int type = this.dataIn.read();
            if (type == -1) {
                throw new MessageEOFException("reached end of data");
            }
            if (type == 5) {
                return this.dataIn.readInt();
            }
            if (type == 4) {
                return this.dataIn.readShort();
            }
            if (type == 2) {
                return this.dataIn.readByte();
            }
            if (type == 9) {
                return Integer.valueOf(this.dataIn.readUTF());
            }
            if (type == 0) {
                this.dataIn.reset();
                throw new NullPointerException("Cannot convert NULL value to int.");
            }
            this.dataIn.reset();
            throw new MessageFormatException(" not an int type");
        }
        catch (NumberFormatException mfe) {
            try {
                this.dataIn.reset();
            }
            catch (IOException ioe) {
                throw JMSExceptionSupport.create(ioe);
            }
            throw mfe;
        }
        catch (EOFException e) {
            throw JMSExceptionSupport.createMessageEOFException(e);
        }
        catch (IOException e2) {
            throw JMSExceptionSupport.createMessageFormatException(e2);
        }
    }
    
    @Override
    public long readLong() throws JMSException {
        this.initializeReading();
        try {
            this.dataIn.mark(65);
            final int type = this.dataIn.read();
            if (type == -1) {
                throw new MessageEOFException("reached end of data");
            }
            if (type == 6) {
                return this.dataIn.readLong();
            }
            if (type == 5) {
                return this.dataIn.readInt();
            }
            if (type == 4) {
                return this.dataIn.readShort();
            }
            if (type == 2) {
                return this.dataIn.readByte();
            }
            if (type == 9) {
                return Long.valueOf(this.dataIn.readUTF());
            }
            if (type == 0) {
                this.dataIn.reset();
                throw new NullPointerException("Cannot convert NULL value to long.");
            }
            this.dataIn.reset();
            throw new MessageFormatException(" not a long type");
        }
        catch (NumberFormatException mfe) {
            try {
                this.dataIn.reset();
            }
            catch (IOException ioe) {
                throw JMSExceptionSupport.create(ioe);
            }
            throw mfe;
        }
        catch (EOFException e) {
            throw JMSExceptionSupport.createMessageEOFException(e);
        }
        catch (IOException e2) {
            throw JMSExceptionSupport.createMessageFormatException(e2);
        }
    }
    
    @Override
    public float readFloat() throws JMSException {
        this.initializeReading();
        try {
            this.dataIn.mark(33);
            final int type = this.dataIn.read();
            if (type == -1) {
                throw new MessageEOFException("reached end of data");
            }
            if (type == 8) {
                return this.dataIn.readFloat();
            }
            if (type == 9) {
                return Float.valueOf(this.dataIn.readUTF());
            }
            if (type == 0) {
                this.dataIn.reset();
                throw new NullPointerException("Cannot convert NULL value to float.");
            }
            this.dataIn.reset();
            throw new MessageFormatException(" not a float type");
        }
        catch (NumberFormatException mfe) {
            try {
                this.dataIn.reset();
            }
            catch (IOException ioe) {
                throw JMSExceptionSupport.create(ioe);
            }
            throw mfe;
        }
        catch (EOFException e) {
            throw JMSExceptionSupport.createMessageEOFException(e);
        }
        catch (IOException e2) {
            throw JMSExceptionSupport.createMessageFormatException(e2);
        }
    }
    
    @Override
    public double readDouble() throws JMSException {
        this.initializeReading();
        try {
            this.dataIn.mark(65);
            final int type = this.dataIn.read();
            if (type == -1) {
                throw new MessageEOFException("reached end of data");
            }
            if (type == 7) {
                return this.dataIn.readDouble();
            }
            if (type == 8) {
                return this.dataIn.readFloat();
            }
            if (type == 9) {
                return Double.valueOf(this.dataIn.readUTF());
            }
            if (type == 0) {
                this.dataIn.reset();
                throw new NullPointerException("Cannot convert NULL value to double.");
            }
            this.dataIn.reset();
            throw new MessageFormatException(" not a double type");
        }
        catch (NumberFormatException mfe) {
            try {
                this.dataIn.reset();
            }
            catch (IOException ioe) {
                throw JMSExceptionSupport.create(ioe);
            }
            throw mfe;
        }
        catch (EOFException e) {
            throw JMSExceptionSupport.createMessageEOFException(e);
        }
        catch (IOException e2) {
            throw JMSExceptionSupport.createMessageFormatException(e2);
        }
    }
    
    @Override
    public String readString() throws JMSException {
        this.initializeReading();
        try {
            this.dataIn.mark(65);
            final int type = this.dataIn.read();
            if (type == -1) {
                throw new MessageEOFException("reached end of data");
            }
            if (type == 0) {
                return null;
            }
            if (type == 13) {
                return MarshallingSupport.readUTF8(this.dataIn);
            }
            if (type == 9) {
                return this.dataIn.readUTF();
            }
            if (type == 6) {
                return new Long(this.dataIn.readLong()).toString();
            }
            if (type == 5) {
                return new Integer(this.dataIn.readInt()).toString();
            }
            if (type == 4) {
                return new Short(this.dataIn.readShort()).toString();
            }
            if (type == 2) {
                return new Byte(this.dataIn.readByte()).toString();
            }
            if (type == 8) {
                return new Float(this.dataIn.readFloat()).toString();
            }
            if (type == 7) {
                return new Double(this.dataIn.readDouble()).toString();
            }
            if (type == 1) {
                return (this.dataIn.readBoolean() ? Boolean.TRUE : Boolean.FALSE).toString();
            }
            if (type == 3) {
                return new Character(this.dataIn.readChar()).toString();
            }
            this.dataIn.reset();
            throw new MessageFormatException(" not a String type");
        }
        catch (NumberFormatException mfe) {
            try {
                this.dataIn.reset();
            }
            catch (IOException ioe) {
                throw JMSExceptionSupport.create(ioe);
            }
            throw mfe;
        }
        catch (EOFException e) {
            throw JMSExceptionSupport.createMessageEOFException(e);
        }
        catch (IOException e2) {
            throw JMSExceptionSupport.createMessageFormatException(e2);
        }
    }
    
    @Override
    public int readBytes(final byte[] value) throws JMSException {
        this.initializeReading();
        try {
            if (value == null) {
                throw new NullPointerException();
            }
            if (this.remainingBytes == -1) {
                this.dataIn.mark(value.length + 1);
                final int type = this.dataIn.read();
                if (type == -1) {
                    throw new MessageEOFException("reached end of data");
                }
                if (type != 10) {
                    throw new MessageFormatException("Not a byte array");
                }
                this.remainingBytes = this.dataIn.readInt();
            }
            else if (this.remainingBytes == 0) {
                return this.remainingBytes = -1;
            }
            if (value.length <= this.remainingBytes) {
                this.remainingBytes -= value.length;
                this.dataIn.readFully(value);
                return value.length;
            }
            final int rc = this.dataIn.read(value, 0, this.remainingBytes);
            this.remainingBytes = 0;
            return rc;
        }
        catch (EOFException e) {
            final JMSException jmsEx = new MessageEOFException(e.getMessage());
            jmsEx.setLinkedException(e);
            throw jmsEx;
        }
        catch (IOException e2) {
            final JMSException jmsEx = new MessageFormatException(e2.getMessage());
            jmsEx.setLinkedException(e2);
            throw jmsEx;
        }
    }
    
    @Override
    public Object readObject() throws JMSException {
        this.initializeReading();
        try {
            this.dataIn.mark(65);
            final int type = this.dataIn.read();
            if (type == -1) {
                throw new MessageEOFException("reached end of data");
            }
            if (type == 0) {
                return null;
            }
            if (type == 13) {
                return MarshallingSupport.readUTF8(this.dataIn);
            }
            if (type == 9) {
                return this.dataIn.readUTF();
            }
            if (type == 6) {
                return this.dataIn.readLong();
            }
            if (type == 5) {
                return this.dataIn.readInt();
            }
            if (type == 4) {
                return this.dataIn.readShort();
            }
            if (type == 2) {
                return this.dataIn.readByte();
            }
            if (type == 8) {
                return new Float(this.dataIn.readFloat());
            }
            if (type == 7) {
                return new Double(this.dataIn.readDouble());
            }
            if (type == 1) {
                return this.dataIn.readBoolean() ? Boolean.TRUE : Boolean.FALSE;
            }
            if (type == 3) {
                return this.dataIn.readChar();
            }
            if (type == 10) {
                final int len = this.dataIn.readInt();
                final byte[] value = new byte[len];
                this.dataIn.readFully(value);
                return value;
            }
            this.dataIn.reset();
            throw new MessageFormatException("unknown type");
        }
        catch (NumberFormatException mfe) {
            try {
                this.dataIn.reset();
            }
            catch (IOException ioe) {
                throw JMSExceptionSupport.create(ioe);
            }
            throw mfe;
        }
        catch (EOFException e) {
            final JMSException jmsEx = new MessageEOFException(e.getMessage());
            jmsEx.setLinkedException(e);
            throw jmsEx;
        }
        catch (IOException e2) {
            final JMSException jmsEx = new MessageFormatException(e2.getMessage());
            jmsEx.setLinkedException(e2);
            throw jmsEx;
        }
    }
    
    @Override
    public void writeBoolean(final boolean value) throws JMSException {
        this.initializeWriting();
        try {
            MarshallingSupport.marshalBoolean(this.dataOut, value);
        }
        catch (IOException ioe) {
            throw JMSExceptionSupport.create(ioe);
        }
    }
    
    @Override
    public void writeByte(final byte value) throws JMSException {
        this.initializeWriting();
        try {
            MarshallingSupport.marshalByte(this.dataOut, value);
        }
        catch (IOException ioe) {
            throw JMSExceptionSupport.create(ioe);
        }
    }
    
    @Override
    public void writeShort(final short value) throws JMSException {
        this.initializeWriting();
        try {
            MarshallingSupport.marshalShort(this.dataOut, value);
        }
        catch (IOException ioe) {
            throw JMSExceptionSupport.create(ioe);
        }
    }
    
    @Override
    public void writeChar(final char value) throws JMSException {
        this.initializeWriting();
        try {
            MarshallingSupport.marshalChar(this.dataOut, value);
        }
        catch (IOException ioe) {
            throw JMSExceptionSupport.create(ioe);
        }
    }
    
    @Override
    public void writeInt(final int value) throws JMSException {
        this.initializeWriting();
        try {
            MarshallingSupport.marshalInt(this.dataOut, value);
        }
        catch (IOException ioe) {
            throw JMSExceptionSupport.create(ioe);
        }
    }
    
    @Override
    public void writeLong(final long value) throws JMSException {
        this.initializeWriting();
        try {
            MarshallingSupport.marshalLong(this.dataOut, value);
        }
        catch (IOException ioe) {
            throw JMSExceptionSupport.create(ioe);
        }
    }
    
    @Override
    public void writeFloat(final float value) throws JMSException {
        this.initializeWriting();
        try {
            MarshallingSupport.marshalFloat(this.dataOut, value);
        }
        catch (IOException ioe) {
            throw JMSExceptionSupport.create(ioe);
        }
    }
    
    @Override
    public void writeDouble(final double value) throws JMSException {
        this.initializeWriting();
        try {
            MarshallingSupport.marshalDouble(this.dataOut, value);
        }
        catch (IOException ioe) {
            throw JMSExceptionSupport.create(ioe);
        }
    }
    
    @Override
    public void writeString(final String value) throws JMSException {
        this.initializeWriting();
        try {
            if (value == null) {
                MarshallingSupport.marshalNull(this.dataOut);
            }
            else {
                MarshallingSupport.marshalString(this.dataOut, value);
            }
        }
        catch (IOException ioe) {
            throw JMSExceptionSupport.create(ioe);
        }
    }
    
    @Override
    public void writeBytes(final byte[] value) throws JMSException {
        this.writeBytes(value, 0, value.length);
    }
    
    @Override
    public void writeBytes(final byte[] value, final int offset, final int length) throws JMSException {
        this.initializeWriting();
        try {
            MarshallingSupport.marshalByteArray(this.dataOut, value, offset, length);
        }
        catch (IOException ioe) {
            throw JMSExceptionSupport.create(ioe);
        }
    }
    
    @Override
    public void writeObject(final Object value) throws JMSException {
        this.initializeWriting();
        if (value == null) {
            try {
                MarshallingSupport.marshalNull(this.dataOut);
                return;
            }
            catch (IOException ioe) {
                throw JMSExceptionSupport.create(ioe);
            }
        }
        if (value instanceof String) {
            this.writeString(value.toString());
        }
        else if (value instanceof Character) {
            this.writeChar((char)value);
        }
        else if (value instanceof Boolean) {
            this.writeBoolean((boolean)value);
        }
        else if (value instanceof Byte) {
            this.writeByte((byte)value);
        }
        else if (value instanceof Short) {
            this.writeShort((short)value);
        }
        else if (value instanceof Integer) {
            this.writeInt((int)value);
        }
        else if (value instanceof Float) {
            this.writeFloat((float)value);
        }
        else if (value instanceof Double) {
            this.writeDouble((double)value);
        }
        else if (value instanceof byte[]) {
            this.writeBytes((byte[])value);
        }
        else {
            if (!(value instanceof Long)) {
                throw new MessageFormatException("Unsupported Object type: " + value.getClass());
            }
            this.writeLong((long)value);
        }
    }
    
    @Override
    public void reset() throws JMSException {
        this.storeContent();
        this.bytesOut = null;
        this.dataIn = null;
        this.dataOut = null;
        this.remainingBytes = -1;
        this.setReadOnlyBody(true);
    }
    
    private void initializeWriting() throws JMSException {
        this.checkReadOnlyBody();
        if (this.dataOut == null) {
            this.bytesOut = new ByteArrayOutputStream();
            OutputStream os = this.bytesOut;
            final ActiveMQConnection connection = this.getConnection();
            if (connection != null && connection.isUseCompression()) {
                this.compressed = true;
                os = new DeflaterOutputStream(os);
            }
            this.dataOut = new DataOutputStream(os);
        }
        if (this.content != null && this.content.length > 0) {
            try {
                if (this.compressed) {
                    final ByteArrayInputStream input = new ByteArrayInputStream(this.content.getData(), this.content.getOffset(), this.content.getLength());
                    final InflaterInputStream inflater = new InflaterInputStream(input);
                    try {
                        final byte[] buffer = new byte[8192];
                        int read = 0;
                        while ((read = inflater.read(buffer)) != -1) {
                            this.dataOut.write(buffer, 0, read);
                        }
                    }
                    finally {
                        inflater.close();
                    }
                }
                else {
                    this.dataOut.write(this.content.getData(), this.content.getOffset(), this.content.getLength());
                }
                this.content = null;
            }
            catch (IOException ioe) {
                throw JMSExceptionSupport.create(ioe);
            }
        }
    }
    
    protected void checkWriteOnlyBody() throws MessageNotReadableException {
        if (!this.readOnlyBody) {
            throw new MessageNotReadableException("Message body is write-only");
        }
    }
    
    private void initializeReading() throws MessageNotReadableException {
        this.checkWriteOnlyBody();
        if (this.dataIn == null) {
            ByteSequence data = this.getContent();
            if (data == null) {
                data = new ByteSequence(new byte[0], 0, 0);
            }
            InputStream is = new ByteArrayInputStream(data);
            if (this.isCompressed()) {
                is = new InflaterInputStream(is);
                is = new BufferedInputStream(is);
            }
            this.dataIn = new DataInputStream(is);
        }
    }
    
    @Override
    public void compress() throws IOException {
        this.storeContent();
        super.compress();
    }
    
    @Override
    public String toString() {
        return super.toString() + " ActiveMQStreamMessage{ bytesOut = " + this.bytesOut + ", dataOut = " + this.dataOut + ", dataIn = " + this.dataIn + " }";
    }
}
