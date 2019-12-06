// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.slf4j.LoggerFactory;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.command.NetworkBridgeFilter;
import org.apache.activemq.filter.BooleanExpression;
import org.apache.activemq.broker.region.Region;
import org.apache.activemq.broker.region.AbstractRegion;
import org.apache.activemq.broker.region.RegionBroker;
import org.apache.activemq.broker.region.DurableTopicSubscription;
import java.util.Collection;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.filter.DestinationFilter;
import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.filter.MessageEvaluationContext;
import java.util.Arrays;
import org.apache.activemq.command.TransactionId;
import org.apache.activemq.usage.MemoryUsage;
import org.apache.activemq.security.SecurityContext;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.advisory.AdvisoryBroker;
import org.apache.activemq.DestinationDoesNotExistException;
import java.util.Iterator;
import org.apache.activemq.command.SubscriptionInfo;
import org.apache.activemq.command.RemoveSubscriptionInfo;
import org.apache.activemq.command.RemoveInfo;
import org.apache.activemq.command.ActiveMQTempDestination;
import org.apache.activemq.command.DestinationInfo;
import org.apache.activemq.command.DataStructure;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.command.Response;
import org.apache.activemq.transport.FutureResponse;
import org.apache.activemq.transport.ResponseCallback;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ConnectionError;
import org.apache.activemq.command.MessageDispatch;
import java.security.GeneralSecurityException;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.advisory.AdvisorySupport;
import java.security.cert.X509Certificate;
import org.apache.activemq.command.ExceptionResponse;
import org.apache.activemq.transport.tcp.SslTransport;
import org.apache.activemq.transport.TransportFilter;
import org.apache.activemq.command.ConnectionId;
import java.util.Properties;
import java.util.Map;
import org.apache.activemq.util.IntrospectionSupport;
import org.apache.activemq.util.MarshallingSupport;
import org.apache.activemq.util.ServiceSupport;
import org.apache.activemq.Service;
import org.apache.activemq.util.ServiceStopper;
import java.util.List;
import org.apache.activemq.command.ShutdownInfo;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.broker.Connection;
import org.apache.activemq.transport.TransportDisposedIOException;
import org.apache.activemq.transport.TransportListener;
import java.io.IOException;
import org.apache.activemq.transport.DefaultTransportListener;
import org.apache.activemq.command.Command;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import javax.management.ObjectName;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnection;
import org.apache.activemq.command.BrokerInfo;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.CountDownLatch;
import org.apache.activemq.command.ConsumerId;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.BrokerId;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.command.SessionInfo;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.util.LongSequenceGenerator;
import org.apache.activemq.util.IdGenerator;
import org.apache.activemq.transport.Transport;
import org.slf4j.Logger;
import org.apache.activemq.broker.BrokerServiceAware;

public abstract class DemandForwardingBridgeSupport implements NetworkBridge, BrokerServiceAware
{
    private static final Logger LOG;
    protected static final String DURABLE_SUB_PREFIX = "NC-DS_";
    protected final Transport localBroker;
    protected final Transport remoteBroker;
    protected IdGenerator idGenerator;
    protected final LongSequenceGenerator consumerIdGenerator;
    protected ConnectionInfo localConnectionInfo;
    protected ConnectionInfo remoteConnectionInfo;
    protected SessionInfo localSessionInfo;
    protected ProducerInfo producerInfo;
    protected String remoteBrokerName;
    protected String localClientId;
    protected ConsumerInfo demandConsumerInfo;
    protected int demandConsumerDispatched;
    protected final AtomicBoolean localBridgeStarted;
    protected final AtomicBoolean remoteBridgeStarted;
    protected final AtomicBoolean bridgeFailed;
    protected final AtomicBoolean disposed;
    protected BrokerId localBrokerId;
    protected ActiveMQDestination[] excludedDestinations;
    protected ActiveMQDestination[] dynamicallyIncludedDestinations;
    protected ActiveMQDestination[] staticallyIncludedDestinations;
    protected ActiveMQDestination[] durableDestinations;
    protected final ConcurrentHashMap<ConsumerId, DemandSubscription> subscriptionMapByLocalId;
    protected final ConcurrentHashMap<ConsumerId, DemandSubscription> subscriptionMapByRemoteId;
    protected final BrokerId[] localBrokerPath;
    protected final CountDownLatch startedLatch;
    protected final CountDownLatch localStartedLatch;
    protected final AtomicBoolean lastConnectSucceeded;
    protected NetworkBridgeConfiguration configuration;
    protected final NetworkBridgeFilterFactory defaultFilterFactory;
    protected final BrokerId[] remoteBrokerPath;
    protected BrokerId remoteBrokerId;
    final AtomicLong enqueueCounter;
    final AtomicLong dequeueCounter;
    private NetworkBridgeListener networkBridgeListener;
    private boolean createdByDuplex;
    private BrokerInfo localBrokerInfo;
    private BrokerInfo remoteBrokerInfo;
    private final FutureBrokerInfo futureRemoteBrokerInfo;
    private final FutureBrokerInfo futureLocalBrokerInfo;
    private final AtomicBoolean started;
    private TransportConnection duplexInitiatingConnection;
    private final AtomicBoolean duplexInitiatingConnectionInfoReceived;
    protected BrokerService brokerService;
    private ObjectName mbeanObjectName;
    private final ExecutorService serialExecutor;
    private Transport duplexInboundLocalBroker;
    private ProducerInfo duplexInboundLocalProducerInfo;
    
    public DemandForwardingBridgeSupport(final NetworkBridgeConfiguration configuration, final Transport localBroker, final Transport remoteBroker) {
        this.consumerIdGenerator = new LongSequenceGenerator();
        this.remoteBrokerName = "Unknown";
        this.localBridgeStarted = new AtomicBoolean(false);
        this.remoteBridgeStarted = new AtomicBoolean(false);
        this.bridgeFailed = new AtomicBoolean();
        this.disposed = new AtomicBoolean();
        this.subscriptionMapByLocalId = new ConcurrentHashMap<ConsumerId, DemandSubscription>();
        this.subscriptionMapByRemoteId = new ConcurrentHashMap<ConsumerId, DemandSubscription>();
        this.localBrokerPath = new BrokerId[] { null };
        this.startedLatch = new CountDownLatch(2);
        this.localStartedLatch = new CountDownLatch(1);
        this.lastConnectSucceeded = new AtomicBoolean(false);
        this.defaultFilterFactory = new DefaultNetworkBridgeFilterFactory();
        this.remoteBrokerPath = new BrokerId[] { null };
        this.enqueueCounter = new AtomicLong();
        this.dequeueCounter = new AtomicLong();
        this.futureRemoteBrokerInfo = new FutureBrokerInfo(this.remoteBrokerInfo, this.disposed);
        this.futureLocalBrokerInfo = new FutureBrokerInfo(this.localBrokerInfo, this.disposed);
        this.started = new AtomicBoolean();
        this.duplexInitiatingConnectionInfoReceived = new AtomicBoolean();
        this.brokerService = null;
        this.serialExecutor = Executors.newSingleThreadExecutor();
        this.duplexInboundLocalBroker = null;
        this.configuration = configuration;
        this.localBroker = localBroker;
        this.remoteBroker = remoteBroker;
    }
    
    public void duplexStart(final TransportConnection connection, final BrokerInfo localBrokerInfo, final BrokerInfo remoteBrokerInfo) throws Exception {
        this.localBrokerInfo = localBrokerInfo;
        this.remoteBrokerInfo = remoteBrokerInfo;
        this.duplexInitiatingConnection = connection;
        this.start();
        this.serviceRemoteCommand(remoteBrokerInfo);
    }
    
