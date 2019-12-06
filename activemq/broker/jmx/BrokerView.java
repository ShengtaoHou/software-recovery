// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import org.slf4j.LoggerFactory;
import java.io.File;
import java.net.URI;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.activemq.command.RemoveSubscriptionInfo;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.ConsumerId;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.util.BrokerSupport;
import org.apache.activemq.network.NetworkConnector;
import org.apache.activemq.broker.TransportConnector;
import java.util.NoSuchElementException;
import java.io.IOException;
import org.apache.activemq.ActiveMQConnectionMetaData;
import javax.management.ObjectName;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;

public class BrokerView implements BrokerViewMBean
{
    private static final Logger LOG;
    ManagedRegionBroker broker;
    private final BrokerService brokerService;
    private final AtomicInteger sessionIdCounter;
    private ObjectName jmsJobScheduler;
    
    public BrokerView(final BrokerService brokerService, final ManagedRegionBroker managedBroker) throws Exception {
        this.sessionIdCounter = new AtomicInteger(0);
        this.brokerService = brokerService;
        this.broker = managedBroker;
    }
    
    public ManagedRegionBroker getBroker() {
        return this.broker;
    }
    
    public void setBroker(final ManagedRegionBroker broker) {
        this.broker = broker;
    }
    
    @Override
    public String getBrokerId() {
        return this.safeGetBroker().getBrokerId().toString();
    }
    
    @Override
    public String getBrokerName() {
        return this.safeGetBroker().getBrokerName();
    }
    
    @Override
    public String getBrokerVersion() {
        return ActiveMQConnectionMetaData.PROVIDER_VERSION;
    }
    
    @Override
    public String getUptime() {
        return this.brokerService.getUptime();
    }
    
    @Override
    public int getCurrentConnectionsCount() {
        return this.brokerService.getCurrentConnections();
    }
    
    @Override
    public long getTotalConnectionsCount() {
        return this.brokerService.getTotalConnections();
    }
    
    @Override
    public void gc() throws Exception {
        this.brokerService.getBroker().gc();
        try {
            this.brokerService.getPersistenceAdapter().checkpoint(true);
        }
        catch (IOException e) {
            BrokerView.LOG.error("Failed to checkpoint persistence adapter on gc request", e);
        }
    }
    
    @Override
    public void start() throws Exception {
        this.brokerService.start();
    }
    
    @Override
    public void stop() throws Exception {
        this.brokerService.stop();
    }
    
    @Override
    public void restart() throws Exception {
        if (this.brokerService.isRestartAllowed()) {
            this.brokerService.requestRestart();
            this.brokerService.stop();
            return;
        }
        throw new Exception("Restart is not allowed");
    }
    
    @Override
    public void stopGracefully(final String connectorName, final String queueName, final long timeout, final long pollInterval) throws Exception {
        this.brokerService.stopGracefully(connectorName, queueName, timeout, pollInterval);
    }
    
    @Override
    public long getTotalEnqueueCount() {
        return this.safeGetBroker().getDestinationStatistics().getEnqueues().getCount();
    }
    
    @Override
    public long getTotalDequeueCount() {
        return this.safeGetBroker().getDestinationStatistics().getDequeues().getCount();
    }
    
    @Override
    public long getTotalConsumerCount() {
        return this.safeGetBroker().getDestinationStatistics().getConsumers().getCount();
    }
    
    @Override
    public long getTotalProducerCount() {
        return this.safeGetBroker().getDestinationStatistics().getProducers().getCount();
    }
    
    @Override
    public long getTotalMessageCount() {
        return this.safeGetBroker().getDestinationStatistics().getMessages().getCount();
    }
    
    @Override
    public long getAverageMessageSize() {
        return (long)this.safeGetBroker().getDestinationStatistics().getMessageSize().getAverageSize();
    }
    
    @Override
    public long getMaxMessageSize() {
        return this.safeGetBroker().getDestinationStatistics().getMessageSize().getMaxSize();
    }
    
    @Override
    public long getMinMessageSize() {
        return this.safeGetBroker().getDestinationStatistics().getMessageSize().getMinSize();
    }
    
