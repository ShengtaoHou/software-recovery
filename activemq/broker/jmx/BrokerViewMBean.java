// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import java.util.Map;
import javax.management.ObjectName;
import org.apache.activemq.Service;

public interface BrokerViewMBean extends Service
{
    @MBeanInfo("The unique id of the broker.")
    String getBrokerId();
    
    @MBeanInfo("The name of the broker.")
    String getBrokerName();
    
    @MBeanInfo("The version of the broker.")
    String getBrokerVersion();
    
    @MBeanInfo("Uptime of the broker.")
    String getUptime();
    
    int getCurrentConnectionsCount();
    
    long getTotalConnectionsCount();
    
    @MBeanInfo("Runs the Garbage Collector.")
    void gc() throws Exception;
    
    @MBeanInfo("Reset all broker statistics.")
    void resetStatistics();
    
    @MBeanInfo("Enable broker statistics.")
    void enableStatistics();
    
    @MBeanInfo("Disable broker statistics.")
    void disableStatistics();
    
    @MBeanInfo("Broker statistics enabled.")
    boolean isStatisticsEnabled();
    
    @MBeanInfo("Number of messages that have been sent to the broker.")
    long getTotalEnqueueCount();
    
    @MBeanInfo("Number of messages that have been acknowledged on the broker.")
    long getTotalDequeueCount();
    
    @MBeanInfo("Number of message consumers subscribed to destinations on the broker.")
    long getTotalConsumerCount();
    
    @MBeanInfo("Number of message producers active on destinations on the broker.")
    long getTotalProducerCount();
    
    @MBeanInfo("Number of unacknowledged messages on the broker.")
    long getTotalMessageCount();
    
    @MBeanInfo("Average message size on this broker")
    long getAverageMessageSize();
    
    @MBeanInfo("Max message size on this broker")
    long getMaxMessageSize();
    
    @MBeanInfo("Min message size on this broker")
    long getMinMessageSize();
    
    @MBeanInfo("Percent of memory limit used.")
    int getMemoryPercentUsage();
    
    @MBeanInfo("Memory limit, in bytes, used for holding undelivered messages before paging to temporary storage.")
    long getMemoryLimit();
    
    void setMemoryLimit(@MBeanInfo("bytes") final long p0);
    
    @MBeanInfo("Percent of store limit used.")
    int getStorePercentUsage();
    
    @MBeanInfo("Disk limit, in bytes, used for persistent messages before producers are blocked.")
    long getStoreLimit();
    
    void setStoreLimit(@MBeanInfo("bytes") final long p0);
    
    @MBeanInfo("Percent of temp limit used.")
    int getTempPercentUsage();
    
    @MBeanInfo("Disk limit, in bytes, used for non-persistent messages and temporary data before producers are blocked.")
    long getTempLimit();
    
    void setTempLimit(@MBeanInfo("bytes") final long p0);
    
    @MBeanInfo("Percent of job store limit used.")
    int getJobSchedulerStorePercentUsage();
    
    @MBeanInfo("Disk limit, in bytes, used for scheduled messages before producers are blocked.")
    long getJobSchedulerStoreLimit();
    
    void setJobSchedulerStoreLimit(@MBeanInfo("bytes") final long p0);
    
    @MBeanInfo("Messages are synchronized to disk.")
    boolean isPersistent();
    
    @MBeanInfo("Slave broker.")
    boolean isSlave();
    
    @MBeanInfo("Shuts down the JVM.")
    void terminateJVM(@MBeanInfo("exitCode") final int p0);
    
    @MBeanInfo("Stop the broker and all its components.")
    void stop() throws Exception;
    
    @MBeanInfo("Restart the broker and all its components.")
    void restart() throws Exception;
    
    @MBeanInfo("Poll for queues matching queueName are empty before stopping")
    void stopGracefully(final String p0, final String p1, final long p2, final long p3) throws Exception;
    
    @MBeanInfo("Topics (broadcasted 'queues'); generally system information.")
    ObjectName[] getTopics();
    
    @MBeanInfo("Standard Queues containing AIE messages.")
    ObjectName[] getQueues();
    
    @MBeanInfo("Temporary Topics; generally unused.")
    ObjectName[] getTemporaryTopics();
    
    @MBeanInfo("Temporary Queues; generally temporary message response holders.")
    ObjectName[] getTemporaryQueues();
    
