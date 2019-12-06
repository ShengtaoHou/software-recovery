// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.mqtt;

import org.slf4j.LoggerFactory;
import org.apache.activemq.transport.tcp.SslTransport;
import java.security.cert.X509Certificate;
import org.fusesource.mqtt.codec.SUBACK;
import org.fusesource.mqtt.codec.CONNACK;
import org.fusesource.mqtt.codec.PUBCOMP;
import org.fusesource.mqtt.codec.PUBREL;
import org.fusesource.mqtt.codec.PUBREC;
import org.fusesource.mqtt.codec.PUBACK;
import org.fusesource.mqtt.codec.PUBLISH;
import org.fusesource.mqtt.codec.UNSUBSCRIBE;
import org.fusesource.mqtt.codec.SUBSCRIBE;
import org.fusesource.mqtt.codec.DISCONNECT;
import org.fusesource.mqtt.codec.CONNECT;
import org.fusesource.mqtt.codec.PINGRESP;
import org.fusesource.mqtt.codec.PINGREQ;
import org.apache.activemq.transport.TransportListener;
import javax.jms.JMSException;
import org.fusesource.mqtt.codec.MQTTFrame;
import java.io.IOException;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.command.Command;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.wireformat.WireFormat;
import org.apache.activemq.transport.Transport;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.apache.activemq.transport.TransportFilter;

public class MQTTTransportFilter extends TransportFilter implements MQTTTransport
{
    private static final Logger LOG;
    private static final Logger TRACE;
    private final MQTTProtocolConverter protocolConverter;
    private MQTTInactivityMonitor monitor;
    private MQTTWireFormat wireFormat;
    private final AtomicBoolean stopped;
    private boolean trace;
    private final Object sendLock;
    
    public MQTTTransportFilter(final Transport next, final WireFormat wireFormat, final BrokerService brokerService) {
        super(next);
        this.stopped = new AtomicBoolean();
        this.sendLock = new Object();
        this.protocolConverter = new MQTTProtocolConverter(this, brokerService);
        if (wireFormat instanceof MQTTWireFormat) {
            this.wireFormat = (MQTTWireFormat)wireFormat;
        }
    }
    
    @Override
    public void oneway(final Object o) throws IOException {
        try {
            final Command command = (Command)o;
            this.protocolConverter.onActiveMQCommand(command);
        }
        catch (Exception e) {
            throw IOExceptionSupport.create(e);
        }
    }
    
    @Override
    public void onCommand(final Object command) {
        try {
            final MQTTFrame frame = (MQTTFrame)command;
            if (this.trace) {
                MQTTTransportFilter.TRACE.trace("Received: " + toString(frame));
            }
            this.protocolConverter.onMQTTCommand(frame);
        }
        catch (IOException e) {
            this.onException(e);
        }
        catch (JMSException e2) {
            this.onException(IOExceptionSupport.create(e2));
        }
    }
    
    @Override
    public void sendToActiveMQ(final Command command) {
        final TransportListener l = this.transportListener;
        if (l != null) {
            l.onCommand(command);
        }
    }
    
    @Override
    public void sendToMQTT(final MQTTFrame command) throws IOException {
        if (!this.stopped.get()) {
            if (this.trace) {
                MQTTTransportFilter.TRACE.trace("Sending : " + toString(command));
            }
            final Transport n = this.next;
            if (n != null) {
                synchronized (this.sendLock) {
                    n.oneway(command);
                }
            }
        }
    }
    
    private static String toString(final MQTTFrame frame) {
        if (frame == null) {
            return null;
        }
        try {
            switch (frame.messageType()) {
                case 12: {
                    return new PINGREQ().decode(frame).toString();
                }
                case 13: {
                    return new PINGRESP().decode(frame).toString();
                }
                case 1: {
                    return new CONNECT().decode(frame).toString();
                }
                case 14: {
                    return new DISCONNECT().decode(frame).toString();
                }
                case 8: {
                    return new SUBSCRIBE().decode(frame).toString();
                }
                case 10: {
                    return new UNSUBSCRIBE().decode(frame).toString();
                }
                case 3: {
                    return new PUBLISH().decode(frame).toString();
                }
                case 4: {
                    return new PUBACK().decode(frame).toString();
                }
                case 5: {
                    return new PUBREC().decode(frame).toString();
                }
                case 6: {
                    return new PUBREL().decode(frame).toString();
                }
                case 7: {
                    return new PUBCOMP().decode(frame).toString();
                }
                case 2: {
                    return new CONNACK().decode(frame).toString();
                }
                case 9: {
                    return new SUBACK().decode(frame).toString();
                }
                default: {
                    return frame.toString();
                }
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
            return frame.toString();
        }
    }
    
    @Override
    public void stop() throws Exception {
        if (this.stopped.compareAndSet(false, true)) {
            super.stop();
        }
    }
    
    @Override
    public X509Certificate[] getPeerCertificates() {
        if (this.next instanceof SslTransport) {
            final X509Certificate[] peerCerts = ((SslTransport)this.next).getPeerCertificates();
            if (this.trace && peerCerts != null) {
                MQTTTransportFilter.LOG.debug("Peer Identity has been verified\n");
            }
            return peerCerts;
        }
        return null;
    }
    
    public boolean isTrace() {
        return this.trace;
    }
    
    public void setTrace(final boolean trace) {
        this.trace = trace;
    }
    
    @Override
    public MQTTInactivityMonitor getInactivityMonitor() {
        return this.monitor;
    }
    
    public void setInactivityMonitor(final MQTTInactivityMonitor monitor) {
        this.monitor = monitor;
    }
    
    @Override
    public MQTTWireFormat getWireFormat() {
        return this.wireFormat;
    }
    
    @Override
    public void onException(final IOException error) {
        this.protocolConverter.onTransportError();
        super.onException(error);
    }
    
    public long getDefaultKeepAlive() {
        return (this.protocolConverter != null) ? this.protocolConverter.getDefaultKeepAlive() : -1L;
    }
    
    public void setDefaultKeepAlive(final long defaultHeartBeat) {
        this.protocolConverter.setDefaultKeepAlive(defaultHeartBeat);
    }
    
    public int getActiveMQSubscriptionPrefetch() {
        return this.protocolConverter.getActiveMQSubscriptionPrefetch();
    }
    
    public void setActiveMQSubscriptionPrefetch(final int activeMQSubscriptionPrefetch) {
        this.protocolConverter.setActiveMQSubscriptionPrefetch(activeMQSubscriptionPrefetch);
    }
    
    static {
        LOG = LoggerFactory.getLogger(MQTTTransportFilter.class);
        TRACE = LoggerFactory.getLogger(MQTTTransportFilter.class.getPackage().getName() + ".MQTTIO");
    }
}
