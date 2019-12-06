// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network;

import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.activemq.broker.jmx.AnnotatedMBean;
import org.apache.activemq.broker.jmx.BrokerMBeanSupport;
import org.apache.activemq.command.Message;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.activemq.broker.jmx.NetworkDestinationView;
import javax.management.ObjectName;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.Map;
import org.apache.activemq.thread.Scheduler;
import org.apache.activemq.broker.jmx.NetworkBridgeView;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;

public class MBeanBridgeDestination
{
    private static final Logger LOG;
    private final BrokerService brokerService;
    private final NetworkBridge bridge;
    private final NetworkBridgeView networkBridgeView;
    private final NetworkBridgeConfiguration networkBridgeConfiguration;
    private final Scheduler scheduler;
    private final Runnable purgeInactiveDestinationViewTask;
    private Map<ActiveMQDestination, ObjectName> destinationObjectNameMap;
    private Map<ActiveMQDestination, NetworkDestinationView> outboundDestinationViewMap;
    private Map<ActiveMQDestination, NetworkDestinationView> inboundDestinationViewMap;
    
    public MBeanBridgeDestination(final BrokerService brokerService, final NetworkBridgeConfiguration networkBridgeConfiguration, final NetworkBridge bridge, final NetworkBridgeView networkBridgeView) {
        this.destinationObjectNameMap = new ConcurrentHashMap<ActiveMQDestination, ObjectName>();
        this.outboundDestinationViewMap = new ConcurrentHashMap<ActiveMQDestination, NetworkDestinationView>();
        this.inboundDestinationViewMap = new ConcurrentHashMap<ActiveMQDestination, NetworkDestinationView>();
        this.brokerService = brokerService;
        this.networkBridgeConfiguration = networkBridgeConfiguration;
        this.bridge = bridge;
        this.networkBridgeView = networkBridgeView;
        this.scheduler = brokerService.getScheduler();
        this.purgeInactiveDestinationViewTask = new Runnable() {
            @Override
            public void run() {
                MBeanBridgeDestination.this.purgeInactiveDestinationViews();
            }
        };
    }
    
    public void onOutboundMessage(final Message message) {
        final ActiveMQDestination destination = message.getDestination();
        NetworkDestinationView networkDestinationView = this.outboundDestinationViewMap.get(destination);
        if (networkDestinationView == null) {
            synchronized (this.destinationObjectNameMap) {
                if ((networkDestinationView = this.outboundDestinationViewMap.get(destination)) == null) {
                    final ObjectName bridgeObjectName = this.bridge.getMbeanObjectName();
                    try {
                        final ObjectName objectName = BrokerMBeanSupport.createNetworkOutBoundDestinationObjectName(bridgeObjectName, destination);
                        networkDestinationView = new NetworkDestinationView(this.networkBridgeView, destination.getPhysicalName());
                        AnnotatedMBean.registerMBean(this.brokerService.getManagementContext(), networkDestinationView, objectName);
                        this.destinationObjectNameMap.put(destination, objectName);
                        this.outboundDestinationViewMap.put(destination, networkDestinationView);
                    }
                    catch (Exception e) {
                        MBeanBridgeDestination.LOG.warn("Failed to register " + destination, e);
                    }
                }
            }
        }
        networkDestinationView.messageSent();
    }
    
    public void onInboundMessage(final Message message) {
        final ActiveMQDestination destination = message.getDestination();
        NetworkDestinationView networkDestinationView = this.inboundDestinationViewMap.get(destination);
        if (networkDestinationView == null) {
            synchronized (this.destinationObjectNameMap) {
                if ((networkDestinationView = this.inboundDestinationViewMap.get(destination)) == null) {
                    final ObjectName bridgeObjectName = this.bridge.getMbeanObjectName();
                    try {
                        final ObjectName objectName = BrokerMBeanSupport.createNetworkInBoundDestinationObjectName(bridgeObjectName, destination);
                        networkDestinationView = new NetworkDestinationView(this.networkBridgeView, destination.getPhysicalName());
                        this.networkBridgeView.addNetworkDestinationView(networkDestinationView);
                        AnnotatedMBean.registerMBean(this.brokerService.getManagementContext(), networkDestinationView, objectName);
                        this.destinationObjectNameMap.put(destination, objectName);
                        this.inboundDestinationViewMap.put(destination, networkDestinationView);
                    }
                    catch (Exception e) {
                        MBeanBridgeDestination.LOG.warn("Failed to register " + destination, e);
                    }
                }
            }
        }
        networkDestinationView.messageSent();
    }
    
    public void start() {
        if (this.networkBridgeConfiguration.isGcDestinationViews()) {
            final long period = this.networkBridgeConfiguration.getGcSweepTime();
            if (period > 0L) {
                this.scheduler.executePeriodically(this.purgeInactiveDestinationViewTask, period);
            }
        }
    }
    
    public void stop() {
        if (!this.brokerService.isUseJmx()) {
            return;
        }
        this.scheduler.cancel(this.purgeInactiveDestinationViewTask);
        for (final ObjectName objectName : this.destinationObjectNameMap.values()) {
            try {
                if (objectName == null) {
                    continue;
                }
                this.brokerService.getManagementContext().unregisterMBean(objectName);
            }
            catch (Throwable e) {
                MBeanBridgeDestination.LOG.debug("Network bridge could not be unregistered in JMX: {}", e.getMessage(), e);
            }
        }
        this.destinationObjectNameMap.clear();
        this.outboundDestinationViewMap.clear();
        this.inboundDestinationViewMap.clear();
    }
    
    private void purgeInactiveDestinationViews() {
        if (!this.brokerService.isUseJmx()) {
            return;
        }
        this.purgeInactiveDestinationView(this.inboundDestinationViewMap);
        this.purgeInactiveDestinationView(this.outboundDestinationViewMap);
    }
    
    private void purgeInactiveDestinationView(final Map<ActiveMQDestination, NetworkDestinationView> map) {
        final long time = System.currentTimeMillis() - this.networkBridgeConfiguration.getGcSweepTime();
        Map<ActiveMQDestination, NetworkDestinationView> gc = null;
        for (final Map.Entry<ActiveMQDestination, NetworkDestinationView> entry : map.entrySet()) {
            if (entry.getValue().getLastAccessTime() <= time) {
                if (gc == null) {
                    gc = new HashMap<ActiveMQDestination, NetworkDestinationView>();
                }
                gc.put(entry.getKey(), entry.getValue());
            }
        }
        if (gc != null) {
            for (final Map.Entry<ActiveMQDestination, NetworkDestinationView> entry : gc.entrySet()) {
                map.remove(entry.getKey());
                final ObjectName objectName = this.destinationObjectNameMap.get(entry.getKey());
                if (objectName != null) {
                    try {
                        if (objectName != null) {
                            this.brokerService.getManagementContext().unregisterMBean(objectName);
                        }
                    }
                    catch (Throwable e) {
                        MBeanBridgeDestination.LOG.debug("Network bridge could not be unregistered in JMX: {}", e.getMessage(), e);
                    }
                }
                entry.getValue().close();
            }
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(MBeanBridgeDestination.class);
    }
}
