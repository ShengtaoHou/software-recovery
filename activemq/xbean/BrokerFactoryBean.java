// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.xbean;

import java.beans.PropertyEditorManager;
import org.apache.xbean.spring.context.impl.URIEditor;
import java.net.URI;
import org.springframework.beans.BeansException;
import org.apache.activemq.broker.BrokerService;
import org.springframework.context.ApplicationContext;
import org.apache.xbean.spring.context.ResourceXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.context.ApplicationContextAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.FactoryBean;

public class BrokerFactoryBean implements FactoryBean, InitializingBean, DisposableBean, ApplicationContextAware
{
    private Resource config;
    private XBeanBrokerService broker;
    private boolean start;
    private ResourceXmlApplicationContext context;
    private ApplicationContext parentContext;
    private boolean systemExitOnShutdown;
    private int systemExitOnShutdownExitCode;
    
    public BrokerFactoryBean() {
    }
    
    public BrokerFactoryBean(final Resource config) {
        this.config = config;
    }
    
    public Object getObject() throws Exception {
        return this.broker;
    }
    
    public Class getObjectType() {
        return BrokerService.class;
    }
    
    public boolean isSingleton() {
        return true;
    }
    
    public void setApplicationContext(final ApplicationContext parentContext) throws BeansException {
        this.parentContext = parentContext;
    }
    
    public void afterPropertiesSet() throws Exception {
        if (this.config == null) {
            throw new IllegalArgumentException("config property must be set");
        }
        this.context = new ResourceXmlApplicationContext(this.config, this.parentContext);
        try {
            this.broker = (XBeanBrokerService)this.context.getBean("broker");
        }
        catch (BeansException ex) {}
        if (this.broker == null) {
            final String[] names = this.context.getBeanNamesForType((Class)BrokerService.class);
            for (int i = 0; i < names.length; ++i) {
                final String name = names[i];
                this.broker = (XBeanBrokerService)this.context.getBean(name);
                if (this.broker != null) {
                    break;
                }
            }
        }
        if (this.broker == null) {
            throw new IllegalArgumentException("The configuration has no BrokerService instance for resource: " + this.config);
        }
        if (this.systemExitOnShutdown) {
            this.broker.addShutdownHook(new Runnable() {
                @Override
                public void run() {
                    System.exit(BrokerFactoryBean.this.systemExitOnShutdownExitCode);
                }
            });
        }
        if (this.start) {
            this.broker.start();
        }
    }
    
    public void destroy() throws Exception {
        if (this.context != null) {
            this.context.close();
        }
        if (this.broker != null) {
            this.broker.stop();
        }
    }
    
    public Resource getConfig() {
        return this.config;
    }
    
    public void setConfig(final Resource config) {
        this.config = config;
    }
    
    public BrokerService getBroker() {
        return this.broker;
    }
    
    public boolean isStart() {
        return this.start;
    }
    
    public void setStart(final boolean start) {
        this.start = start;
    }
    
    public boolean isSystemExitOnStop() {
        return this.systemExitOnShutdown;
    }
    
    public void setSystemExitOnStop(final boolean systemExitOnStop) {
        this.systemExitOnShutdown = systemExitOnStop;
    }
    
    public boolean isSystemExitOnShutdown() {
        return this.systemExitOnShutdown;
    }
    
    public void setSystemExitOnShutdown(final boolean systemExitOnShutdown) {
        this.systemExitOnShutdown = systemExitOnShutdown;
    }
    
    public int getSystemExitOnShutdownExitCode() {
        return this.systemExitOnShutdownExitCode;
    }
    
    public void setSystemExitOnShutdownExitCode(final int systemExitOnShutdownExitCode) {
        this.systemExitOnShutdownExitCode = systemExitOnShutdownExitCode;
    }
    
    static {
        PropertyEditorManager.registerEditor(URI.class, URIEditor.class);
    }
}
