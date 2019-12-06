// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import java.util.zip.Deflater;
import java.util.Arrays;
import org.apache.activemq.util.ByteSequenceData;
import java.util.zip.Inflater;
import java.io.InputStream;
import org.apache.activemq.util.ByteArrayInputStream;
import javax.jms.MessageNotReadableException;
import org.apache.activemq.ActiveMQConnection;
import java.io.OutputStream;
import javax.jms.MessageFormatException;
import java.io.EOFException;
import org.apache.activemq.util.JMSExceptionSupport;
import org.apache.activemq.util.ByteSequence;
import java.io.IOException;
import javax.jms.JMSException;
import java.io.DataInputStream;
import org.apache.activemq.util.ByteArrayOutputStream;
import java.io.DataOutputStream;
import javax.jms.BytesMessage;

public class ActiveMQBytesMessage extends ActiveMQMessage implements BytesMessage
{
    public static final byte DATA_STRUCTURE_TYPE = 24;
    protected transient DataOutputStream dataOut;
    protected transient ByteArrayOutputStream bytesOut;
    protected transient DataInputStream dataIn;
    protected transient int length;
    
    @Override
    public Message copy() {
        final ActiveMQBytesMessage copy = new ActiveMQBytesMessage();
        this.copy(copy);
        return copy;
    }
    
