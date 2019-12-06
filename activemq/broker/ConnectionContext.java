// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

import java.io.IOException;
import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.command.XATransactionId;
import org.apache.activemq.state.ConnectionState;
import org.apache.activemq.filter.MessageEvaluationContext;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.security.MessageAuthorizationPolicy;
import org.apache.activemq.command.WireFormatInfo;
import org.apache.activemq.command.ConnectionId;
import org.apache.activemq.security.SecurityContext;
import org.apache.activemq.command.TransactionId;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.activemq.transaction.Transaction;

public class ConnectionContext
{
    private Connection connection;
    private Connector connector;
    private Broker broker;
    private boolean inRecoveryMode;
    private Transaction transaction;
    private ConcurrentHashMap<TransactionId, Transaction> transactions;
    private SecurityContext securityContext;
    private ConnectionId connectionId;
    private String clientId;
    private String userName;
    private boolean reconnect;
    private WireFormatInfo wireFormatInfo;
    private Object longTermStoreContext;
    private boolean producerFlowControl;
    private MessageAuthorizationPolicy messageAuthorizationPolicy;
    private boolean networkConnection;
    private boolean faultTolerant;
    private final AtomicBoolean stopping;
    private final MessageEvaluationContext messageEvaluationContext;
    private boolean dontSendReponse;
    private boolean clientMaster;
    private ConnectionState connectionState;
    private XATransactionId xid;
    
    public ConnectionContext() {
        this.producerFlowControl = true;
        this.stopping = new AtomicBoolean();
        this.clientMaster = true;
        this.messageEvaluationContext = new MessageEvaluationContext();
    }
    
    public ConnectionContext(final MessageEvaluationContext messageEvaluationContext) {
        this.producerFlowControl = true;
        this.stopping = new AtomicBoolean();
        this.clientMaster = true;
        this.messageEvaluationContext = messageEvaluationContext;
    }
    
    public ConnectionContext(final ConnectionInfo info) {
        this();
        this.setClientId(info.getClientId());
        this.setUserName(info.getUserName());
        this.setConnectionId(info.getConnectionId());
    }
    
    public ConnectionContext copy() {
        final ConnectionContext rc = new ConnectionContext(this.messageEvaluationContext);
        rc.connection = this.connection;
        rc.connector = this.connector;
        rc.broker = this.broker;
        rc.inRecoveryMode = this.inRecoveryMode;
        rc.transaction = this.transaction;
        rc.transactions = this.transactions;
        rc.securityContext = this.securityContext;
        rc.connectionId = this.connectionId;
        rc.clientId = this.clientId;
        rc.userName = this.userName;
        rc.reconnect = this.reconnect;
        rc.wireFormatInfo = this.wireFormatInfo;
        rc.longTermStoreContext = this.longTermStoreContext;
        rc.producerFlowControl = this.producerFlowControl;
        rc.messageAuthorizationPolicy = this.messageAuthorizationPolicy;
        rc.networkConnection = this.networkConnection;
        rc.faultTolerant = this.faultTolerant;
        rc.stopping.set(this.stopping.get());
        rc.dontSendReponse = this.dontSendReponse;
        rc.clientMaster = this.clientMaster;
        return rc;
    }
    
    public SecurityContext getSecurityContext() {
        return this.securityContext;
    }
    
    public void setSecurityContext(final SecurityContext subject) {
        this.securityContext = subject;
        if (subject != null) {
            this.setUserName(subject.getUserName());
        }
        else {
            this.setUserName(null);
        }
    }
    
    public Broker getBroker() {
        return this.broker;
    }
    
    public void setBroker(final Broker broker) {
        this.broker = broker;
    }
    
    public Connection getConnection() {
        return this.connection;
    }
    
    public void setConnection(final Connection connection) {
        this.connection = connection;
    }
    
    public Transaction getTransaction() {
        return this.transaction;
    }
    
    public void setTransaction(final Transaction transaction) {
        this.transaction = transaction;
    }
    
