// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.security;

import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.DestinationInfo;
import java.util.Set;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.ConnectionContext;
import java.util.Arrays;
import org.apache.activemq.broker.region.DestinationInterceptor;
import org.apache.activemq.broker.region.CompositeDestinationInterceptor;
import org.apache.activemq.broker.region.RegionBroker;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerFilter;

public class AuthorizationBroker extends BrokerFilter implements SecurityAdminMBean
{
    private volatile AuthorizationMap authorizationMap;
    
    public AuthorizationBroker(final Broker next, final AuthorizationMap authorizationMap) {
        super(next);
        this.authorizationMap = authorizationMap;
        final RegionBroker regionBroker = (RegionBroker)next.getAdaptor(RegionBroker.class);
        final CompositeDestinationInterceptor compositeInterceptor = (CompositeDestinationInterceptor)regionBroker.getDestinationInterceptor();
        DestinationInterceptor[] interceptors = compositeInterceptor.getInterceptors();
        interceptors = Arrays.copyOf(interceptors, interceptors.length + 1);
        interceptors[interceptors.length - 1] = new AuthorizationDestinationInterceptor(this);
        compositeInterceptor.setInterceptors(interceptors);
    }
    
    public AuthorizationMap getAuthorizationMap() {
        return this.authorizationMap;
    }
    
    public void setAuthorizationMap(final AuthorizationMap map) {
        this.authorizationMap = map;
    }
    
    protected SecurityContext checkSecurityContext(final ConnectionContext context) throws SecurityException {
        final SecurityContext securityContext = context.getSecurityContext();
        if (securityContext == null) {
            throw new SecurityException("User is not authenticated.");
        }
        return securityContext;
    }
    