    private void copy(final ActiveMQBytesMessage copy) {
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
                final ByteSequence bs = this.bytesOut.toByteSequence();
                this.setContent(bs);
                if (this.compressed) {
                    this.doCompress();
                }
            }
            catch (IOException ioe) {
                throw new RuntimeException(ioe.getMessage(), ioe);
            }
            finally {
                try {
                    if (this.bytesOut != null) {
                        this.bytesOut.close();
                        this.bytesOut = null;
                    }
                    if (this.dataOut != null) {
                        this.dataOut.close();
                        this.dataOut = null;
                    }
                }
                catch (IOException ex) {}
            }
        }
    }
    
    @Override
    public byte getDataStructureType() {
        return 24;
    }
    
    @Override
    public String getJMSXMimeType() {
        return "jms/bytes-message";
    }
    
    @Override
    public void clearBody() throws JMSException {
        super.clearBody();
        this.dataOut = null;
        this.dataIn = null;
        this.bytesOut = null;
    }
    
    @Override
    public long getBodyLength() throws JMSException {
        this.initializeReading();
        return this.length;
    }
    
    @Override
    public boolean readBoolean() throws JMSException {
        this.initializeReading();
        try {
            return this.dataIn.readBoolean();
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
            return this.dataIn.readByte();
        }
        catch (EOFException e) {
            throw JMSExceptionSupport.createMessageEOFException(e);
        }
        catch (IOException e2) {
            throw JMSExceptionSupport.createMessageFormatException(e2);
        }
    }
    
    @Override
    public int readUnsignedByte() throws JMSException {
        this.initializeReading();
        try {
            return this.dataIn.readUnsignedByte();
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
            return this.dataIn.readShort();
        }
        catch (EOFException e) {
            throw JMSExceptionSupport.createMessageEOFException(e);
        }
        catch (IOException e2) {
            throw JMSExceptionSupport.createMessageFormatException(e2);
        }
    }
    
    @Override
    public int readUnsignedShort() throws JMSException {
        this.initializeReading();
        try {
            return this.dataIn.readUnsignedShort();
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
            return this.dataIn.readChar();
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
            return this.dataIn.readInt();
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
            return this.dataIn.readLong();
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
            return this.dataIn.readFloat();
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
            return this.dataIn.readDouble();
        }
        catch (EOFException e) {
            throw JMSExceptionSupport.createMessageEOFException(e);
        }
        catch (IOException e2) {
            throw JMSExceptionSupport.createMessageFormatException(e2);
        }
    }
    
    @Override
    public String readUTF() throws JMSException {
        this.initializeReading();
        try {
            return this.dataIn.readUTF();
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
        return this.readBytes(value, value.length);
    }
    
    @Override
    public int readBytes(final byte[] value, final int length) throws JMSException {
        this.initializeReading();
        try {
            int n;
            int count;
            for (n = 0; n < length; n += count) {
                count = this.dataIn.read(value, n, length - n);
                if (count < 0) {
                    break;
                }
            }
            if (n == 0 && length > 0) {
                n = -1;
            }
            return n;
        }
        catch (EOFException e) {
            throw JMSExceptionSupport.createMessageEOFException(e);
        }
        catch (IOException e2) {
            throw JMSExceptionSupport.createMessageFormatException(e2);
        }
    }
    
    @Override
    public void writeBoolean(final boolean value) throws JMSException {
        this.initializeWriting();
        try {
            this.dataOut.writeBoolean(value);
        }
        catch (IOException ioe) {
            throw JMSExceptionSupport.create(ioe);
        }
    }
    
    @Override
    public void writeByte(final byte value) throws JMSException {
        this.initializeWriting();
        try {
            this.dataOut.writeByte(value);
        }
        catch (IOException ioe) {
            throw JMSExceptionSupport.create(ioe);
        }
    }
    
    @Override
    public void writeShort(final short value) throws JMSException {
        this.initializeWriting();
        try {
            this.dataOut.writeShort(value);
        }
        catch (IOException ioe) {
            throw JMSExceptionSupport.create(ioe);
        }
    }
    
    @Override
    public void writeChar(final char value) throws JMSException {
        this.initializeWriting();
        try {
            this.dataOut.writeChar(value);
        }
        catch (IOException ioe) {
            throw JMSExceptionSupport.create(ioe);
        }
    }
    
    @Override
    public void writeInt(final int value) throws JMSException {
        this.initializeWriting();
        try {
            this.dataOut.writeInt(value);
        }
        catch (IOException ioe) {
            throw JMSExceptionSupport.create(ioe);
        }
    }
    
    @Override
    public void writeLong(final long value) throws JMSException {
        this.initializeWriting();
        try {
            this.dataOut.writeLong(value);
        }
        catch (IOException ioe) {
            throw JMSExceptionSupport.create(ioe);
        }
    }
    
    @Override
    public void writeFloat(final float value) throws JMSException {
        this.initializeWriting();
        try {
            this.dataOut.writeFloat(value);
        }
        catch (IOException ioe) {
            throw JMSExceptionSupport.create(ioe);
        }
    }
    
    @Override
    public void writeDouble(final double value) throws JMSException {
        this.initializeWriting();
        try {
            this.dataOut.writeDouble(value);
        }
        catch (IOException ioe) {
            throw JMSExceptionSupport.create(ioe);
        }
    }
    
    @Override
    public void writeUTF(final String value) throws JMSException {
        this.initializeWriting();
        try {
            this.dataOut.writeUTF(value);
        }
        catch (IOException ioe) {
            throw JMSExceptionSupport.create(ioe);
        }
    }
    
    @Override
    public void writeBytes(final byte[] value) throws JMSException {
        this.initializeWriting();
        try {
            this.dataOut.write(value);
        }
        catch (IOException ioe) {
            throw JMSExceptionSupport.create(ioe);
        }
    }
    
    @Override
    public void writeBytes(final byte[] value, final int offset, final int length) throws JMSException {
        this.initializeWriting();
        try {
            this.dataOut.write(value, offset, length);
        }
        catch (IOException ioe) {
            throw JMSExceptionSupport.create(ioe);
        }
    }
    
    @Override
    public void writeObject(final Object value) throws JMSException {
        if (value == null) {
            throw new NullPointerException();
        }
        this.initializeWriting();
        if (value instanceof Boolean) {
            this.writeBoolean((boolean)value);
        }
        else if (value instanceof Character) {
            this.writeChar((char)value);
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
        else if (value instanceof Long) {
            this.writeLong((long)value);
        }
        else if (value instanceof Float) {
            this.writeFloat((float)value);
        }
        else if (value instanceof Double) {
            this.writeDouble((double)value);
        }
        else if (value instanceof String) {
            this.writeUTF(value.toString());
        }
        else {
            if (!(value instanceof byte[])) {
                throw new MessageFormatException("Cannot write non-primitive type:" + value.getClass());
            }
            this.writeBytes((byte[])value);
        }
    }
    
    @Override
    public void reset() throws JMSException {
        this.storeContent();
        this.setReadOnlyBody(true);
        try {
            if (this.bytesOut != null) {
                this.bytesOut.close();
                this.bytesOut = null;
            }
            if (this.dataIn != null) {
                this.dataIn.close();
                this.dataIn = null;
            }
            if (this.dataOut != null) {
                this.dataOut.close();
                this.dataOut = null;
            }
        }
        catch (IOException ioe) {
            throw JMSExceptionSupport.create(ioe);
        }
    }
    
    private void initializeWriting() throws JMSException {
        this.checkReadOnlyBody();
        if (this.dataOut == null) {
            this.bytesOut = new ByteArrayOutputStream();
            final OutputStream os = this.bytesOut;
            this.dataOut = new DataOutputStream(os);
        }
        final ActiveMQConnection connection = this.getConnection();
        if (connection != null && connection.isUseCompression()) {
            this.compressed = true;
        }
        this.restoreOldContent();
    }
    
    private void restoreOldContent() throws JMSException {
        if (this.content != null && this.content.length > 0) {
            try {
                ByteSequence toRestore = this.content;
                if (this.compressed) {
                    toRestore = new ByteSequence(this.decompress(this.content));
                }
                this.dataOut.write(toRestore.getData(), toRestore.getOffset(), toRestore.getLength());
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
    
    private void initializeReading() throws JMSException {
        this.checkWriteOnlyBody();
        if (this.dataIn == null) {
            try {
                ByteSequence data = this.getContent();
                if (data == null) {
                    data = new ByteSequence(new byte[0], 0, 0);
                }
                InputStream is = new ByteArrayInputStream(data);
                if (this.isCompressed()) {
                    if (data.length != 0) {
                        is = new ByteArrayInputStream(this.decompress(data));
                    }
                }
                else {
                    this.length = data.getLength();
                }
                this.dataIn = new DataInputStream(is);
            }
            catch (IOException ioe) {
                throw JMSExceptionSupport.create(ioe);
            }
        }
    }
    
    protected byte[] decompress(final ByteSequence dataSequence) throws IOException {
        final Inflater inflater = new Inflater();
        final ByteArrayOutputStream decompressed = new ByteArrayOutputStream();
        try {
            this.length = ByteSequenceData.readIntBig(dataSequence);
            dataSequence.offset = 0;
            final byte[] data = Arrays.copyOfRange(dataSequence.getData(), 4, dataSequence.getLength());
            inflater.setInput(data);
            final byte[] buffer = new byte[this.length];
            final int count = inflater.inflate(buffer);
            decompressed.write(buffer, 0, count);
            return decompressed.toByteArray();
        }
        catch (Exception e) {
            throw new IOException(e);
        }
        finally {
            inflater.end();
            decompressed.close();
        }
    }
    
    @Override
    public void setObjectProperty(final String name, final Object value) throws JMSException {
        this.initializeWriting();
        super.setObjectProperty(name, value);
    }
    
    @Override
    public String toString() {
        return super.toString() + " ActiveMQBytesMessage{ bytesOut = " + this.bytesOut + ", dataOut = " + this.dataOut + ", dataIn = " + this.dataIn + " }";
    }
    
    @Override
    protected void doCompress() throws IOException {
        this.compressed = true;
        ByteSequence bytes = this.getContent();
        if (bytes != null) {
            final int length = bytes.getLength();
            final ByteArrayOutputStream compressed = new ByteArrayOutputStream();
            compressed.write(new byte[4]);
            final Deflater deflater = new Deflater();
            try {
                deflater.setInput(bytes.data);
                deflater.finish();
                final byte[] buffer = new byte[1024];
                while (!deflater.finished()) {
                    final int count = deflater.deflate(buffer);
                    compressed.write(buffer, 0, count);
                }
                bytes = compressed.toByteSequence();
                ByteSequenceData.writeIntBig(bytes, length);
                bytes.offset = 0;
                this.setContent(bytes);
            }
            finally {
                deflater.end();
                compressed.close();
            }
        }
    }
}