    public Connector getConnector() {
        return this.connector;
    }
    
    public void setConnector(final Connector connector) {
        this.connector = connector;
    }
    
    public MessageAuthorizationPolicy getMessageAuthorizationPolicy() {
        return this.messageAuthorizationPolicy;
    }
    
    public void setMessageAuthorizationPolicy(final MessageAuthorizationPolicy messageAuthorizationPolicy) {
        this.messageAuthorizationPolicy = messageAuthorizationPolicy;
    }
    
    public boolean isInRecoveryMode() {
        return this.inRecoveryMode;
    }
    
    public void setInRecoveryMode(final boolean inRecoveryMode) {
        this.inRecoveryMode = inRecoveryMode;
    }
    
    public ConcurrentHashMap<TransactionId, Transaction> getTransactions() {
        return this.transactions;
    }
    
    public void setTransactions(final ConcurrentHashMap<TransactionId, Transaction> transactions) {
        this.transactions = transactions;
    }
    
    public boolean isInTransaction() {
        return this.transaction != null;
    }
    
    public String getClientId() {
        return this.clientId;
    }
    
    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }
    
    public boolean isReconnect() {
        return this.reconnect;
    }
    
    public void setReconnect(final boolean reconnect) {
        this.reconnect = reconnect;
    }
    
    public WireFormatInfo getWireFormatInfo() {
        return this.wireFormatInfo;
    }
    
    public void setWireFormatInfo(final WireFormatInfo wireFormatInfo) {
        this.wireFormatInfo = wireFormatInfo;
    }
    
    public ConnectionId getConnectionId() {
        return this.connectionId;
    }
    
    public void setConnectionId(final ConnectionId connectionId) {
        this.connectionId = connectionId;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    protected void setUserName(final String userName) {
        this.userName = userName;
    }
    
    public MessageEvaluationContext getMessageEvaluationContext() {
        return this.messageEvaluationContext;
    }
    
    public Object getLongTermStoreContext() {
        return this.longTermStoreContext;
    }
    
    public void setLongTermStoreContext(final Object longTermStoreContext) {
        this.longTermStoreContext = longTermStoreContext;
    }
    
    public boolean isProducerFlowControl() {
        return this.producerFlowControl;
    }
    
    public void setProducerFlowControl(final boolean disableProducerFlowControl) {
        this.producerFlowControl = disableProducerFlowControl;
    }
    
    public boolean isAllowedToConsume(final MessageReference n) throws IOException {
        return this.messageAuthorizationPolicy == null || this.messageAuthorizationPolicy.isAllowedToConsume(this, n.getMessage());
    }
    
    public synchronized boolean isNetworkConnection() {
        return this.networkConnection;
    }
    
    public synchronized void setNetworkConnection(final boolean networkConnection) {
        this.networkConnection = networkConnection;
    }
    
    public AtomicBoolean getStopping() {
        return this.stopping;
    }
    
    public void setDontSendReponse(final boolean b) {
        this.dontSendReponse = b;
    }
    
    public boolean isDontSendReponse() {
        return this.dontSendReponse;
    }
    
    public boolean isClientMaster() {
        return this.clientMaster;
    }
    
    public void setClientMaster(final boolean clientMaster) {
        this.clientMaster = clientMaster;
    }
    
    public boolean isFaultTolerant() {
        return this.faultTolerant;
    }
    
    public void setFaultTolerant(final boolean faultTolerant) {
        this.faultTolerant = faultTolerant;
    }
    
    public void setConnectionState(final ConnectionState connectionState) {
        this.connectionState = connectionState;
    }
    
    public ConnectionState getConnectionState() {
        return this.connectionState;
    }
    
    public void setXid(final XATransactionId id) {
        this.xid = id;
    }
    
    public XATransactionId getXid() {
        return this.xid;
    }
    
    public boolean isAllowLinkStealing() {
        return this.connector != null && this.connector.isAllowLinkStealing();
    }
}
