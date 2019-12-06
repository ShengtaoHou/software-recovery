// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.camel.component.broker;

import org.apache.camel.spi.UriParam;

public class BrokerConfiguration
{
    @UriParam
    private String brokerName;
    
    public BrokerConfiguration() {
        this.brokerName = "";
    }
    
    public String getBrokerName() {
        return this.brokerName;
    }
    
    public void setBrokerName(final String brokerName) {
        this.brokerName = brokerName;
    }
}
