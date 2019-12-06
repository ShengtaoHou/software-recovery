// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.virtual;

import org.apache.activemq.broker.Broker;
import java.util.Iterator;
import org.apache.activemq.filter.MessageEvaluationContext;
import org.apache.activemq.broker.BrokerService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.CountDownLatch;
import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.filter.NonCachedMessageEvaluationContext;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.LinkedList;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.broker.region.Destination;
import java.util.Collection;
import org.apache.activemq.broker.region.DestinationFilter;

public class CompositeDestinationFilter extends DestinationFilter
{
    private Collection forwardDestinations;
    private boolean forwardOnly;
    private boolean copyMessage;
    private boolean concurrentSend;
    
    public CompositeDestinationFilter(final Destination next, final Collection forwardDestinations, final boolean forwardOnly, final boolean copyMessage, final boolean concurrentSend) {
        super(next);
        this.concurrentSend = false;
        this.forwardDestinations = forwardDestinations;
        this.forwardOnly = forwardOnly;
        this.copyMessage = copyMessage;
        this.concurrentSend = concurrentSend;
    }
    
    @Override
    public void send(final ProducerBrokerExchange context, final Message message) throws Exception {
        MessageEvaluationContext messageContext = null;
        final Collection<ActiveMQDestination> matchingDestinations = new LinkedList<ActiveMQDestination>();
        final Iterator iter = this.forwardDestinations.iterator();
        while (iter.hasNext()) {
            ActiveMQDestination destination = null;
            final Object value = iter.next();
            if (value instanceof FilteredDestination) {
                final FilteredDestination filteredDestination = (FilteredDestination)value;
                if (messageContext == null) {
                    messageContext = new NonCachedMessageEvaluationContext();
                    messageContext.setMessageReference(message);
                }
                messageContext.setDestination(filteredDestination.getDestination());
                if (filteredDestination.matches(messageContext)) {
                    destination = filteredDestination.getDestination();
                }
            }
            else if (value instanceof ActiveMQDestination) {
                destination = (ActiveMQDestination)value;
            }
            if (destination == null) {
                continue;
            }
            matchingDestinations.add(destination);
        }
        final CountDownLatch concurrent = new CountDownLatch(this.concurrentSend ? matchingDestinations.size() : 0);
        final AtomicReference<Exception> exceptionAtomicReference = new AtomicReference<Exception>();
        final BrokerService brokerService = context.getConnectionContext().getBroker().getBrokerService();
        for (final ActiveMQDestination destination2 : matchingDestinations) {
            if (concurrent.getCount() > 0L) {
                brokerService.getTaskRunnerFactory().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (exceptionAtomicReference.get() == null) {
                                CompositeDestinationFilter.this.doForward(context.copy(), message, brokerService.getRegionBroker(), destination2);
                            }
                        }
                        catch (Exception e) {
                            exceptionAtomicReference.set(e);
                        }
                        finally {
                            concurrent.countDown();
                        }
                    }
                });
            }
            else {
                this.doForward(context, message, brokerService.getRegionBroker(), destination2);
            }
        }
        if (!this.forwardOnly) {
            super.send(context, message);
        }
        concurrent.await();
        if (exceptionAtomicReference.get() != null) {
            throw exceptionAtomicReference.get();
        }
    }
    
    private void doForward(final ProducerBrokerExchange context, final Message message, final Broker regionBroker, final ActiveMQDestination destination) throws Exception {
        Message forwarded_message;
        if (this.copyMessage) {
            forwarded_message = message.copy();
            forwarded_message.setDestination(destination);
        }
        else {
            forwarded_message = message;
        }
        context.setMutable(true);
        regionBroker.send(context, forwarded_message);
    }
}
