// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.util;

import org.slf4j.LoggerFactory;
import java.net.UnknownHostException;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import org.apache.activemq.openwire.OpenWireFormatFactory;
import org.apache.activemq.command.RemoveSubscriptionInfo;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.MessageDispatchNotification;
import org.apache.activemq.command.MessageDispatch;
import org.apache.activemq.command.Response;
import org.apache.activemq.command.MessagePull;
import org.apache.activemq.command.TransactionInfo;
import org.apache.activemq.command.TransactionId;
import org.apache.activemq.command.SessionInfo;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.command.DestinationInfo;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.broker.ConsumerBrokerExchange;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.util.ByteSequence;
import java.net.DatagramPacket;
import java.io.DataOutput;
import java.io.OutputStream;
import java.io.DataOutputStream;
import org.apache.activemq.util.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.activemq.command.DataStructure;
import org.apache.activemq.command.JournalTrace;
import java.net.URISyntaxException;
import java.net.SocketAddress;
import org.apache.activemq.command.BrokerId;
import java.net.DatagramSocket;
import java.net.URI;
import org.apache.activemq.wireformat.WireFormatFactory;
import org.apache.activemq.wireformat.WireFormat;
import org.slf4j.Logger;
import org.apache.activemq.broker.BrokerPluginSupport;

public class UDPTraceBrokerPlugin extends BrokerPluginSupport
{
    private static final Logger LOG;
    protected WireFormat wireFormat;
    protected WireFormatFactory wireFormatFactory;
    protected int maxTraceDatagramSize;
    protected URI destination;
    protected DatagramSocket socket;
    protected BrokerId brokerId;
    protected SocketAddress address;
    protected boolean broadcast;
    
    public UDPTraceBrokerPlugin() {
        this.maxTraceDatagramSize = 4096;
        try {
            this.destination = new URI("udp://127.0.0.1:61616");
        }
        catch (URISyntaxException ex) {}
    }
    
    @Override
    public void start() throws Exception {
        super.start();
        if (this.getWireFormat() == null) {
            throw new IllegalArgumentException("Wireformat must be specifed.");
        }
        if (this.address == null) {
            this.address = this.createSocketAddress(this.destination);
        }
        this.socket = this.createSocket();
        this.brokerId = super.getBrokerId();
        this.trace(new JournalTrace("START"));
    }
    
    protected DatagramSocket createSocket() throws IOException {
        final DatagramSocket s = new DatagramSocket();
        s.setSendBufferSize(this.maxTraceDatagramSize);
        s.setBroadcast(this.broadcast);
        return s;
    }
    
    @Override
    public void stop() throws Exception {
        this.trace(new JournalTrace("STOP"));
        this.socket.close();
        super.stop();
    }
    
