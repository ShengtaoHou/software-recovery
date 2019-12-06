// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.amqp;

import org.slf4j.LoggerFactory;
import org.apache.activemq.transport.tcp.SslTransport;
import java.security.cert.X509Certificate;
import org.apache.activemq.transport.TransportListener;
import java.io.IOException;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.command.Command;
import org.apache.activemq.broker.BrokerContext;
import org.apache.activemq.wireformat.WireFormat;
import org.apache.activemq.transport.Transport;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.apache.activemq.transport.TransportFilter;

public class AmqpTransportFilter extends TransportFilter implements AmqpTransport
{
    private static final Logger LOG;
    static final Logger TRACE_BYTES;
    static final Logger TRACE_FRAMES;
    private IAmqpProtocolConverter protocolConverter;
    private AmqpWireFormat wireFormat;
    private boolean trace;
    private String transformer;
    private final ReentrantLock lock;
    
    public AmqpTransportFilter(final Transport next, final WireFormat wireFormat, final BrokerContext brokerContext) {
        super(next);
        this.transformer = "native";
        this.lock = new ReentrantLock();
        this.protocolConverter = new AMQPProtocolDiscriminator(this);
        if (wireFormat instanceof AmqpWireFormat) {
            this.wireFormat = (AmqpWireFormat)wireFormat;
        }
    }
    
    @Override
    public void oneway(final Object o) throws IOException {
        try {
            final Command command = (Command)o;
            this.lock.lock();
            try {
                this.protocolConverter.onActiveMQCommand(command);
            }
            finally {
                this.lock.unlock();
            }
        }
        catch (Exception e) {
            throw IOExceptionSupport.create(e);
        }
    }
    
    @Override
    public void onException(final IOException error) {
        this.lock.lock();
        try {
            this.protocolConverter.onAMQPException(error);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void sendToActiveMQ(final IOException error) {
        super.onException(error);
    }
    
    @Override
    public void onCommand(final Object command) {
        try {
            if (this.trace) {
                AmqpTransportFilter.TRACE_BYTES.trace("Received: \n{}", command);
            }
            this.lock.lock();
            try {
                this.protocolConverter.onAMQPData(command);
            }
            finally {
                this.lock.unlock();
            }
        }
        catch (IOException e) {
            this.handleException(e);
        }
        catch (Exception e2) {
            this.onException(IOExceptionSupport.create(e2));
        }
    }
    
    @Override
    public void sendToActiveMQ(final Command command) {
        assert this.lock.isHeldByCurrentThread();
        final TransportListener l = this.transportListener;
        if (l != null) {
            l.onCommand(command);
        }
    }
    
    @Override
    public void sendToAmqp(final Object command) throws IOException {
        assert this.lock.isHeldByCurrentThread();
        if (this.trace) {
            AmqpTransportFilter.TRACE_BYTES.trace("Sending: \n{}", command);
        }
        final Transport n = this.next;
        if (n != null) {
            n.oneway(command);
        }
    }
    
    @Override
    public X509Certificate[] getPeerCertificates() {
        if (this.next instanceof SslTransport) {
            final X509Certificate[] peerCerts = ((SslTransport)this.next).getPeerCertificates();
            if (this.trace && peerCerts != null) {
                AmqpTransportFilter.LOG.debug("Peer Identity has been verified\n");
            }
            return peerCerts;
        }
        return null;
    }
    
    @Override
    public boolean isTrace() {
        return this.trace;
    }
    
    public void setTrace(final boolean trace) {
        this.trace = trace;
        this.protocolConverter.updateTracer();
    }
    
    @Override
    public AmqpWireFormat getWireFormat() {
        return this.wireFormat;
    }
    
    public void handleException(final IOException e) {
        super.onException(e);
    }
    
    @Override
    public String getTransformer() {
        return this.transformer;
    }
    
    public void setTransformer(final String transformer) {
        this.transformer = transformer;
    }
    
    @Override
    public IAmqpProtocolConverter getProtocolConverter() {
        return this.protocolConverter;
    }
    
    @Override
    public void setProtocolConverter(final IAmqpProtocolConverter protocolConverter) {
        this.protocolConverter = protocolConverter;
    }
    
    static {
        LOG = LoggerFactory.getLogger(AmqpTransportFilter.class);
        TRACE_BYTES = LoggerFactory.getLogger(AmqpTransportFilter.class.getPackage().getName() + ".BYTES");
        TRACE_FRAMES = LoggerFactory.getLogger(AmqpTransportFilter.class.getPackage().getName() + ".FRAMES");
    }
}
