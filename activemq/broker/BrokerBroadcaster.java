// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

import java.util.ArrayList;
import java.util.List;
import org.apache.activemq.command.BrokerInfo;
import org.apache.activemq.command.SessionInfo;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.RemoveSubscriptionInfo;
import org.apache.activemq.command.TransactionId;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.command.MessageAck;

public class BrokerBroadcaster extends BrokerFilter
{
    protected volatile Broker[] listeners;
    
    public BrokerBroadcaster(final Broker next) {
        super(next);
        this.listeners = new Broker[0];
    }
    
    @Override
    public void acknowledge(final ConsumerBrokerExchange consumerExchange, final MessageAck ack) throws Exception {
        this.next.acknowledge(consumerExchange, ack);
        final Broker[] brokers = this.getListeners();
        for (int i = 0; i < brokers.length; ++i) {
            brokers[i].acknowledge(consumerExchange, ack);
        }
    }
    
    @Override
    public void addConnection(final ConnectionContext context, final ConnectionInfo info) throws Exception {
        this.next.addConnection(context, info);
        final Broker[] brokers = this.getListeners();
        for (int i = 0; i < brokers.length; ++i) {
            brokers[i].addConnection(context, info);
        }
    }
    
    @Override
    public Subscription addConsumer(final ConnectionContext context, final ConsumerInfo info) throws Exception {
        final Subscription answer = this.next.addConsumer(context, info);
        final Broker[] brokers = this.getListeners();
        for (int i = 0; i < brokers.length; ++i) {
            brokers[i].addConsumer(context, info);
        }
        return answer;
    }
    
    @Override
    public void addProducer(final ConnectionContext context, final ProducerInfo info) throws Exception {
        this.next.addProducer(context, info);
        final Broker[] brokers = this.getListeners();
        for (int i = 0; i < brokers.length; ++i) {
            brokers[i].addProducer(context, info);
        }
    }
    
    @Override
    public void commitTransaction(final ConnectionContext context, final TransactionId xid, final boolean onePhase) throws Exception {
        this.next.commitTransaction(context, xid, onePhase);
        final Broker[] brokers = this.getListeners();
        for (int i = 0; i < brokers.length; ++i) {
            brokers[i].commitTransaction(context, xid, onePhase);
        }
    }
    
    @Override
    public void removeSubscription(final ConnectionContext context, final RemoveSubscriptionInfo info) throws Exception {
        this.next.removeSubscription(context, info);
        final Broker[] brokers = this.getListeners();
        for (int i = 0; i < brokers.length; ++i) {
            brokers[i].removeSubscription(context, info);
        }
    }
    
    @Override
    public int prepareTransaction(final ConnectionContext context, final TransactionId xid) throws Exception {
        final int result = this.next.prepareTransaction(context, xid);
        final Broker[] brokers = this.getListeners();
        for (int i = 0; i < brokers.length; ++i) {
            brokers[i].prepareTransaction(context, xid);
        }
        return result;
    }
    
    @Override
    public void removeConnection(final ConnectionContext context, final ConnectionInfo info, final Throwable error) throws Exception {
        this.next.removeConnection(context, info, error);
        final Broker[] brokers = this.getListeners();
        for (int i = 0; i < brokers.length; ++i) {
            brokers[i].removeConnection(context, info, error);
        }
    }
    
    @Override
    public void removeConsumer(final ConnectionContext context, final ConsumerInfo info) throws Exception {
        this.next.removeConsumer(context, info);
        final Broker[] brokers = this.getListeners();
        for (int i = 0; i < brokers.length; ++i) {
            brokers[i].removeConsumer(context, info);
        }
    }
    
    @Override
    public void removeProducer(final ConnectionContext context, final ProducerInfo info) throws Exception {
        this.next.removeProducer(context, info);
        final Broker[] brokers = this.getListeners();
        for (int i = 0; i < brokers.length; ++i) {
            brokers[i].removeProducer(context, info);
        }
    }
    
    @Override
    public void rollbackTransaction(final ConnectionContext context, final TransactionId xid) throws Exception {
        this.next.rollbackTransaction(context, xid);
        final Broker[] brokers = this.getListeners();
        for (int i = 0; i < brokers.length; ++i) {
            brokers[i].rollbackTransaction(context, xid);
        }
    }
    