    @Override
    public void start() throws Exception {
        if (this.started.compareAndSet(false, true)) {
            if (this.brokerService == null) {
                throw new IllegalArgumentException("BrokerService is null on " + this);
            }
            if (this.isDuplex()) {
                (this.duplexInboundLocalBroker = NetworkBridgeFactory.createLocalTransport(this.brokerService.getBroker())).setTransportListener(new DefaultTransportListener() {
                    @Override
                    public void onCommand(final Object o) {
                        final Command command = (Command)o;
                        DemandForwardingBridgeSupport.this.serviceLocalCommand(command);
                    }
                    
                    @Override
                    public void onException(final IOException error) {
                        DemandForwardingBridgeSupport.this.serviceLocalException(error);
                    }
                });
                this.duplexInboundLocalBroker.start();
            }
            this.localBroker.setTransportListener(new DefaultTransportListener() {
                @Override
                public void onCommand(final Object o) {
                    final Command command = (Command)o;
                    DemandForwardingBridgeSupport.this.serviceLocalCommand(command);
                }
                
                @Override
                public void onException(final IOException error) {
                    if (!DemandForwardingBridgeSupport.this.futureLocalBrokerInfo.isDone()) {
                        DemandForwardingBridgeSupport.this.futureLocalBrokerInfo.cancel(true);
                        return;
                    }
                    DemandForwardingBridgeSupport.this.serviceLocalException(error);
                }
            });
            this.remoteBroker.setTransportListener(new DefaultTransportListener() {
                @Override
                public void onCommand(final Object o) {
                    final Command command = (Command)o;
                    DemandForwardingBridgeSupport.this.serviceRemoteCommand(command);
                }
                
                @Override
                public void onException(final IOException error) {
                    if (!DemandForwardingBridgeSupport.this.futureRemoteBrokerInfo.isDone()) {
                        DemandForwardingBridgeSupport.this.futureRemoteBrokerInfo.cancel(true);
                        return;
                    }
                    DemandForwardingBridgeSupport.this.serviceRemoteException(error);
                }
            });
            this.remoteBroker.start();
            this.localBroker.start();
            if (this.disposed.get()) {
                DemandForwardingBridgeSupport.LOG.warn("Bridge was disposed before the start() method was fully executed.");
                throw new TransportDisposedIOException();
            }
            try {
                this.triggerStartAsyncNetworkBridgeCreation();
            }
            catch (IOException e) {
                DemandForwardingBridgeSupport.LOG.warn("Caught exception from remote start", e);
            }
        }
    }
    
