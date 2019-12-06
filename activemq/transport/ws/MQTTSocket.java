// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.ws;

import org.slf4j.LoggerFactory;
import org.apache.activemq.transport.mqtt.MQTTInactivityMonitor;
import java.security.cert.X509Certificate;
import java.io.IOException;
import org.apache.activemq.command.Command;
import org.apache.activemq.util.ServiceStopper;
import org.fusesource.mqtt.codec.DISCONNECT;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.util.ByteSequence;
import org.fusesource.mqtt.codec.MQTTFrame;
import org.apache.activemq.broker.BrokerService;
import java.util.concurrent.CountDownLatch;
import org.apache.activemq.transport.mqtt.MQTTWireFormat;
import org.apache.activemq.transport.mqtt.MQTTProtocolConverter;
import org.slf4j.Logger;
import org.apache.activemq.broker.BrokerServiceAware;
import org.apache.activemq.transport.mqtt.MQTTTransport;
import org.eclipse.jetty.websocket.WebSocket;
import org.apache.activemq.transport.TransportSupport;

public class MQTTSocket extends TransportSupport implements WebSocket.OnBinaryMessage, MQTTTransport, BrokerServiceAware
{
    private static final Logger LOG;
    WebSocket.Connection outbound;
    MQTTProtocolConverter protocolConverter;
    MQTTWireFormat wireFormat;
    private final CountDownLatch socketTransportStarted;
    private BrokerService brokerService;
    
    public MQTTSocket() {
        this.protocolConverter = null;
        this.wireFormat = new MQTTWireFormat();
        this.socketTransportStarted = new CountDownLatch(1);
    }
    
    public void onMessage(final byte[] bytes, final int offset, final int length) {
        if (!this.transportStartedAtLeastOnce()) {
            MQTTSocket.LOG.debug("Waiting for StompSocket to be properly started...");
            try {
                this.socketTransportStarted.await();
            }
            catch (InterruptedException e2) {
                MQTTSocket.LOG.warn("While waiting for StompSocket to be properly started, we got interrupted!! Should be okay, but you could see race conditions...");
            }
        }
        try {
            final MQTTFrame frame = (MQTTFrame)this.wireFormat.unmarshal(new ByteSequence(bytes, offset, length));
            this.getProtocolConverter().onMQTTCommand(frame);
        }
        catch (Exception e) {
            this.onException(IOExceptionSupport.create(e));
        }
    }
    
    private MQTTProtocolConverter getProtocolConverter() {
        if (this.protocolConverter == null) {
            this.protocolConverter = new MQTTProtocolConverter(this, this.brokerService);
        }
        return this.protocolConverter;
    }
    
    public void onOpen(final WebSocket.Connection connection) {
        this.outbound = connection;
    }
    
    public void onClose(final int closeCode, final String message) {
        try {
            this.getProtocolConverter().onMQTTCommand(new DISCONNECT().encode());
        }
        catch (Exception e) {
            MQTTSocket.LOG.warn("Failed to close WebSocket", e);
        }
    }
    
    protected void doStart() throws Exception {
        this.socketTransportStarted.countDown();
    }
    
    protected void doStop(final ServiceStopper stopper) throws Exception {
    }
    
    private boolean transportStartedAtLeastOnce() {
        return this.socketTransportStarted.getCount() == 0L;
    }
    
    public int getReceiveCounter() {
        return 0;
    }
    
    public String getRemoteAddress() {
        return "MQTTSocket_" + this.hashCode();
    }
    
    public void oneway(final Object command) throws IOException {
        try {
            this.getProtocolConverter().onActiveMQCommand((Command)command);
        }
        catch (Exception e) {
            this.onException(IOExceptionSupport.create(e));
        }
    }
    
    public void sendToActiveMQ(final Command command) {
        this.doConsume(command);
    }
    
    public void sendToMQTT(final MQTTFrame command) throws IOException {
        final ByteSequence bytes = this.wireFormat.marshal(command);
        this.outbound.sendMessage(bytes.getData(), 0, bytes.getLength());
    }
    
    public X509Certificate[] getPeerCertificates() {
        return new X509Certificate[0];
    }
    
    public MQTTInactivityMonitor getInactivityMonitor() {
        return null;
    }
    
    public MQTTWireFormat getWireFormat() {
        return this.wireFormat;
    }
    
    public void setBrokerService(final BrokerService brokerService) {
        this.brokerService = brokerService;
    }
    
    static {
        LOG = LoggerFactory.getLogger(MQTTSocket.class);
    }
}
