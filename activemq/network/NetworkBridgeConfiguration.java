// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.List;

public class NetworkBridgeConfiguration
{
    private boolean conduitSubscriptions;
    private boolean dynamicOnly;
    private boolean dispatchAsync;
    private boolean decreaseNetworkConsumerPriority;
    private int consumerPriorityBase;
    private boolean duplex;
    private boolean bridgeTempDestinations;
    private int prefetchSize;
    private int networkTTL;
    private int consumerTTL;
    private int messageTTL;
    private String brokerName;
    private String brokerURL;
    private String userName;
    private String password;
    private String destinationFilter;
    private String name;
    protected List<ActiveMQDestination> excludedDestinations;
    protected List<ActiveMQDestination> dynamicallyIncludedDestinations;
    protected List<ActiveMQDestination> staticallyIncludedDestinations;
    private boolean suppressDuplicateQueueSubscriptions;
    private boolean suppressDuplicateTopicSubscriptions;
    private boolean alwaysSyncSend;
    private boolean staticBridge;
    private boolean useCompression;
    private boolean advisoryForFailedForward;
    private boolean useBrokerNamesAsIdSeed;
    private boolean gcDestinationViews;
    private long gcSweepTime;
    private boolean checkDuplicateMessagesOnDuplex;
    
    public NetworkBridgeConfiguration() {
        this.conduitSubscriptions = true;
        this.dispatchAsync = true;
        this.consumerPriorityBase = -5;
        this.bridgeTempDestinations = true;
        this.prefetchSize = 1000;
        this.networkTTL = 1;
        this.consumerTTL = this.networkTTL;
        this.messageTTL = this.networkTTL;
        this.brokerName = "localhost";
        this.brokerURL = "";
        this.destinationFilter = null;
        this.name = "NC";
        this.excludedDestinations = new CopyOnWriteArrayList<ActiveMQDestination>();
        this.dynamicallyIncludedDestinations = new CopyOnWriteArrayList<ActiveMQDestination>();
        this.staticallyIncludedDestinations = new CopyOnWriteArrayList<ActiveMQDestination>();
        this.suppressDuplicateQueueSubscriptions = false;
        this.suppressDuplicateTopicSubscriptions = true;
        this.alwaysSyncSend = true;
        this.staticBridge = false;
        this.useCompression = false;
        this.advisoryForFailedForward = false;
        this.useBrokerNamesAsIdSeed = true;
        this.gcDestinationViews = true;
        this.gcSweepTime = 60000L;
        this.checkDuplicateMessagesOnDuplex = false;
    }
    
    public boolean isConduitSubscriptions() {
        return this.conduitSubscriptions;
    }
    
    public void setConduitSubscriptions(final boolean conduitSubscriptions) {
        this.conduitSubscriptions = conduitSubscriptions;
    }
    
    public boolean isDynamicOnly() {
        return this.dynamicOnly;
    }
    
    public void setDynamicOnly(final boolean dynamicOnly) {
        this.dynamicOnly = dynamicOnly;
    }
    
    public boolean isBridgeTempDestinations() {
        return this.bridgeTempDestinations;
    }
    
    public void setBridgeTempDestinations(final boolean bridgeTempDestinations) {
        this.bridgeTempDestinations = bridgeTempDestinations;
    }
    
    public boolean isDecreaseNetworkConsumerPriority() {
        return this.decreaseNetworkConsumerPriority;
    }
    
    public void setDecreaseNetworkConsumerPriority(final boolean decreaseNetworkConsumerPriority) {
        this.decreaseNetworkConsumerPriority = decreaseNetworkConsumerPriority;
    }
    
    public boolean isDispatchAsync() {
        return this.dispatchAsync;
    }
    
    public void setDispatchAsync(final boolean dispatchAsync) {
        this.dispatchAsync = dispatchAsync;
    }
    
    public boolean isDuplex() {
        return this.duplex;
    }
    
    public void setDuplex(final boolean duplex) {
        this.duplex = duplex;
    }
    
    public String getBrokerName() {
        return this.brokerName;
    }
    
    public void setBrokerName(final String brokerName) {
        this.brokerName = brokerName;
    }
    
    public int getNetworkTTL() {
        return this.networkTTL;
    }
    
