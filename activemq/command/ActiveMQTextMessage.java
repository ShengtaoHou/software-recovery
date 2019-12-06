// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import java.util.Map;
import java.util.HashMap;
import org.apache.activemq.ActiveMQConnection;
import java.io.OutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.util.zip.DeflaterOutputStream;
import org.apache.activemq.util.ByteArrayOutputStream;
import org.apache.activemq.wireformat.WireFormat;
import java.io.InputStream;
import java.io.IOException;
import org.apache.activemq.util.JMSExceptionSupport;
import java.io.DataInput;
import org.apache.activemq.util.MarshallingSupport;
import java.io.DataInputStream;
import java.util.zip.InflaterInputStream;
import org.apache.activemq.util.ByteArrayInputStream;
import javax.jms.JMSException;
import javax.jms.MessageNotWriteableException;
import org.apache.activemq.util.ByteSequence;
import javax.jms.TextMessage;

public class ActiveMQTextMessage extends ActiveMQMessage implements TextMessage
{
    public static final byte DATA_STRUCTURE_TYPE = 28;
    protected String text;
    
    @Override
    public Message copy() {
        final ActiveMQTextMessage copy = new ActiveMQTextMessage();
        this.copy(copy);
        return copy;
    }
    
    private void copy(final ActiveMQTextMessage copy) {
        super.copy(copy);
        copy.text = this.text;
    }
    
    @Override
    public byte getDataStructureType() {
        return 28;
    }
    
    @Override
    public String getJMSXMimeType() {
        return "jms/text-message";
    }
    
    @Override
    public void setText(final String text) throws MessageNotWriteableException {
        this.checkReadOnlyBody();
        this.text = text;
        this.setContent(null);
    }
    
    @Override
    public String getText() throws JMSException {
        if (this.text == null && this.getContent() != null) {
            this.text = this.decodeContent();
            this.setContent(null);
            this.setCompressed(false);
        }
        return this.text;
    }
    
    private String decodeContent() throws JMSException {
        String text = null;
        if (this.getContent() != null) {
            InputStream is = null;
            try {
                final ByteSequence bodyAsBytes = this.getContent();
                if (bodyAsBytes != null) {
                    is = new ByteArrayInputStream(bodyAsBytes);
                    if (this.isCompressed()) {
                        is = new InflaterInputStream(is);
                    }
                    final DataInputStream dataIn = new DataInputStream(is);
                    text = MarshallingSupport.readUTF8(dataIn);
                    dataIn.close();
                }
            }
            catch (IOException ioe) {
                throw JMSExceptionSupport.create(ioe);
            }
            finally {
                if (is != null) {
                    try {
                        is.close();
                    }
                    catch (IOException ex) {}
                }
            }
        }
        return text;
    }
    
    @Override
    public void beforeMarshall(final WireFormat wireFormat) throws IOException {
        super.beforeMarshall(wireFormat);
        this.storeContent();
    }
    
    @Override
    public void storeContentAndClear() {
        this.storeContent();
        this.text = null;
    }
    
    @Override
    public void storeContent() {
        try {
            final ByteSequence content = this.getContent();
            if (content == null && this.text != null) {
                OutputStream os;
                final ByteArrayOutputStream bytesOut = (ByteArrayOutputStream)(os = new ByteArrayOutputStream());
                final ActiveMQConnection connection = this.getConnection();
                if (connection != null && connection.isUseCompression()) {
                    this.compressed = true;
                    os = new DeflaterOutputStream(os);
                }
                final DataOutputStream dataOut = new DataOutputStream(os);
                MarshallingSupport.writeUTF8(dataOut, this.text);
                dataOut.close();
                this.setContent(bytesOut.toByteSequence());
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void clearMarshalledState() throws JMSException {
        super.clearMarshalledState();
        this.text = null;
    }
    
    @Override
    public void clearBody() throws JMSException {
        super.clearBody();
        this.text = null;
    }
    
    @Override
    public int getSize() {
        if (this.size == 0 && this.content == null && this.text != null) {
            this.size = this.getMinimumMessageSize();
            if (this.marshalledProperties != null) {
                this.size += this.marshalledProperties.getLength();
            }
            this.size += this.text.length() * 2;
        }
        return super.getSize();
    }
    
    @Override
    public String toString() {
        try {
            String text = this.text;
            if (text == null) {
                text = this.decodeContent();
            }
            if (text != null) {
                text = MarshallingSupport.truncate64(text);
                final HashMap<String, Object> overrideFields = new HashMap<String, Object>();
                overrideFields.put("text", text);
                return super.toString(overrideFields);
            }
        }
        catch (JMSException ex) {}
        return super.toString();
    }
}
