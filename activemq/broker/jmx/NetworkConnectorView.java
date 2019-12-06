// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import org.apache.activemq.network.NetworkConnector;

public class NetworkConnectorView implements NetworkConnectorViewMBean
{
    private final NetworkConnector connector;
    
    public NetworkConnectorView(final NetworkConnector connector) {
        this.connector = connector;
    }
    
    @Override
    public void start() throws Exception {
        this.connector.start();
    }
    
    @Override
    public void stop() throws Exception {
        this.connector.stop();
    }
    
    @Override
    public String getName() {
        return this.connector.getName();
    }
    
    @Override
    public int getMessageTTL() {
        return this.connector.getMessageTTL();
    }
    
    @Override
    public int getConsumerTTL() {
        return this.connector.getConsumerTTL();
    }
    
    @Override
    public int getPrefetchSize() {
        return this.connector.getPrefetchSize();
    }
    
    @Override
    public String getUserName() {
        return this.connector.getUserName();
    }
    
    @Override
    public boolean isBridgeTempDestinations() {
        return this.connector.isBridgeTempDestinations();
    }
    
    @Override
    public boolean isConduitSubscriptions() {
        return this.connector.isConduitSubscriptions();
    }
    
    @Override
    public boolean isDecreaseNetworkConsumerPriority() {
        return this.connector.isDecreaseNetworkConsumerPriority();
    }
    
    @Override
    public boolean isDispatchAsync() {
        return this.connector.isDispatchAsync();
    }
    
    @Override
    public boolean isDynamicOnly() {
        return this.connector.isDynamicOnly();
    }
    
    @Override
    public boolean isDuplex() {
        return this.connector.isDuplex();
    }
    
    @Override
    public boolean isSuppressDuplicateQueueSubscriptions() {
        return this.connector.isSuppressDuplicateQueueSubscriptions();
    }
    
    @Override
    public boolean isSuppressDuplicateTopicSubscriptions() {
        return this.connector.isSuppressDuplicateTopicSubscriptions();
    }
    
    @Override
    public void setBridgeTempDestinations(final boolean bridgeTempDestinations) {
        this.connector.setBridgeTempDestinations(bridgeTempDestinations);
    }
    
    @Override
    public void setConduitSubscriptions(final boolean conduitSubscriptions) {
        this.connector.setConduitSubscriptions(conduitSubscriptions);
    }
    
    @Override
    public void setDispatchAsync(final boolean dispatchAsync) {
        this.connector.setDispatchAsync(dispatchAsync);
    }
    
    @Override
    public void setDynamicOnly(final boolean dynamicOnly) {
        this.connector.setDynamicOnly(dynamicOnly);
    }
    
    @Override
    public void setMessageTTL(final int messageTTL) {
        this.connector.setMessageTTL(messageTTL);
    }
    
    @Override
    public void setConsumerTTL(final int consumerTTL) {
        this.connector.setConsumerTTL(consumerTTL);
    }
    
    @Override
    public void setPassword(final String password) {
        this.connector.setPassword(password);
    }
    
    @Override
    public void setPrefetchSize(final int prefetchSize) {
        this.connector.setPrefetchSize(prefetchSize);
    }
    
    @Override
    public void setUserName(final String userName) {
        this.connector.setUserName(userName);
    }
    
    @Override
    public String getPassword() {
        String pw = this.connector.getPassword();
        if (pw != null) {
            pw = pw.replaceAll(".", "*");
        }
        return pw;
    }
    
    @Override
    public void setDecreaseNetworkConsumerPriority(final boolean decreaseNetworkConsumerPriority) {
        this.connector.setDecreaseNetworkConsumerPriority(decreaseNetworkConsumerPriority);
    }
    
    @Override
    public void setSuppressDuplicateQueueSubscriptions(final boolean val) {
        this.connector.setSuppressDuplicateQueueSubscriptions(val);
    }
    
    @Override
    public void setSuppressDuplicateTopicSubscriptions(final boolean val) {
        this.connector.setSuppressDuplicateTopicSubscriptions(val);
    }
}
