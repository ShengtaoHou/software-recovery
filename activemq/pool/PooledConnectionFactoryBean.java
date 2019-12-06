// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.pool;

import org.slf4j.LoggerFactory;
import javax.annotation.PreDestroy;
import javax.transaction.TransactionManager;
import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.FactoryBean;

public class PooledConnectionFactoryBean implements FactoryBean
{
    private static final Logger LOGGER;
    private PooledConnectionFactory pooledConnectionFactory;
    private ConnectionFactory connectionFactory;
    private int maxConnections;
    private int maximumActive;
    private Object transactionManager;
    private String resourceName;
    
    public PooledConnectionFactoryBean() {
        this.maxConnections = 1;
        this.maximumActive = 500;
    }
    
    public int getMaxConnections() {
        return this.maxConnections;
    }
    
    public void setMaxConnections(final int maxConnections) {
        this.maxConnections = maxConnections;
    }
    
    public int getMaximumActive() {
        return this.maximumActive;
    }
    
    public void setMaximumActive(final int maximumActive) {
        this.maximumActive = maximumActive;
    }
    
    public Object getTransactionManager() {
        return this.transactionManager;
    }
    
    public void setTransactionManager(final Object transactionManager) {
        this.transactionManager = transactionManager;
    }
    
    public String getResourceName() {
        return this.resourceName;
    }
    
    public void setResourceName(final String resourceName) {
        this.resourceName = resourceName;
    }
    
    public ConnectionFactory getConnectionFactory() {
        return this.connectionFactory;
    }
    
    public void setConnectionFactory(final ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
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
        if (this.pooledConnectionFactory == null && this.transactionManager != null && this.resourceName != null) {
            try {
                PooledConnectionFactoryBean.LOGGER.debug("Trying to build a JcaPooledConnectionFactory");
                final JcaPooledConnectionFactory f = new JcaPooledConnectionFactory();
                f.setName(this.resourceName);
                f.setTransactionManager((TransactionManager)this.transactionManager);
                f.setMaxConnections(this.maxConnections);
                f.setMaximumActiveSessionPerConnection(this.maximumActive);
                f.setConnectionFactory(this.connectionFactory);
                this.pooledConnectionFactory = f;
            }
            catch (Throwable t) {
                PooledConnectionFactoryBean.LOGGER.debug("Could not create JCA enabled connection factory: " + t, t);
            }
        }
        if (this.pooledConnectionFactory == null && this.transactionManager != null) {
            try {
                PooledConnectionFactoryBean.LOGGER.debug("Trying to build a XaPooledConnectionFactory");
                final XaPooledConnectionFactory f2 = new XaPooledConnectionFactory();
                f2.setTransactionManager((TransactionManager)this.transactionManager);
                f2.setMaxConnections(this.maxConnections);
                f2.setMaximumActiveSessionPerConnection(this.maximumActive);
                f2.setConnectionFactory(this.connectionFactory);
                this.pooledConnectionFactory = f2;
            }
            catch (Throwable t) {
                PooledConnectionFactoryBean.LOGGER.debug("Could not create XA enabled connection factory: " + t, t);
            }
        }
        if (this.pooledConnectionFactory == null) {
            try {
                PooledConnectionFactoryBean.LOGGER.debug("Trying to build a PooledConnectionFactory");
                final PooledConnectionFactory f3 = new PooledConnectionFactory();
                f3.setMaxConnections(this.maxConnections);
                f3.setMaximumActiveSessionPerConnection(this.maximumActive);
                f3.setConnectionFactory(this.connectionFactory);
                this.pooledConnectionFactory = f3;
            }
            catch (Throwable t) {
                PooledConnectionFactoryBean.LOGGER.debug("Could not create pooled connection factory: " + t, t);
            }
        }
        if (this.pooledConnectionFactory == null) {
            throw new IllegalStateException("Unable to create pooled connection factory.  Enable DEBUG log level for more informations");
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
        if (this.pooledConnectionFactory != null) {
            this.pooledConnectionFactory.stop();
            this.pooledConnectionFactory = null;
        }
    }
    
    public Object getObject() throws Exception {
        if (this.pooledConnectionFactory == null) {
            this.afterPropertiesSet();
        }
        return this.pooledConnectionFactory;
    }
    
    public Class getObjectType() {
        return ConnectionFactory.class;
    }
    
    public boolean isSingleton() {
        return true;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(PooledConnectionFactoryBean.class);
    }
}
