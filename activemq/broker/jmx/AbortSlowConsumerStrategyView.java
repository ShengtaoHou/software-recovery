// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import org.slf4j.LoggerFactory;
import javax.management.ObjectName;
import javax.management.openmbean.OpenDataException;
import java.util.Iterator;
import javax.management.openmbean.CompositeType;
import org.apache.activemq.broker.region.Subscription;
import java.util.Map;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;
import org.apache.activemq.broker.region.policy.SlowConsumerEntry;
import javax.management.openmbean.TabularData;
import org.apache.activemq.broker.region.policy.AbortSlowConsumerStrategy;
import org.slf4j.Logger;

public class AbortSlowConsumerStrategyView implements AbortSlowConsumerStrategyViewMBean
{
    private static final Logger LOG;
    private ManagedRegionBroker broker;
    private AbortSlowConsumerStrategy strategy;
    
    public AbortSlowConsumerStrategyView(final ManagedRegionBroker managedRegionBroker, final AbortSlowConsumerStrategy slowConsumerStrategy) {
        this.broker = managedRegionBroker;
        this.strategy = slowConsumerStrategy;
    }
    
    @Override
    public long getMaxSlowCount() {
        return this.strategy.getMaxSlowCount();
    }
    
    @Override
    public void setMaxSlowCount(final long maxSlowCount) {
        this.strategy.setMaxSlowCount(maxSlowCount);
    }
    
    @Override
    public long getMaxSlowDuration() {
        return this.strategy.getMaxSlowDuration();
    }
    
    @Override
    public void setMaxSlowDuration(final long maxSlowDuration) {
        this.strategy.setMaxSlowDuration(maxSlowDuration);
    }
    
    @Override
    public long getCheckPeriod() {
        return this.strategy.getCheckPeriod();
    }
    
    @Override
    public TabularData getSlowConsumers() throws OpenDataException {
        final OpenTypeSupport.OpenTypeFactory factory = OpenTypeSupport.getFactory(SlowConsumerEntry.class);
        final CompositeType ct = factory.getCompositeType();
        final TabularType tt = new TabularType("SlowConsumers", "Table of current slow Consumers", ct, new String[] { "subscription" });
        final TabularDataSupport rc = new TabularDataSupport(tt);
        final int index = 0;
        final Map<Subscription, SlowConsumerEntry> slowConsumers = this.strategy.getSlowConsumers();
        for (final Map.Entry<Subscription, SlowConsumerEntry> entry : slowConsumers.entrySet()) {
            entry.getValue().setSubscription(this.broker.getSubscriberObjectName(entry.getKey()));
            rc.put(OpenTypeSupport.convert(entry.getValue()));
        }
        return rc;
    }
    
    @Override
    public void abortConsumer(final ObjectName consumerToAbort) {
        final Subscription sub = this.broker.getSubscriber(consumerToAbort);
        if (sub != null) {
            AbortSlowConsumerStrategyView.LOG.info("aborting consumer via jmx: {}", sub.getConsumerInfo().getConsumerId());
            this.strategy.abortConsumer(sub, false);
        }
        else {
            AbortSlowConsumerStrategyView.LOG.warn("cannot resolve subscription matching name: {}", consumerToAbort);
        }
    }
    
    @Override
    public void abortConnection(final ObjectName consumerToAbort) {
        final Subscription sub = this.broker.getSubscriber(consumerToAbort);
        if (sub != null) {
            AbortSlowConsumerStrategyView.LOG.info("aborting consumer connection via jmx: {}", sub.getConsumerInfo().getConsumerId().getConnectionId());
            this.strategy.abortConsumer(sub, true);
        }
        else {
            AbortSlowConsumerStrategyView.LOG.warn("cannot resolve subscription matching name: {}", consumerToAbort);
        }
    }
    
    @Override
    public void abortConsumer(final String objectNameOfConsumerToAbort) {
        this.abortConsumer(this.toObjectName(objectNameOfConsumerToAbort));
    }
    
    @Override
    public void abortConnection(final String objectNameOfConsumerToAbort) {
        this.abortConnection(this.toObjectName(objectNameOfConsumerToAbort));
    }
    
    private ObjectName toObjectName(final String objectName) {
        ObjectName result = null;
        try {
            result = new ObjectName(objectName);
        }
        catch (Exception e) {
            AbortSlowConsumerStrategyView.LOG.warn("cannot create subscription ObjectName to abort, from string: {}", objectName);
        }
        return result;
    }
    
    static {
        LOG = LoggerFactory.getLogger(AbortSlowConsumerStrategyView.class);
    }
}