    private void trace(final DataStructure command) {
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream(this.maxTraceDatagramSize);
            final DataOutputStream out = new DataOutputStream(baos);
            this.wireFormat.marshal(this.brokerId, out);
            this.wireFormat.marshal(command, out);
            out.close();
            final ByteSequence sequence = baos.toByteSequence();
            final DatagramPacket datagram = new DatagramPacket(sequence.getData(), sequence.getOffset(), sequence.getLength(), this.address);
            this.socket.send(datagram);
        }
        catch (Throwable e) {
            UDPTraceBrokerPlugin.LOG.debug("Failed to trace: {}", command, e);
        }
    }
    
    @Override
    public void send(final ProducerBrokerExchange producerExchange, final Message messageSend) throws Exception {
        this.trace(messageSend);
        super.send(producerExchange, messageSend);
    }
    
    @Override
    public void acknowledge(final ConsumerBrokerExchange consumerExchange, final MessageAck ack) throws Exception {
        this.trace(ack);
        super.acknowledge(consumerExchange, ack);
    }
    
    @Override
    public void addConnection(final ConnectionContext context, final ConnectionInfo info) throws Exception {
        this.trace(info);
        super.addConnection(context, info);
    }
    
    @Override
    public Subscription addConsumer(final ConnectionContext context, final ConsumerInfo info) throws Exception {
        this.trace(info);
        return super.addConsumer(context, info);
    }
    
    @Override
    public void addDestinationInfo(final ConnectionContext context, final DestinationInfo info) throws Exception {
        this.trace(info);
        super.addDestinationInfo(context, info);
    }
    
    @Override
    public void addProducer(final ConnectionContext context, final ProducerInfo info) throws Exception {
        this.trace(info);
        super.addProducer(context, info);
    }
    
    @Override
    public void addSession(final ConnectionContext context, final SessionInfo info) throws Exception {
        this.trace(info);
        super.addSession(context, info);
    }
    
    @Override
    public void beginTransaction(final ConnectionContext context, final TransactionId xid) throws Exception {
        this.trace(new TransactionInfo(context.getConnectionId(), xid, (byte)0));
        super.beginTransaction(context, xid);
    }
    
    @Override
    public void commitTransaction(final ConnectionContext context, final TransactionId xid, final boolean onePhase) throws Exception {
        this.trace(new TransactionInfo(context.getConnectionId(), xid, (byte)(onePhase ? 2 : 3)));
        super.commitTransaction(context, xid, onePhase);
    }
    
    @Override
    public void forgetTransaction(final ConnectionContext context, final TransactionId xid) throws Exception {
        this.trace(new TransactionInfo(context.getConnectionId(), xid, (byte)6));
        super.forgetTransaction(context, xid);
    }
    
    @Override
    public Response messagePull(final ConnectionContext context, final MessagePull pull) throws Exception {
        this.trace(pull);
        return super.messagePull(context, pull);
    }
    
    @Override
    public int prepareTransaction(final ConnectionContext context, final TransactionId xid) throws Exception {
        this.trace(new TransactionInfo(context.getConnectionId(), xid, (byte)1));
        return super.prepareTransaction(context, xid);
    }
    
    @Override
    public void postProcessDispatch(final MessageDispatch messageDispatch) {
        this.trace(messageDispatch);
        super.postProcessDispatch(messageDispatch);
    }
    
    @Override
    public void processDispatchNotification(final MessageDispatchNotification messageDispatchNotification) throws Exception {
        this.trace(messageDispatchNotification);
        super.processDispatchNotification(messageDispatchNotification);
    }
    
    @Override
    public void removeConnection(final ConnectionContext context, final ConnectionInfo info, final Throwable error) throws Exception {
        this.trace(info.createRemoveCommand());
        super.removeConnection(context, info, error);
    }
    
    @Override
    public void removeConsumer(final ConnectionContext context, final ConsumerInfo info) throws Exception {
        this.trace(info.createRemoveCommand());
        super.removeConsumer(context, info);
    }
    
    @Override
    public void removeDestination(final ConnectionContext context, final ActiveMQDestination destination, final long timeout) throws Exception {
        super.removeDestination(context, destination, timeout);
    }
    
    @Override
    public void removeDestinationInfo(final ConnectionContext context, final DestinationInfo info) throws Exception {
        this.trace(info);
        super.removeDestinationInfo(context, info);
    }
    
    @Override
    public void removeProducer(final ConnectionContext context, final ProducerInfo info) throws Exception {
        this.trace(info.createRemoveCommand());
        super.removeProducer(context, info);
    }
    
    @Override
    public void removeSession(final ConnectionContext context, final SessionInfo info) throws Exception {
        this.trace(info.createRemoveCommand());
        super.removeSession(context, info);
    }
    
    @Override
    public void removeSubscription(final ConnectionContext context, final RemoveSubscriptionInfo info) throws Exception {
        this.trace(info);
        super.removeSubscription(context, info);
    }
    
    @Override
    public void rollbackTransaction(final ConnectionContext context, final TransactionId xid) throws Exception {
        this.trace(new TransactionInfo(context.getConnectionId(), xid, (byte)4));
        super.rollbackTransaction(context, xid);
    }
    
    public WireFormat getWireFormat() {
        if (this.wireFormat == null) {
            this.wireFormat = this.createWireFormat();
        }
        return this.wireFormat;
    }
    
    protected WireFormat createWireFormat() {
        return this.getWireFormatFactory().createWireFormat();
    }
    
    public void setWireFormat(final WireFormat wireFormat) {
        this.wireFormat = wireFormat;
    }
    
    public WireFormatFactory getWireFormatFactory() {
        if (this.wireFormatFactory == null) {
            this.wireFormatFactory = this.createWireFormatFactory();
        }
        return this.wireFormatFactory;
    }
    
    protected OpenWireFormatFactory createWireFormatFactory() {
        final OpenWireFormatFactory wf = new OpenWireFormatFactory();
        wf.setCacheEnabled(false);
        wf.setVersion(1);
        wf.setTightEncodingEnabled(true);
        wf.setSizePrefixDisabled(true);
        return wf;
    }
    
    public void setWireFormatFactory(final WireFormatFactory wireFormatFactory) {
        this.wireFormatFactory = wireFormatFactory;
    }
    
    protected SocketAddress createSocketAddress(final URI location) throws UnknownHostException {
        final InetAddress a = InetAddress.getByName(location.getHost());
        final int port = location.getPort();
        return new InetSocketAddress(a, port);
    }
    
    public URI getDestination() {
        return this.destination;
    }
    
    public void setDestination(final URI destination) {
        this.destination = destination;
    }
    
    public int getMaxTraceDatagramSize() {
        return this.maxTraceDatagramSize;
    }
    
    public void setMaxTraceDatagramSize(final int maxTraceDatagramSize) {
        this.maxTraceDatagramSize = maxTraceDatagramSize;
    }
    
    public boolean isBroadcast() {
        return this.broadcast;
    }
    
    public void setBroadcast(final boolean broadcast) {
        this.broadcast = broadcast;
    }
    
    public SocketAddress getAddress() {
        return this.address;
    }
    
    public void setAddress(final SocketAddress address) {
        this.address = address;
    }
    
    static {
        LOG = LoggerFactory.getLogger(UDPTraceBrokerPlugin.class);
    }
}
