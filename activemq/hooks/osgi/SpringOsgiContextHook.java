// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.hooks.osgi;

import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.osgi.framework.BundleException;
import org.springframework.osgi.context.support.OsgiBundleXmlApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.ApplicationContext;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContextAware;

public class SpringOsgiContextHook implements Runnable, ApplicationContextAware
{
    private static final transient Logger LOG;
    ApplicationContext applicationContext;
    
    @Override
    public void run() {
        if (this.applicationContext instanceof ConfigurableApplicationContext) {
            ((ConfigurableApplicationContext)this.applicationContext).close();
        }
        if (this.applicationContext instanceof OsgiBundleXmlApplicationContext) {
            try {
                ((OsgiBundleXmlApplicationContext)this.applicationContext).getBundle().stop();
            }
            catch (BundleException e) {
                SpringOsgiContextHook.LOG.info("Error stopping OSGi bundle " + e, (Throwable)e);
            }
        }
    }
    
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    
    static {
        LOG = LoggerFactory.getLogger(SpringOsgiContextHook.class);
    }
}
