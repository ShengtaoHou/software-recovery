// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.xbean;

import org.apache.activemq.broker.BrokerService;
import org.springframework.core.io.Resource;
import java.util.HashMap;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.FactoryBean;

public class PooledBrokerFactoryBean implements FactoryBean, InitializingBean, DisposableBean
{
    static final HashMap<String, SharedBroker> SHARED_BROKER_MAP;
    private boolean start;
    private Resource config;
    
    public void afterPropertiesSet() throws Exception {
        synchronized (PooledBrokerFactoryBean.SHARED_BROKER_MAP) {
            SharedBroker sharedBroker = PooledBrokerFactoryBean.SHARED_BROKER_MAP.get(this.config.getFilename());
            if (sharedBroker == null) {
                sharedBroker = new SharedBroker();
                (sharedBroker.factory = new BrokerFactoryBean()).setConfig(this.config);
                sharedBroker.factory.setStart(this.start);
                sharedBroker.factory.afterPropertiesSet();
                PooledBrokerFactoryBean.SHARED_BROKER_MAP.put(this.config.getFilename(), sharedBroker);
            }
            final SharedBroker sharedBroker2 = sharedBroker;
            ++sharedBroker2.refCount;
        }
    }
    
    public void destroy() throws Exception {
        synchronized (PooledBrokerFactoryBean.SHARED_BROKER_MAP) {
            final SharedBroker sharedBroker = PooledBrokerFactoryBean.SHARED_BROKER_MAP.get(this.config.getFilename());
            if (sharedBroker != null) {
                final SharedBroker sharedBroker2 = sharedBroker;
                --sharedBroker2.refCount;
                if (sharedBroker.refCount == 0) {
                    sharedBroker.factory.destroy();
                    PooledBrokerFactoryBean.SHARED_BROKER_MAP.remove(this.config.getFilename());
                }
            }
        }
    }
    
    public Resource getConfig() {
        return this.config;
    }
    
    public Object getObject() throws Exception {
        synchronized (PooledBrokerFactoryBean.SHARED_BROKER_MAP) {
            final SharedBroker sharedBroker = PooledBrokerFactoryBean.SHARED_BROKER_MAP.get(this.config.getFilename());
            if (sharedBroker != null) {
                return sharedBroker.factory.getObject();
            }
        }
        return null;
    }
    
    public Class getObjectType() {
        return BrokerService.class;
    }
    
    public boolean isSingleton() {
        return true;
    }
    
    public boolean isStart() {
        return this.start;
    }
    
    public void setConfig(final Resource config) {
        this.config = config;
    }
    
    public void setStart(final boolean start) {
        this.start = start;
    }
    
    static {
        SHARED_BROKER_MAP = new HashMap<String, SharedBroker>();
    }
    
    static class SharedBroker
    {
        BrokerFactoryBean factory;
        int refCount;
    }
}
