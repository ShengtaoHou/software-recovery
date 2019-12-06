// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import java.util.Properties;
import org.apache.activemq.management.JMSStatsImpl;
import org.apache.activemq.transport.Transport;
import javax.jms.XATopicConnection;
import javax.jms.XAQueueConnection;
import javax.jms.JMSException;
import javax.jms.XAConnection;
import java.net.URI;
import javax.jms.XATopicConnectionFactory;
import javax.jms.XAQueueConnectionFactory;
import javax.jms.XAConnectionFactory;

public class ActiveMQXAConnectionFactory extends ActiveMQConnectionFactory implements XAConnectionFactory, XAQueueConnectionFactory, XATopicConnectionFactory
{
    public ActiveMQXAConnectionFactory() {
    }
    
    public ActiveMQXAConnectionFactory(final String userName, final String password, final String brokerURL) {
        super(userName, password, brokerURL);
    }
    
    public ActiveMQXAConnectionFactory(final String userName, final String password, final URI brokerURL) {
        super(userName, password, brokerURL);
    }
    
    public ActiveMQXAConnectionFactory(final String brokerURL) {
        super(brokerURL);
    }
    
    public ActiveMQXAConnectionFactory(final URI brokerURL) {
        super(brokerURL);
    }
    
    @Override
    public XAConnection createXAConnection() throws JMSException {
        return (XAConnection)this.createActiveMQConnection();
    }
    
    @Override
    public XAConnection createXAConnection(final String userName, final String password) throws JMSException {
        return (XAConnection)this.createActiveMQConnection(userName, password);
    }
    
    @Override
    public XAQueueConnection createXAQueueConnection() throws JMSException {
        return (XAQueueConnection)this.createActiveMQConnection();
    }
    
    @Override
    public XAQueueConnection createXAQueueConnection(final String userName, final String password) throws JMSException {
        return (XAQueueConnection)this.createActiveMQConnection(userName, password);
    }
    
    @Override
    public XATopicConnection createXATopicConnection() throws JMSException {
        return (XATopicConnection)this.createActiveMQConnection();
    }
    
    @Override
    public XATopicConnection createXATopicConnection(final String userName, final String password) throws JMSException {
        return (XATopicConnection)this.createActiveMQConnection(userName, password);
    }
    
    @Override
    protected ActiveMQConnection createActiveMQConnection(final Transport transport, final JMSStatsImpl stats) throws Exception {
        final ActiveMQXAConnection connection = new ActiveMQXAConnection(transport, this.getClientIdGenerator(), this.getConnectionIdGenerator(), stats);
        this.configureXAConnection(connection);
        return connection;
    }
    
    private void configureXAConnection(final ActiveMQXAConnection connection) {
        connection.setXaAckMode(this.xaAckMode);
    }
    
    public int getXaAckMode() {
        return this.xaAckMode;
    }
    
    public void setXaAckMode(final int xaAckMode) {
        this.xaAckMode = xaAckMode;
    }
    
    @Override
    public void populateProperties(final Properties props) {
        super.populateProperties(props);
        props.put("xaAckMode", Integer.toString(this.xaAckMode));
    }
}
