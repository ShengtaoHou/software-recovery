// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import org.slf4j.LoggerFactory;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.command.Response;
import org.apache.activemq.command.ConnectionInfo;
import java.io.IOException;
import org.apache.activemq.broker.Connection;
import org.apache.activemq.thread.TaskRunnerFactory;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.broker.TransportConnector;
import javax.management.ObjectName;
import org.slf4j.Logger;
import org.apache.activemq.broker.TransportConnection;

public class ManagedTransportConnection extends TransportConnection
{
    private static final Logger LOG;
    private final ManagementContext managementContext;
    private final ObjectName connectorName;
    private final ConnectionViewMBean mbean;
    private ObjectName byClientIdName;
    private ObjectName byAddressName;
    private final boolean populateUserName;
    
    public ManagedTransportConnection(final TransportConnector connector, final Transport transport, final Broker broker, final TaskRunnerFactory factory, final TaskRunnerFactory stopFactory, final ManagementContext context, final ObjectName connectorName) throws IOException {
        super(connector, transport, broker, factory, stopFactory);
        this.managementContext = context;
        this.connectorName = connectorName;
        this.mbean = new ConnectionView(this, this.managementContext);
        this.populateUserName = broker.getBrokerService().isPopulateUserNameInMBeans();
        if (this.managementContext.isAllowRemoteAddressInMBeanNames()) {
            this.registerMBean(this.byAddressName = this.createObjectName("remoteAddress", transport.getRemoteAddress()));
        }
    }
    
    @Override
    public void stopAsync() {
        if (!this.isStopping()) {
            synchronized (this) {
                this.unregisterMBean(this.byClientIdName);
                this.unregisterMBean(this.byAddressName);
                this.byClientIdName = null;
                this.byAddressName = null;
            }
        }
        super.stopAsync();
    }
    
    @Override
    public Response processAddConnection(final ConnectionInfo info) throws Exception {
        final Response answer = super.processAddConnection(info);
        final String clientId = info.getClientId();
        if (this.populateUserName) {
            ((ConnectionView)this.mbean).setUserName(info.getUserName());
        }
        if (clientId != null && this.byClientIdName == null) {
            this.registerMBean(this.byClientIdName = this.createObjectName("clientId", clientId));
        }
        return answer;
    }
    
    protected void registerMBean(final ObjectName name) {
        if (name != null) {
            try {
                AnnotatedMBean.registerMBean(this.managementContext, this.mbean, name);
            }
            catch (Throwable e) {
                ManagedTransportConnection.LOG.warn("Failed to register MBean {}", name);
                ManagedTransportConnection.LOG.debug("Failure reason: ", e);
            }
        }
    }
    
    protected void unregisterMBean(final ObjectName name) {
        if (name != null) {
            try {
                this.managementContext.unregisterMBean(name);
            }
            catch (Throwable e) {
                ManagedTransportConnection.LOG.warn("Failed to unregister MBean {}", name);
                ManagedTransportConnection.LOG.debug("Failure reason: ", e);
            }
        }
    }
    
    protected ObjectName createObjectName(final String type, final String value) throws IOException {
        try {
            return BrokerMBeanSupport.createConnectionViewByType(this.connectorName, type, value);
        }
        catch (Throwable e) {
            throw IOExceptionSupport.create(e);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(ManagedTransportConnection.class);
    }
}
