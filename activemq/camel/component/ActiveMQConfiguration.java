// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.camel.component;

import java.lang.reflect.Constructor;
import org.apache.activemq.Service;
import org.springframework.jms.connection.SingleConnectionFactory;
import javax.jms.ConnectionFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.apache.camel.component.jms.JmsConfiguration;

public class ActiveMQConfiguration extends JmsConfiguration
{
    private String brokerURL;
    private boolean useSingleConnection;
    private boolean usePooledConnection;
    private String userName;
    private String password;
    private ActiveMQComponent activeMQComponent;
    
    public ActiveMQConfiguration() {
        this.brokerURL = ActiveMQConnectionFactory.DEFAULT_BROKER_URL;
        this.useSingleConnection = false;
        this.usePooledConnection = true;
    }
    
    public String getBrokerURL() {
        return this.brokerURL;
    }
    
    public void setBrokerURL(final String brokerURL) {
        this.brokerURL = brokerURL;
    }
    
    public boolean isUseSingleConnection() {
        return this.useSingleConnection;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public void setUserName(final String userName) {
        this.userName = userName;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public void setUseSingleConnection(final boolean useSingleConnection) {
        this.useSingleConnection = useSingleConnection;
    }
    
    public boolean isUsePooledConnection() {
        return this.usePooledConnection;
    }
    
    public void setUsePooledConnection(final boolean usePooledConnection) {
        this.usePooledConnection = usePooledConnection;
    }
    
    protected PlatformTransactionManager createTransactionManager() {
        final JmsTransactionManager answer = new JmsTransactionManager(this.getConnectionFactory());
        answer.afterPropertiesSet();
        return (PlatformTransactionManager)answer;
    }
    
    protected void setActiveMQComponent(final ActiveMQComponent activeMQComponent) {
        this.activeMQComponent = activeMQComponent;
    }
    
    protected ConnectionFactory createConnectionFactory() {
        final ActiveMQConnectionFactory answer = new ActiveMQConnectionFactory();
        if (this.userName != null) {
            answer.setUserName(this.userName);
        }
        if (this.password != null) {
            answer.setPassword(this.password);
        }
        if (answer.getBeanName() == null) {
            answer.setBeanName("Camel");
        }
        answer.setBrokerURL(this.getBrokerURL());
        if (this.isUseSingleConnection()) {
            final SingleConnectionFactory scf = new SingleConnectionFactory((ConnectionFactory)answer);
            if (this.activeMQComponent != null) {
                this.activeMQComponent.addSingleConnectionFactory(scf);
            }
            return (ConnectionFactory)scf;
        }
        if (this.isUsePooledConnection()) {
            final ConnectionFactory pcf = this.createPooledConnectionFactory(answer);
            if (this.activeMQComponent != null) {
                this.activeMQComponent.addPooledConnectionFactoryService((Service)pcf);
            }
            return pcf;
        }
        return answer;
    }
    
    protected ConnectionFactory createPooledConnectionFactory(final ActiveMQConnectionFactory connectionFactory) {
        try {
            final Class type = loadClass("org.apache.activemq.pool.PooledConnectionFactory", this.getClass().getClassLoader());
            final Constructor constructor = type.getConstructor(org.apache.activemq.ActiveMQConnectionFactory.class);
            return constructor.newInstance(connectionFactory);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to instantiate PooledConnectionFactory: " + e, e);
        }
    }
    
    public static Class<?> loadClass(final String name, final ClassLoader loader) throws ClassNotFoundException {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
            try {
                return contextClassLoader.loadClass(name);
            }
            catch (ClassNotFoundException e2) {
                try {
                    return loader.loadClass(name);
                }
                catch (ClassNotFoundException e1) {
                    throw e1;
                }
            }
        }
        return loader.loadClass(name);
    }
}
