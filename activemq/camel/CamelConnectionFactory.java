// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.camel;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.management.JMSStatsImpl;
import org.apache.activemq.transport.Transport;
import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.apache.activemq.spring.ActiveMQConnectionFactory;

public class CamelConnectionFactory extends ActiveMQConnectionFactory implements CamelContextAware
{
    private CamelContext camelContext;
    
    public CamelContext getCamelContext() {
        return this.camelContext;
    }
    
    public void setCamelContext(final CamelContext camelContext) {
        this.camelContext = camelContext;
    }
    
    protected CamelConnection createActiveMQConnection(final Transport transport, final JMSStatsImpl stats) throws Exception {
        final CamelConnection connection = new CamelConnection(transport, this.getClientIdGenerator(), this.getConnectionIdGenerator(), stats);
        final CamelContext context = this.getCamelContext();
        if (context != null) {
            connection.setCamelContext(context);
        }
        return connection;
    }
}
