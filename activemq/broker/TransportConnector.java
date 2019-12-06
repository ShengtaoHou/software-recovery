// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

import java.util.regex.Pattern;
import java.util.StringTokenizer;
import org.apache.activemq.command.ConnectionControl;
import org.apache.activemq.transport.discovery.DiscoveryAgentFactory;
import org.apache.activemq.transport.TransportFactorySupport;
import java.util.Iterator;
import org.apache.activemq.util.ServiceStopper;
import org.apache.activemq.Service;
import org.apache.activemq.util.ServiceSupport;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.transport.TransportAcceptListener;
import java.net.URISyntaxException;
import java.io.IOException;
import org.apache.activemq.broker.jmx.ManagedTransportConnector;
import javax.management.ObjectName;
import org.apache.activemq.broker.jmx.ManagementContext;
import org.slf4j.LoggerFactory;
import java.util.LinkedList;
import org.apache.activemq.broker.region.ConnectorStatistics;
import org.apache.activemq.transport.discovery.DiscoveryAgent;
import org.apache.activemq.security.MessageAuthorizationPolicy;
import org.apache.activemq.thread.TaskRunnerFactory;
import org.apache.activemq.command.BrokerInfo;
import java.net.URI;
import org.apache.activemq.transport.TransportServer;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;

public class TransportConnector implements Connector, BrokerServiceAware
{
    final Logger LOG;
    protected final CopyOnWriteArrayList<TransportConnection> connections;
    protected TransportStatusDetector statusDector;
    private BrokerService brokerService;
    private TransportServer server;
    private URI uri;
    private BrokerInfo brokerInfo;
    private TaskRunnerFactory taskRunnerFactory;
    private MessageAuthorizationPolicy messageAuthorizationPolicy;
    private DiscoveryAgent discoveryAgent;
    private final ConnectorStatistics statistics;
    private URI discoveryUri;
    private String name;
    private boolean disableAsyncDispatch;
    private boolean enableStatusMonitor;
    private Broker broker;
    private boolean updateClusterClients;
    private boolean rebalanceClusterClients;
    private boolean updateClusterClientsOnRemove;
    private String updateClusterFilter;
    private boolean auditNetworkProducers;
    private int maximumProducersAllowedPerConnection;
    private int maximumConsumersAllowedPerConnection;
    private PublishedAddressPolicy publishedAddressPolicy;
    private boolean allowLinkStealing;
    LinkedList<String> peerBrokers;
    
    public TransportConnector() {
        this.LOG = LoggerFactory.getLogger(TransportConnector.class);
        this.connections = new CopyOnWriteArrayList<TransportConnection>();
        this.brokerInfo = new BrokerInfo();
        this.statistics = new ConnectorStatistics();
        this.enableStatusMonitor = false;
        this.updateClusterClients = false;
        this.updateClusterClientsOnRemove = false;
        this.auditNetworkProducers = false;
        this.maximumProducersAllowedPerConnection = Integer.MAX_VALUE;
        this.maximumConsumersAllowedPerConnection = Integer.MAX_VALUE;
        this.publishedAddressPolicy = new PublishedAddressPolicy();
        this.peerBrokers = new LinkedList<String>();
    }
    
    public TransportConnector(final TransportServer server) {
        this();
        this.setServer(server);
        if (server != null && server.getConnectURI() != null) {
            final URI uri = server.getConnectURI();
            if (uri != null && uri.getScheme().equals("vm")) {
                this.setEnableStatusMonitor(false);
            }
        }
        if (server != null) {
            this.setAllowLinkStealing(server.isAllowLinkStealing());
        }
    }
    
    public CopyOnWriteArrayList<TransportConnection> getConnections() {
        return this.connections;
    }
    
    public ManagedTransportConnector asManagedConnector(final ManagementContext context, final ObjectName connectorName) throws IOException, URISyntaxException {
        final ManagedTransportConnector rc = new ManagedTransportConnector(context, connectorName, this.getServer());
        rc.setBrokerInfo(this.getBrokerInfo());
        rc.setDisableAsyncDispatch(this.isDisableAsyncDispatch());
        rc.setDiscoveryAgent(this.getDiscoveryAgent());
        rc.setDiscoveryUri(this.getDiscoveryUri());
        rc.setEnableStatusMonitor(this.isEnableStatusMonitor());
        rc.setMessageAuthorizationPolicy(this.getMessageAuthorizationPolicy());
        rc.setName(this.getName());
        rc.setTaskRunnerFactory(this.getTaskRunnerFactory());
        rc.setUri(this.getUri());
        rc.setBrokerService(this.brokerService);
        rc.setUpdateClusterClients(this.isUpdateClusterClients());
        rc.setRebalanceClusterClients(this.isRebalanceClusterClients());
        rc.setUpdateClusterFilter(this.getUpdateClusterFilter());
        rc.setUpdateClusterClientsOnRemove(this.isUpdateClusterClientsOnRemove());
        rc.setAuditNetworkProducers(this.isAuditNetworkProducers());
        rc.setMaximumConsumersAllowedPerConnection(this.getMaximumConsumersAllowedPerConnection());
        rc.setMaximumProducersAllowedPerConnection(this.getMaximumProducersAllowedPerConnection());
        rc.setPublishedAddressPolicy(this.getPublishedAddressPolicy());
        rc.setAllowLinkStealing(this.isAllowLinkStealing());
        return rc;
    }
    
