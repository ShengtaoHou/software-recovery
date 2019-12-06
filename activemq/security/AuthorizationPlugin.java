// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.security;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;

public class AuthorizationPlugin implements BrokerPlugin
{
    private AuthorizationMap map;
    
    public AuthorizationPlugin() {
    }
    
    public AuthorizationPlugin(final AuthorizationMap map) {
        this.map = map;
    }
    
    @Override
    public Broker installPlugin(final Broker broker) {
        if (this.map == null) {
            throw new IllegalArgumentException("You must configure a 'map' property");
        }
        return new AuthorizationBroker(broker, this.map);
    }
    
    public AuthorizationMap getMap() {
        return this.map;
    }
    
    public void setMap(final AuthorizationMap map) {
        this.map = map;
    }
}
