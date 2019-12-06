// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import org.slf4j.LoggerFactory;
import java.util.Arrays;
import javax.jms.JMSException;
import java.io.IOException;
import org.apache.activemq.util.JMSExceptionSupport;
import org.apache.activemq.filter.MessageEvaluationContext;
import org.slf4j.Logger;
import org.apache.activemq.filter.BooleanExpression;

public class NetworkBridgeFilter implements DataStructure, BooleanExpression
{
    public static final byte DATA_STRUCTURE_TYPE = 91;
    static final Logger LOG;
    protected BrokerId networkBrokerId;
    protected int messageTTL;
    protected int consumerTTL;
    transient ConsumerInfo consumerInfo;
    
    public NetworkBridgeFilter() {
    }
    
    public NetworkBridgeFilter(final ConsumerInfo consumerInfo, final BrokerId networkBrokerId, final int messageTTL, final int consumerTTL) {
        this.networkBrokerId = networkBrokerId;
        this.messageTTL = messageTTL;
        this.consumerTTL = consumerTTL;
        this.consumerInfo = consumerInfo;
    }
    
    @Override
    public byte getDataStructureType() {
        return 91;
    }
    
    @Override
    public boolean isMarshallAware() {
        return false;
    }
    
    @Override
    public boolean matches(final MessageEvaluationContext mec) throws JMSException {
        try {
            final Message message = mec.getMessage();
            return message != null && this.matchesForwardingFilter(message, mec);
        }
        catch (IOException e) {
            throw JMSExceptionSupport.create(e);
        }
    }
    
    @Override
    public Object evaluate(final MessageEvaluationContext message) throws JMSException {
        return this.matches(message) ? Boolean.TRUE : Boolean.FALSE;
    }
    
    protected boolean matchesForwardingFilter(final Message message, final MessageEvaluationContext mec) {
        if (contains(message.getBrokerPath(), this.networkBrokerId)) {
            if (NetworkBridgeFilter.LOG.isTraceEnabled()) {
                NetworkBridgeFilter.LOG.trace("Message all ready routed once through target broker (" + this.networkBrokerId + "), path: " + Arrays.toString(message.getBrokerPath()) + " - ignoring: " + message);
            }
            return false;
        }
        int hops = (message.getBrokerPath() == null) ? 0 : message.getBrokerPath().length;
        if (this.messageTTL > -1 && hops >= this.messageTTL) {
            if (NetworkBridgeFilter.LOG.isTraceEnabled()) {
                NetworkBridgeFilter.LOG.trace("Message restricted to " + this.messageTTL + " network hops ignoring: " + message);
            }
            return false;
        }
        if (message.isAdvisory()) {
            if (this.consumerInfo != null && this.consumerInfo.isNetworkSubscription()) {
                if (NetworkBridgeFilter.LOG.isTraceEnabled()) {
                    NetworkBridgeFilter.LOG.trace("not propagating advisory to network sub: " + this.consumerInfo.getConsumerId() + ", message: " + message);
                }
                return false;
            }
            if (message.getDataStructure() != null && message.getDataStructure().getDataStructureType() == 5) {
                final ConsumerInfo info = (ConsumerInfo)message.getDataStructure();
                hops = ((info.getBrokerPath() == null) ? 0 : info.getBrokerPath().length);
                if (this.consumerTTL > -1 && hops >= this.consumerTTL) {
                    if (NetworkBridgeFilter.LOG.isTraceEnabled()) {
                        NetworkBridgeFilter.LOG.trace("ConsumerInfo advisory restricted to " + this.consumerTTL + " network hops ignoring: " + message);
                    }
                    return false;
                }
                if (contains(info.getBrokerPath(), this.networkBrokerId)) {
                    NetworkBridgeFilter.LOG.trace("ConsumerInfo advisory all ready routed once through target broker (" + this.networkBrokerId + "), path: " + Arrays.toString(info.getBrokerPath()) + " - ignoring: " + message);
                    return false;
                }
            }
        }
        return true;
    }
    
    public static boolean contains(final BrokerId[] brokerPath, final BrokerId brokerId) {
        if (brokerPath != null && brokerId != null) {
            for (int i = 0; i < brokerPath.length; ++i) {
                if (brokerId.equals(brokerPath[i])) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public int getNetworkTTL() {
        return this.messageTTL;
    }
    
    public void setNetworkTTL(final int networkTTL) {
        this.messageTTL = networkTTL;
        this.consumerTTL = networkTTL;
    }
    
    public BrokerId getNetworkBrokerId() {
        return this.networkBrokerId;
    }
    
    public void setNetworkBrokerId(final BrokerId remoteBrokerPath) {
        this.networkBrokerId = remoteBrokerPath;
    }
    
    public void setMessageTTL(final int messageTTL) {
        this.messageTTL = messageTTL;
    }
    
    public int getMessageTTL() {
        return this.messageTTL;
    }
    
    public void setConsumerTTL(final int consumerTTL) {
        this.consumerTTL = consumerTTL;
    }
    
    public int getConsumerTTL() {
        return this.consumerTTL;
    }
    
    static {
        LOG = LoggerFactory.getLogger(NetworkBridgeFilter.class);
    }
}
