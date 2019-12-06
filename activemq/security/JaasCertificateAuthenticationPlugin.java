// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.security;

import org.apache.activemq.broker.Broker;

public class JaasCertificateAuthenticationPlugin extends JaasAuthenticationPlugin
{
    @Override
    public Broker installPlugin(final Broker broker) {
        this.initialiseJaas();
        return new JaasCertificateAuthenticationBroker(broker, this.configuration);
    }
}