    protected boolean checkDestinationAdmin(final SecurityContext securityContext, final ActiveMQDestination destination) {
        final Destination existing = this.getDestinationMap().get(destination);
        if (existing != null) {
            return true;
        }
        if (!securityContext.isBrokerContext()) {
            Set<?> allowedACLs = null;
            if (!destination.isTemporary()) {
                allowedACLs = this.authorizationMap.getAdminACLs(destination);
            }
            else {
                allowedACLs = this.authorizationMap.getTempDestinationAdminACLs();
            }
            if (allowedACLs != null && !securityContext.isInOneOf(allowedACLs)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void addDestinationInfo(final ConnectionContext context, final DestinationInfo info) throws Exception {
        final SecurityContext securityContext = this.checkSecurityContext(context);
        if (!this.checkDestinationAdmin(securityContext, info.getDestination())) {
            throw new SecurityException("User " + securityContext.getUserName() + " is not authorized to create: " + info.getDestination());
        }
        super.addDestinationInfo(context, info);
    }
    
    @Override
    public Destination addDestination(final ConnectionContext context, final ActiveMQDestination destination, final boolean create) throws Exception {
        final SecurityContext securityContext = this.checkSecurityContext(context);
        if (!this.checkDestinationAdmin(securityContext, destination)) {
            throw new SecurityException("User " + securityContext.getUserName() + " is not authorized to create: " + destination);
        }
        return super.addDestination(context, destination, create);
    }
    
    @Override
    public void removeDestination(final ConnectionContext context, final ActiveMQDestination destination, final long timeout) throws Exception {
        final SecurityContext securityContext = this.checkSecurityContext(context);
        if (!this.checkDestinationAdmin(securityContext, destination)) {
            throw new SecurityException("User " + securityContext.getUserName() + " is not authorized to remove: " + destination);
        }
        super.removeDestination(context, destination, timeout);
    }
    
    @Override
    public void removeDestinationInfo(final ConnectionContext context, final DestinationInfo info) throws Exception {
        final SecurityContext securityContext = this.checkSecurityContext(context);
        if (!this.checkDestinationAdmin(securityContext, info.getDestination())) {
            throw new SecurityException("User " + securityContext.getUserName() + " is not authorized to remove: " + info.getDestination());
        }
        super.removeDestinationInfo(context, info);
    }
    
    @Override
    public Subscription addConsumer(final ConnectionContext context, final ConsumerInfo info) throws Exception {
        final SecurityContext securityContext = this.checkSecurityContext(context);
        Set<?> allowedACLs = null;
        if (!info.getDestination().isTemporary()) {
            allowedACLs = this.authorizationMap.getReadACLs(info.getDestination());
        }
        else {
            allowedACLs = this.authorizationMap.getTempDestinationReadACLs();
        }
        if (!securityContext.isBrokerContext() && allowedACLs != null && !securityContext.isInOneOf(allowedACLs)) {
            throw new SecurityException("User " + securityContext.getUserName() + " is not authorized to read from: " + info.getDestination());
        }
        securityContext.getAuthorizedReadDests().put(info.getDestination(), info.getDestination());
        return super.addConsumer(context, info);
    }
    
    @Override
    public void addProducer(final ConnectionContext context, final ProducerInfo info) throws Exception {
        final SecurityContext securityContext = this.checkSecurityContext(context);
        if (!securityContext.isBrokerContext() && info.getDestination() != null) {
            Set<?> allowedACLs = null;
            if (!info.getDestination().isTemporary()) {
                allowedACLs = this.authorizationMap.getWriteACLs(info.getDestination());
            }
            else {
                allowedACLs = this.authorizationMap.getTempDestinationWriteACLs();
            }
            if (allowedACLs != null && !securityContext.isInOneOf(allowedACLs)) {
                throw new SecurityException("User " + securityContext.getUserName() + " is not authorized to write to: " + info.getDestination());
            }
            securityContext.getAuthorizedWriteDests().put(info.getDestination(), info.getDestination());
        }
        super.addProducer(context, info);
    }
    
    @Override
    public void send(final ProducerBrokerExchange producerExchange, final Message messageSend) throws Exception {
        final SecurityContext securityContext = this.checkSecurityContext(producerExchange.getConnectionContext());
        if (!securityContext.isBrokerContext() && !securityContext.getAuthorizedWriteDests().contains(messageSend.getDestination())) {
            Set<?> allowedACLs = null;
            if (!messageSend.getDestination().isTemporary()) {
                allowedACLs = this.authorizationMap.getWriteACLs(messageSend.getDestination());
            }
            else {
                allowedACLs = this.authorizationMap.getTempDestinationWriteACLs();
            }
            if (allowedACLs != null && !securityContext.isInOneOf(allowedACLs)) {
                throw new SecurityException("User " + securityContext.getUserName() + " is not authorized to write to: " + messageSend.getDestination());
            }
            securityContext.getAuthorizedWriteDests().put(messageSend.getDestination(), messageSend.getDestination());
        }
        super.send(producerExchange, messageSend);
    }
    
    @Override
    public void addQueueRole(final String queue, final String operation, final String role) {
        this.addDestinationRole(new ActiveMQQueue(queue), operation, role);
    }
    
    @Override
    public void addTopicRole(final String topic, final String operation, final String role) {
        this.addDestinationRole(new ActiveMQTopic(topic), operation, role);
    }
    
    @Override
    public void removeQueueRole(final String queue, final String operation, final String role) {
        this.removeDestinationRole(new ActiveMQQueue(queue), operation, role);
    }
    
    @Override
    public void removeTopicRole(final String topic, final String operation, final String role) {
        this.removeDestinationRole(new ActiveMQTopic(topic), operation, role);
    }
    
    public void addDestinationRole(final javax.jms.Destination destination, final String operation, final String role) {
    }
    
    public void removeDestinationRole(final javax.jms.Destination destination, final String operation, final String role) {
    }
    
    @Override
    public void addRole(final String role) {
    }
    
    @Override
    public void addUserRole(final String user, final String role) {
    }
    
    @Override
    public void removeRole(final String role) {
    }
    
    @Override
    public void removeUserRole(final String user, final String role) {
    }
}
