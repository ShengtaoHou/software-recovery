// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network;

import org.slf4j.LoggerFactory;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.Response;
import org.apache.activemq.command.ExceptionResponse;
import org.apache.activemq.transport.FutureResponse;
import org.apache.activemq.transport.ResponseCallback;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.command.TransactionId;
import org.apache.activemq.command.MessageDispatch;
import org.apache.activemq.util.ServiceSupport;
import org.apache.activemq.util.ServiceStopper;
import org.apache.activemq.command.ShutdownInfo;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ConnectionId;
import org.apache.activemq.transport.TransportListener;
import java.io.IOException;
import org.apache.activemq.command.Command;
import org.apache.activemq.transport.DefaultTransportListener;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.command.BrokerInfo;
import org.apache.activemq.command.BrokerId;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.command.SessionInfo;
import org.apache.activemq.command.ConnectionInfo;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.apache.activemq.util.IdGenerator;
import org.apache.activemq.Service;

public class ForwardingBridge implements Service
{
    private static final IdGenerator ID_GENERATOR;
    private static final Logger LOG;
    final AtomicLong enqueueCounter;
    final AtomicLong dequeueCounter;
    ConnectionInfo connectionInfo;
    SessionInfo sessionInfo;
    ProducerInfo producerInfo;
    ConsumerInfo queueConsumerInfo;
    ConsumerInfo topicConsumerInfo;
    BrokerId localBrokerId;
    BrokerId remoteBrokerId;
    BrokerInfo localBrokerInfo;
    BrokerInfo remoteBrokerInfo;
    private final Transport localBroker;
    private final Transport remoteBroker;
    private String clientId;
    private int prefetchSize;
    private boolean dispatchAsync;
    private String destinationFilter;
    private NetworkBridgeListener bridgeFailedListener;
    private boolean useCompression;
    
    public ForwardingBridge(final Transport localBroker, final Transport remoteBroker) {
        this.enqueueCounter = new AtomicLong();
        this.dequeueCounter = new AtomicLong();
        this.prefetchSize = 1000;
        this.destinationFilter = ">";
        this.useCompression = false;
        this.localBroker = localBroker;
        this.remoteBroker = remoteBroker;
    }
    
    @Override
    public void start() throws Exception {
        ForwardingBridge.LOG.info("Starting a network connection between {} and {} has been established.", this.localBroker, this.remoteBroker);
        this.localBroker.setTransportListener(new DefaultTransportListener() {
            @Override
            public void onCommand(final Object o) {
                final Command command = (Command)o;
                ForwardingBridge.this.serviceLocalCommand(command);
            }
            
            @Override
            public void onException(final IOException error) {
                ForwardingBridge.this.serviceLocalException(error);
            }
        });
        this.remoteBroker.setTransportListener(new DefaultTransportListener() {
            @Override
            public void onCommand(final Object o) {
                final Command command = (Command)o;
                ForwardingBridge.this.serviceRemoteCommand(command);
            }
            
            @Override
            public void onException(final IOException error) {
                ForwardingBridge.this.serviceRemoteException(error);
            }
        });
        this.localBroker.start();
        this.remoteBroker.start();
    }
    
    protected void triggerStartBridge() throws IOException {
        final Thread thead = new Thread() {
            @Override
            public void run() {
                try {
                    ForwardingBridge.this.startBridge();
                }
                catch (IOException e) {
                    ForwardingBridge.LOG.error("Failed to start network bridge: ", e);
                }
            }
        };
        thead.start();
    }
    
