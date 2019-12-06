// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.virtual;

import java.util.Arrays;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.broker.region.DestinationFilter;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.Broker;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.filter.DestinationMap;
import org.apache.activemq.broker.region.DestinationInterceptor;

public class VirtualDestinationInterceptor implements DestinationInterceptor
{
    private DestinationMap destinationMap;
    private VirtualDestination[] virtualDestinations;
    
    public VirtualDestinationInterceptor() {
        this.destinationMap = new DestinationMap();
    }
    
    @Override
    public Destination intercept(final Destination destination) {
        final Set matchingDestinations = this.destinationMap.get(destination.getActiveMQDestination());
        final List<Destination> destinations = new ArrayList<Destination>();
        for (final VirtualDestination virtualDestination : matchingDestinations) {
            final Destination newDestination = virtualDestination.intercept(destination);
            destinations.add(newDestination);
        }
        if (destinations.isEmpty()) {
            return destination;
        }
        if (destinations.size() == 1) {
            return destinations.get(0);
        }
        return this.createCompositeDestination(destination, destinations);
    }
    
    @Override
    public synchronized void create(final Broker broker, final ConnectionContext context, final ActiveMQDestination destination) throws Exception {
        for (final VirtualDestination virt : this.virtualDestinations) {
            virt.create(broker, context, destination);
        }
    }
    
    @Override
    public synchronized void remove(final Destination destination) {
    }
    
    public VirtualDestination[] getVirtualDestinations() {
        return this.virtualDestinations;
    }
    
    public void setVirtualDestinations(final VirtualDestination[] virtualDestinations) {
        this.destinationMap = new DestinationMap();
        this.virtualDestinations = virtualDestinations;
        for (int i = 0; i < virtualDestinations.length; ++i) {
            final VirtualDestination virtualDestination = virtualDestinations[i];
            this.destinationMap.put(virtualDestination.getVirtualDestination(), virtualDestination);
        }
    }
    
    protected Destination createCompositeDestination(final Destination destination, final List<Destination> destinations) {
        return new DestinationFilter(destination) {
            @Override
            public void send(final ProducerBrokerExchange context, final Message messageSend) throws Exception {
                for (final Destination destination : destinations) {
                    destination.send(context, messageSend);
                }
            }
        };
    }
    
    @Override
    public String toString() {
        return "VirtualDestinationInterceptor" + Arrays.asList(this.virtualDestinations);
    }
}
