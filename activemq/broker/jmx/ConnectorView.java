// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import org.apache.activemq.command.BrokerInfo;
import org.apache.activemq.broker.Connector;

public class ConnectorView implements ConnectorViewMBean
{
    private final Connector connector;
    
    public ConnectorView(final Connector connector) {
        this.connector = connector;
    }
    
    @Override
    public void start() throws Exception {
        this.connector.start();
    }
    
    public String getBrokerName() {
        return this.getBrokerInfo().getBrokerName();
    }
    
    @Override
    public void stop() throws Exception {
        this.connector.stop();
    }
    
    public String getBrokerURL() {
        return this.getBrokerInfo().getBrokerURL();
    }
    
    public BrokerInfo getBrokerInfo() {
        return this.connector.getBrokerInfo();
    }
    
    @Override
    public void resetStatistics() {
        this.connector.getStatistics().reset();
    }
    
    @Override
    public void enableStatistics() {
        this.connector.getStatistics().setEnabled(true);
    }
    
    @Override
    public void disableStatistics() {
        this.connector.getStatistics().setEnabled(false);
    }
    
    @Override
    public boolean isStatisticsEnabled() {
        return this.connector.getStatistics().isEnabled();
    }
    
    @Override
    public int connectionCount() {
        return this.connector.connectionCount();
    }
}
