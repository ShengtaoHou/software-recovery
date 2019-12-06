// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.stomp;

import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import org.fusesource.hawtbuf.UTF8Buffer;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import org.apache.activemq.command.DataStructure;
import java.util.Iterator;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import java.io.Writer;
import org.codehaus.jettison.mapped.Configuration;
import java.util.Locale;
import java.io.StringWriter;
import java.io.IOException;
import java.io.Serializable;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import java.util.HashMap;
import javax.jms.JMSException;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import java.util.Map;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import java.io.Reader;
import com.thoughtworks.xstream.io.xml.XppReader;
import com.thoughtworks.xstream.io.xml.xppdom.XppFactory;
import java.io.StringReader;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.broker.BrokerContext;
import com.thoughtworks.xstream.XStream;
import org.apache.activemq.broker.BrokerContextAware;

public class JmsFrameTranslator extends LegacyFrameTranslator implements BrokerContextAware
{
    XStream xStream;
    BrokerContext brokerContext;
    
    public JmsFrameTranslator() {
        this.xStream = null;
    }
    
    @Override
    public ActiveMQMessage convertFrame(final ProtocolConverter converter, final StompFrame command) throws JMSException, ProtocolException {
        final Map<String, String> headers = command.getHeaders();
        final String transformation = headers.get("transformation");
        ActiveMQMessage msg = null;
        if (headers.containsKey("content-length") || transformation.equals(Stomp.Transformations.JMS_BYTE.toString())) {
            msg = super.convertFrame(converter, command);
        }
        else {
            try {
                final String text = new String(command.getContent(), "UTF-8");
                switch (Stomp.Transformations.getValue(transformation)) {
                    case JMS_OBJECT_XML: {
                        final HierarchicalStreamReader in = (HierarchicalStreamReader)new XppReader((Reader)new StringReader(text), XppFactory.createDefaultParser());
                        msg = this.createObjectMessage(in);
                        break;
                    }
                    case JMS_OBJECT_JSON: {
                        final HierarchicalStreamReader in = new JettisonMappedXmlDriver().createReader((Reader)new StringReader(text));
                        msg = this.createObjectMessage(in);
                        break;
                    }
                    case JMS_MAP_XML: {
                        final HierarchicalStreamReader in = (HierarchicalStreamReader)new XppReader((Reader)new StringReader(text), XppFactory.createDefaultParser());
                        msg = this.createMapMessage(in);
                        break;
                    }
                    case JMS_MAP_JSON: {
                        final HierarchicalStreamReader in = new JettisonMappedXmlDriver().createReader((Reader)new StringReader(text));
                        msg = this.createMapMessage(in);
                        break;
                    }
                    default: {
                        throw new Exception("Unkown transformation: " + transformation);
                    }
                }
            }
            catch (Throwable e) {
                command.getHeaders().put("transformation-error", e.getMessage());
                msg = super.convertFrame(converter, command);
            }
        }
        FrameTranslator.Helper.copyStandardHeadersFromFrameToMessage(converter, command, msg, this);
        return msg;
    }
    
    @Override
    public StompFrame convertMessage(final ProtocolConverter converter, final ActiveMQMessage message) throws IOException, JMSException {
        if (message.getDataStructureType() == 26) {
            final StompFrame command = new StompFrame();
            command.setAction("MESSAGE");
            final Map<String, String> headers = new HashMap<String, String>(25);
            command.setHeaders(headers);
            FrameTranslator.Helper.copyStandardHeadersFromMessageToFrame(converter, message, command, this);
            if (headers.get("transformation").equals(Stomp.Transformations.JMS_XML.toString())) {
                headers.put("transformation", Stomp.Transformations.JMS_OBJECT_XML.toString());
            }
            else if (headers.get("transformation").equals(Stomp.Transformations.JMS_JSON.toString())) {
                headers.put("transformation", Stomp.Transformations.JMS_OBJECT_JSON.toString());
            }
            final ActiveMQObjectMessage msg = (ActiveMQObjectMessage)message.copy();
            command.setContent(this.marshall(msg.getObject(), headers.get("transformation")).getBytes("UTF-8"));
            return command;
        }
        if (message.getDataStructureType() == 25) {
            final StompFrame command = new StompFrame();
            command.setAction("MESSAGE");
            final Map<String, String> headers = new HashMap<String, String>(25);
            command.setHeaders(headers);
            FrameTranslator.Helper.copyStandardHeadersFromMessageToFrame(converter, message, command, this);
            if (headers.get("transformation").equals(Stomp.Transformations.JMS_XML.toString())) {
                headers.put("transformation", Stomp.Transformations.JMS_MAP_XML.toString());
            }
            else if (headers.get("transformation").equals(Stomp.Transformations.JMS_JSON.toString())) {
                headers.put("transformation", Stomp.Transformations.JMS_MAP_JSON.toString());
            }
            final ActiveMQMapMessage msg2 = (ActiveMQMapMessage)message.copy();
            command.setContent(this.marshall((Serializable)msg2.getContentMap(), headers.get("transformation")).getBytes("UTF-8"));
            return command;
        }
        if (message.getDataStructureType() == 23 && "Advisory".equals(message.getType())) {
            final StompFrame command = new StompFrame();
            command.setAction("MESSAGE");
            final Map<String, String> headers = new HashMap<String, String>(25);
            command.setHeaders(headers);
            FrameTranslator.Helper.copyStandardHeadersFromMessageToFrame(converter, message, command, this);
            if (!headers.containsKey("transformation")) {
                headers.put("transformation", Stomp.Transformations.JMS_ADVISORY_JSON.toString());
            }
            if (headers.get("transformation").equals(Stomp.Transformations.JMS_XML.toString())) {
                headers.put("transformation", Stomp.Transformations.JMS_ADVISORY_XML.toString());
            }
            else if (headers.get("transformation").equals(Stomp.Transformations.JMS_JSON.toString())) {
                headers.put("transformation", Stomp.Transformations.JMS_ADVISORY_JSON.toString());
            }
            final String body = this.marshallAdvisory(message.getDataStructure(), headers.get("transformation"));
            command.setContent(body.getBytes("UTF-8"));
            return command;
        }
        return super.convertMessage(converter, message);
    }
    
