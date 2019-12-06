// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

import org.apache.activemq.usage.MemoryUsage;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ProducerInfo;

public class CompositeDestinationBroker extends BrokerFilter
{
    public CompositeDestinationBroker(final Broker next) {
        super(next);
    }
    
    @Override
    public void addProducer(final ConnectionContext context, final ProducerInfo info) throws Exception {
        final ActiveMQDestination destination = info.getDestination();
        if (destination != null && destination.isComposite()) {
            final ActiveMQDestination[] destinations = destination.getCompositeDestinations();
            for (int i = 0; i < destinations.length; ++i) {
                final ProducerInfo copy = info.copy();
                copy.setDestination(destinations[i]);
                this.next.addProducer(context, copy);
            }
        }
        else {
            this.next.addProducer(context, info);
        }
    }
    
    @Override
    public void removeProducer(final ConnectionContext context, final ProducerInfo info) throws Exception {
        final ActiveMQDestination destination = info.getDestination();
        if (destination != null && destination.isComposite()) {
            final ActiveMQDestination[] destinations = destination.getCompositeDestinations();
            for (int i = 0; i < destinations.length; ++i) {
                final ProducerInfo copy = info.copy();
                copy.setDestination(destinations[i]);
                this.next.removeProducer(context, copy);
            }
        }
        else {
            this.next.removeProducer(context, info);
        }
    }
    
    @Override
    public void send(final ProducerBrokerExchange producerExchange, Message message) throws Exception {
        final ActiveMQDestination destination = message.getDestination();
        if (destination.isComposite()) {
            final ActiveMQDestination[] destinations = destination.getCompositeDestinations();
            for (int i = 0; i < destinations.length; ++i) {
                if (i != 0) {
                    message = message.copy();
                    message.setMemoryUsage(null);
                }
                message.setOriginalDestination(destination);
                message.setDestination(destinations[i]);
                this.next.send(producerExchange, message);
            }
        }
        else {
            this.next.send(producerExchange, message);
        }
    }
}