    public long getTotalMessagesCached() {
        return this.safeGetBroker().getDestinationStatistics().getMessagesCached().getCount();
    }
    
    @Override
    public int getMemoryPercentUsage() {
        return this.brokerService.getSystemUsage().getMemoryUsage().getPercentUsage();
    }
    
    @Override
    public long getMemoryLimit() {
        return this.brokerService.getSystemUsage().getMemoryUsage().getLimit();
    }
    
    @Override
    public void setMemoryLimit(final long limit) {
        this.brokerService.getSystemUsage().getMemoryUsage().setLimit(limit);
    }
    
    @Override
    public long getStoreLimit() {
        return this.brokerService.getSystemUsage().getStoreUsage().getLimit();
    }
    
    @Override
    public int getStorePercentUsage() {
        return this.brokerService.getSystemUsage().getStoreUsage().getPercentUsage();
    }
    
    @Override
    public long getTempLimit() {
        return this.brokerService.getSystemUsage().getTempUsage().getLimit();
    }
    
    @Override
    public int getTempPercentUsage() {
        return this.brokerService.getSystemUsage().getTempUsage().getPercentUsage();
    }
    
    @Override
    public long getJobSchedulerStoreLimit() {
        return this.brokerService.getSystemUsage().getJobSchedulerUsage().getLimit();
    }
    
    @Override
    public int getJobSchedulerStorePercentUsage() {
        return this.brokerService.getSystemUsage().getJobSchedulerUsage().getPercentUsage();
    }
    
    @Override
    public void setStoreLimit(final long limit) {
        this.brokerService.getSystemUsage().getStoreUsage().setLimit(limit);
    }
    
    @Override
    public void setTempLimit(final long limit) {
        this.brokerService.getSystemUsage().getTempUsage().setLimit(limit);
    }
    
    @Override
    public void setJobSchedulerStoreLimit(final long limit) {
        this.brokerService.getSystemUsage().getJobSchedulerUsage().setLimit(limit);
    }
    
    @Override
    public void resetStatistics() {
        this.safeGetBroker().getDestinationStatistics().reset();
    }
    
    @Override
    public void enableStatistics() {
        this.safeGetBroker().getDestinationStatistics().setEnabled(true);
    }
    
    @Override
    public void disableStatistics() {
        this.safeGetBroker().getDestinationStatistics().setEnabled(false);
    }
    
    @Override
    public boolean isStatisticsEnabled() {
        return this.safeGetBroker().getDestinationStatistics().isEnabled();
    }
    
    @Override
    public boolean isPersistent() {
        return this.brokerService.isPersistent();
    }
    
    @Override
    public void terminateJVM(final int exitCode) {
        System.exit(exitCode);
    }
    
    @Override
    public ObjectName[] getTopics() {
        return this.safeGetBroker().getTopics();
    }
    
    @Override
    public ObjectName[] getQueues() {
        return this.safeGetBroker().getQueues();
    }
    
    @Override
    public ObjectName[] getTemporaryTopics() {
        return this.safeGetBroker().getTemporaryTopics();
    }
    
    @Override
    public ObjectName[] getTemporaryQueues() {
        return this.safeGetBroker().getTemporaryQueues();
    }
    
    @Override
    public ObjectName[] getTopicSubscribers() {
        return this.safeGetBroker().getTopicSubscribers();
    }
    
    @Override
    public ObjectName[] getDurableTopicSubscribers() {
        return this.safeGetBroker().getDurableTopicSubscribers();
    }
    
    @Override
    public ObjectName[] getQueueSubscribers() {
        return this.safeGetBroker().getQueueSubscribers();
    }
    
    @Override
    public ObjectName[] getTemporaryTopicSubscribers() {
        return this.safeGetBroker().getTemporaryTopicSubscribers();
    }
    
    @Override
    public ObjectName[] getTemporaryQueueSubscribers() {
        return this.safeGetBroker().getTemporaryQueueSubscribers();
    }
    
    @Override
    public ObjectName[] getInactiveDurableTopicSubscribers() {
        return this.safeGetBroker().getInactiveDurableTopicSubscribers();
    }
    