    @Override
    public BrokerInfo getBrokerInfo() {
        return this.brokerInfo;
    }
    
    public void setBrokerInfo(final BrokerInfo brokerInfo) {
        this.brokerInfo = brokerInfo;
    }
    
    public TransportServer getServer() throws IOException, URISyntaxException {
        if (this.server == null) {
            this.setServer(this.createTransportServer());
        }
        return this.server;
    }
    
    public void setServer(final TransportServer server) {
        this.server = server;
    }
    
    public URI getUri() {
        if (this.uri == null) {
            try {
                this.uri = this.getConnectUri();
            }
            catch (Throwable t) {}
        }
        return this.uri;
    }
    
    public void setUri(final URI uri) {
        this.uri = uri;
    }
    
    public TaskRunnerFactory getTaskRunnerFactory() {
        return this.taskRunnerFactory;
    }
    
    public void setTaskRunnerFactory(final TaskRunnerFactory taskRunnerFactory) {
        this.taskRunnerFactory = taskRunnerFactory;
    }
    
    @Override
    public ConnectorStatistics getStatistics() {
        return this.statistics;
    }
    
    public MessageAuthorizationPolicy getMessageAuthorizationPolicy() {
        return this.messageAuthorizationPolicy;
    }
    
    public void setMessageAuthorizationPolicy(final MessageAuthorizationPolicy messageAuthorizationPolicy) {
        this.messageAuthorizationPolicy = messageAuthorizationPolicy;
    }
    
