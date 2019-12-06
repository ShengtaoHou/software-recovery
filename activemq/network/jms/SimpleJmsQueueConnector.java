// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network.jms;

import org.slf4j.LoggerFactory;
import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.QueueSession;
import javax.jms.MessageConsumer;
import javax.naming.NamingException;
import javax.jms.ExceptionListener;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import org.slf4j.Logger;

public class SimpleJmsQueueConnector extends JmsConnector
{
    private static final Logger LOG;
    private String outboundQueueConnectionFactoryName;
    private String localConnectionFactoryName;
    private QueueConnectionFactory outboundQueueConnectionFactory;
    private QueueConnectionFactory localQueueConnectionFactory;
    private InboundQueueBridge[] inboundQueueBridges;
    private OutboundQueueBridge[] outboundQueueBridges;
    
    public InboundQueueBridge[] getInboundQueueBridges() {
        return this.inboundQueueBridges;
    }
    
    public void setInboundQueueBridges(final InboundQueueBridge[] inboundQueueBridges) {
        this.inboundQueueBridges = inboundQueueBridges;
    }
    
    public OutboundQueueBridge[] getOutboundQueueBridges() {
        return this.outboundQueueBridges;
    }
    
    public void setOutboundQueueBridges(final OutboundQueueBridge[] outboundQueueBridges) {
        this.outboundQueueBridges = outboundQueueBridges;
    }
    
    public QueueConnectionFactory getLocalQueueConnectionFactory() {
        return this.localQueueConnectionFactory;
    }
    
    public void setLocalQueueConnectionFactory(final QueueConnectionFactory localConnectionFactory) {
        this.localQueueConnectionFactory = localConnectionFactory;
    }
    
    public QueueConnectionFactory getOutboundQueueConnectionFactory() {
        return this.outboundQueueConnectionFactory;
    }
    
    public String getOutboundQueueConnectionFactoryName() {
        return this.outboundQueueConnectionFactoryName;
    }
    
    public void setOutboundQueueConnectionFactoryName(final String foreignQueueConnectionFactoryName) {
        this.outboundQueueConnectionFactoryName = foreignQueueConnectionFactoryName;
    }
    
    public String getLocalConnectionFactoryName() {
        return this.localConnectionFactoryName;
    }
    
    public void setLocalConnectionFactoryName(final String localConnectionFactoryName) {
        this.localConnectionFactoryName = localConnectionFactoryName;
    }
    
    public QueueConnection getLocalQueueConnection() {
        return this.localConnection.get();
    }
    
    public void setLocalQueueConnection(final QueueConnection localQueueConnection) {
        this.localConnection.set(localQueueConnection);
    }
    
    public QueueConnection getOutboundQueueConnection() {
        return this.foreignConnection.get();
    }
    
    public void setOutboundQueueConnection(final QueueConnection foreignQueueConnection) {
        this.foreignConnection.set(foreignQueueConnection);
    }
    
    public void setOutboundQueueConnectionFactory(final QueueConnectionFactory foreignQueueConnectionFactory) {
        this.outboundQueueConnectionFactory = foreignQueueConnectionFactory;
    }
    
    @Override
    protected void initializeForeignConnection() throws NamingException, JMSException {
        QueueConnection newConnection;
        if (this.foreignConnection.get() == null) {
            if (this.outboundQueueConnectionFactory == null) {
                if (this.outboundQueueConnectionFactoryName == null) {
                    throw new JMSException("Cannot create foreignConnection - no information");
                }
                this.outboundQueueConnectionFactory = this.jndiOutboundTemplate.lookup(this.outboundQueueConnectionFactoryName, QueueConnectionFactory.class);
                if (this.outboundUsername != null) {
                    newConnection = this.outboundQueueConnectionFactory.createQueueConnection(this.outboundUsername, this.outboundPassword);
                }
                else {
                    newConnection = this.outboundQueueConnectionFactory.createQueueConnection();
                }
            }
            else if (this.outboundUsername != null) {
                newConnection = this.outboundQueueConnectionFactory.createQueueConnection(this.outboundUsername, this.outboundPassword);
            }
            else {
                newConnection = this.outboundQueueConnectionFactory.createQueueConnection();
            }
        }
        else {
            newConnection = this.foreignConnection.getAndSet(null);
        }
        if (this.outboundClientId != null && this.outboundClientId.length() > 0) {
            newConnection.setClientID(this.getOutboundClientId());
        }
        newConnection.start();
        this.outboundMessageConvertor.setConnection(newConnection);
        this.initializeInboundDestinationBridgesOutboundSide(newConnection);
        this.initializeOutboundDestinationBridgesOutboundSide(newConnection);
        newConnection.setExceptionListener(new ExceptionListener() {
            @Override
            public void onException(final JMSException exception) {
                SimpleJmsQueueConnector.this.handleConnectionFailure(newConnection);
            }
        });
        this.foreignConnection.set(newConnection);
    }
    