    protected String marshall(final Serializable object, final String transformation) throws JMSException {
        final StringWriter buffer = new StringWriter();
        HierarchicalStreamWriter out;
        if (transformation.toLowerCase(Locale.ENGLISH).endsWith("json")) {
            out = new JettisonMappedXmlDriver(new Configuration(), false).createWriter((Writer)buffer);
        }
        else {
            out = (HierarchicalStreamWriter)new PrettyPrintWriter((Writer)buffer);
        }
        this.getXStream().marshal((Object)object, out);
        return buffer.toString();
    }
    
    protected ActiveMQObjectMessage createObjectMessage(final HierarchicalStreamReader in) throws JMSException {
        final ActiveMQObjectMessage objMsg = new ActiveMQObjectMessage();
        final Object obj = this.getXStream().unmarshal(in);
        objMsg.setObject((Serializable)obj);
        return objMsg;
    }
    
    protected ActiveMQMapMessage createMapMessage(final HierarchicalStreamReader in) throws JMSException {
        final ActiveMQMapMessage mapMsg = new ActiveMQMapMessage();
        final Map<String, Object> map = (Map<String, Object>)this.getXStream().unmarshal(in);
        for (final String key : map.keySet()) {
            mapMsg.setObject(key, map.get(key));
        }
        return mapMsg;
    }
    
    protected String marshallAdvisory(final DataStructure ds, final String transformation) {
        final StringWriter buffer = new StringWriter();
        HierarchicalStreamWriter out;
        if (transformation.toLowerCase(Locale.ENGLISH).endsWith("json")) {
            out = new JettisonMappedXmlDriver().createWriter((Writer)buffer);
        }
        else {
            out = (HierarchicalStreamWriter)new PrettyPrintWriter((Writer)buffer);
        }
        final XStream xstream = this.getXStream();
        xstream.setMode(1001);
        xstream.aliasPackage("", "org.apache.activemq.command");
        xstream.marshal((Object)ds, out);
        return buffer.toString();
    }
    
    public XStream getXStream() {
        if (this.xStream == null) {
            this.xStream = this.createXStream();
        }
        return this.xStream;
    }
    
    public void setXStream(final XStream xStream) {
        this.xStream = xStream;
    }
    
    protected XStream createXStream() {
        XStream xstream = null;
        if (this.brokerContext != null) {
            final Map<String, XStream> beans = (Map<String, XStream>)this.brokerContext.getBeansOfType(XStream.class);
            for (final XStream bean : beans.values()) {
                if (bean != null) {
                    xstream = bean;
                    break;
                }
            }
        }
        if (xstream == null) {
            xstream = new XStream();
            xstream.ignoreUnknownElements();
        }
        xstream.registerConverter((SingleValueConverter)new AbstractSingleValueConverter() {
            public Object fromString(final String str) {
                return str;
            }
            
            public boolean canConvert(final Class type) {
                return type.equals(UTF8Buffer.class);
            }
        });
        xstream.alias("string", (Class)UTF8Buffer.class);
        return xstream;
    }
    
    @Override
    public void setBrokerContext(final BrokerContext brokerContext) {
        this.brokerContext = brokerContext;
    }
    
    @Override
    public BrokerContext getBrokerContext() {
        return this.brokerContext;
    }
    
    protected String marshallAdvisory(final DataStructure ds) {
        final XStream xstream = new XStream((HierarchicalStreamDriver)new JsonHierarchicalStreamDriver());
        xstream.setMode(1001);
        xstream.aliasPackage("", "org.apache.activemq.command");
        return xstream.toXML((Object)ds);
    }
}