    public void setNetworkTTL(final int networkTTL) {
        this.setConsumerTTL(this.networkTTL = networkTTL);
        this.setMessageTTL(networkTTL);
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public int getPrefetchSize() {
        return this.prefetchSize;
    }
    
    public void setPrefetchSize(final int prefetchSize) {
        this.prefetchSize = prefetchSize;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public void setUserName(final String userName) {
        this.userName = userName;
    }
    
    public String getDestinationFilter() {
        if (this.destinationFilter == null) {
            if (this.dynamicallyIncludedDestinations != null && !this.dynamicallyIncludedDestinations.isEmpty()) {
                final StringBuffer filter = new StringBuffer();
                String delimiter = "";
                for (final ActiveMQDestination destination : this.dynamicallyIncludedDestinations) {
                    if (!destination.isTemporary()) {
                        filter.append(delimiter);
                        filter.append("ActiveMQ.Advisory.Consumer.");
                        filter.append(destination.getDestinationTypeAsString());
                        filter.append(".");
                        filter.append(destination.getPhysicalName());
                        delimiter = ",";
                    }
                }
                return filter.toString();
            }
            return "ActiveMQ.Advisory.Consumer.>";
        }
        else {
            if (!this.destinationFilter.startsWith("ActiveMQ.Advisory.Consumer.")) {
                return "ActiveMQ.Advisory.Consumer." + this.destinationFilter;
            }
            return this.destinationFilter;
        }
    }
    
    public void setDestinationFilter(final String destinationFilter) {
        this.destinationFilter = destinationFilter;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public List<ActiveMQDestination> getExcludedDestinations() {
        return this.excludedDestinations;
    }
    
    public void setExcludedDestinations(final List<ActiveMQDestination> excludedDestinations) {
        this.excludedDestinations = excludedDestinations;
    }
    
    public List<ActiveMQDestination> getDynamicallyIncludedDestinations() {
        return this.dynamicallyIncludedDestinations;
    }
    
    public void setDynamicallyIncludedDestinations(final List<ActiveMQDestination> dynamicallyIncludedDestinations) {
        this.dynamicallyIncludedDestinations = dynamicallyIncludedDestinations;
    }
    
    public List<ActiveMQDestination> getStaticallyIncludedDestinations() {
        return this.staticallyIncludedDestinations;
    }
    
    public void setStaticallyIncludedDestinations(final List<ActiveMQDestination> staticallyIncludedDestinations) {
        this.staticallyIncludedDestinations = staticallyIncludedDestinations;
    }
    
    public boolean isSuppressDuplicateQueueSubscriptions() {
        return this.suppressDuplicateQueueSubscriptions;
    }
    
    public void setSuppressDuplicateQueueSubscriptions(final boolean val) {
        this.suppressDuplicateQueueSubscriptions = val;
    }
    
    public boolean isSuppressDuplicateTopicSubscriptions() {
        return this.suppressDuplicateTopicSubscriptions;
    }
    
    public void setSuppressDuplicateTopicSubscriptions(final boolean val) {
        this.suppressDuplicateTopicSubscriptions = val;
    }
    
    public String getBrokerURL() {
        return this.brokerURL;
    }
    
    public void setBrokerURL(final String brokerURL) {
        this.brokerURL = brokerURL;
    }
    
    public boolean isAlwaysSyncSend() {
        return this.alwaysSyncSend;
    }
    
    public void setAlwaysSyncSend(final boolean alwaysSyncSend) {
        this.alwaysSyncSend = alwaysSyncSend;
    }
    
    public int getConsumerPriorityBase() {
        return this.consumerPriorityBase;
    }
    
    public void setConsumerPriorityBase(final int consumerPriorityBase) {
        this.consumerPriorityBase = consumerPriorityBase;
    }
    
    public boolean isStaticBridge() {
        return this.staticBridge;
    }
    
    public void setStaticBridge(final boolean staticBridge) {
        this.staticBridge = staticBridge;
    }
    
    public void setUseCompression(final boolean useCompression) {
        this.useCompression = useCompression;
    }
    
    public boolean isUseCompression() {
        return this.useCompression;
    }
    
    public boolean isAdvisoryForFailedForward() {
        return this.advisoryForFailedForward;
    }
    
    public void setAdvisoryForFailedForward(final boolean advisoryForFailedForward) {
        this.advisoryForFailedForward = advisoryForFailedForward;
    }
    
    public void setConsumerTTL(final int consumerTTL) {
        this.consumerTTL = consumerTTL;
    }
    
    public int getConsumerTTL() {
        return this.consumerTTL;
    }
    
    public void setMessageTTL(final int messageTTL) {
        this.messageTTL = messageTTL;
    }
    
    public int getMessageTTL() {
        return this.messageTTL;
    }
    
    public boolean isUseBrokerNamesAsIdSeed() {
        return this.useBrokerNamesAsIdSeed;
    }
    
    public void setUseBrokerNameAsIdSees(final boolean val) {
        this.useBrokerNamesAsIdSeed = val;
    }
    
    public boolean isGcDestinationViews() {
        return this.gcDestinationViews;
    }
    
    public void setGcDestinationViews(final boolean gcDestinationViews) {
        this.gcDestinationViews = gcDestinationViews;
    }
    
    public long getGcSweepTime() {
        return this.gcSweepTime;
    }
    
    public void setGcSweepTime(final long gcSweepTime) {
        this.gcSweepTime = gcSweepTime;
    }
    
    public boolean isCheckDuplicateMessagesOnDuplex() {
        return this.checkDuplicateMessagesOnDuplex;
    }
    
    public void setCheckDuplicateMessagesOnDuplex(final boolean checkDuplicateMessagesOnDuplex) {
        this.checkDuplicateMessagesOnDuplex = checkDuplicateMessagesOnDuplex;
    }
}
