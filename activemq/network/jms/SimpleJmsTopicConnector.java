// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network.jms;

import org.slf4j.LoggerFactory;
import javax.jms.Destination;
import javax.jms.Topic;
import javax.jms.TopicSession;
import javax.jms.MessageConsumer;
import javax.naming.NamingException;
import javax.jms.ExceptionListener;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import org.slf4j.Logger;

public class SimpleJmsTopicConnector extends JmsConnector
{
    private static final Logger LOG;
    private String outboundTopicConnectionFactoryName;
    private String localConnectionFactoryName;
    private TopicConnectionFactory outboundTopicConnectionFactory;
    private TopicConnectionFactory localTopicConnectionFactory;
    private InboundTopicBridge[] inboundTopicBridges;
    private OutboundTopicBridge[] outboundTopicBridges;
    
    public InboundTopicBridge[] getInboundTopicBridges() {
        return this.inboundTopicBridges;
    }
    
    public void setInboundTopicBridges(final InboundTopicBridge[] inboundTopicBridges) {
        this.inboundTopicBridges = inboundTopicBridges;
    }
    
    public OutboundTopicBridge[] getOutboundTopicBridges() {
        return this.outboundTopicBridges;
    }
    
    public void setOutboundTopicBridges(final OutboundTopicBridge[] outboundTopicBridges) {
        this.outboundTopicBridges = outboundTopicBridges;
    }
    
    public TopicConnectionFactory getLocalTopicConnectionFactory() {
        return this.localTopicConnectionFactory;
    }
    
    public void setLocalTopicConnectionFactory(final TopicConnectionFactory localConnectionFactory) {
        this.localTopicConnectionFactory = localConnectionFactory;
    }
    
    public TopicConnectionFactory getOutboundTopicConnectionFactory() {
        return this.outboundTopicConnectionFactory;
    }
    
    public String getOutboundTopicConnectionFactoryName() {
        return this.outboundTopicConnectionFactoryName;
    }
    
    public void setOutboundTopicConnectionFactoryName(final String foreignTopicConnectionFactoryName) {
        this.outboundTopicConnectionFactoryName = foreignTopicConnectionFactoryName;
    }
    
    public String getLocalConnectionFactoryName() {
        return this.localConnectionFactoryName;
    }
    
    public void setLocalConnectionFactoryName(final String localConnectionFactoryName) {
        this.localConnectionFactoryName = localConnectionFactoryName;
    }
    
    public TopicConnection getLocalTopicConnection() {
        return this.localConnection.get();
    }
    
    public void setLocalTopicConnection(final TopicConnection localTopicConnection) {
        this.localConnection.set(localTopicConnection);
    }
    
    public TopicConnection getOutboundTopicConnection() {
        return this.foreignConnection.get();
    }
    
    public void setOutboundTopicConnection(final TopicConnection foreignTopicConnection) {
        this.foreignConnection.set(foreignTopicConnection);
    }
    
    public void setOutboundTopicConnectionFactory(final TopicConnectionFactory foreignTopicConnectionFactory) {
        this.outboundTopicConnectionFactory = foreignTopicConnectionFactory;
    }
    
