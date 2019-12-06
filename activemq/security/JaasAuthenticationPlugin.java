// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.security;

import java.net.URL;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;

public class JaasAuthenticationPlugin implements BrokerPlugin
{
    protected String configuration;
    protected boolean discoverLoginConfig;
    
    public JaasAuthenticationPlugin() {
        this.configuration = "activemq-domain";
        this.discoverLoginConfig = true;
    }
    
    @Override
    public Broker installPlugin(final Broker broker) {
        this.initialiseJaas();
        return new JaasAuthenticationBroker(broker, this.configuration);
    }
    
    public String getConfiguration() {
        return this.configuration;
    }
    
    public void setConfiguration(final String jaasConfiguration) {
        this.configuration = jaasConfiguration;
    }
    
    public boolean isDiscoverLoginConfig() {
        return this.discoverLoginConfig;
    }
    
    public void setDiscoverLoginConfig(final boolean discoverLoginConfig) {
        this.discoverLoginConfig = discoverLoginConfig;
    }
    
    protected void initialiseJaas() {
        if (this.discoverLoginConfig) {
            String path = System.getProperty("java.security.auth.login.config");
            if (path == null) {
                URL resource = null;
                if (resource == null) {
                    resource = this.getClass().getClassLoader().getResource("login.config");
                }
                if (resource != null) {
                    path = resource.getFile();
                    System.setProperty("java.security.auth.login.config", path);
                }
            }
        }
    }
}
