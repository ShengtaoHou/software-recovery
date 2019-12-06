// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.http;

import org.springframework.core.io.Resource;
import org.apache.activemq.xbean.BrokerFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.apache.activemq.broker.BrokerService;

public class HttpSpringEmbeddedTunnelServlet extends HttpEmbeddedTunnelServlet
{
    private static final long serialVersionUID = -6568661997192814908L;
    
    @Override
    protected BrokerService createBroker() throws Exception {
        String configFile = this.getServletContext().getInitParameter("org.activemq.config.file");
        if (configFile == null) {
            configFile = "activemq.xml";
        }
        final BrokerFactoryBean factory = new BrokerFactoryBean((Resource)new ClassPathResource(configFile));
        factory.afterPropertiesSet();
        return factory.getBroker();
    }
}