    @Override
    protected void initializeLocalConnection() throws NamingException, JMSException {
        QueueConnection newConnection;
        if (this.localConnection.get() == null) {
            if (this.localQueueConnectionFactory == null) {
                if (this.embeddedConnectionFactory == null) {
                    if (this.localConnectionFactoryName == null) {
                        throw new JMSException("Cannot create localConnection - no information");
                    }
                    this.localQueueConnectionFactory = this.jndiLocalTemplate.lookup(this.localConnectionFactoryName, QueueConnectionFactory.class);
                    if (this.localUsername != null) {
                        newConnection = this.localQueueConnectionFactory.createQueueConnection(this.localUsername, this.localPassword);
                    }
                    else {
                        newConnection = this.localQueueConnectionFactory.createQueueConnection();
                    }
                }
                else {
                    newConnection = this.embeddedConnectionFactory.createQueueConnection();
                }
            }
            else if (this.localUsername != null) {
                newConnection = this.localQueueConnectionFactory.createQueueConnection(this.localUsername, this.localPassword);
            }
            else {
                newConnection = this.localQueueConnectionFactory.createQueueConnection();
            }
        }
        else {
            newConnection = this.localConnection.getAndSet(null);
        }
        if (this.localClientId != null && this.localClientId.length() > 0) {
            newConnection.setClientID(this.getLocalClientId());
        }
        newConnection.start();
        this.inboundMessageConvertor.setConnection(newConnection);
        this.initializeInboundDestinationBridgesLocalSide(newConnection);
        this.initializeOutboundDestinationBridgesLocalSide(newConnection);
        newConnection.setExceptionListener(new ExceptionListener() {
            @Override
            public void onException(final JMSException exception) {
                SimpleJmsQueueConnector.this.handleConnectionFailure(newConnection);
            }
        });
        this.localConnection.set(newConnection);
    }
    
    protected void initializeInboundDestinationBridgesOutboundSide(final QueueConnection connection) throws JMSException {
        if (this.inboundQueueBridges != null) {
            final QueueSession outboundSession = connection.createQueueSession(false, 1);
            for (final InboundQueueBridge bridge : this.inboundQueueBridges) {
                final String queueName = bridge.getInboundQueueName();
                final Queue foreignQueue = this.createForeignQueue(outboundSession, queueName);
                bridge.setConsumer(null);
                bridge.setConsumerQueue(foreignQueue);
                bridge.setConsumerConnection(connection);
                bridge.setJmsConnector(this);
                this.addInboundBridge(bridge);
            }
            outboundSession.close();
        }
    }
    
    protected void initializeInboundDestinationBridgesLocalSide(final QueueConnection connection) throws JMSException {
        if (this.inboundQueueBridges != null) {
            final QueueSession localSession = connection.createQueueSession(false, 1);
            for (final InboundQueueBridge bridge : this.inboundQueueBridges) {
                final String localQueueName = bridge.getLocalQueueName();
                final Queue activemqQueue = this.createActiveMQQueue(localSession, localQueueName);
                bridge.setProducerQueue(activemqQueue);
                bridge.setProducerConnection(connection);
                if (bridge.getJmsMessageConvertor() == null) {
                    bridge.setJmsMessageConvertor(this.getInboundMessageConvertor());
                }
                bridge.setJmsConnector(this);
                this.addInboundBridge(bridge);
            }
            localSession.close();
        }
    }
    
    protected void initializeOutboundDestinationBridgesOutboundSide(final QueueConnection connection) throws JMSException {
        if (this.outboundQueueBridges != null) {
            final QueueSession outboundSession = connection.createQueueSession(false, 1);
            for (final OutboundQueueBridge bridge : this.outboundQueueBridges) {
                final String queueName = bridge.getOutboundQueueName();
                final Queue foreignQueue = this.createForeignQueue(outboundSession, queueName);
                bridge.setProducerQueue(foreignQueue);
                bridge.setProducerConnection(connection);
                if (bridge.getJmsMessageConvertor() == null) {
                    bridge.setJmsMessageConvertor(this.getOutboundMessageConvertor());
                }
                bridge.setJmsConnector(this);
                this.addOutboundBridge(bridge);
            }
            outboundSession.close();
        }
    }
    
    protected void initializeOutboundDestinationBridgesLocalSide(final QueueConnection connection) throws JMSException {
        if (this.outboundQueueBridges != null) {
            final QueueSession localSession = connection.createQueueSession(false, 1);
            for (final OutboundQueueBridge bridge : this.outboundQueueBridges) {
                final String localQueueName = bridge.getLocalQueueName();
                final Queue activemqQueue = this.createActiveMQQueue(localSession, localQueueName);
                bridge.setConsumer(null);
                bridge.setConsumerQueue(activemqQueue);
                bridge.setConsumerConnection(connection);
                bridge.setJmsConnector(this);
                this.addOutboundBridge(bridge);
            }
            localSession.close();
        }
    }
    
