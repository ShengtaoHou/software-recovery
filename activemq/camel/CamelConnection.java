// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.camel;

import org.apache.activemq.management.JMSStatsImpl;
import org.apache.activemq.util.IdGenerator;
import org.apache.activemq.transport.Transport;
import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.apache.activemq.ActiveMQConnection;

public class CamelConnection extends ActiveMQConnection implements CamelContextAware
{
    private CamelContext camelContext;
    
    protected CamelConnection(final Transport transport, final IdGenerator clientIdGenerator, final IdGenerator connectionIdGenerator, final JMSStatsImpl factoryStats) throws Exception {
        super(transport, clientIdGenerator, connectionIdGenerator, factoryStats);
    }
    
    public CamelContext getCamelContext() {
        return this.camelContext;
    }
    
    public void setCamelContext(final CamelContext camelContext) {
        this.camelContext = camelContext;
    }
}