    @Override
    public void start() throws Exception {
        this.broker = this.brokerService.getBroker();
        this.brokerInfo.setBrokerName(this.broker.getBrokerName());
        this.brokerInfo.setBrokerId(this.broker.getBrokerId());
        this.brokerInfo.setPeerBrokerInfos(this.broker.getPeerBrokerInfos());
        this.brokerInfo.setFaultTolerantConfiguration(this.broker.isFaultTolerantConfiguration());
        this.brokerInfo.setBrokerURL(this.broker.getBrokerService().getDefaultSocketURIString());
        this.getServer().setAcceptListener(new TransportAcceptListener() {
            @Override
            public void onAccept(final Transport transport) {
                try {
                    TransportConnector.this.brokerService.getTaskRunnerFactory().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (TransportConnector.this.brokerService.isStopping()) {
                                    throw new BrokerStoppedException("Broker " + TransportConnector.this.brokerService + " is being stopped");
                                }
                                final Connection connection = TransportConnector.this.createConnection(transport);
                                connection.start();
                            }
                            catch (Exception e) {
                                final String remoteHost = transport.getRemoteAddress();
                                ServiceSupport.dispose(transport);
                                TransportConnector$1.this.onAcceptError(e, remoteHost);
                            }
                        }
                    });
                }
                catch (Exception e) {
                    final String remoteHost = transport.getRemoteAddress();
                    ServiceSupport.dispose(transport);
                    this.onAcceptError(e, remoteHost);
                }
            }
            
            @Override
            public void onAcceptError(final Exception error) {
                this.onAcceptError(error, null);
            }
            
            private void onAcceptError(final Exception error, final String remoteHost) {
                TransportConnector.this.LOG.error("Could not accept connection " + ((remoteHost == null) ? "" : ("from " + remoteHost)) + ": " + error);
                TransportConnector.this.LOG.debug("Reason: " + error, error);
            }
        });
        this.getServer().setBrokerInfo(this.brokerInfo);
        this.getServer().start();
        final DiscoveryAgent da = this.getDiscoveryAgent();
        if (da != null) {
            da.registerService(this.getPublishableConnectString());
            da.start();
        }
        if (this.enableStatusMonitor) {
            (this.statusDector = new TransportStatusDetector(this)).start();
        }
        this.LOG.info("Connector {} started", this.getName());
    }
    
    public String getPublishableConnectString() throws Exception {
        final String publishableConnectString = this.publishedAddressPolicy.getPublishableConnectString(this);
        this.LOG.debug("Publishing: {} for broker transport URI: {}", publishableConnectString, this.getConnectUri());
        return publishableConnectString;
    }
    
    public URI getPublishableConnectURI() throws Exception {
        return this.publishedAddressPolicy.getPublishableConnectURI(this);
    }
    
    @Override
    public void stop() throws Exception {
        final ServiceStopper ss = new ServiceStopper();
        if (this.discoveryAgent != null) {
            ss.stop(this.discoveryAgent);
        }
        if (this.server != null) {
            ss.stop(this.server);
        }
        if (this.statusDector != null) {
            this.statusDector.stop();
        }
        for (final TransportConnection connection : this.connections) {
            ss.stop(connection);
        }
        this.server = null;
        ss.throwFirstException();
        this.LOG.info("Connector {} stopped", this.getName());
    }
    
    protected Connection createConnection(final Transport transport) throws IOException {
        final TransportConnection answer = new TransportConnection(this, transport, this.broker, this.disableAsyncDispatch ? null : this.taskRunnerFactory, this.brokerService.getTaskRunnerFactory());
        final boolean statEnabled = this.getStatistics().isEnabled();
        answer.getStatistics().setEnabled(statEnabled);
        answer.setMessageAuthorizationPolicy(this.messageAuthorizationPolicy);
        return answer;
    }
    
    protected TransportServer createTransportServer() throws IOException, URISyntaxException {
        if (this.uri == null) {
            throw new IllegalArgumentException("You must specify either a server or uri property");
        }
        if (this.brokerService == null) {
            throw new IllegalArgumentException("You must specify the brokerService property. Maybe this connector should be added to a broker?");
        }
        return TransportFactorySupport.bind(this.brokerService, this.uri);
    }
    
    public DiscoveryAgent getDiscoveryAgent() throws IOException {
        if (this.discoveryAgent == null) {
            this.discoveryAgent = this.createDiscoveryAgent();
        }
        return this.discoveryAgent;
    }
    
    protected DiscoveryAgent createDiscoveryAgent() throws IOException {
        if (this.discoveryUri != null) {
            final DiscoveryAgent agent = DiscoveryAgentFactory.createDiscoveryAgent(this.discoveryUri);
            if (agent != null && agent instanceof BrokerServiceAware) {
                ((BrokerServiceAware)agent).setBrokerService(this.brokerService);
            }
            return agent;
        }
        return null;
    }
    
    public void setDiscoveryAgent(final DiscoveryAgent discoveryAgent) {
        this.discoveryAgent = discoveryAgent;
    }
    
    public URI getDiscoveryUri() {
        return this.discoveryUri;
    }
    
    public void setDiscoveryUri(final URI discoveryUri) {
        this.discoveryUri = discoveryUri;
    }
    
    public URI getConnectUri() throws IOException, URISyntaxException {
        if (this.server != null) {
            return this.server.getConnectURI();
        }
        return this.uri;
    }
    
    public void onStarted(final TransportConnection connection) {
        this.connections.add(connection);
    }
    
    public void onStopped(final TransportConnection connection) {
        this.connections.remove(connection);
    }
    
    public String getName() {
        if (this.name == null) {
            this.uri = this.getUri();
            if (this.uri != null) {
                this.name = this.uri.toString();
            }
        }
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        String rc = this.getName();
        if (rc == null) {
            rc = super.toString();
        }
        return rc;
    }
    
    protected ConnectionControl getConnectionControl() {
        final boolean rebalance = this.isRebalanceClusterClients();
        String connectedBrokers = "";
        String separator = "";
        if (this.isUpdateClusterClients()) {
            synchronized (this.peerBrokers) {
                for (final String uri : this.getPeerBrokers()) {
                    connectedBrokers = connectedBrokers + separator + uri;
                    separator = ",";
                }
                if (rebalance) {
                    final String shuffle = this.peerBrokers.removeFirst();
                    this.peerBrokers.addLast(shuffle);
                }
            }
        }
        final ConnectionControl control = new ConnectionControl();
        control.setConnectedBrokers(connectedBrokers);
        control.setRebalanceConnection(rebalance);
        return control;
    }
    
    public void addPeerBroker(final BrokerInfo info) {
        if (this.isMatchesClusterFilter(info.getBrokerName())) {
            synchronized (this.peerBrokers) {
                this.getPeerBrokers().addLast(info.getBrokerURL());
            }
        }
    }
    
    public void removePeerBroker(final BrokerInfo info) {
        synchronized (this.peerBrokers) {
            this.getPeerBrokers().remove(info.getBrokerURL());
        }
    }
    
    public LinkedList<String> getPeerBrokers() {
        synchronized (this.peerBrokers) {
            if (this.peerBrokers.isEmpty()) {
                this.peerBrokers.add(this.brokerService.getDefaultSocketURIString());
            }
            return this.peerBrokers;
        }
    }
    
    @Override
    public void updateClientClusterInfo() {
        if (this.isRebalanceClusterClients() || this.isUpdateClusterClients()) {
            ConnectionControl control = this.getConnectionControl();
            for (final Connection c : this.connections) {
                c.updateClient(control);
                if (this.isRebalanceClusterClients()) {
                    control = this.getConnectionControl();
                }
            }
        }
    }
    
    private boolean isMatchesClusterFilter(final String brokerName) {
        boolean result = true;
        String filter = this.getUpdateClusterFilter();
        if (filter != null) {
            filter = filter.trim();
            if (filter.length() > 0) {
                String token;
                for (StringTokenizer tokenizer = new StringTokenizer(filter, ","); result && tokenizer.hasMoreTokens(); result = this.isMatchesClusterFilter(brokerName, token)) {
                    token = tokenizer.nextToken();
                }
            }
        }
        return result;
    }
    
    private boolean isMatchesClusterFilter(final String brokerName, final String match) {
        boolean result = true;
        if (brokerName != null && match != null && brokerName.length() > 0 && match.length() > 0) {
            result = Pattern.matches(match, brokerName);
        }
        return result;
    }
    
    public boolean isDisableAsyncDispatch() {
        return this.disableAsyncDispatch;
    }
    
    public void setDisableAsyncDispatch(final boolean disableAsyncDispatch) {
        this.disableAsyncDispatch = disableAsyncDispatch;
    }
    
    public boolean isEnableStatusMonitor() {
        return this.enableStatusMonitor;
    }
    
    public void setEnableStatusMonitor(final boolean enableStatusMonitor) {
        this.enableStatusMonitor = enableStatusMonitor;
    }
    
    @Override
    public void setBrokerService(final BrokerService brokerService) {
        this.brokerService = brokerService;
    }
    
    public Broker getBroker() {
        return this.broker;
    }
    
    public BrokerService getBrokerService() {
        return this.brokerService;
    }
    
    @Override
    public boolean isUpdateClusterClients() {
        return this.updateClusterClients;
    }
    
    public void setUpdateClusterClients(final boolean updateClusterClients) {
        this.updateClusterClients = updateClusterClients;
    }
    
    @Override
    public boolean isRebalanceClusterClients() {
        return this.rebalanceClusterClients;
    }
    
    public void setRebalanceClusterClients(final boolean rebalanceClusterClients) {
        this.rebalanceClusterClients = rebalanceClusterClients;
    }
    
    @Override
    public boolean isUpdateClusterClientsOnRemove() {
        return this.updateClusterClientsOnRemove;
    }
    
    public void setUpdateClusterClientsOnRemove(final boolean updateClusterClientsOnRemove) {
        this.updateClusterClientsOnRemove = updateClusterClientsOnRemove;
    }
    
    public String getUpdateClusterFilter() {
        return this.updateClusterFilter;
    }
    
    public void setUpdateClusterFilter(final String updateClusterFilter) {
        this.updateClusterFilter = updateClusterFilter;
    }
    
    @Override
    public int connectionCount() {
        return this.connections.size();
    }
    
    @Override
    public boolean isAllowLinkStealing() {
        return this.allowLinkStealing;
    }
    
    public void setAllowLinkStealing(final boolean allowLinkStealing) {
        this.allowLinkStealing = allowLinkStealing;
    }
    
    public boolean isAuditNetworkProducers() {
        return this.auditNetworkProducers;
    }
    
    public void setAuditNetworkProducers(final boolean auditNetworkProducers) {
        this.auditNetworkProducers = auditNetworkProducers;
    }
    
    public int getMaximumProducersAllowedPerConnection() {
        return this.maximumProducersAllowedPerConnection;
    }
    
    public void setMaximumProducersAllowedPerConnection(final int maximumProducersAllowedPerConnection) {
        this.maximumProducersAllowedPerConnection = maximumProducersAllowedPerConnection;
    }
    
    public int getMaximumConsumersAllowedPerConnection() {
        return this.maximumConsumersAllowedPerConnection;
    }
    
    public void setMaximumConsumersAllowedPerConnection(final int maximumConsumersAllowedPerConnection) {
        this.maximumConsumersAllowedPerConnection = maximumConsumersAllowedPerConnection;
    }
    
    public PublishedAddressPolicy getPublishedAddressPolicy() {
        return this.publishedAddressPolicy;
    }
    
    public void setPublishedAddressPolicy(final PublishedAddressPolicy publishedAddressPolicy) {
        this.publishedAddressPolicy = publishedAddressPolicy;
    }
}
