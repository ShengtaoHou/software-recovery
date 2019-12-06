// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

import java.util.concurrent.ThreadPoolExecutor;
import org.apache.activemq.thread.Scheduler;
import org.apache.activemq.usage.Usage;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.broker.region.MessageReference;
import java.net.URI;
import org.apache.activemq.store.PListStore;
import org.apache.activemq.command.DestinationInfo;
import java.util.Set;
import org.apache.activemq.command.MessageDispatch;
import org.apache.activemq.command.TransactionId;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.command.SessionInfo;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.command.BrokerInfo;
import org.apache.activemq.command.BrokerId;
import org.apache.activemq.Service;
import org.apache.activemq.broker.region.Region;

public interface Broker extends Region, Service
{
    Broker getAdaptor(final Class p0);
    
    BrokerId getBrokerId();
    
    String getBrokerName();
    
    void addBroker(final Connection p0, final BrokerInfo p1);
    
    void removeBroker(final Connection p0, final BrokerInfo p1);
    
    void addConnection(final ConnectionContext p0, final ConnectionInfo p1) throws Exception;
    
    void removeConnection(final ConnectionContext p0, final ConnectionInfo p1, final Throwable p2) throws Exception;
    
    void addSession(final ConnectionContext p0, final SessionInfo p1) throws Exception;
    
    void removeSession(final ConnectionContext p0, final SessionInfo p1) throws Exception;
    
    void addProducer(final ConnectionContext p0, final ProducerInfo p1) throws Exception;
    
    void removeProducer(final ConnectionContext p0, final ProducerInfo p1) throws Exception;
    
    Connection[] getClients() throws Exception;
    
    ActiveMQDestination[] getDestinations() throws Exception;
    
    TransactionId[] getPreparedTransactions(final ConnectionContext p0) throws Exception;
    
    void beginTransaction(final ConnectionContext p0, final TransactionId p1) throws Exception;
    
    int prepareTransaction(final ConnectionContext p0, final TransactionId p1) throws Exception;
    
    void rollbackTransaction(final ConnectionContext p0, final TransactionId p1) throws Exception;
    
    void commitTransaction(final ConnectionContext p0, final TransactionId p1, final boolean p2) throws Exception;
    
    void forgetTransaction(final ConnectionContext p0, final TransactionId p1) throws Exception;
    
    BrokerInfo[] getPeerBrokerInfos();
    
    void preProcessDispatch(final MessageDispatch p0);
    
    void postProcessDispatch(final MessageDispatch p0);
    
    boolean isStopped();
    
    Set<ActiveMQDestination> getDurableDestinations();
    
    void addDestinationInfo(final ConnectionContext p0, final DestinationInfo p1) throws Exception;
    
    void removeDestinationInfo(final ConnectionContext p0, final DestinationInfo p1) throws Exception;
    
    boolean isFaultTolerantConfiguration();
    
    ConnectionContext getAdminConnectionContext();
    
    void setAdminConnectionContext(final ConnectionContext p0);
    
    PListStore getTempDataStore();
    
    URI getVmConnectorURI();
    
    void brokerServiceStarted();
    
    BrokerService getBrokerService();
    
    Broker getRoot();
    
    boolean isExpired(final MessageReference p0);
    
    void messageExpired(final ConnectionContext p0, final MessageReference p1, final Subscription p2);
    
    boolean sendToDeadLetterQueue(final ConnectionContext p0, final MessageReference p1, final Subscription p2, final Throwable p3);
    
    long getBrokerSequenceId();
    
    void messageConsumed(final ConnectionContext p0, final MessageReference p1);
    
    void messageDelivered(final ConnectionContext p0, final MessageReference p1);
    
    void messageDiscarded(final ConnectionContext p0, final Subscription p1, final MessageReference p2);
    
    void slowConsumer(final ConnectionContext p0, final Destination p1, final Subscription p2);
    
    void fastProducer(final ConnectionContext p0, final ProducerInfo p1, final ActiveMQDestination p2);
    
    void isFull(final ConnectionContext p0, final Destination p1, final Usage p2);
    
    void nowMasterBroker();
    
    Scheduler getScheduler();
    
    ThreadPoolExecutor getExecutor();
    
    void networkBridgeStarted(final BrokerInfo p0, final boolean p1, final String p2);
    
    void networkBridgeStopped(final BrokerInfo p0);
}
