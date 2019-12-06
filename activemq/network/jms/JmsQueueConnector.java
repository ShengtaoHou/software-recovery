// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network.jms;

import org.springframework.jndi.JndiTemplate;

public class JmsQueueConnector extends SimpleJmsQueueConnector
{
    public void setJndiLocalTemplate(final JndiTemplate template) {
        super.setJndiLocalTemplate(new JndiTemplateLookupFactory(template));
    }
    
    public void setJndiOutboundTemplate(final JndiTemplate template) {
        super.setJndiOutboundTemplate(new JndiTemplateLookupFactory(template));
    }
}
