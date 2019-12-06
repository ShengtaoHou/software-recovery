// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.security;

import org.apache.activemq.broker.Broker;

public class JaasDualAuthenticationPlugin extends JaasAuthenticationPlugin
{
    private String sslConfiguration;
    
    public JaasDualAuthenticationPlugin() {
        this.sslConfiguration = "activemq-ssl-domain";
    }
    
    @Override
    public Broker installPlugin(final Broker broker) {
        this.initialiseJaas();
        return new JaasDualAuthenticationBroker(broker, this.configuration, this.sslConfiguration);
    }
    
    public void setSslConfiguration(final String sslConfiguration) {
        this.sslConfiguration = sslConfiguration;
    }
    
    public String getSslConfiguration() {
        return this.sslConfiguration;
    }
}
