// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.ws;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import org.apache.activemq.command.Command;
import org.apache.activemq.util.ServiceStopper;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.transport.stomp.StompFrame;
import org.apache.activemq.wireformat.WireFormat;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.broker.BrokerContext;
import org.apache.activemq.transport.stomp.StompInactivityMonitor;
import java.util.concurrent.CountDownLatch;
import org.apache.activemq.transport.stomp.StompWireFormat;
import org.apache.activemq.transport.stomp.ProtocolConverter;
import org.slf4j.Logger;
import org.apache.activemq.transport.stomp.StompTransport;
import org.eclipse.jetty.websocket.WebSocket;
import org.apache.activemq.transport.TransportSupport;

class StompSocket extends TransportSupport implements WebSocket.OnTextMessage, StompTransport
{
    private static final Logger LOG;
    WebSocket.Connection outbound;
    ProtocolConverter protocolConverter;
    StompWireFormat wireFormat;
    private final CountDownLatch socketTransportStarted;
    private StompInactivityMonitor stompInactivityMonitor;
    
    StompSocket() {
        this.protocolConverter = new ProtocolConverter(this, null);
        this.wireFormat = new StompWireFormat();
        this.socketTransportStarted = new CountDownLatch(1);
        this.stompInactivityMonitor = new StompInactivityMonitor(this, this.wireFormat);
    }
    
    public void onOpen(final WebSocket.Connection connection) {
        this.outbound = connection;
    }
    
    public void onClose(final int closeCode, final String message) {
        try {
            this.protocolConverter.onStompCommand(new StompFrame("DISCONNECT"));
        }
        catch (Exception e) {
            StompSocket.LOG.warn("Failed to close WebSocket", e);
        }
    }
    
    public void onMessage(final String data) {
        if (!this.transportStartedAtLeastOnce()) {
            StompSocket.LOG.debug("Waiting for StompSocket to be properly started...");
            try {
                this.socketTransportStarted.await();
            }
            catch (InterruptedException e2) {
                StompSocket.LOG.warn("While waiting for StompSocket to be properly started, we got interrupted!! Should be okay, but you could see race conditions...");
            }
        }
        try {
            this.protocolConverter.onStompCommand((StompFrame)this.wireFormat.unmarshal(new ByteSequence(data.getBytes("UTF-8"))));
        }
        catch (Exception e) {
            this.onException(IOExceptionSupport.create(e));
        }
    }
    
    private boolean transportStartedAtLeastOnce() {
        return this.socketTransportStarted.getCount() == 0L;
    }
    
    protected void doStart() throws Exception {
        this.socketTransportStarted.countDown();
    }
    
    protected void doStop(final ServiceStopper stopper) throws Exception {
    }
    
    public int getReceiveCounter() {
        return 0;
    }
    
    public String getRemoteAddress() {
        return "StompSocket_" + this.hashCode();
    }
    
    public void oneway(final Object command) throws IOException {
        try {
            this.protocolConverter.onActiveMQCommand((Command)command);
        }
        catch (Exception e) {
            this.onException(IOExceptionSupport.create(e));
        }
    }
    
    public void sendToActiveMQ(final Command command) {
        this.doConsume(command);
    }
    
    public void sendToStomp(final StompFrame command) throws IOException {
        this.outbound.sendMessage(command.format());
    }
    
    public StompInactivityMonitor getInactivityMonitor() {
        return this.stompInactivityMonitor;
    }
    
    public StompWireFormat getWireFormat() {
        return this.wireFormat;
    }
    
    static {
        LOG = LoggerFactory.getLogger(StompSocket.class);
    }
}
