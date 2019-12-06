// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.jms.pool;

import org.slf4j.LoggerFactory;
import javax.jms.TopicConnection;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.naming.NamingEnumeration;
import javax.naming.Binding;
import java.util.Map;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.jms.Connection;
import javax.naming.InitialContext;
import javax.transaction.TransactionManager;
import org.slf4j.Logger;
import javax.jms.TopicConnectionFactory;
import javax.jms.QueueConnectionFactory;
import java.io.Serializable;
import javax.naming.spi.ObjectFactory;

public class XaPooledConnectionFactory extends PooledConnectionFactory implements ObjectFactory, Serializable, QueueConnectionFactory, TopicConnectionFactory
{
    private static final transient Logger LOG;
    private TransactionManager transactionManager;
    private boolean tmFromJndi;
    private String tmJndiName;
    
    public XaPooledConnectionFactory() {
        this.tmFromJndi = false;
        this.tmJndiName = "java:/TransactionManager";
    }
    
    public TransactionManager getTransactionManager() {
        if (this.transactionManager == null && this.tmFromJndi) {
            try {
                this.transactionManager = (TransactionManager)new InitialContext().lookup(this.getTmJndiName());
            }
            catch (Throwable ignored) {
                if (XaPooledConnectionFactory.LOG.isTraceEnabled()) {
                    XaPooledConnectionFactory.LOG.trace("exception on tmFromJndi: " + this.getTmJndiName(), ignored);
                }
            }
        }
        return this.transactionManager;
    }
    
    public void setTransactionManager(final TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
    
    @Override
    protected ConnectionPool createConnectionPool(final Connection connection) {
        return new XaConnectionPool(connection, this.getTransactionManager());
    }
    
    @Override
    public Object getObjectInstance(final Object obj, final Name name, final Context nameCtx, final Hashtable<?, ?> environment) throws Exception {
        this.setTmFromJndi(true);
        this.configFromJndiConf(obj);
        if (environment != null) {
            IntrospectionSupport.setProperties(this, environment);
        }
        return this;
    }
    
    private void configFromJndiConf(final Object rootContextName) {
        if (rootContextName instanceof String) {
            String name = (String)rootContextName;
            name = name.substring(0, name.lastIndexOf(47)) + "/conf" + name.substring(name.lastIndexOf(47));
            try {
                final InitialContext ctx = new InitialContext();
                final NamingEnumeration bindings = ctx.listBindings(name);
                while (bindings.hasMore()) {
                    final Binding bd = bindings.next();
                    IntrospectionSupport.setProperty(this, bd.getName(), bd.getObject());
                }
            }
            catch (Exception ignored) {
                if (XaPooledConnectionFactory.LOG.isTraceEnabled()) {
                    XaPooledConnectionFactory.LOG.trace("exception on config from jndi: " + name, ignored);
                }
            }
        }
    }
    
    public String getTmJndiName() {
        return this.tmJndiName;
    }
    
    public void setTmJndiName(final String tmJndiName) {
        this.tmJndiName = tmJndiName;
    }
    
    public boolean isTmFromJndi() {
        return this.tmFromJndi;
    }
    
    public void setTmFromJndi(final boolean tmFromJndi) {
        this.tmFromJndi = tmFromJndi;
    }
    
    @Override
    public QueueConnection createQueueConnection() throws JMSException {
        return (QueueConnection)this.createConnection();
    }
    
    @Override
    public QueueConnection createQueueConnection(final String userName, final String password) throws JMSException {
        return (QueueConnection)this.createConnection(userName, password);
    }
    
    @Override
    public TopicConnection createTopicConnection() throws JMSException {
        return (TopicConnection)this.createConnection();
    }
    
    @Override
    public TopicConnection createTopicConnection(final String userName, final String password) throws JMSException {
        return (TopicConnection)this.createConnection(userName, password);
    }
    
    static {
        LOG = LoggerFactory.getLogger(XaPooledConnectionFactory.class);
    }
}