    final void startBridge() throws IOException {
        (this.connectionInfo = new ConnectionInfo()).setConnectionId(new ConnectionId(ForwardingBridge.ID_GENERATOR.generateId()));
        this.connectionInfo.setClientId(this.clientId);
        this.localBroker.oneway(this.connectionInfo);
        this.remoteBroker.oneway(this.connectionInfo);
        this.sessionInfo = new SessionInfo(this.connectionInfo, 1L);
        this.localBroker.oneway(this.sessionInfo);
        this.remoteBroker.oneway(this.sessionInfo);
        (this.queueConsumerInfo = new ConsumerInfo(this.sessionInfo, 1L)).setDispatchAsync(this.dispatchAsync);
        this.queueConsumerInfo.setDestination(new ActiveMQQueue(this.destinationFilter));
        this.queueConsumerInfo.setPrefetchSize(this.prefetchSize);
        this.queueConsumerInfo.setPriority((byte)(-5));
        this.localBroker.oneway(this.queueConsumerInfo);
        (this.producerInfo = new ProducerInfo(this.sessionInfo, 1L)).setResponseRequired(false);
        this.remoteBroker.oneway(this.producerInfo);
        if (this.connectionInfo.getClientId() != null) {
            (this.topicConsumerInfo = new ConsumerInfo(this.sessionInfo, 2L)).setDispatchAsync(this.dispatchAsync);
            this.topicConsumerInfo.setSubscriptionName("topic-bridge");
            this.topicConsumerInfo.setRetroactive(true);
            this.topicConsumerInfo.setDestination(new ActiveMQTopic(this.destinationFilter));
            this.topicConsumerInfo.setPrefetchSize(this.prefetchSize);
            this.topicConsumerInfo.setPriority((byte)(-5));
            this.localBroker.oneway(this.topicConsumerInfo);
        }
        ForwardingBridge.LOG.info("Network connection between {} and {} has been established.", this.localBroker, this.remoteBroker);
    }
    
    @Override
    public void stop() throws Exception {
        try {
            if (this.connectionInfo != null) {
                this.localBroker.request(this.connectionInfo.createRemoveCommand());
                this.remoteBroker.request(this.connectionInfo.createRemoveCommand());
            }
            this.localBroker.setTransportListener(null);
            this.remoteBroker.setTransportListener(null);
            this.localBroker.oneway(new ShutdownInfo());
            this.remoteBroker.oneway(new ShutdownInfo());
        }
        finally {
            final ServiceStopper ss = new ServiceStopper();
            ss.stop(this.localBroker);
            ss.stop(this.remoteBroker);
            ss.throwFirstException();
        }
    }
    
    public void serviceRemoteException(final Throwable error) {
        ForwardingBridge.LOG.info("Unexpected remote exception: {}", error.getMessage());
        ForwardingBridge.LOG.debug("Exception trace: ", error);
    }
    
    protected void serviceRemoteCommand(final Command command) {
        try {
            if (command.isBrokerInfo()) {
                synchronized (this) {
                    this.remoteBrokerInfo = (BrokerInfo)command;
                    this.remoteBrokerId = this.remoteBrokerInfo.getBrokerId();
                    if (this.localBrokerId != null) {
                        if (this.localBrokerId.equals(this.remoteBrokerId)) {
                            ForwardingBridge.LOG.info("Disconnecting loop back connection.");
                            ServiceSupport.dispose(this);
                        }
                        else {
                            this.triggerStartBridge();
                        }
                    }
                }
            }
            else {
                ForwardingBridge.LOG.warn("Unexpected remote command: {}", command);
            }
        }
        catch (IOException e) {
            this.serviceLocalException(e);
        }
    }
    
    public void serviceLocalException(final Throwable error) {
        ForwardingBridge.LOG.info("Unexpected local exception: {}", error.getMessage());
        ForwardingBridge.LOG.debug("Exception trace: ", error);
        this.fireBridgeFailed();
    }
    