    @Override
    public void stop() throws Exception {
        if (this.started.compareAndSet(true, false)) {
            if (this.disposed.compareAndSet(false, true)) {
                DemandForwardingBridgeSupport.LOG.debug(" stopping {} bridge to {}", this.configuration.getBrokerName(), this.remoteBrokerName);
                this.futureRemoteBrokerInfo.cancel(true);
                this.futureLocalBrokerInfo.cancel(true);
                final NetworkBridgeListener l = this.networkBridgeListener;
                if (l != null) {
                    l.onStop(this);
                }
                try {
                    if (this.startedLatch.getCount() < 2L) {
                        DemandForwardingBridgeSupport.LOG.trace("{} unregister bridge ({}) to {}", this.configuration.getBrokerName(), this, this.remoteBrokerName);
                        this.brokerService.getBroker().removeBroker(null, this.remoteBrokerInfo);
                        this.brokerService.getBroker().networkBridgeStopped(this.remoteBrokerInfo);
                    }
                    this.remoteBridgeStarted.set(false);
                    final CountDownLatch sendShutdown = new CountDownLatch(1);
                    this.brokerService.getTaskRunnerFactory().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                DemandForwardingBridgeSupport.this.serialExecutor.shutdown();
                                if (!DemandForwardingBridgeSupport.this.serialExecutor.awaitTermination(5L, TimeUnit.SECONDS)) {
                                    final List<Runnable> pendingTasks = DemandForwardingBridgeSupport.this.serialExecutor.shutdownNow();
                                    DemandForwardingBridgeSupport.LOG.info("pending tasks on stop {}", pendingTasks);
                                }
                                DemandForwardingBridgeSupport.this.localBroker.oneway(new ShutdownInfo());
                                DemandForwardingBridgeSupport.this.remoteBroker.oneway(new ShutdownInfo());
                            }
                            catch (Throwable e) {
                                DemandForwardingBridgeSupport.LOG.debug("Caught exception sending shutdown", e);
                            }
                            finally {
                                sendShutdown.countDown();
                            }
                        }
                    }, "ActiveMQ ForwardingBridge StopTask");
                    if (!sendShutdown.await(10L, TimeUnit.SECONDS)) {
                        DemandForwardingBridgeSupport.LOG.info("Network Could not shutdown in a timely manner");
                    }
                }
                finally {
                    final ServiceStopper ss = new ServiceStopper();
                    ss.stop(this.remoteBroker);
                    ss.stop(this.localBroker);
                    ss.stop(this.duplexInboundLocalBroker);
                    this.startedLatch.countDown();
                    this.startedLatch.countDown();
                    this.localStartedLatch.countDown();
                    ss.throwFirstException();
                }
            }
            DemandForwardingBridgeSupport.LOG.info("{} bridge to {} stopped", this.configuration.getBrokerName(), this.remoteBrokerName);
        }
    }
    
    protected void triggerStartAsyncNetworkBridgeCreation() throws IOException {
        this.brokerService.getTaskRunnerFactory().execute(new Runnable() {
            @Override
            public void run() {
                final String originalName = Thread.currentThread().getName();
                Thread.currentThread().setName("triggerStartAsyncNetworkBridgeCreation: remoteBroker=" + DemandForwardingBridgeSupport.this.remoteBroker + ", localBroker= " + DemandForwardingBridgeSupport.this.localBroker);
                try {
                    DemandForwardingBridgeSupport.this.collectBrokerInfos();
                    DemandForwardingBridgeSupport.this.doStartLocalAndRemoteBridges();
                }
                finally {
                    Thread.currentThread().setName(originalName);
                }
            }
        });
    }
    
    private void collectBrokerInfos() {
        try {
            this.remoteBrokerInfo = this.futureRemoteBrokerInfo.get();
            if (this.remoteBrokerInfo == null) {
                this.fireBridgeFailed();
            }
        }
        catch (Exception e) {
            this.serviceRemoteException(e);
            return;
        }
        try {
            this.localBrokerInfo = this.futureLocalBrokerInfo.get();
            if (this.localBrokerInfo == null) {
                this.fireBridgeFailed();
            }
            this.remoteBrokerId = this.remoteBrokerInfo.getBrokerId();
            if (this.localBrokerId.equals(this.remoteBrokerId)) {
                DemandForwardingBridgeSupport.LOG.trace("{} disconnecting remote loop back connector for: {}, with id: {}", this.configuration.getBrokerName(), this.remoteBrokerName, this.remoteBrokerId);
                ServiceSupport.dispose(this.localBroker);
                ServiceSupport.dispose(this.remoteBroker);
                return;
            }
            this.remoteBrokerPath[0] = this.remoteBrokerId;
            this.remoteBrokerName = this.remoteBrokerInfo.getBrokerName();
            if (this.configuration.isUseBrokerNamesAsIdSeed()) {
                this.idGenerator = new IdGenerator(this.brokerService.getBrokerName() + "->" + this.remoteBrokerName);
            }
            else {
                this.idGenerator = new IdGenerator();
            }
        }
        catch (Throwable e2) {
            this.serviceLocalException(e2);
        }
    }
    
    private void doStartLocalAndRemoteBridges() {
        if (this.disposed.get()) {
            return;
        }
        if (this.isCreatedByDuplex()) {
            Properties props = null;
            try {
                props = MarshallingSupport.stringToProperties(this.remoteBrokerInfo.getNetworkProperties());
                IntrospectionSupport.getProperties(this.configuration, props, null);
                if (this.configuration.getExcludedDestinations() != null) {
                    this.excludedDestinations = this.configuration.getExcludedDestinations().toArray(new ActiveMQDestination[this.configuration.getExcludedDestinations().size()]);
                }
                if (this.configuration.getStaticallyIncludedDestinations() != null) {
                    this.staticallyIncludedDestinations = this.configuration.getStaticallyIncludedDestinations().toArray(new ActiveMQDestination[this.configuration.getStaticallyIncludedDestinations().size()]);
                }
                if (this.configuration.getDynamicallyIncludedDestinations() != null) {
                    this.dynamicallyIncludedDestinations = this.configuration.getDynamicallyIncludedDestinations().toArray(new ActiveMQDestination[this.configuration.getDynamicallyIncludedDestinations().size()]);
                }
            }
            catch (Throwable t) {
                DemandForwardingBridgeSupport.LOG.error("Error mapping remote configuration: {}", props, t);
            }
        }
        try {
            this.startLocalBridge();
        }
        catch (Throwable e) {
            this.serviceLocalException(e);
            return;
        }
        try {
            this.startRemoteBridge();
        }
        catch (Throwable e) {
            this.serviceRemoteException(e);
        }
    }
    
    private void startLocalBridge() throws Throwable {
        if (this.localBridgeStarted.compareAndSet(false, true)) {
            synchronized (this) {
                DemandForwardingBridgeSupport.LOG.trace("{} starting local Bridge, localBroker={}", this.configuration.getBrokerName(), this.localBroker);
                if (!this.disposed.get()) {
                    (this.localConnectionInfo = new ConnectionInfo()).setConnectionId(new ConnectionId(this.idGenerator.generateId()));
                    this.localClientId = this.configuration.getName() + "_" + this.remoteBrokerName + "_inbound_" + this.configuration.getBrokerName();
                    this.localConnectionInfo.setClientId(this.localClientId);
                    this.localConnectionInfo.setUserName(this.configuration.getUserName());
                    this.localConnectionInfo.setPassword(this.configuration.getPassword());
                    Transport originalTransport;
                    for (originalTransport = this.remoteBroker; originalTransport instanceof TransportFilter; originalTransport = ((TransportFilter)originalTransport).getNext()) {}
                    if (originalTransport instanceof SslTransport) {
                        final X509Certificate[] peerCerts = ((SslTransport)originalTransport).getPeerCertificates();
                        this.localConnectionInfo.setTransportContext(peerCerts);
                    }
                    Object resp = this.localBroker.request(this.localConnectionInfo);
                    if (resp instanceof ExceptionResponse) {
                        throw ((ExceptionResponse)resp).getException();
                    }
                    this.localSessionInfo = new SessionInfo(this.localConnectionInfo, 1L);
                    this.localBroker.oneway(this.localSessionInfo);
                    if (this.configuration.isDuplex()) {
                        final ConnectionInfo duplexLocalConnectionInfo = new ConnectionInfo();
                        duplexLocalConnectionInfo.setConnectionId(new ConnectionId(this.idGenerator.generateId()));
                        duplexLocalConnectionInfo.setClientId(this.configuration.getName() + "_" + this.remoteBrokerName + "_inbound_duplex_" + this.configuration.getBrokerName());
                        duplexLocalConnectionInfo.setUserName(this.configuration.getUserName());
                        duplexLocalConnectionInfo.setPassword(this.configuration.getPassword());
                        if (originalTransport instanceof SslTransport) {
                            final X509Certificate[] peerCerts2 = ((SslTransport)originalTransport).getPeerCertificates();
                            duplexLocalConnectionInfo.setTransportContext(peerCerts2);
                        }
                        resp = this.duplexInboundLocalBroker.request(duplexLocalConnectionInfo);
                        if (resp instanceof ExceptionResponse) {
                            throw ((ExceptionResponse)resp).getException();
                        }
                        final SessionInfo duplexInboundSession = new SessionInfo(duplexLocalConnectionInfo, 1L);
                        this.duplexInboundLocalProducerInfo = new ProducerInfo(duplexInboundSession, 1L);
                        this.duplexInboundLocalBroker.oneway(duplexInboundSession);
                        this.duplexInboundLocalBroker.oneway(this.duplexInboundLocalProducerInfo);
                    }
                    this.brokerService.getBroker().networkBridgeStarted(this.remoteBrokerInfo, this.createdByDuplex, this.remoteBroker.toString());
                    final NetworkBridgeListener l = this.networkBridgeListener;
                    if (l != null) {
                        l.onStart(this);
                    }
                    this.localBroker.oneway(this.remoteBrokerInfo);
                    this.brokerService.getBroker().addBroker(null, this.remoteBrokerInfo);
                    DemandForwardingBridgeSupport.LOG.info("Network connection between {} and {} ({}) has been established.", this.localBroker, this.remoteBroker, this.remoteBrokerName);
                    DemandForwardingBridgeSupport.LOG.trace("{} register bridge ({}) to {}", this.configuration.getBrokerName(), this, this.remoteBrokerName);
                }
                else {
                    DemandForwardingBridgeSupport.LOG.warn("Bridge was disposed before the startLocalBridge() method was fully executed.");
                }
                this.startedLatch.countDown();
                this.localStartedLatch.countDown();
            }
            if (!this.disposed.get()) {
                this.setupStaticDestinations();
            }
            else {
                DemandForwardingBridgeSupport.LOG.warn("Network connection between {} and {} ({}) was interrupted during establishment.", this.localBroker, this.remoteBroker, this.remoteBrokerName);
            }
        }
    }
    
    protected void startRemoteBridge() throws Exception {
        if (this.remoteBridgeStarted.compareAndSet(false, true)) {
            DemandForwardingBridgeSupport.LOG.trace("{} starting remote Bridge, remoteBroker={}", this.configuration.getBrokerName(), this.remoteBroker);
            synchronized (this) {
                if (!this.isCreatedByDuplex()) {
                    final BrokerInfo brokerInfo = new BrokerInfo();
                    brokerInfo.setBrokerName(this.configuration.getBrokerName());
                    brokerInfo.setBrokerURL(this.configuration.getBrokerURL());
                    brokerInfo.setNetworkConnection(true);
                    brokerInfo.setDuplexConnection(this.configuration.isDuplex());
                    final Properties props = new Properties();
                    IntrospectionSupport.getProperties(this.configuration, props, null);
                    props.remove("networkTTL");
                    final String str = MarshallingSupport.propertiesToString(props);
                    brokerInfo.setNetworkProperties(str);
                    brokerInfo.setBrokerId(this.localBrokerId);
                    this.remoteBroker.oneway(brokerInfo);
                }
                if (this.remoteConnectionInfo != null) {
                    this.remoteBroker.oneway(this.remoteConnectionInfo.createRemoveCommand());
                }
                (this.remoteConnectionInfo = new ConnectionInfo()).setConnectionId(new ConnectionId(this.idGenerator.generateId()));
                this.remoteConnectionInfo.setClientId(this.configuration.getName() + "_" + this.configuration.getBrokerName() + "_outbound");
                this.remoteConnectionInfo.setUserName(this.configuration.getUserName());
                this.remoteConnectionInfo.setPassword(this.configuration.getPassword());
                this.remoteBroker.oneway(this.remoteConnectionInfo);
                final SessionInfo remoteSessionInfo = new SessionInfo(this.remoteConnectionInfo, 1L);
                this.remoteBroker.oneway(remoteSessionInfo);
                (this.producerInfo = new ProducerInfo(remoteSessionInfo, 1L)).setResponseRequired(false);
                this.remoteBroker.oneway(this.producerInfo);
                if (!this.configuration.isStaticBridge()) {
                    (this.demandConsumerInfo = new ConsumerInfo(remoteSessionInfo, 1L)).setDispatchAsync(true);
                    String advisoryTopic = this.configuration.getDestinationFilter();
                    if (this.configuration.isBridgeTempDestinations()) {
                        advisoryTopic = advisoryTopic + "," + AdvisorySupport.TEMP_DESTINATION_COMPOSITE_ADVISORY_TOPIC;
                    }
                    this.demandConsumerInfo.setDestination(new ActiveMQTopic(advisoryTopic));
                    this.demandConsumerInfo.setPrefetchSize(this.configuration.getPrefetchSize());
                    this.remoteBroker.oneway(this.demandConsumerInfo);
                }
                this.startedLatch.countDown();
            }
        }
    }
    
    @Override
    public void serviceRemoteException(final Throwable error) {
        if (!this.disposed.get()) {
            if (error instanceof SecurityException || error instanceof GeneralSecurityException) {
                DemandForwardingBridgeSupport.LOG.error("Network connection between {} and {} shutdown due to a remote error: {}", this.localBroker, this.remoteBroker, error);
            }
            else {
                DemandForwardingBridgeSupport.LOG.warn("Network connection between {} and {} shutdown due to a remote error: {}", this.localBroker, this.remoteBroker, error);
            }
            DemandForwardingBridgeSupport.LOG.debug("The remote Exception was: {}", error, error);
            this.brokerService.getTaskRunnerFactory().execute(new Runnable() {
                @Override
                public void run() {
                    ServiceSupport.dispose(DemandForwardingBridgeSupport.this.getControllingService());
                }
            });
            this.fireBridgeFailed();
        }
    }
    
    protected void serviceRemoteCommand(final Command command) {
        if (!this.disposed.get()) {
            try {
                if (command.isMessageDispatch()) {
                    this.safeWaitUntilStarted();
                    final MessageDispatch md = (MessageDispatch)command;
                    this.serviceRemoteConsumerAdvisory(md.getMessage().getDataStructure());
                    this.ackAdvisory(md.getMessage());
                }
                else if (command.isBrokerInfo()) {
                    this.futureRemoteBrokerInfo.set((BrokerInfo)command);
                }
                else if (command.getClass() == ConnectionError.class) {
                    final ConnectionError ce = (ConnectionError)command;
                    this.serviceRemoteException(ce.getException());
                }
                else if (this.isDuplex()) {
                    DemandForwardingBridgeSupport.LOG.trace("{} duplex command type: {}", this.configuration.getBrokerName(), command.getDataStructureType());
                    if (command.isMessage()) {
                        final ActiveMQMessage message = (ActiveMQMessage)command;
                        if (AdvisorySupport.isConsumerAdvisoryTopic(message.getDestination()) || AdvisorySupport.isDestinationAdvisoryTopic(message.getDestination())) {
                            this.serviceRemoteConsumerAdvisory(message.getDataStructure());
                            this.ackAdvisory(message);
                        }
                        else {
                            if (!this.isPermissableDestination(message.getDestination(), true)) {
                                return;
                            }
                            if (this.canDuplexDispatch(message)) {
                                message.setProducerId(this.duplexInboundLocalProducerInfo.getProducerId());
                                if (message.isResponseRequired() || this.configuration.isAlwaysSyncSend()) {
                                    this.duplexInboundLocalBroker.asyncRequest(message, new ResponseCallback() {
                                        final int correlationId = message.getCommandId();
                                        
                                        @Override
                                        public void onCompletion(final FutureResponse resp) {
                                            try {
                                                final Response reply = resp.getResult();
                                                reply.setCorrelationId(this.correlationId);
                                                DemandForwardingBridgeSupport.this.remoteBroker.oneway(reply);
                                            }
                                            catch (IOException error) {
                                                DemandForwardingBridgeSupport.LOG.error("Exception: {} on duplex forward of: {}", error, message);
                                                DemandForwardingBridgeSupport.this.serviceRemoteException(error);
                                            }
                                        }
                                    });
                                }
                                else {
                                    this.duplexInboundLocalBroker.oneway(message);
                                }
                                this.serviceInboundMessage(message);
                            }
                            else if (message.isResponseRequired() || this.configuration.isAlwaysSyncSend()) {
                                final Response reply = new Response();
                                reply.setCorrelationId(message.getCommandId());
                                this.remoteBroker.oneway(reply);
                            }
                        }
                    }
                    else {
                        switch (command.getDataStructureType()) {
                            case 3: {
                                if (this.duplexInitiatingConnection != null && this.duplexInitiatingConnectionInfoReceived.compareAndSet(false, true)) {
                                    this.duplexInitiatingConnection.processAddConnection((ConnectionInfo)command);
                                    break;
                                }
                                this.localBroker.oneway(command);
                                break;
                            }
                            case 4: {
                                this.localBroker.oneway(command);
                                break;
                            }
                            case 6: {
                                break;
                            }
                            case 22: {
                                final MessageAck ack = (MessageAck)command;
                                final DemandSubscription localSub = this.subscriptionMapByRemoteId.get(ack.getConsumerId());
                                if (localSub != null) {
                                    ack.setConsumerId(localSub.getLocalInfo().getConsumerId());
                                    this.localBroker.oneway(ack);
                                    break;
                                }
                                DemandForwardingBridgeSupport.LOG.warn("Matching local subscription not found for ack: {}", ack);
                                break;
                            }
                            case 5: {
                                this.localStartedLatch.await();
                                if (this.started.get()) {
                                    this.addConsumerInfo((ConsumerInfo)command);
                                    break;
                                }
                                DemandForwardingBridgeSupport.LOG.warn("Stopping - ignoring ConsumerInfo: {}", command);
                                break;
                            }
                            case 11: {
                                DemandForwardingBridgeSupport.LOG.info("Stopping network bridge on shutdown of remote broker");
                                this.serviceRemoteException(new IOException(command.toString()));
                                break;
                            }
                            default: {
                                DemandForwardingBridgeSupport.LOG.debug("Ignoring remote command: {}", command);
                                break;
                            }
                        }
                    }
                }
                else {
                    switch (command.getDataStructureType()) {
                        case 1:
                        case 10:
                        case 11: {
                            break;
                        }
                        default: {
                            DemandForwardingBridgeSupport.LOG.warn("Unexpected remote command: {}", command);
                            break;
                        }
                    }
                }
            }
            catch (Throwable e) {
                DemandForwardingBridgeSupport.LOG.debug("Exception processing remote command: {}", command, e);
                this.serviceRemoteException(e);
            }
        }
    }
    
    private void ackAdvisory(final Message message) throws IOException {
        ++this.demandConsumerDispatched;
        if (this.demandConsumerDispatched > this.demandConsumerInfo.getPrefetchSize() * 0.75) {
            final MessageAck ack = new MessageAck(message, (byte)2, this.demandConsumerDispatched);
            ack.setConsumerId(this.demandConsumerInfo.getConsumerId());
            this.remoteBroker.oneway(ack);
            this.demandConsumerDispatched = 0;
        }
    }
    
    private void serviceRemoteConsumerAdvisory(final DataStructure data) throws IOException {
        final int networkTTL = this.configuration.getConsumerTTL();
        if (data.getClass() == ConsumerInfo.class) {
            final ConsumerInfo info = (ConsumerInfo)data;
            final BrokerId[] path = info.getBrokerPath();
            if (info.isBrowser()) {
                DemandForwardingBridgeSupport.LOG.debug("{} Ignoring sub from {}, browsers explicitly suppressed", this.configuration.getBrokerName(), this.remoteBrokerName);
                return;
            }
            if (path != null && networkTTL > -1 && path.length >= networkTTL) {
                DemandForwardingBridgeSupport.LOG.debug("{} Ignoring sub from {}, restricted to {} network hops only: {}", this.configuration.getBrokerName(), this.remoteBrokerName, networkTTL, info);
                return;
            }
            if (contains(path, this.localBrokerPath[0])) {
                DemandForwardingBridgeSupport.LOG.debug("{} Ignoring sub from {}, already routed through this broker once: {}", this.configuration.getBrokerName(), this.remoteBrokerName, info);
                return;
            }
            if (!this.isPermissableDestination(info.getDestination())) {
                DemandForwardingBridgeSupport.LOG.debug("{} Ignoring sub from {}, destination {} is not permitted: {}", this.configuration.getBrokerName(), this.remoteBrokerName, info.getDestination(), info);
                return;
            }
            synchronized (this.brokerService.getVmConnectorURI()) {
                this.addConsumerInfo(info);
            }
        }
        else if (data.getClass() == DestinationInfo.class) {
            final DestinationInfo destInfo = (DestinationInfo)data;
            final BrokerId[] path = destInfo.getBrokerPath();
            if (path != null && networkTTL > -1 && path.length >= networkTTL) {
                DemandForwardingBridgeSupport.LOG.debug("{} Ignoring destination {} restricted to {} network hops only", this.configuration.getBrokerName(), destInfo, networkTTL);
                return;
            }
            if (contains(destInfo.getBrokerPath(), this.localBrokerPath[0])) {
                DemandForwardingBridgeSupport.LOG.debug("{} Ignoring destination {} already routed through this broker once", this.configuration.getBrokerName(), destInfo);
                return;
            }
            destInfo.setConnectionId(this.localConnectionInfo.getConnectionId());
            if (destInfo.getDestination() instanceof ActiveMQTempDestination) {
                final ActiveMQTempDestination tempDest = (ActiveMQTempDestination)destInfo.getDestination();
                tempDest.setConnectionId(this.localSessionInfo.getSessionId().getConnectionId());
            }
            destInfo.setBrokerPath(this.appendToBrokerPath(destInfo.getBrokerPath(), this.getRemoteBrokerPath()));
            DemandForwardingBridgeSupport.LOG.trace("{} bridging {} destination on {} from {}, destination: {}", this.configuration.getBrokerName(), destInfo.isAddOperation() ? "add" : "remove", this.localBroker, this.remoteBrokerName, destInfo);
            if (destInfo.isRemoveOperation()) {
                this.serialExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            DemandForwardingBridgeSupport.this.localBroker.oneway(destInfo);
                        }
                        catch (IOException e) {
                            DemandForwardingBridgeSupport.LOG.warn("failed to deliver remove command for destination: {}", destInfo.getDestination(), e);
                        }
                    }
                });
            }
            else {
                this.localBroker.oneway(destInfo);
            }
        }
        else if (data.getClass() == RemoveInfo.class) {
            final ConsumerId id = (ConsumerId)((RemoveInfo)data).getObjectId();
            this.removeDemandSubscription(id);
        }
        else if (data.getClass() == RemoveSubscriptionInfo.class) {
            final RemoveSubscriptionInfo info2 = (RemoveSubscriptionInfo)data;
            final SubscriptionInfo subscriptionInfo = new SubscriptionInfo(info2.getClientId(), info2.getSubscriptionName());
            for (final DemandSubscription ds : this.subscriptionMapByLocalId.values()) {
                final boolean removed = ds.getDurableRemoteSubs().remove(subscriptionInfo);
                if (removed && ds.getDurableRemoteSubs().isEmpty()) {
                    final RemoveInfo removeInfo = new RemoveInfo(ds.getLocalInfo().getConsumerId());
                    this.localBroker.oneway(removeInfo);
                    final RemoveSubscriptionInfo sending = new RemoveSubscriptionInfo();
                    sending.setClientId(this.localClientId);
                    sending.setSubscriptionName(ds.getLocalDurableSubscriber().getSubscriptionName());
                    sending.setConnectionId(this.localConnectionInfo.getConnectionId());
                    this.localBroker.oneway(sending);
                }
            }
        }
    }
    
    @Override
    public void serviceLocalException(final Throwable error) {
        this.serviceLocalException(null, error);
    }
    
    public void serviceLocalException(final MessageDispatch messageDispatch, final Throwable error) {
        if (!this.disposed.get()) {
            if (error instanceof DestinationDoesNotExistException && ((DestinationDoesNotExistException)error).isTemporary()) {
                if (messageDispatch != null) {
                    DemandForwardingBridgeSupport.LOG.warn("PoisonAck of {} on forwarding error: {}", messageDispatch.getMessage().getMessageId(), error);
                    try {
                        final MessageAck poisonAck = new MessageAck(messageDispatch, (byte)1, 1);
                        poisonAck.setPoisonCause(error);
                        this.localBroker.oneway(poisonAck);
                    }
                    catch (IOException ioe) {
                        DemandForwardingBridgeSupport.LOG.error("Failed to posion ack message following forward failure: ", ioe);
                    }
                    this.fireFailedForwardAdvisory(messageDispatch, error);
                }
                else {
                    DemandForwardingBridgeSupport.LOG.warn("Ignoring exception on forwarding to non existent temp dest: ", error);
                }
                return;
            }
            DemandForwardingBridgeSupport.LOG.info("Network connection between {} and {} shutdown due to a local error: {}", this.localBroker, this.remoteBroker, error);
            DemandForwardingBridgeSupport.LOG.debug("The local Exception was: {}", error, error);
            this.brokerService.getTaskRunnerFactory().execute(new Runnable() {
                @Override
                public void run() {
                    ServiceSupport.dispose(DemandForwardingBridgeSupport.this.getControllingService());
                }
            });
            this.fireBridgeFailed();
        }
    }
    
    private void fireFailedForwardAdvisory(final MessageDispatch messageDispatch, final Throwable error) {
        if (this.configuration.isAdvisoryForFailedForward()) {
            AdvisoryBroker advisoryBroker = null;
            try {
                advisoryBroker = (AdvisoryBroker)this.brokerService.getBroker().getAdaptor(AdvisoryBroker.class);
                if (advisoryBroker != null) {
                    final ConnectionContext context = new ConnectionContext();
                    context.setSecurityContext(SecurityContext.BROKER_SECURITY_CONTEXT);
                    context.setBroker(this.brokerService.getBroker());
                    final ActiveMQMessage advisoryMessage = new ActiveMQMessage();
                    advisoryMessage.setStringProperty("cause", error.getLocalizedMessage());
                    advisoryBroker.fireAdvisory(context, AdvisorySupport.getNetworkBridgeForwardFailureAdvisoryTopic(), messageDispatch.getMessage(), null, advisoryMessage);
                }
            }
            catch (Exception e) {
                DemandForwardingBridgeSupport.LOG.warn("failed to fire forward failure advisory, cause: {}", e);
                DemandForwardingBridgeSupport.LOG.debug("detail", e);
            }
        }
    }
    
    protected Service getControllingService() {
        return (Service)((this.duplexInitiatingConnection != null) ? this.duplexInitiatingConnection : this);
    }
    
    protected void addSubscription(final DemandSubscription sub) throws IOException {
        if (sub != null) {
            if (this.isDuplex()) {
                this.localBroker.request(sub.getLocalInfo());
            }
            else {
                this.localBroker.oneway(sub.getLocalInfo());
            }
        }
    }
    
    protected void removeSubscription(final DemandSubscription sub) throws IOException {
        if (sub != null) {
            DemandForwardingBridgeSupport.LOG.trace("{} remove local subscription: {} for remote {}", this.configuration.getBrokerName(), sub.getLocalInfo().getConsumerId(), sub.getRemoteInfo().getConsumerId());
            this.subscriptionMapByLocalId.remove(sub.getLocalInfo().getConsumerId());
            this.subscriptionMapByRemoteId.remove(sub.getRemoteInfo().getConsumerId());
            this.serialExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    sub.waitForCompletion();
                    try {
                        DemandForwardingBridgeSupport.this.localBroker.oneway(sub.getLocalInfo().createRemoveCommand());
                    }
                    catch (IOException e) {
                        DemandForwardingBridgeSupport.LOG.warn("failed to deliver remove command for local subscription, for remote {}", sub.getRemoteInfo().getConsumerId(), e);
                    }
                }
            });
        }
    }
    
    protected Message configureMessage(final MessageDispatch md) throws IOException {
        final Message message = md.getMessage().copy();
        message.setBrokerPath(this.appendToBrokerPath(message.getBrokerPath(), this.localBrokerPath));
        message.setProducerId(this.producerInfo.getProducerId());
        message.setDestination(md.getDestination());
        message.setMemoryUsage(null);
        if (message.getOriginalTransactionId() == null) {
            message.setOriginalTransactionId(message.getTransactionId());
        }
        message.setTransactionId(null);
        if (this.configuration.isUseCompression()) {
            message.compress();
        }
        return message;
    }
    
    protected void serviceLocalCommand(final Command command) {
        if (!this.disposed.get()) {
            try {
                if (command.isMessageDispatch()) {
                    this.safeWaitUntilStarted();
                    this.enqueueCounter.incrementAndGet();
                    final MessageDispatch md = (MessageDispatch)command;
                    final DemandSubscription sub = this.subscriptionMapByLocalId.get(md.getConsumerId());
                    if (sub != null && md.getMessage() != null && sub.incrementOutstandingResponses()) {
                        if (this.suppressMessageDispatch(md, sub)) {
                            DemandForwardingBridgeSupport.LOG.debug("{} message not forwarded to {} because message came from there or fails TTL, brokerPath: {}, message: {}", this.configuration.getBrokerName(), this.remoteBrokerName, Arrays.toString(md.getMessage().getBrokerPath()), md.getMessage());
                            try {
                                this.localBroker.oneway(new MessageAck(md, (byte)4, 1));
                            }
                            finally {
                                sub.decrementOutstandingResponses();
                            }
                            return;
                        }
                        final Message message = this.configureMessage(md);
                        DemandForwardingBridgeSupport.LOG.debug("bridging ({} -> {}), consumer: {}, destinaition: {}, brokerPath: {}, message: {}", this.configuration.getBrokerName(), this.remoteBrokerName, DemandForwardingBridgeSupport.LOG.isTraceEnabled() ? message : message.getMessageId(), md.getConsumerId(), message.getDestination(), Arrays.toString(message.getBrokerPath()), message);
                        if (this.isDuplex() && "Advisory".equals(message.getType())) {
                            try {
                                this.remoteBroker.oneway(message);
                            }
                            finally {
                                sub.decrementOutstandingResponses();
                            }
                            return;
                        }
                        if (message.isPersistent() || this.configuration.isAlwaysSyncSend()) {
                            this.remoteBroker.asyncRequest(message, new ResponseCallback() {
                                @Override
                                public void onCompletion(final FutureResponse future) {
                                    try {
                                        final Response response = future.getResult();
                                        if (response.isException()) {
                                            final ExceptionResponse er = (ExceptionResponse)response;
                                            DemandForwardingBridgeSupport.this.serviceLocalException(md, er.getException());
                                        }
                                        else {
                                            DemandForwardingBridgeSupport.this.localBroker.oneway(new MessageAck(md, (byte)4, 1));
                                            DemandForwardingBridgeSupport.this.dequeueCounter.incrementAndGet();
                                        }
                                    }
                                    catch (IOException e) {
                                        DemandForwardingBridgeSupport.this.serviceLocalException(md, e);
                                    }
                                    finally {
                                        sub.decrementOutstandingResponses();
                                    }
                                }
                            });
                        }
                        else {
                            try {
                                this.remoteBroker.oneway(message);
                                this.localBroker.oneway(new MessageAck(md, (byte)4, 1));
                                this.dequeueCounter.incrementAndGet();
                            }
                            finally {
                                sub.decrementOutstandingResponses();
                            }
                        }
                        this.serviceOutbound(message);
                    }
                    else {
                        DemandForwardingBridgeSupport.LOG.debug("No subscription registered with this network bridge for consumerId: {} for message: {}", md.getConsumerId(), md.getMessage());
                    }
                }
                else if (command.isBrokerInfo()) {
                    this.futureLocalBrokerInfo.set((BrokerInfo)command);
                }
                else if (command.isShutdownInfo()) {
                    DemandForwardingBridgeSupport.LOG.info("{} Shutting down", this.configuration.getBrokerName());
                    this.stop();
                }
                else if (command.getClass() == ConnectionError.class) {
                    final ConnectionError ce = (ConnectionError)command;
                    this.serviceLocalException(ce.getException());
                }
                else {
                    switch (command.getDataStructureType()) {
                        case 1: {
                            break;
                        }
                        default: {
                            DemandForwardingBridgeSupport.LOG.warn("Unexpected local command: {}", command);
                            break;
                        }
                    }
                }
            }
            catch (Throwable e) {
                DemandForwardingBridgeSupport.LOG.warn("Caught an exception processing local command", e);
                this.serviceLocalException(e);
            }
        }
    }
    
    private boolean suppressMessageDispatch(final MessageDispatch md, final DemandSubscription sub) throws Exception {
        boolean suppress = false;
        if (sub.getLocalInfo().isDurable()) {
            final MessageEvaluationContext messageEvalContext = new MessageEvaluationContext();
            messageEvalContext.setMessageReference(md.getMessage());
            messageEvalContext.setDestination(md.getDestination());
            suppress = !sub.getNetworkBridgeFilter().matches(messageEvalContext);
        }
        return suppress;
    }
    
    public static boolean contains(final BrokerId[] brokerPath, final BrokerId brokerId) {
        if (brokerPath != null) {
            for (final BrokerId id : brokerPath) {
                if (brokerId.equals(id)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    protected BrokerId[] appendToBrokerPath(final BrokerId[] brokerPath, final BrokerId[] pathsToAppend) {
        if (brokerPath == null || brokerPath.length == 0) {
            return pathsToAppend;
        }
        final BrokerId[] rc = new BrokerId[brokerPath.length + pathsToAppend.length];
        System.arraycopy(brokerPath, 0, rc, 0, brokerPath.length);
        System.arraycopy(pathsToAppend, 0, rc, brokerPath.length, pathsToAppend.length);
        return rc;
    }
    
    protected BrokerId[] appendToBrokerPath(final BrokerId[] brokerPath, final BrokerId idToAppend) {
        if (brokerPath == null || brokerPath.length == 0) {
            return new BrokerId[] { idToAppend };
        }
        final BrokerId[] rc = new BrokerId[brokerPath.length + 1];
        System.arraycopy(brokerPath, 0, rc, 0, brokerPath.length);
        rc[brokerPath.length] = idToAppend;
        return rc;
    }
    
    protected boolean isPermissableDestination(final ActiveMQDestination destination) {
        return this.isPermissableDestination(destination, false);
    }
    
    protected boolean isPermissableDestination(final ActiveMQDestination destination, final boolean allowTemporary) {
        if (destination.isTemporary()) {
            return allowTemporary || this.configuration.isBridgeTempDestinations();
        }
        ActiveMQDestination[] dests = this.staticallyIncludedDestinations;
        if (dests != null && dests.length > 0) {
            for (final ActiveMQDestination dest : dests) {
                final DestinationFilter inclusionFilter = DestinationFilter.parseFilter(dest);
                if (dest != null && inclusionFilter.matches(destination) && dest.getDestinationType() == destination.getDestinationType()) {
                    return true;
                }
            }
        }
        dests = this.excludedDestinations;
        if (dests != null && dests.length > 0) {
            for (final ActiveMQDestination dest : dests) {
                final DestinationFilter exclusionFilter = DestinationFilter.parseFilter(dest);
                if (dest != null && exclusionFilter.matches(destination) && dest.getDestinationType() == destination.getDestinationType()) {
                    return false;
                }
            }
        }
        dests = this.dynamicallyIncludedDestinations;
        if (dests != null && dests.length > 0) {
            for (final ActiveMQDestination dest : dests) {
                final DestinationFilter inclusionFilter = DestinationFilter.parseFilter(dest);
                if (dest != null && inclusionFilter.matches(destination) && dest.getDestinationType() == destination.getDestinationType()) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    protected void setupStaticDestinations() {
        final ActiveMQDestination[] dests = this.staticallyIncludedDestinations;
        if (dests != null) {
            for (final ActiveMQDestination dest : dests) {
                final DemandSubscription sub = this.createDemandSubscription(dest);
                sub.setStaticallyIncluded(true);
                try {
                    this.addSubscription(sub);
                }
                catch (IOException e) {
                    DemandForwardingBridgeSupport.LOG.error("Failed to add static destination {}", dest, e);
                }
                DemandForwardingBridgeSupport.LOG.trace("{}, bridging messages for static destination: {}", this.configuration.getBrokerName(), dest);
            }
        }
    }
    
    protected void addConsumerInfo(final ConsumerInfo consumerInfo) throws IOException {
        final ConsumerInfo info = consumerInfo.copy();
        this.addRemoteBrokerToBrokerPath(info);
        final DemandSubscription sub = this.createDemandSubscription(info);
        if (sub != null) {
            if (this.duplicateSuppressionIsRequired(sub)) {
                this.undoMapRegistration(sub);
            }
            else {
                if (consumerInfo.isDurable()) {
                    sub.getDurableRemoteSubs().add(new SubscriptionInfo(sub.getRemoteInfo().getClientId(), consumerInfo.getSubscriptionName()));
                }
                this.addSubscription(sub);
                DemandForwardingBridgeSupport.LOG.debug("{} new demand subscription: {}", this.configuration.getBrokerName(), sub);
            }
        }
    }
    
    private void undoMapRegistration(final DemandSubscription sub) {
        this.subscriptionMapByLocalId.remove(sub.getLocalInfo().getConsumerId());
        this.subscriptionMapByRemoteId.remove(sub.getRemoteInfo().getConsumerId());
    }
    
    private boolean duplicateSuppressionIsRequired(final DemandSubscription candidate) {
        final ConsumerInfo consumerInfo = candidate.getRemoteInfo();
        boolean suppress = false;
        if ((consumerInfo.getDestination().isQueue() && !this.configuration.isSuppressDuplicateQueueSubscriptions()) || (consumerInfo.getDestination().isTopic() && !this.configuration.isSuppressDuplicateTopicSubscriptions())) {
            return suppress;
        }
        final List<ConsumerId> candidateConsumers = consumerInfo.getNetworkConsumerIds();
        final Collection<Subscription> currentSubs = this.getRegionSubscriptions(consumerInfo.getDestination());
        for (final Subscription sub : currentSubs) {
            final List<ConsumerId> networkConsumers = sub.getConsumerInfo().getNetworkConsumerIds();
            if (!networkConsumers.isEmpty() && this.matchFound(candidateConsumers, networkConsumers)) {
                suppress = (!this.isInActiveDurableSub(sub) && this.hasLowerPriority(sub, candidate.getLocalInfo()));
                break;
            }
        }
        return suppress;
    }
    
    private boolean isInActiveDurableSub(final Subscription sub) {
        return sub.getConsumerInfo().isDurable() && sub instanceof DurableTopicSubscription && !((DurableTopicSubscription)sub).isActive();
    }
    
    private boolean hasLowerPriority(final Subscription existingSub, final ConsumerInfo candidateInfo) {
        boolean suppress = false;
        if (existingSub.getConsumerInfo().getPriority() >= candidateInfo.getPriority()) {
            DemandForwardingBridgeSupport.LOG.debug("{} Ignoring duplicate subscription from {}, sub: {} is duplicate by network subscription with equal or higher network priority: {}, networkConsumerIds: {}", this.configuration.getBrokerName(), this.remoteBrokerName, candidateInfo, existingSub, existingSub.getConsumerInfo().getNetworkConsumerIds());
            suppress = true;
        }
        else {
            try {
                this.removeDuplicateSubscription(existingSub);
                DemandForwardingBridgeSupport.LOG.debug("{} Replacing duplicate subscription {} with sub from {}, which has a higher priority, new sub: {}, networkConsumerIds: {}", this.configuration.getBrokerName(), existingSub.getConsumerInfo(), this.remoteBrokerName, candidateInfo, candidateInfo.getNetworkConsumerIds());
            }
            catch (IOException e) {
                DemandForwardingBridgeSupport.LOG.error("Failed to remove duplicated sub as a result of sub with higher priority, sub: {}", existingSub, e);
            }
        }
        return suppress;
    }
    
    private void removeDuplicateSubscription(final Subscription existingSub) throws IOException {
        for (final NetworkConnector connector : this.brokerService.getNetworkConnectors()) {
            if (connector.removeDemandSubscription(existingSub.getConsumerInfo().getConsumerId())) {
                break;
            }
        }
    }
    
    private boolean matchFound(final List<ConsumerId> candidateConsumers, final List<ConsumerId> networkConsumers) {
        boolean found = false;
        for (final ConsumerId aliasConsumer : networkConsumers) {
            if (candidateConsumers.contains(aliasConsumer)) {
                found = true;
                break;
            }
        }
        return found;
    }
    
    protected final Collection<Subscription> getRegionSubscriptions(final ActiveMQDestination dest) {
        final RegionBroker region_broker = (RegionBroker)this.brokerService.getRegionBroker();
        Region region = null;
        switch (dest.getDestinationType()) {
            case 1: {
                region = region_broker.getQueueRegion();
                break;
            }
            case 2: {
                region = region_broker.getTopicRegion();
                break;
            }
            case 5: {
                region = region_broker.getTempQueueRegion();
                break;
            }
            case 6: {
                region = region_broker.getTempTopicRegion();
                break;
            }
        }
        Collection<Subscription> subs;
        if (region instanceof AbstractRegion) {
            subs = ((AbstractRegion)region).getSubscriptions().values();
        }
        else {
            subs = null;
        }
        return subs;
    }
    
    protected DemandSubscription createDemandSubscription(final ConsumerInfo info) throws IOException {
        info.addNetworkConsumerId(info.getConsumerId());
        return this.doCreateDemandSubscription(info);
    }
    
    protected DemandSubscription doCreateDemandSubscription(final ConsumerInfo info) throws IOException {
        final DemandSubscription result = new DemandSubscription(info);
        result.getLocalInfo().setConsumerId(new ConsumerId(this.localSessionInfo.getSessionId(), this.consumerIdGenerator.getNextSequenceId()));
        if (info.getDestination().isTemporary()) {
            final ActiveMQTempDestination dest = (ActiveMQTempDestination)result.getLocalInfo().getDestination();
            dest.setConnectionId(this.localConnectionInfo.getConnectionId().toString());
        }
        if (this.configuration.isDecreaseNetworkConsumerPriority()) {
            byte priority = (byte)this.configuration.getConsumerPriorityBase();
            if (info.getBrokerPath() != null && info.getBrokerPath().length > 1) {
                priority -= (byte)(info.getBrokerPath().length + 1);
            }
            result.getLocalInfo().setPriority(priority);
            DemandForwardingBridgeSupport.LOG.debug("{} using priority: {} for subscription: {}", this.configuration.getBrokerName(), priority, info);
        }
        this.configureDemandSubscription(info, result);
        return result;
    }
    
    protected final DemandSubscription createDemandSubscription(final ActiveMQDestination destination) {
        final ConsumerInfo info = new ConsumerInfo();
        info.setNetworkSubscription(true);
        info.setDestination(destination);
        info.setBrokerPath(new BrokerId[] { this.remoteBrokerId });
        info.setConsumerId(new ConsumerId(this.localSessionInfo.getSessionId(), this.consumerIdGenerator.getNextSequenceId()));
        DemandSubscription result = null;
        try {
            result = this.createDemandSubscription(info);
        }
        catch (IOException e) {
            DemandForwardingBridgeSupport.LOG.error("Failed to create DemandSubscription ", e);
        }
        return result;
    }
    
    protected void configureDemandSubscription(final ConsumerInfo info, final DemandSubscription sub) throws IOException {
        if (AdvisorySupport.isConsumerAdvisoryTopic(info.getDestination())) {
            sub.getLocalInfo().setDispatchAsync(true);
        }
        else {
            sub.getLocalInfo().setDispatchAsync(this.configuration.isDispatchAsync());
        }
        sub.getLocalInfo().setPrefetchSize(this.configuration.getPrefetchSize());
        this.subscriptionMapByLocalId.put(sub.getLocalInfo().getConsumerId(), sub);
        this.subscriptionMapByRemoteId.put(sub.getRemoteInfo().getConsumerId(), sub);
        sub.setNetworkBridgeFilter(this.createNetworkBridgeFilter(info));
        if (!info.isDurable()) {
            sub.getLocalInfo().setAdditionalPredicate(sub.getNetworkBridgeFilter());
        }
        else {
            sub.setLocalDurableSubscriber(new SubscriptionInfo(info.getClientId(), info.getSubscriptionName()));
        }
    }
    
    protected void removeDemandSubscription(final ConsumerId id) throws IOException {
        final DemandSubscription sub = this.subscriptionMapByRemoteId.remove(id);
        DemandForwardingBridgeSupport.LOG.debug("{} remove request on {} from {}, consumer id: {}, matching sub: {}", this.configuration.getBrokerName(), this.localBroker, this.remoteBrokerName, id, sub);
        if (sub != null) {
            this.removeSubscription(sub);
            DemandForwardingBridgeSupport.LOG.debug("{} removed sub on {} from {}: {}", this.configuration.getBrokerName(), this.localBroker, this.remoteBrokerName, sub.getRemoteInfo());
        }
    }
    
    protected boolean removeDemandSubscriptionByLocalId(final ConsumerId consumerId) {
        boolean removeDone = false;
        final DemandSubscription sub = this.subscriptionMapByLocalId.get(consumerId);
        if (sub != null) {
            try {
                this.removeDemandSubscription(sub.getRemoteInfo().getConsumerId());
                removeDone = true;
            }
            catch (IOException e) {
                DemandForwardingBridgeSupport.LOG.debug("removeDemandSubscriptionByLocalId failed for localId: {}", consumerId, e);
            }
        }
        return removeDone;
    }
    
    protected void safeWaitUntilStarted() throws InterruptedException {
        while (!this.disposed.get()) {
            if (this.startedLatch.await(1L, TimeUnit.SECONDS)) {
                return;
            }
        }
    }
    
    protected NetworkBridgeFilter createNetworkBridgeFilter(final ConsumerInfo info) throws IOException {
        NetworkBridgeFilterFactory filterFactory = this.defaultFilterFactory;
        if (this.brokerService != null && this.brokerService.getDestinationPolicy() != null) {
            final PolicyEntry entry = this.brokerService.getDestinationPolicy().getEntryFor(info.getDestination());
            if (entry != null && entry.getNetworkBridgeFilterFactory() != null) {
                filterFactory = entry.getNetworkBridgeFilterFactory();
            }
        }
        return filterFactory.create(info, this.getRemoteBrokerPath(), this.configuration.getMessageTTL(), this.configuration.getConsumerTTL());
    }
    
    protected void addRemoteBrokerToBrokerPath(final ConsumerInfo info) throws IOException {
        info.setBrokerPath(this.appendToBrokerPath(info.getBrokerPath(), this.getRemoteBrokerPath()));
    }
    
    protected BrokerId[] getRemoteBrokerPath() {
        return this.remoteBrokerPath;
    }
    
    @Override
    public void setNetworkBridgeListener(final NetworkBridgeListener listener) {
        this.networkBridgeListener = listener;
    }
    
    private void fireBridgeFailed() {
        final NetworkBridgeListener l = this.networkBridgeListener;
        if (l != null && this.bridgeFailed.compareAndSet(false, true)) {
            l.bridgeFailed();
        }
    }
    
    public ActiveMQDestination[] getDynamicallyIncludedDestinations() {
        return this.dynamicallyIncludedDestinations;
    }
    
    public void setDynamicallyIncludedDestinations(final ActiveMQDestination[] dynamicallyIncludedDestinations) {
        this.dynamicallyIncludedDestinations = dynamicallyIncludedDestinations;
    }
    
    public ActiveMQDestination[] getExcludedDestinations() {
        return this.excludedDestinations;
    }
    
    public void setExcludedDestinations(final ActiveMQDestination[] excludedDestinations) {
        this.excludedDestinations = excludedDestinations;
    }
    
    public ActiveMQDestination[] getStaticallyIncludedDestinations() {
        return this.staticallyIncludedDestinations;
    }
    
    public void setStaticallyIncludedDestinations(final ActiveMQDestination[] staticallyIncludedDestinations) {
        this.staticallyIncludedDestinations = staticallyIncludedDestinations;
    }
    
    public ActiveMQDestination[] getDurableDestinations() {
        return this.durableDestinations;
    }
    
    public void setDurableDestinations(final ActiveMQDestination[] durableDestinations) {
        this.durableDestinations = durableDestinations;
    }
    
    public Transport getLocalBroker() {
        return this.localBroker;
    }
    
    public Transport getRemoteBroker() {
        return this.remoteBroker;
    }
    
    public boolean isCreatedByDuplex() {
        return this.createdByDuplex;
    }
    
    public void setCreatedByDuplex(final boolean createdByDuplex) {
        this.createdByDuplex = createdByDuplex;
    }
    
    @Override
    public String getRemoteAddress() {
        return this.remoteBroker.getRemoteAddress();
    }
    
    @Override
    public String getLocalAddress() {
        return this.localBroker.getRemoteAddress();
    }
    
    @Override
    public String getRemoteBrokerName() {
        return (this.remoteBrokerInfo == null) ? null : this.remoteBrokerInfo.getBrokerName();
    }
    
    @Override
    public String getRemoteBrokerId() {
        return (this.remoteBrokerInfo == null || this.remoteBrokerInfo.getBrokerId() == null) ? null : this.remoteBrokerInfo.getBrokerId().toString();
    }
    
    @Override
    public String getLocalBrokerName() {
        return (this.localBrokerInfo == null) ? null : this.localBrokerInfo.getBrokerName();
    }
    
    @Override
    public long getDequeueCounter() {
        return this.dequeueCounter.get();
    }
    
    @Override
    public long getEnqueueCounter() {
        return this.enqueueCounter.get();
    }
    
    protected boolean isDuplex() {
        return this.configuration.isDuplex() || this.createdByDuplex;
    }
    
    public ConcurrentHashMap<ConsumerId, DemandSubscription> getLocalSubscriptionMap() {
        return this.subscriptionMapByRemoteId;
    }
    
    @Override
    public void setBrokerService(final BrokerService brokerService) {
        this.brokerService = brokerService;
        this.localBrokerId = brokerService.getRegionBroker().getBrokerId();
        this.localBrokerPath[0] = this.localBrokerId;
    }
    
    @Override
    public void setMbeanObjectName(final ObjectName objectName) {
        this.mbeanObjectName = objectName;
    }
    
    @Override
    public ObjectName getMbeanObjectName() {
        return this.mbeanObjectName;
    }
    
    @Override
    public void resetStats() {
        this.enqueueCounter.set(0L);
        this.dequeueCounter.set(0L);
    }
    
    protected void serviceOutbound(final Message message) {
        final NetworkBridgeListener l = this.networkBridgeListener;
        if (l != null) {
            l.onOutboundMessage(this, message);
        }
    }
    
    protected void serviceInboundMessage(final Message message) {
        final NetworkBridgeListener l = this.networkBridgeListener;
        if (l != null) {
            l.onInboundMessage(this, message);
        }
    }
    
    protected boolean canDuplexDispatch(final Message message) {
        boolean result = true;
        if (this.configuration.isCheckDuplicateMessagesOnDuplex()) {
            final long producerSequenceId = message.getMessageId().getProducerSequenceId();
            final long lastStoredForMessageProducer = this.getStoredSequenceIdForMessage(message.getMessageId());
            if (producerSequenceId <= lastStoredForMessageProducer) {
                result = false;
                DemandForwardingBridgeSupport.LOG.debug("suppressing duplicate message send [{}] from network producer with producerSequence [{}] less than last stored: {}", DemandForwardingBridgeSupport.LOG.isTraceEnabled() ? message : message.getMessageId(), producerSequenceId, lastStoredForMessageProducer);
            }
        }
        return result;
    }
    
    protected long getStoredSequenceIdForMessage(final MessageId messageId) {
        try {
            return this.brokerService.getPersistenceAdapter().getLastProducerSequenceId(messageId.getProducerId());
        }
        catch (IOException ignored) {
            DemandForwardingBridgeSupport.LOG.debug("Failed to determine last producer sequence id for: {}", messageId, ignored);
            return -1L;
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(DemandForwardingBridgeSupport.class);
    }
    
    private static class FutureBrokerInfo implements Future<BrokerInfo>
    {
        private final CountDownLatch slot;
        private final AtomicBoolean disposed;
        private BrokerInfo info;
        
        public FutureBrokerInfo(final BrokerInfo info, final AtomicBoolean disposed) {
            this.slot = new CountDownLatch(1);
            this.info = null;
            this.info = info;
            this.disposed = disposed;
        }
        
        @Override
        public boolean cancel(final boolean mayInterruptIfRunning) {
            this.slot.countDown();
            return true;
        }
        
        @Override
        public boolean isCancelled() {
            return this.slot.getCount() == 0L && this.info == null;
        }
        
        @Override
        public boolean isDone() {
            return this.info != null;
        }
        
        @Override
        public BrokerInfo get() throws InterruptedException, ExecutionException {
            try {
                if (this.info == null) {
                    while (!this.disposed.get() && !this.slot.await(1L, TimeUnit.SECONDS)) {}
                }
                return this.info;
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                DemandForwardingBridgeSupport.LOG.debug("Operation interrupted: {}", e, e);
                throw new InterruptedException("Interrupted.");
            }
        }
        
        @Override
        public BrokerInfo get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            try {
                if (this.info == null) {
                    final long deadline = System.currentTimeMillis() + unit.toMillis(timeout);
                    while ((!this.disposed.get() || System.currentTimeMillis() < deadline) && !this.slot.await(1L, TimeUnit.MILLISECONDS)) {}
                    if (this.info == null) {
                        throw new TimeoutException();
                    }
                }
                return this.info;
            }
            catch (InterruptedException e) {
                throw new InterruptedException("Interrupted.");
            }
        }
        
        public void set(final BrokerInfo info) {
            this.info = info;
            this.slot.countDown();
        }
    }
}
