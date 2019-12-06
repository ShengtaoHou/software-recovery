// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import org.apache.activemq.wireformat.WireFormat;
import org.apache.activemq.util.JMSExceptionSupport;
import java.io.InputStream;
import org.apache.activemq.util.ClassLoadingAwareObjectInputStream;
import java.io.DataInputStream;
import java.util.zip.InflaterInputStream;
import org.apache.activemq.util.ByteArrayInputStream;
import javax.jms.JMSException;
import org.apache.activemq.util.ByteSequence;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.DataOutputStream;
import java.util.zip.DeflaterOutputStream;
import org.apache.activemq.util.ByteArrayOutputStream;
import org.apache.activemq.ActiveMQConnection;
import java.io.Serializable;
import javax.jms.ObjectMessage;

public class ActiveMQObjectMessage extends ActiveMQMessage implements ObjectMessage
{
    public static final byte DATA_STRUCTURE_TYPE = 26;
    static final ClassLoader ACTIVEMQ_CLASSLOADER;
    protected transient Serializable object;
    
    @Override
    public Message copy() {
        final ActiveMQObjectMessage copy = new ActiveMQObjectMessage();
        this.copy(copy);
        return copy;
    }
    
    private void copy(final ActiveMQObjectMessage copy) {
        final ActiveMQConnection connection = this.getConnection();
        if (connection == null || !connection.isObjectMessageSerializationDefered()) {
            this.storeContent();
            copy.object = null;
        }
        else {
            copy.object = this.object;
        }
        super.copy(copy);
    }
    
    @Override
    public void storeContentAndClear() {
        this.storeContent();
        this.object = null;
    }
    
    @Override
    public void storeContent() {
        final ByteSequence bodyAsBytes = this.getContent();
        if (bodyAsBytes == null && this.object != null) {
            try {
                OutputStream os;
                final ByteArrayOutputStream bytesOut = (ByteArrayOutputStream)(os = new ByteArrayOutputStream());
                final ActiveMQConnection connection = this.getConnection();
                if (connection != null && connection.isUseCompression()) {
                    this.compressed = true;
                    os = new DeflaterOutputStream(os);
                }
                final DataOutputStream dataOut = new DataOutputStream(os);
                final ObjectOutputStream objOut = new ObjectOutputStream(dataOut);
                objOut.writeObject(this.object);
                objOut.flush();
                objOut.reset();
                objOut.close();
                this.setContent(bytesOut.toByteSequence());
            }
            catch (IOException ioe) {
                throw new RuntimeException(ioe.getMessage(), ioe);
            }
        }
    }
    
    @Override
    public byte getDataStructureType() {
        return 26;
    }
    
    @Override
    public String getJMSXMimeType() {
        return "jms/object-message";
    }
    
    @Override
    public void clearBody() throws JMSException {
        super.clearBody();
        this.object = null;
    }
    
    @Override
    public void setObject(final Serializable newObject) throws JMSException {
        this.checkReadOnlyBody();
        this.object = newObject;
        this.setContent(null);
        final ActiveMQConnection connection = this.getConnection();
        if (connection == null || !connection.isObjectMessageSerializationDefered()) {
            this.storeContent();
        }
    }
    
    @Override
    public Serializable getObject() throws JMSException {
        if (this.object == null && this.getContent() != null) {
            try {
                final ByteSequence content = this.getContent();
                InputStream is = new ByteArrayInputStream(content);
                if (this.isCompressed()) {
                    is = new InflaterInputStream(is);
                }
                final DataInputStream dataIn = new DataInputStream(is);
                final ClassLoadingAwareObjectInputStream objIn = new ClassLoadingAwareObjectInputStream(dataIn);
                try {
                    this.object = (Serializable)objIn.readObject();
                }
                catch (ClassNotFoundException ce) {
                    throw JMSExceptionSupport.create("Failed to build body from content. Serializable class not available to broker. Reason: " + ce, ce);
                }
                finally {
                    dataIn.close();
                }
            }
            catch (IOException e) {
                throw JMSExceptionSupport.create("Failed to build body from bytes. Reason: " + e, e);
            }
        }
        return this.object;
    }
    
    @Override
    public void beforeMarshall(final WireFormat wireFormat) throws IOException {
        super.beforeMarshall(wireFormat);
        this.storeContent();
    }
    
    @Override
    public void clearMarshalledState() throws JMSException {
        super.clearMarshalledState();
        this.object = null;
    }
    
    @Override
    public void onMessageRolledBack() {
        super.onMessageRolledBack();
        this.object = null;
    }
    
    @Override
    public void compress() throws IOException {
        this.storeContent();
        super.compress();
    }
    
    @Override
    public String toString() {
        try {
            this.getObject();
        }
        catch (JMSException ex) {}
        return super.toString();
    }
    
    static {
        ACTIVEMQ_CLASSLOADER = ActiveMQObjectMessage.class.getClassLoader();
    }
}
