// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.spring;

import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.apache.activemq.broker.BrokerContext;

public class SpringBrokerContext implements BrokerContext, ApplicationContextAware
{
    ApplicationContext applicationContext;
    String configurationUrl;
    
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    
    @Override
    public Object getBean(final String name) {
        try {
            return this.applicationContext.getBean(name);
        }
        catch (BeansException ex) {
            return null;
        }
    }
    
    @Override
    public Map getBeansOfType(final Class type) {
        return this.applicationContext.getBeansOfType(type);
    }
    
    public void setConfigurationUrl(final String configurationUrl) {
        this.configurationUrl = configurationUrl;
    }
    
    @Override
    public String getConfigurationUrl() {
        return this.configurationUrl;
    }
}