    @Override
    public ObjectName[] getTopicProducers() {
        return this.safeGetBroker().getTopicProducers();
    }
    
    @Override
    public ObjectName[] getQueueProducers() {
        return this.safeGetBroker().getQueueProducers();
    }
    
    @Override
    public ObjectName[] getTemporaryTopicProducers() {
        return this.safeGetBroker().getTemporaryTopicProducers();
    }
    
    @Override
    public ObjectName[] getTemporaryQueueProducers() {
        return this.safeGetBroker().getTemporaryQueueProducers();
    }
    
    @Override
    public ObjectName[] getDynamicDestinationProducers() {
        return this.safeGetBroker().getDynamicDestinationProducers();
    }
    
    @Override
    public String addConnector(final String discoveryAddress) throws Exception {
        final TransportConnector connector = this.brokerService.addConnector(discoveryAddress);
        if (connector == null) {
            throw new NoSuchElementException("Not connector matched the given name: " + discoveryAddress);
        }
        connector.start();
        return connector.getName();
    }
    
    @Override
    public String addNetworkConnector(final String discoveryAddress) throws Exception {
        final NetworkConnector connector = this.brokerService.addNetworkConnector(discoveryAddress);
        if (connector == null) {
            throw new NoSuchElementException("Not connector matched the given name: " + discoveryAddress);
        }
        this.brokerService.registerNetworkConnectorMBean(connector);
        connector.start();
        return connector.getName();
    }
    
    @Override
    public boolean removeConnector(final String connectorName) throws Exception {
        final TransportConnector connector = this.brokerService.getConnectorByName(connectorName);
        if (connector == null) {
            throw new NoSuchElementException("Not connector matched the given name: " + connectorName);
        }
        connector.stop();
        return this.brokerService.removeConnector(connector);
    }
    
    @Override
    public boolean removeNetworkConnector(final String connectorName) throws Exception {
        final NetworkConnector connector = this.brokerService.getNetworkConnectorByName(connectorName);
        if (connector == null) {
            throw new NoSuchElementException("Not connector matched the given name: " + connectorName);
        }
        connector.stop();
        return this.brokerService.removeNetworkConnector(connector);
    }
    
    @Override
    public void addTopic(final String name) throws Exception {
        this.safeGetBroker().getContextBroker().addDestination(BrokerSupport.getConnectionContext(this.safeGetBroker().getContextBroker()), new ActiveMQTopic(name), true);
    }
    
    @Override
    public void addQueue(final String name) throws Exception {
        this.safeGetBroker().getContextBroker().addDestination(BrokerSupport.getConnectionContext(this.safeGetBroker().getContextBroker()), new ActiveMQQueue(name), true);
    }
    
    @Override
    public void removeTopic(final String name) throws Exception {
        this.safeGetBroker().getContextBroker().removeDestination(BrokerSupport.getConnectionContext(this.safeGetBroker().getContextBroker()), new ActiveMQTopic(name), 1000L);
    }
    
    @Override
    public void removeQueue(final String name) throws Exception {
        this.safeGetBroker().getContextBroker().removeDestination(BrokerSupport.getConnectionContext(this.safeGetBroker().getContextBroker()), new ActiveMQQueue(name), 1000L);
    }
    
    @Override
    public ObjectName createDurableSubscriber(final String clientId, final String subscriberName, final String topicName, final String selector) throws Exception {
        final ConnectionContext context = new ConnectionContext();
        context.setBroker(this.safeGetBroker());
        context.setClientId(clientId);
        final ConsumerInfo info = new ConsumerInfo();
        final ConsumerId consumerId = new ConsumerId();
        consumerId.setConnectionId(clientId);
        consumerId.setSessionId(this.sessionIdCounter.incrementAndGet());
        consumerId.setValue(0L);
        info.setConsumerId(consumerId);
        info.setDestination(new ActiveMQTopic(topicName));
        info.setSubscriptionName(subscriberName);
        info.setSelector(selector);
        final Subscription subscription = this.safeGetBroker().addConsumer(context, info);
        this.safeGetBroker().removeConsumer(context, info);
        if (subscription != null) {
            return subscription.getObjectName();
        }
        return null;
    }
    
