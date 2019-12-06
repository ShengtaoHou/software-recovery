// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network;

import org.slf4j.LoggerFactory;
import org.apache.activemq.command.Message;
import javax.management.MalformedObjectNameException;
import org.apache.activemq.broker.jmx.BrokerMBeanSupport;
import org.apache.activemq.broker.jmx.AnnotatedMBean;
import org.apache.activemq.broker.jmx.NetworkBridgeView;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import javax.management.ObjectName;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;

public class MBeanNetworkListener implements NetworkBridgeListener
{
    private static final Logger LOG;
    private final BrokerService brokerService;
    private final ObjectName connectorName;
    private final NetworkBridgeConfiguration networkBridgeConfiguration;
    private boolean createdByDuplex;
    private Map<NetworkBridge, MBeanBridgeDestination> destinationObjectNameMap;
    
    public MBeanNetworkListener(final BrokerService brokerService, final NetworkBridgeConfiguration networkBridgeConfiguration, final ObjectName connectorName) {
        this.createdByDuplex = false;
        this.destinationObjectNameMap = new ConcurrentHashMap<NetworkBridge, MBeanBridgeDestination>();
        this.brokerService = brokerService;
        this.networkBridgeConfiguration = networkBridgeConfiguration;
        this.connectorName = connectorName;
    }
    
    @Override
    public void bridgeFailed() {
    }
    
    @Override
    public void onStart(final NetworkBridge bridge) {
        if (!this.brokerService.isUseJmx()) {
            return;
        }
        final NetworkBridgeView view = new NetworkBridgeView(bridge);
        view.setCreateByDuplex(this.createdByDuplex);
        try {
            final ObjectName objectName = this.createNetworkBridgeObjectName(bridge);
            AnnotatedMBean.registerMBean(this.brokerService.getManagementContext(), view, objectName);
            bridge.setMbeanObjectName(objectName);
            final MBeanBridgeDestination mBeanBridgeDestination = new MBeanBridgeDestination(this.brokerService, this.networkBridgeConfiguration, bridge, view);
            this.destinationObjectNameMap.put(bridge, mBeanBridgeDestination);
            mBeanBridgeDestination.start();
            MBeanNetworkListener.LOG.debug("registered: {} as: {}", bridge, objectName);
        }
        catch (Throwable e) {
            MBeanNetworkListener.LOG.debug("Network bridge could not be registered in JMX: {}", e.getMessage(), e);
        }
    }
    
    @Override
    public void onStop(final NetworkBridge bridge) {
        if (!this.brokerService.isUseJmx()) {
            return;
        }
        try {
            final ObjectName objectName = bridge.getMbeanObjectName();
            if (objectName != null) {
                this.brokerService.getManagementContext().unregisterMBean(objectName);
            }
            final MBeanBridgeDestination mBeanBridgeDestination = this.destinationObjectNameMap.remove(bridge);
            if (mBeanBridgeDestination != null) {
                mBeanBridgeDestination.stop();
            }
        }
        catch (Throwable e) {
            MBeanNetworkListener.LOG.debug("Network bridge could not be unregistered in JMX: {}", e.getMessage(), e);
        }
    }
    
    protected ObjectName createNetworkBridgeObjectName(final NetworkBridge bridge) throws MalformedObjectNameException {
        return BrokerMBeanSupport.createNetworkBridgeObjectName(this.connectorName, bridge.getRemoteAddress());
    }
    
    public void setCreatedByDuplex(final boolean createdByDuplex) {
        this.createdByDuplex = createdByDuplex;
    }
    
    @Override
    public void onOutboundMessage(final NetworkBridge bridge, final Message message) {
        final MBeanBridgeDestination mBeanBridgeDestination = this.destinationObjectNameMap.get(bridge);
        if (mBeanBridgeDestination != null) {
            mBeanBridgeDestination.onOutboundMessage(message);
        }
    }
    
    @Override
    public void onInboundMessage(final NetworkBridge bridge, final Message message) {
        final MBeanBridgeDestination mBeanBridgeDestination = this.destinationObjectNameMap.get(bridge);
        if (mBeanBridgeDestination != null) {
            mBeanBridgeDestination.onInboundMessage(message);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(MBeanNetworkListener.class);
    }
}