    @Override
    protected void initializeForeignConnection() throws NamingException, JMSException {
        TopicConnection newConnection;
        if (this.foreignConnection.get() == null) {
            if (this.outboundTopicConnectionFactory == null) {
                if (this.outboundTopicConnectionFactoryName == null) {
                    throw new JMSException("Cannot create foreignConnection - no information");
                }
                this.outboundTopicConnectionFactory = this.jndiOutboundTemplate.lookup(this.outboundTopicConnectionFactoryName, TopicConnectionFactory.class);
                if (this.outboundUsername != null) {
                    newConnection = this.outboundTopicConnectionFactory.createTopicConnection(this.outboundUsername, this.outboundPassword);
                }
                else {
                    newConnection = this.outboundTopicConnectionFactory.createTopicConnection();
                }
            }
            else if (this.outboundUsername != null) {
                newConnection = this.outboundTopicConnectionFactory.createTopicConnection(this.outboundUsername, this.outboundPassword);
            }
            else {
                newConnection = this.outboundTopicConnectionFactory.createTopicConnection();
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
                SimpleJmsTopicConnector.this.handleConnectionFailure(newConnection);
            }
        });
        this.foreignConnection.set(newConnection);
    }
    
    @Override
    protected void initializeLocalConnection() throws NamingException, JMSException {
        TopicConnection newConnection;
        if (this.localConnection.get() == null) {
            if (this.localTopicConnectionFactory == null) {
                if (this.embeddedConnectionFactory == null) {
                    if (this.localConnectionFactoryName == null) {
                        throw new JMSException("Cannot create localConnection - no information");
                    }
                    this.localTopicConnectionFactory = this.jndiLocalTemplate.lookup(this.localConnectionFactoryName, TopicConnectionFactory.class);
                    if (this.localUsername != null) {
                        newConnection = this.localTopicConnectionFactory.createTopicConnection(this.localUsername, this.localPassword);
                    }
                    else {
                        newConnection = this.localTopicConnectionFactory.createTopicConnection();
                    }
                }
                else {
                    newConnection = this.embeddedConnectionFactory.createTopicConnection();
                }
            }
            else if (this.localUsername != null) {
                newConnection = this.localTopicConnectionFactory.createTopicConnection(this.localUsername, this.localPassword);
            }
            else {
                newConnection = this.localTopicConnectionFactory.createTopicConnection();
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
                SimpleJmsTopicConnector.this.handleConnectionFailure(newConnection);
            }
        });
        this.localConnection.set(newConnection);
    }
    
    protected void initializeInboundDestinationBridgesOutboundSide(final TopicConnection connection) throws JMSException {
        if (this.inboundTopicBridges != null) {
            final TopicSession outboundSession = connection.createTopicSession(false, 1);
            for (final InboundTopicBridge bridge : this.inboundTopicBridges) {
                final String TopicName = bridge.getInboundTopicName();
                final Topic foreignTopic = this.createForeignTopic(outboundSession, TopicName);
                bridge.setConsumer(null);
                bridge.setConsumerTopic(foreignTopic);
                bridge.setConsumerConnection(connection);
                bridge.setJmsConnector(this);
                this.addInboundBridge(bridge);
            }
            outboundSession.close();
        }
    }
    
    protected void initializeInboundDestinationBridgesLocalSide(final TopicConnection connection) throws JMSException {
        if (this.inboundTopicBridges != null) {
            final TopicSession localSession = connection.createTopicSession(false, 1);
            for (final InboundTopicBridge bridge : this.inboundTopicBridges) {
                final String localTopicName = bridge.getLocalTopicName();
                final Topic activemqTopic = this.createActiveMQTopic(localSession, localTopicName);
                bridge.setProducerTopic(activemqTopic);
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
    
    protected void initializeOutboundDestinationBridgesOutboundSide(final TopicConnection connection) throws JMSException {
        if (this.outboundTopicBridges != null) {
            final TopicSession outboundSession = connection.createTopicSession(false, 1);
            for (final OutboundTopicBridge bridge : this.outboundTopicBridges) {
                final String topicName = bridge.getOutboundTopicName();
                final Topic foreignTopic = this.createForeignTopic(outboundSession, topicName);
                bridge.setProducerTopic(foreignTopic);
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
    
    protected void initializeOutboundDestinationBridgesLocalSide(final TopicConnection connection) throws JMSException {
        if (this.outboundTopicBridges != null) {
            final TopicSession localSession = connection.createTopicSession(false, 1);
            for (final OutboundTopicBridge bridge : this.outboundTopicBridges) {
                final String localTopicName = bridge.getLocalTopicName();
                final Topic activemqTopic = this.createActiveMQTopic(localSession, localTopicName);
                bridge.setConsumer(null);
                bridge.setConsumerTopic(activemqTopic);
                bridge.setConsumerConnection(connection);
                bridge.setJmsConnector(this);
                this.addOutboundBridge(bridge);
            }
            localSession.close();
        }
    }
    
    @Override
    protected Destination createReplyToBridge(final Destination destination, final Connection replyToProducerConnection, final Connection replyToConsumerConnection) {
        final Topic replyToProducerTopic = (Topic)destination;
        final boolean isInbound = replyToProducerConnection.equals(this.localConnection.get());
        if (isInbound) {
            InboundTopicBridge bridge = this.replyToBridges.get(replyToProducerTopic);
            if (bridge == null) {
                bridge = new InboundTopicBridge() {
                    @Override
                    protected Destination processReplyToDestination(final Destination destination) {
                        return null;
                    }
                };
                try {
                    final TopicSession replyToConsumerSession = ((TopicConnection)replyToConsumerConnection).createTopicSession(false, 1);
                    final Topic replyToConsumerTopic = replyToConsumerSession.createTemporaryTopic();
                    replyToConsumerSession.close();
                    bridge.setConsumerTopic(replyToConsumerTopic);
                    bridge.setProducerTopic(replyToProducerTopic);
                    bridge.setProducerConnection((TopicConnection)replyToProducerConnection);
                    bridge.setConsumerConnection((TopicConnection)replyToConsumerConnection);
                    bridge.setDoHandleReplyTo(false);
                    if (bridge.getJmsMessageConvertor() == null) {
                        bridge.setJmsMessageConvertor(this.getInboundMessageConvertor());
                    }
                    bridge.setJmsConnector(this);
                    bridge.start();
                    SimpleJmsTopicConnector.LOG.info("Created replyTo bridge for {}", replyToProducerTopic);
                }
                catch (Exception e) {
                    SimpleJmsTopicConnector.LOG.error("Failed to create replyTo bridge for topic: {}", replyToProducerTopic, e);
                    return null;
                }
                this.replyToBridges.put(replyToProducerTopic, bridge);
            }
            return bridge.getConsumerTopic();
        }
        OutboundTopicBridge bridge2 = this.replyToBridges.get(replyToProducerTopic);
        if (bridge2 == null) {
            bridge2 = new OutboundTopicBridge() {
                @Override
                protected Destination processReplyToDestination(final Destination destination) {
                    return null;
                }
            };
            try {
                final TopicSession replyToConsumerSession = ((TopicConnection)replyToConsumerConnection).createTopicSession(false, 1);
                final Topic replyToConsumerTopic = replyToConsumerSession.createTemporaryTopic();
                replyToConsumerSession.close();
                bridge2.setConsumerTopic(replyToConsumerTopic);
                bridge2.setProducerTopic(replyToProducerTopic);
                bridge2.setProducerConnection((TopicConnection)replyToProducerConnection);
                bridge2.setConsumerConnection((TopicConnection)replyToConsumerConnection);
                bridge2.setDoHandleReplyTo(false);
                if (bridge2.getJmsMessageConvertor() == null) {
                    bridge2.setJmsMessageConvertor(this.getOutboundMessageConvertor());
                }
                bridge2.setJmsConnector(this);
                bridge2.start();
                SimpleJmsTopicConnector.LOG.info("Created replyTo bridge for {}", replyToProducerTopic);
            }
            catch (Exception e) {
                SimpleJmsTopicConnector.LOG.error("Failed to create replyTo bridge for topic: {}", replyToProducerTopic, e);
                return null;
            }
            this.replyToBridges.put(replyToProducerTopic, bridge2);
        }
        return bridge2.getConsumerTopic();
    }
    
    protected Topic createActiveMQTopic(final TopicSession session, final String topicName) throws JMSException {
        return session.createTopic(topicName);
    }
    
    protected Topic createForeignTopic(final TopicSession session, final String topicName) throws JMSException {
        Topic result = null;
        if (this.preferJndiDestinationLookup) {
            try {
                result = this.jndiOutboundTemplate.lookup(topicName, Topic.class);
            }
            catch (NamingException e) {
                try {
                    result = session.createTopic(topicName);
                }
                catch (JMSException e2) {
                    final String errStr = "Failed to look-up or create Topic for name: " + topicName;
                    SimpleJmsTopicConnector.LOG.error(errStr, e);
                    final JMSException jmsEx = new JMSException(errStr);
                    jmsEx.setLinkedException(e2);
                    throw jmsEx;
                }
            }
        }
        else {
            try {
                result = session.createTopic(topicName);
            }
            catch (JMSException e3) {
                try {
                    result = this.jndiOutboundTemplate.lookup(topicName, Topic.class);
                }
                catch (NamingException e4) {
                    final String errStr = "Failed to look-up Topic for name: " + topicName;
                    SimpleJmsTopicConnector.LOG.error(errStr, e3);
                    final JMSException jmsEx = new JMSException(errStr);
                    jmsEx.setLinkedException(e4);
                    throw jmsEx;
                }
            }
        }
        return result;
    }
    
    static {
        LOG = LoggerFactory.getLogger(SimpleJmsTopicConnector.class);
    }
}
