// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import java.util.Map;
import javax.jms.JMSException;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.util.BrokerSupport;
import javax.management.openmbean.OpenDataException;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.region.QueueMessageReference;
import javax.management.openmbean.CompositeData;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.broker.region.Queue;

public class QueueView extends DestinationView implements QueueViewMBean
{
    public QueueView(final ManagedRegionBroker broker, final Queue destination) {
        super(broker, destination);
    }
    
    @Override
    public CompositeData getMessage(final String messageId) throws OpenDataException {
        CompositeData result = null;
        final QueueMessageReference ref = ((Queue)this.destination).getMessage(messageId);
        if (ref != null) {
            final Message rc = ref.getMessage();
            if (rc == null) {
                return null;
            }
            result = OpenTypeSupport.convert(rc);
        }
        return result;
    }
    
    @Override
    public void purge() throws Exception {
        ((Queue)this.destination).purge();
    }
    
    @Override
    public boolean removeMessage(final String messageId) throws Exception {
        return ((Queue)this.destination).removeMessage(messageId);
    }
    
    @Override
    public int removeMatchingMessages(final String selector) throws Exception {
        return ((Queue)this.destination).removeMatchingMessages(selector);
    }
    
    @Override
    public int removeMatchingMessages(final String selector, final int maximumMessages) throws Exception {
        return ((Queue)this.destination).removeMatchingMessages(selector, maximumMessages);
    }
    
    @Override
    public boolean copyMessageTo(final String messageId, final String destinationName) throws Exception {
        final ConnectionContext context = BrokerSupport.getConnectionContext(this.broker.getContextBroker());
        final ActiveMQDestination toDestination = ActiveMQDestination.createDestination(destinationName, (byte)1);
        return ((Queue)this.destination).copyMessageTo(context, messageId, toDestination);
    }
    
    @Override
    public int copyMatchingMessagesTo(final String selector, final String destinationName) throws Exception {
        final ConnectionContext context = BrokerSupport.getConnectionContext(this.broker.getContextBroker());
        final ActiveMQDestination toDestination = ActiveMQDestination.createDestination(destinationName, (byte)1);
        return ((Queue)this.destination).copyMatchingMessagesTo(context, selector, toDestination);
    }
    
    @Override
    public int copyMatchingMessagesTo(final String selector, final String destinationName, final int maximumMessages) throws Exception {
        final ConnectionContext context = BrokerSupport.getConnectionContext(this.broker.getContextBroker());
        final ActiveMQDestination toDestination = ActiveMQDestination.createDestination(destinationName, (byte)1);
        return ((Queue)this.destination).copyMatchingMessagesTo(context, selector, toDestination, maximumMessages);
    }
    
    @Override
    public boolean moveMessageTo(final String messageId, final String destinationName) throws Exception {
        final ConnectionContext context = BrokerSupport.getConnectionContext(this.broker.getContextBroker());
        final ActiveMQDestination toDestination = ActiveMQDestination.createDestination(destinationName, (byte)1);
        return ((Queue)this.destination).moveMessageTo(context, messageId, toDestination);
    }
    
    @Override
    public int moveMatchingMessagesTo(final String selector, final String destinationName) throws Exception {
        final ConnectionContext context = BrokerSupport.getConnectionContext(this.broker.getContextBroker());
        final ActiveMQDestination toDestination = ActiveMQDestination.createDestination(destinationName, (byte)1);
        return ((Queue)this.destination).moveMatchingMessagesTo(context, selector, toDestination);
    }
    
    @Override
    public int moveMatchingMessagesTo(final String selector, final String destinationName, final int maximumMessages) throws Exception {
        final ConnectionContext context = BrokerSupport.getConnectionContext(this.broker.getContextBroker());
        final ActiveMQDestination toDestination = ActiveMQDestination.createDestination(destinationName, (byte)1);
        return ((Queue)this.destination).moveMatchingMessagesTo(context, selector, toDestination, maximumMessages);
    }
    
    @Override
    public int retryMessages() throws Exception {
        final ConnectionContext context = BrokerSupport.getConnectionContext(this.broker.getContextBroker());
        return ((Queue)this.destination).retryMessages(context, Integer.MAX_VALUE);
    }
    
    @Override
    public boolean retryMessage(final String messageId) throws Exception {
        final Queue queue = (Queue)this.destination;
        final QueueMessageReference ref = queue.getMessage(messageId);
        final Message rc = ref.getMessage();
        if (rc == null) {
            throw new JMSException("Could not find message: " + messageId);
        }
        final ActiveMQDestination originalDestination = rc.getOriginalDestination();
        if (originalDestination != null) {
            final ConnectionContext context = BrokerSupport.getConnectionContext(this.broker.getContextBroker());
            return queue.moveMessageTo(context, ref, originalDestination);
        }
        throw new JMSException("No original destination for message: " + messageId);
    }
    
    @Override
    public int cursorSize() {
        final Queue queue = (Queue)this.destination;
        if (queue.getMessages() != null) {
            return queue.getMessages().size();
        }
        return 0;
    }
    
    @Override
    public boolean doesCursorHaveMessagesBuffered() {
        final Queue queue = (Queue)this.destination;
        return queue.getMessages() != null && queue.getMessages().hasMessagesBufferedToDeliver();
    }
    
    @Override
    public boolean doesCursorHaveSpace() {
        final Queue queue = (Queue)this.destination;
        return queue.getMessages() != null && queue.getMessages().hasSpace();
    }
    
    @Override
    public long getCursorMemoryUsage() {
        final Queue queue = (Queue)this.destination;
        if (queue.getMessages() != null && queue.getMessages().getSystemUsage() != null) {
            return queue.getMessages().getSystemUsage().getMemoryUsage().getUsage();
        }
        return 0L;
    }
    
    @Override
    public int getCursorPercentUsage() {
        final Queue queue = (Queue)this.destination;
        if (queue.getMessages() != null && queue.getMessages().getSystemUsage() != null) {
            return queue.getMessages().getSystemUsage().getMemoryUsage().getPercentUsage();
        }
        return 0;
    }
    
    @Override
    public boolean isCursorFull() {
        final Queue queue = (Queue)this.destination;
        return queue.getMessages() != null && queue.getMessages().isFull();
    }
    
    @Override
    public boolean isCacheEnabled() {
        final Queue queue = (Queue)this.destination;
        return queue.getMessages() != null && queue.getMessages().isCacheEnabled();
    }
    
    @Override
    public Map<String, String> getMessageGroups() {
        final Queue queue = (Queue)this.destination;
        return queue.getMessageGroupOwners().getGroups();
    }
    
    @Override
    public String getMessageGroupType() {
        final Queue queue = (Queue)this.destination;
        return queue.getMessageGroupOwners().getType();
    }
    
    @Override
    public void removeMessageGroup(@MBeanInfo("groupName") final String groupName) {
        final Queue queue = (Queue)this.destination;
        queue.getMessageGroupOwners().removeGroup(groupName);
    }
    
    @Override
    public void removeAllMessageGroups() {
        final Queue queue = (Queue)this.destination;
        queue.getMessageGroupOwners().removeAll();
    }
}