    protected void serviceLocalCommand(final Command command) {
        try {
            if (command.isMessageDispatch()) {
                this.enqueueCounter.incrementAndGet();
                final MessageDispatch md = (MessageDispatch)command;
                final Message message = md.getMessage();
                message.setProducerId(this.producerInfo.getProducerId());
                if (message.getOriginalTransactionId() == null) {
                    message.setOriginalTransactionId(message.getTransactionId());
                }
                message.setTransactionId(null);
                if (this.isUseCompression()) {
                    message.compress();
                }
                if (!message.isResponseRequired()) {
                    this.remoteBroker.oneway(message);
                    this.dequeueCounter.incrementAndGet();
                    this.localBroker.oneway(new MessageAck(md, (byte)2, 1));
                }
                else {
                    final ResponseCallback callback = new ResponseCallback() {
                        @Override
                        public void onCompletion(final FutureResponse future) {
                            try {
                                final Response response = future.getResult();
                                if (response.isException()) {
                                    final ExceptionResponse er = (ExceptionResponse)response;
                                    ForwardingBridge.this.serviceLocalException(er.getException());
                                }
                                else {
                                    ForwardingBridge.this.dequeueCounter.incrementAndGet();
                                    ForwardingBridge.this.localBroker.oneway(new MessageAck(md, (byte)2, 1));
                                }
                            }
                            catch (IOException e) {
                                ForwardingBridge.this.serviceLocalException(e);
                            }
                        }
                    };
                    this.remoteBroker.asyncRequest(message, callback);
                }
            }
            else if (command.isBrokerInfo()) {
                synchronized (this) {
                    this.localBrokerInfo = (BrokerInfo)command;
                    this.localBrokerId = this.localBrokerInfo.getBrokerId();
                    if (this.remoteBrokerId != null) {
                        if (this.remoteBrokerId.equals(this.localBrokerId)) {
                            ForwardingBridge.LOG.info("Disconnecting loop back connection.");
                            ServiceSupport.dispose(this);
                        }
                        else {
                            this.triggerStartBridge();
                        }
                    }
                }
            }
            else {
                ForwardingBridge.LOG.debug("Unexpected local command: {}", command);
            }
        }
        catch (IOException e) {
            this.serviceLocalException(e);
        }
    }
    
    public String getClientId() {
        return this.clientId;
    }
    
    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }
    
    public int getPrefetchSize() {
        return this.prefetchSize;
    }
    
    public void setPrefetchSize(final int prefetchSize) {
        this.prefetchSize = prefetchSize;
    }
    
    public boolean isDispatchAsync() {
        return this.dispatchAsync;
    }
    
    public void setDispatchAsync(final boolean dispatchAsync) {
        this.dispatchAsync = dispatchAsync;
    }
    
    public String getDestinationFilter() {
        return this.destinationFilter;
    }
    
    public void setDestinationFilter(final String destinationFilter) {
        this.destinationFilter = destinationFilter;
    }
    
    public void setNetworkBridgeFailedListener(final NetworkBridgeListener listener) {
        this.bridgeFailedListener = listener;
    }
    
    private void fireBridgeFailed() {
        final NetworkBridgeListener l = this.bridgeFailedListener;
        if (l != null) {
            l.bridgeFailed();
        }
    }
    
    public String getRemoteAddress() {
        return this.remoteBroker.getRemoteAddress();
    }
    
    public String getLocalAddress() {
        return this.localBroker.getRemoteAddress();
    }
    
    public String getLocalBrokerName() {
        return (this.localBrokerInfo == null) ? null : this.localBrokerInfo.getBrokerName();
    }
    
    public String getRemoteBrokerName() {
        return (this.remoteBrokerInfo == null) ? null : this.remoteBrokerInfo.getBrokerName();
    }
    
    public long getDequeueCounter() {
        return this.dequeueCounter.get();
    }
    
    public long getEnqueueCounter() {
        return this.enqueueCounter.get();
    }
    
    public void setUseCompression(final boolean useCompression) {
        this.useCompression = useCompression;
    }
    
    public boolean isUseCompression() {
        return this.useCompression;
    }
    
    static {
        ID_GENERATOR = new IdGenerator();
        LOG = LoggerFactory.getLogger(ForwardingBridge.class);
    }
}
