// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.xbean;

import org.springframework.beans.CachedIntrospectionResults;
import javax.annotation.PreDestroy;
import java.io.IOException;
import org.apache.activemq.usage.SystemUsage;
import javax.annotation.PostConstruct;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;

public class XBeanBrokerService extends BrokerService
{
    private boolean start;
    
    public XBeanBrokerService() {
        this.start = BrokerFactory.getStartDefault();
    }
    
    @PostConstruct
    private void postConstruct() {
        try {
            this.afterPropertiesSet();
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void afterPropertiesSet() throws Exception {
        this.ensureSystemUsageHasStore();
        if (this.shouldAutostart()) {
            this.start();
        }
    }
    
    @Override
    protected boolean shouldAutostart() {
        return this.start;
    }
    
    private void ensureSystemUsageHasStore() throws IOException {
        final SystemUsage usage = this.getSystemUsage();
        if (usage.getStoreUsage().getStore() == null) {
            usage.getStoreUsage().setStore(this.getPersistenceAdapter());
        }
        if (usage.getTempUsage().getStore() == null) {
            usage.getTempUsage().setStore(this.getTempDataStore());
        }
        if (usage.getJobSchedulerUsage().getStore() == null) {
            usage.getJobSchedulerUsage().setStore(this.getJobSchedulerStore());
        }
    }
    
    @PreDestroy
    private void preDestroy() {
        try {
            this.destroy();
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void destroy() throws Exception {
        this.stop();
    }
    
    @Override
    public void stop() throws Exception {
        CachedIntrospectionResults.clearClassLoader(this.getClass().getClassLoader());
        super.stop();
    }
    
    public void setStart(final boolean start) {
        this.start = start;
    }
}