    @Override
    protected Destination createReplyToBridge(final Destination destination, final Connection replyToProducerConnection, final Connection replyToConsumerConnection) {
        final Queue replyToProducerQueue = (Queue)destination;
        final boolean isInbound = replyToProducerConnection.equals(this.localConnection.get());
        if (isInbound) {
            InboundQueueBridge bridge = this.replyToBridges.get(replyToProducerQueue);
            if (bridge == null) {
                bridge = new InboundQueueBridge() {
                    @Override
                    protected Destination processReplyToDestination(final Destination destination) {
                        return null;
                    }
                };
                try {
                    final QueueSession replyToConsumerSession = ((QueueConnection)replyToConsumerConnection).createQueueSession(false, 1);
                    final Queue replyToConsumerQueue = replyToConsumerSession.createTemporaryQueue();
                    replyToConsumerSession.close();
                    bridge.setConsumerQueue(replyToConsumerQueue);
                    bridge.setProducerQueue(replyToProducerQueue);
                    bridge.setProducerConnection((QueueConnection)replyToProducerConnection);
                    bridge.setConsumerConnection((QueueConnection)replyToConsumerConnection);
                    bridge.setDoHandleReplyTo(false);
                    if (bridge.getJmsMessageConvertor() == null) {
                        bridge.setJmsMessageConvertor(this.getInboundMessageConvertor());
                    }
                    bridge.setJmsConnector(this);
                    bridge.start();
                    SimpleJmsQueueConnector.LOG.info("Created replyTo bridge for {}", replyToProducerQueue);
                }
                catch (Exception e) {
                    SimpleJmsQueueConnector.LOG.error("Failed to create replyTo bridge for queue: {}", replyToProducerQueue, e);
                    return null;
                }
                this.replyToBridges.put(replyToProducerQueue, bridge);
            }
            return bridge.getConsumerQueue();
        }
        OutboundQueueBridge bridge2 = this.replyToBridges.get(replyToProducerQueue);
        if (bridge2 == null) {
            bridge2 = new OutboundQueueBridge() {
                @Override
                protected Destination processReplyToDestination(final Destination destination) {
                    return null;
                }
            };
            try {
                final QueueSession replyToConsumerSession = ((QueueConnection)replyToConsumerConnection).createQueueSession(false, 1);
                final Queue replyToConsumerQueue = replyToConsumerSession.createTemporaryQueue();
                replyToConsumerSession.close();
                bridge2.setConsumerQueue(replyToConsumerQueue);
                bridge2.setProducerQueue(replyToProducerQueue);
                bridge2.setProducerConnection((QueueConnection)replyToProducerConnection);
                bridge2.setConsumerConnection((QueueConnection)replyToConsumerConnection);
                bridge2.setDoHandleReplyTo(false);
                if (bridge2.getJmsMessageConvertor() == null) {
                    bridge2.setJmsMessageConvertor(this.getOutboundMessageConvertor());
                }
                bridge2.setJmsConnector(this);
                bridge2.start();
                SimpleJmsQueueConnector.LOG.info("Created replyTo bridge for {}", replyToProducerQueue);
            }
            catch (Exception e) {
                SimpleJmsQueueConnector.LOG.error("Failed to create replyTo bridge for queue: {}", replyToProducerQueue, e);
                return null;
            }
            this.replyToBridges.put(replyToProducerQueue, bridge2);
        }
        return bridge2.getConsumerQueue();
    }
    
    protected Queue createActiveMQQueue(final QueueSession session, final String queueName) throws JMSException {
        return session.createQueue(queueName);
    }
    
    protected Queue createForeignQueue(final QueueSession session, final String queueName) throws JMSException {
        Queue result = null;
        if (this.preferJndiDestinationLookup) {
            try {
                result = this.jndiOutboundTemplate.lookup(queueName, Queue.class);
            }
            catch (NamingException e) {
                try {
                    result = session.createQueue(queueName);
                }
                catch (JMSException e2) {
                    final String errStr = "Failed to look-up or create Queue for name: " + queueName;
                    SimpleJmsQueueConnector.LOG.error(errStr, e);
                    final JMSException jmsEx = new JMSException(errStr);
                    jmsEx.setLinkedException(e2);
                    throw jmsEx;
                }
            }
        }
        else {
            try {
                result = session.createQueue(queueName);
            }
            catch (JMSException e3) {
                try {
                    result = this.jndiOutboundTemplate.lookup(queueName, Queue.class);
                }
                catch (NamingException e4) {
                    final String errStr = "Failed to look-up Queue for name: " + queueName;
                    SimpleJmsQueueConnector.LOG.error(errStr, e3);
                    final JMSException jmsEx = new JMSException(errStr);
                    jmsEx.setLinkedException(e4);
                    throw jmsEx;
                }
            }
        }
        return result;
    }
    
    static {
        LOG = LoggerFactory.getLogger(SimpleJmsQueueConnector.class);
    }
}
