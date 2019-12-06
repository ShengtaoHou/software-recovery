// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.stomp;

import org.slf4j.LoggerFactory;
import org.apache.activemq.transport.TransportListener;
import java.io.IOException;
import javax.jms.JMSException;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.command.Command;
import org.apache.activemq.broker.BrokerContext;
import org.apache.activemq.wireformat.WireFormat;
import org.apache.activemq.transport.Transport;
import org.slf4j.Logger;
import org.apache.activemq.transport.TransportFilter;

public class StompTransportFilter extends TransportFilter implements StompTransport
{
    private static final Logger TRACE;
    private final ProtocolConverter protocolConverter;
    private StompInactivityMonitor monitor;
    private StompWireFormat wireFormat;
    private boolean trace;
    
    public StompTransportFilter(final Transport next, final WireFormat wireFormat, final BrokerContext brokerContext) {
        super(next);
        this.protocolConverter = new ProtocolConverter(this, brokerContext);
        if (wireFormat instanceof StompWireFormat) {
            this.wireFormat = (StompWireFormat)wireFormat;
        }
    }
    
    @Override
    public void oneway(final Object o) throws IOException {
        try {
            final Command command = (Command)o;
            this.protocolConverter.onActiveMQCommand(command);
        }
        catch (JMSException e) {
            throw IOExceptionSupport.create(e);
        }
    }
    
    @Override
    public void onCommand(final Object command) {
        try {
            if (this.trace) {
                StompTransportFilter.TRACE.trace("Received: \n" + command);
            }
            this.protocolConverter.onStompCommand((StompFrame)command);
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
    public void sendToStomp(final StompFrame command) throws IOException {
        if (this.trace) {
            StompTransportFilter.TRACE.trace("Sending: \n" + command);
        }
        final Transport n = this.next;
        if (n != null) {
            n.oneway(command);
        }
    }
    
    public boolean isTrace() {
        return this.trace;
    }
    
    public void setTrace(final boolean trace) {
        this.trace = trace;
    }
    
    @Override
    public StompInactivityMonitor getInactivityMonitor() {
        return this.monitor;
    }
    
    public void setInactivityMonitor(final StompInactivityMonitor monitor) {
        this.monitor = monitor;
    }
    
    @Override
    public StompWireFormat getWireFormat() {
        return this.wireFormat;
    }
    
    public String getDefaultHeartBeat() {
        return (this.protocolConverter != null) ? this.protocolConverter.getDefaultHeartBeat() : null;
    }
    
    public void setDefaultHeartBeat(final String defaultHeartBeat) {
        this.protocolConverter.setDefaultHeartBeat(defaultHeartBeat);
    }
    
    public float getHbGracePeriodMultiplier() {
        return (this.protocolConverter != null) ? Float.valueOf(this.protocolConverter.getHbGracePeriodMultiplier()) : null;
    }
    
    public void setHbGracePeriodMultiplier(final float hbGracePeriodMultiplier) {
        if (hbGracePeriodMultiplier > 1.0f) {
            this.protocolConverter.setHbGracePeriodMultiplier(hbGracePeriodMultiplier);
        }
    }
    
    static {
        TRACE = LoggerFactory.getLogger(StompTransportFilter.class.getPackage().getName() + ".StompIO");
    }
}