    @Override
    public void destroyDurableSubscriber(final String clientId, final String subscriberName) throws Exception {
        final RemoveSubscriptionInfo info = new RemoveSubscriptionInfo();
        info.setClientId(clientId);
        info.setSubscriptionName(subscriberName);
        final ConnectionContext context = new ConnectionContext();
        context.setBroker(this.safeGetBroker());
        context.setClientId(clientId);
        this.brokerService.getBroker().removeSubscription(context, info);
    }
    
    @Override
    public void reloadLog4jProperties() throws Throwable {
        try {
            final ClassLoader cl = this.getClass().getClassLoader();
            final Class<?> logManagerClass = cl.loadClass("org.apache.log4j.LogManager");
            final Method resetConfiguration = logManagerClass.getMethod("resetConfiguration", (Class<?>[])new Class[0]);
            resetConfiguration.invoke(null, new Object[0]);
            final String configurationOptionStr = System.getProperty("log4j.configuration");
            URL log4jprops = null;
            if (configurationOptionStr != null) {
                try {
                    log4jprops = new URL(configurationOptionStr);
                }
                catch (MalformedURLException ex) {
                    log4jprops = cl.getResource("log4j.properties");
                }
            }
            else {
                log4jprops = cl.getResource("log4j.properties");
            }
            if (log4jprops != null) {
                final Class<?> propertyConfiguratorClass = cl.loadClass("org.apache.log4j.PropertyConfigurator");
                final Method configure = propertyConfiguratorClass.getMethod("configure", URL.class);
                configure.invoke(null, log4jprops);
            }
        }
        catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
    
    @Override
    public Map<String, String> getTransportConnectors() {
        final Map<String, String> answer = new HashMap<String, String>();
        try {
            for (final TransportConnector connector : this.brokerService.getTransportConnectors()) {
                answer.put(connector.getName(), connector.getConnectUri().toString());
            }
        }
        catch (Exception e) {
            BrokerView.LOG.debug("Failed to read URI to build transport connectors map", e);
        }
        return answer;
    }
    
    @Override
    public String getTransportConnectorByType(final String type) {
        return this.brokerService.getTransportConnectorURIsAsMap().get(type);
    }
    
    @Deprecated
    @Override
    public String getOpenWireURL() {
        final String answer = this.brokerService.getTransportConnectorURIsAsMap().get("tcp");
        return (answer != null) ? answer : "";
    }
    
    @Deprecated
    @Override
    public String getStompURL() {
        final String answer = this.brokerService.getTransportConnectorURIsAsMap().get("stomp");
        return (answer != null) ? answer : "";
    }
    
    @Deprecated
    @Override
    public String getSslURL() {
        final String answer = this.brokerService.getTransportConnectorURIsAsMap().get("ssl");
        return (answer != null) ? answer : "";
    }
    
    @Deprecated
    @Override
    public String getStompSslURL() {
        final String answer = this.brokerService.getTransportConnectorURIsAsMap().get("stomp+ssl");
        return (answer != null) ? answer : "";
    }
    
    @Override
    public String getVMURL() {
        final URI answer = this.brokerService.getVmConnectorURI();
        return (answer != null) ? answer.toString() : "";
    }
    
    @Override
    public String getDataDirectory() {
        final File file = this.brokerService.getDataDirectoryFile();
        try {
            return (file != null) ? file.getCanonicalPath() : "";
        }
        catch (IOException e) {
            return "";
        }
    }
    
    @Override
    public ObjectName getJMSJobScheduler() {
        return this.jmsJobScheduler;
    }
    
    public void setJMSJobScheduler(final ObjectName name) {
        this.jmsJobScheduler = name;
    }
    
    @Override
    public boolean isSlave() {
        return this.brokerService.isSlave();
    }
    
    private ManagedRegionBroker safeGetBroker() {
        if (this.broker == null) {
            throw new IllegalStateException("Broker is not yet started.");
        }
        return this.broker;
    }
    
    static {
        LOG = LoggerFactory.getLogger(BrokerView.class);
    }
}
