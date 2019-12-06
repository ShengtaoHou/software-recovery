// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.view;

import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.command.ActiveMQTempQueue;
import org.apache.activemq.command.ActiveMQTempTopic;
import org.apache.activemq.command.ActiveMQQueue;
import java.util.Iterator;
import org.apache.activemq.command.ActiveMQTopic;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.activemq.broker.BrokerRegistry;
import org.apache.activemq.util.LRUCache;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.Map;
import org.apache.activemq.broker.BrokerService;

public class MessageBrokerView
{
    private final BrokerService brokerService;
    private Map<ActiveMQDestination, BrokerDestinationView> destinationViewMap;
    
    public MessageBrokerView(final BrokerService brokerService) {
        this.destinationViewMap = new LRUCache<ActiveMQDestination, BrokerDestinationView>();
        this.brokerService = brokerService;
        if (brokerService == null) {
            throw new NullPointerException("BrokerService is null");
        }
        if (!brokerService.isStarted()) {
            throw new IllegalStateException("BrokerService " + brokerService.getBrokerName() + " is not started");
        }
    }
    
    public MessageBrokerView(final String brokerName) {
        this.destinationViewMap = new LRUCache<ActiveMQDestination, BrokerDestinationView>();
        this.brokerService = BrokerRegistry.getInstance().lookup(brokerName);
        if (this.brokerService == null) {
            throw new NullPointerException("BrokerService is null");
        }
        if (!this.brokerService.isStarted()) {
            throw new IllegalStateException("BrokerService " + this.brokerService.getBrokerName() + " is not started");
        }
    }
    
    public String getBrokerName() {
        return this.brokerService.getBrokerName();
    }
    
    public String getBrokerId() {
        try {
            return this.brokerService.getBroker().getBrokerId().toString();
        }
        catch (Exception e) {
            return "";
        }
    }
    
    public int getMemoryPercentUsage() {
        return this.brokerService.getSystemUsage().getMemoryUsage().getPercentUsage();
    }
    
    public int getStorePercentUsage() {
        return this.brokerService.getSystemUsage().getStoreUsage().getPercentUsage();
    }
    
    public int getTempPercentUsage() {
        return this.brokerService.getSystemUsage().getTempUsage().getPercentUsage();
    }
    
    public int getJobSchedulerStorePercentUsage() {
        return this.brokerService.getSystemUsage().getJobSchedulerUsage().getPercentUsage();
    }
    
    public boolean isPersistent() {
        return this.brokerService.isPersistent();
    }
    
    public BrokerService getBrokerService() {
        return this.brokerService;
    }
    
    public Set<ActiveMQDestination> getDestinations() {
        Set<ActiveMQDestination> result;
        try {
            final ActiveMQDestination[] destinations = this.brokerService.getBroker().getDestinations();
            result = new HashSet<ActiveMQDestination>();
            Collections.addAll(result, destinations);
        }
        catch (Exception e) {
            result = Collections.emptySet();
        }
        return result;
    }
    
    public Set<ActiveMQTopic> getTopics() {
        final Set<ActiveMQTopic> result = new HashSet<ActiveMQTopic>();
        for (final ActiveMQDestination destination : this.getDestinations()) {
            if (destination.isTopic() && !destination.isTemporary()) {
                result.add((ActiveMQTopic)destination);
            }
        }
        return result;
    }
    
    public Set<ActiveMQQueue> getQueues() {
        final Set<ActiveMQQueue> result = new HashSet<ActiveMQQueue>();
        for (final ActiveMQDestination destination : this.getDestinations()) {
            if (destination.isQueue() && !destination.isTemporary()) {
                result.add((ActiveMQQueue)destination);
            }
        }
        return result;
    }
    
    public Set<ActiveMQTempTopic> getTempTopics() {
        final Set<ActiveMQTempTopic> result = new HashSet<ActiveMQTempTopic>();
        for (final ActiveMQDestination destination : this.getDestinations()) {
            if (destination.isTopic() && destination.isTemporary()) {
                result.add((ActiveMQTempTopic)destination);
            }
        }
        return result;
    }
    
    public Set<ActiveMQTempQueue> getTempQueues() {
        final Set<ActiveMQTempQueue> result = new HashSet<ActiveMQTempQueue>();
        for (final ActiveMQDestination destination : this.getDestinations()) {
            if (destination.isTopic() && destination.isTemporary()) {
                result.add((ActiveMQTempQueue)destination);
            }
        }
        return result;
    }
    
    public BrokerDestinationView getDestinationView(final String destinationName) throws Exception {
        return this.getDestinationView(destinationName, (byte)1);
    }
    
    public BrokerDestinationView getTopicDestinationView(final String destinationName) throws Exception {
        return this.getDestinationView(destinationName, (byte)2);
    }
    
    public BrokerDestinationView getQueueDestinationView(final String destinationName) throws Exception {
        return this.getDestinationView(destinationName, (byte)1);
    }
    
    public BrokerDestinationView getDestinationView(final String destinationName, final byte type) throws Exception {
        final ActiveMQDestination activeMQDestination = ActiveMQDestination.createDestination(destinationName, type);
        return this.getDestinationView(activeMQDestination);
    }
    
    public BrokerDestinationView getDestinationView(final ActiveMQDestination activeMQDestination) throws Exception {
        BrokerDestinationView view = null;
        synchronized (this.destinationViewMap) {
            view = this.destinationViewMap.get(activeMQDestination);
            if (view == null) {
                final Destination destination = this.brokerService.getDestination(activeMQDestination);
                view = new BrokerDestinationView(destination);
                this.destinationViewMap.put(activeMQDestination, view);
            }
        }
        return view;
    }
}