    @MBeanInfo("Topic Subscribers")
    ObjectName[] getTopicSubscribers();
    
    @MBeanInfo("Durable (persistent) topic subscribers")
    ObjectName[] getDurableTopicSubscribers();
    
    @MBeanInfo("Inactive (disconnected persistent) topic subscribers")
    ObjectName[] getInactiveDurableTopicSubscribers();
    
    @MBeanInfo("Queue Subscribers.")
    ObjectName[] getQueueSubscribers();
    
    @MBeanInfo("Temporary Topic Subscribers.")
    ObjectName[] getTemporaryTopicSubscribers();
    
    @MBeanInfo("Temporary Queue Subscribers.")
    ObjectName[] getTemporaryQueueSubscribers();
    
    @MBeanInfo("Topic Producers.")
    ObjectName[] getTopicProducers();
    
    @MBeanInfo("Queue Producers.")
    ObjectName[] getQueueProducers();
    
    @MBeanInfo("Temporary Topic Producers.")
    ObjectName[] getTemporaryTopicProducers();
    
    @MBeanInfo("Temporary Queue Producers.")
    ObjectName[] getTemporaryQueueProducers();
    
    @MBeanInfo("Dynamic Destination Producers.")
    ObjectName[] getDynamicDestinationProducers();
    
    @MBeanInfo("Adds a Connector to the broker.")
    String addConnector(@MBeanInfo("discoveryAddress") final String p0) throws Exception;
    
    @MBeanInfo("Adds a Network Connector to the broker.")
    String addNetworkConnector(@MBeanInfo("discoveryAddress") final String p0) throws Exception;
    
    @MBeanInfo("Removes a Connector from the broker.")
    boolean removeConnector(@MBeanInfo("connectorName") final String p0) throws Exception;
    
    @MBeanInfo("Removes a Network Connector from the broker.")
    boolean removeNetworkConnector(@MBeanInfo("connectorName") final String p0) throws Exception;
    
    @MBeanInfo("Adds a Topic destination to the broker.")
    void addTopic(@MBeanInfo("name") final String p0) throws Exception;
    
    @MBeanInfo("Adds a Queue destination to the broker.")
    void addQueue(@MBeanInfo("name") final String p0) throws Exception;
    
    @MBeanInfo("Removes a Topic destination from the broker.")
    void removeTopic(@MBeanInfo("name") final String p0) throws Exception;
    
    @MBeanInfo("Removes a Queue destination from the broker.")
    void removeQueue(@MBeanInfo("name") final String p0) throws Exception;
    
    @MBeanInfo("Creates a new durable topic subscriber.")
    ObjectName createDurableSubscriber(@MBeanInfo("clientId") final String p0, @MBeanInfo("subscriberName") final String p1, @MBeanInfo("topicName") final String p2, @MBeanInfo("selector") final String p3) throws Exception;
    
    @MBeanInfo("Destroys a durable subscriber.")
    void destroyDurableSubscriber(@MBeanInfo("clientId") final String p0, @MBeanInfo("subscriberName") final String p1) throws Exception;
    
    @MBeanInfo("Reloads log4j.properties from the classpath.")
    void reloadLog4jProperties() throws Throwable;
    
    @Deprecated
    @MBeanInfo("The url of the openwire connector - deprecated, use getTransportConnectors or getTransportConnectorByType instead")
    String getOpenWireURL();
    
    @Deprecated
    @MBeanInfo("The url of the stomp connector - deprecated, use getTransportConnectors or getTransportConnectorByType instead")
    String getStompURL();
    
    @Deprecated
    @MBeanInfo("The url of the SSL connector - deprecated, use getTransportConnectors or getTransportConnectorByType instead")
    String getSslURL();
    
    @Deprecated
    @MBeanInfo("The url of the Stomp SSL connector - deprecated, use getTransportConnectors or getTransportConnectorByType instead")
    String getStompSslURL();
    
    @MBeanInfo("The url of the VM connector")
    String getVMURL();
    
    @MBeanInfo("The map of all defined transport connectors, with transport name as a key")
    Map<String, String> getTransportConnectors();
    
    @MBeanInfo("The url of transport connector by it's type; e.g. tcp, stomp, ssl, etc.")
    String getTransportConnectorByType(final String p0);
    
    @MBeanInfo("The location of the data directory")
    String getDataDirectory();
    
    @MBeanInfo("JMSJobScheduler")
    ObjectName getJMSJobScheduler();
}