    @Override
    public void send(final ProducerBrokerExchange producerExchange, final Message messageSend) throws Exception {
        this.next.send(producerExchange, messageSend);
        final Broker[] brokers = this.getListeners();
        for (int i = 0; i < brokers.length; ++i) {
            brokers[i].send(producerExchange, messageSend);
        }
    }
    
    @Override
    public void beginTransaction(final ConnectionContext context, final TransactionId xid) throws Exception {
        this.next.beginTransaction(context, xid);
        final Broker[] brokers = this.getListeners();
        for (int i = 0; i < brokers.length; ++i) {
            brokers[i].beginTransaction(context, xid);
        }
    }
    
    @Override
    public void forgetTransaction(final ConnectionContext context, final TransactionId transactionId) throws Exception {
        this.next.forgetTransaction(context, transactionId);
        final Broker[] brokers = this.getListeners();
        for (int i = 0; i < brokers.length; ++i) {
            brokers[i].forgetTransaction(context, transactionId);
        }
    }
    
    @Override
    public Destination addDestination(final ConnectionContext context, final ActiveMQDestination destination, final boolean createIfTemporary) throws Exception {
        final Destination result = this.next.addDestination(context, destination, createIfTemporary);
        final Broker[] brokers = this.getListeners();
        for (int i = 0; i < brokers.length; ++i) {
            brokers[i].addDestination(context, destination, createIfTemporary);
        }
        return result;
    }
    
    @Override
    public void removeDestination(final ConnectionContext context, final ActiveMQDestination destination, final long timeout) throws Exception {
        this.next.removeDestination(context, destination, timeout);
        final Broker[] brokers = this.getListeners();
        for (int i = 0; i < brokers.length; ++i) {
            brokers[i].removeDestination(context, destination, timeout);
        }
    }
    
    @Override
    public void start() throws Exception {
        this.next.start();
        final Broker[] brokers = this.getListeners();
        for (int i = 0; i < brokers.length; ++i) {
            brokers[i].start();
        }
    }
    
    @Override
    public void stop() throws Exception {
        this.next.stop();
        final Broker[] brokers = this.getListeners();
        for (int i = 0; i < brokers.length; ++i) {
            brokers[i].stop();
        }
    }
    
    @Override
    public void addSession(final ConnectionContext context, final SessionInfo info) throws Exception {
        this.next.addSession(context, info);
        final Broker[] brokers = this.getListeners();
        for (int i = 0; i < brokers.length; ++i) {
            brokers[i].addSession(context, info);
        }
    }
    
    @Override
    public void removeSession(final ConnectionContext context, final SessionInfo info) throws Exception {
        this.next.removeSession(context, info);
        final Broker[] brokers = this.getListeners();
        for (int i = 0; i < brokers.length; ++i) {
            brokers[i].removeSession(context, info);
        }
    }
    
    @Override
    public void gc() {
        this.next.gc();
        final Broker[] brokers = this.getListeners();
        for (int i = 0; i < brokers.length; ++i) {
            brokers[i].gc();
        }
    }
    
    @Override
    public void addBroker(final Connection connection, final BrokerInfo info) {
        this.next.addBroker(connection, info);
        final Broker[] brokers = this.getListeners();
        for (int i = 0; i < brokers.length; ++i) {
            brokers[i].addBroker(connection, info);
        }
    }
    
    protected Broker[] getListeners() {
        return this.listeners;
    }
    
    public synchronized void addListener(final Broker broker) {
        final List<Broker> tmp = this.getListenersAsList();
        tmp.add(broker);
        this.listeners = tmp.toArray(new Broker[tmp.size()]);
    }
    
    public synchronized void removeListener(final Broker broker) {
        final List<Broker> tmp = this.getListenersAsList();
        tmp.remove(broker);
        this.listeners = tmp.toArray(new Broker[tmp.size()]);
    }
    
    protected List<Broker> getListenersAsList() {
        final List<Broker> tmp = new ArrayList<Broker>();
        final Broker[] brokers = this.getListeners();
        for (int i = 0; i < brokers.length; ++i) {
            tmp.add(brokers[i]);
        }
        return tmp;
    }
}
