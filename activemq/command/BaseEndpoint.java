// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

public class BaseEndpoint implements Endpoint
{
    private String name;
    private BrokerInfo brokerInfo;
    
    public BaseEndpoint(final String name) {
        this.name = name;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String toString() {
        String brokerText = "";
        final BrokerId brokerId = this.getBrokerId();
        if (brokerId != null) {
            brokerText = " broker: " + brokerId;
        }
        return "Endpoint[name:" + this.name + brokerText + "]";
    }
    
    @Override
    public BrokerId getBrokerId() {
        if (this.brokerInfo != null) {
            return this.brokerInfo.getBrokerId();
        }
        return null;
    }
    
    @Override
    public BrokerInfo getBrokerInfo() {
        return this.brokerInfo;
    }
    
    @Override
    public void setBrokerInfo(final BrokerInfo brokerInfo) {
        this.brokerInfo = brokerInfo;
    }
}
