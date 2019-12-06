// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.util;

import org.apache.activemq.command.Response;
import org.apache.activemq.command.MessagePull;
import org.apache.activemq.command.ConsumerControl;
import org.apache.activemq.command.DestinationInfo;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.broker.ConsumerBrokerExchange;
import java.util.List;
import org.apache.activemq.filter.DestinationPath;
import java.util.StringTokenizer;
import java.util.ArrayList;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.BrokerPluginSupport;

public class DestinationPathSeparatorBroker extends BrokerPluginSupport
{
    String pathSeparator;
    
    public DestinationPathSeparatorBroker() {
        this.pathSeparator = "/";
    }
    
    protected ActiveMQDestination convertDestination(final ActiveMQDestination destination) {
        if (destination != null && destination.getPhysicalName().contains(this.pathSeparator)) {
            final List<String> l = new ArrayList<String>();
            final StringTokenizer iter = new StringTokenizer(destination.getPhysicalName(), this.pathSeparator);
            while (iter.hasMoreTokens()) {
                final String name = iter.nextToken().trim();
                if (name.length() == 0) {
                    continue;
                }
                l.add(name);
            }
            final String newName = DestinationPath.toString(l.toArray(new String[l.size()]));
            return ActiveMQDestination.createDestination(newName, destination.getDestinationType());
        }
        return destination;
    }
    
    @Override
    public void acknowledge(final ConsumerBrokerExchange consumerExchange, final MessageAck ack) throws Exception {
        ack.setDestination(this.convertDestination(ack.getDestination()));
        super.acknowledge(consumerExchange, ack);
    }
    
    @Override
    public Subscription addConsumer(final ConnectionContext context, final ConsumerInfo info) throws Exception {
        info.setDestination(this.convertDestination(info.getDestination()));
        return super.addConsumer(context, info);
    }
    
    @Override
    public void addProducer(final ConnectionContext context, final ProducerInfo info) throws Exception {
        info.setDestination(this.convertDestination(info.getDestination()));
        super.addProducer(context, info);
    }
    
    @Override
    public void removeConsumer(final ConnectionContext context, final ConsumerInfo info) throws Exception {
        info.setDestination(this.convertDestination(info.getDestination()));
        super.removeConsumer(context, info);
    }
    
    @Override
    public void removeProducer(final ConnectionContext context, final ProducerInfo info) throws Exception {
        info.setDestination(this.convertDestination(info.getDestination()));
        super.removeProducer(context, info);
    }
    
    @Override
    public void send(final ProducerBrokerExchange producerExchange, final Message messageSend) throws Exception {
        messageSend.setDestination(this.convertDestination(messageSend.getDestination()));
        super.send(producerExchange, messageSend);
    }
    
    @Override
    public Destination addDestination(final ConnectionContext context, final ActiveMQDestination destination, final boolean createIfTemporary) throws Exception {
        return super.addDestination(context, this.convertDestination(destination), createIfTemporary);
    }
    
    @Override
    public void removeDestination(final ConnectionContext context, final ActiveMQDestination destination, final long timeout) throws Exception {
        super.removeDestination(context, this.convertDestination(destination), timeout);
    }
    
    @Override
    public void addDestinationInfo(final ConnectionContext context, final DestinationInfo info) throws Exception {
        info.setDestination(this.convertDestination(info.getDestination()));
        super.addDestinationInfo(context, info);
    }
    
    @Override
    public void removeDestinationInfo(final ConnectionContext context, final DestinationInfo info) throws Exception {
        info.setDestination(this.convertDestination(info.getDestination()));
        super.removeDestinationInfo(context, info);
    }
    
    @Override
    public void processConsumerControl(final ConsumerBrokerExchange consumerExchange, final ConsumerControl control) {
        control.setDestination(this.convertDestination(control.getDestination()));
        super.processConsumerControl(consumerExchange, control);
    }
    
    @Override
    public Response messagePull(final ConnectionContext context, final MessagePull pull) throws Exception {
        pull.setDestination(this.convertDestination(pull.getDestination()));
        return super.messagePull(context, pull);
    }
    
    public void setPathSeparator(final String pathSeparator) {
        this.pathSeparator = pathSeparator;
    }
}
