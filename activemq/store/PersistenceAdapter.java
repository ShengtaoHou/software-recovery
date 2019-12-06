// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store;

import org.apache.activemq.command.ProducerId;
import java.io.File;
import org.apache.activemq.usage.SystemUsage;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.ActiveMQTopic;
import java.io.IOException;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.Set;
import org.apache.activemq.Service;

public interface PersistenceAdapter extends Service
{
    Set<ActiveMQDestination> getDestinations();
    
    MessageStore createQueueMessageStore(final ActiveMQQueue p0) throws IOException;
    
    TopicMessageStore createTopicMessageStore(final ActiveMQTopic p0) throws IOException;
    
    void removeQueueMessageStore(final ActiveMQQueue p0);
    
    void removeTopicMessageStore(final ActiveMQTopic p0);
    
    TransactionStore createTransactionStore() throws IOException;
    
    void beginTransaction(final ConnectionContext p0) throws IOException;
    
    void commitTransaction(final ConnectionContext p0) throws IOException;
    
    void rollbackTransaction(final ConnectionContext p0) throws IOException;
    
    long getLastMessageBrokerSequenceId() throws IOException;
    
    void deleteAllMessages() throws IOException;
    
    void setUsageManager(final SystemUsage p0);
    
    void setBrokerName(final String p0);
    
    void setDirectory(final File p0);
    
    File getDirectory();
    
    void checkpoint(final boolean p0) throws IOException;
    
    long size();
    
    long getLastProducerSequenceId(final ProducerId p0) throws IOException;
}
