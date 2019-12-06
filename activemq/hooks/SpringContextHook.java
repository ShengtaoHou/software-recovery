// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.hooks;

import org.springframework.beans.BeansException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringContextHook implements Runnable, ApplicationContextAware
{
    ApplicationContext applicationContext;
    
    @Override
    public void run() {
        if (this.applicationContext instanceof ConfigurableApplicationContext) {
            ((ConfigurableApplicationContext)this.applicationContext).close();
        }
    }
    
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
