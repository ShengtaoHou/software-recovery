// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import org.slf4j.LoggerFactory;
import org.apache.activemq.command.ActiveMQTempDestination;
import org.apache.activemq.command.DataStructure;
import org.apache.activemq.command.DestinationInfo;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.command.MessageDispatch;
import javax.jms.JMSException;
import org.apache.activemq.command.Command;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.advisory.AdvisorySupport;
import org.apache.activemq.command.ConsumerId;
import org.apache.activemq.command.ConsumerInfo;
import org.slf4j.Logger;

public class AdvisoryConsumer implements ActiveMQDispatcher
{
    private static final transient Logger LOG;
    int deliveredCounter;
    private final ActiveMQConnection connection;
    private ConsumerInfo info;
    private boolean closed;
    
    public AdvisoryConsumer(final ActiveMQConnection connection, final ConsumerId consumerId) throws JMSException {
        this.connection = connection;
        (this.info = new ConsumerInfo(consumerId)).setDestination(AdvisorySupport.TEMP_DESTINATION_COMPOSITE_ADVISORY_TOPIC);
        this.info.setPrefetchSize(1000);
        this.info.setNoLocal(true);
        this.info.setDispatchAsync(true);
        this.connection.addDispatcher(this.info.getConsumerId(), this);
        this.connection.syncSendPacket(this.info);
    }
    
    public synchronized void dispose() {
        if (!this.closed) {
            try {
                this.connection.asyncSendPacket(this.info.createRemoveCommand());
            }
            catch (JMSException e) {
                AdvisoryConsumer.LOG.debug("Failed to send remove command: " + e, e);
            }
            this.connection.removeDispatcher(this.info.getConsumerId());
            this.closed = true;
        }
    }
    
    @Override
    public void dispatch(final MessageDispatch md) {
        ++this.deliveredCounter;
        if (this.deliveredCounter > 0.75 * this.info.getPrefetchSize()) {
            try {
                final MessageAck ack = new MessageAck(md, (byte)2, this.deliveredCounter);
                this.connection.asyncSendPacket(ack);
                this.deliveredCounter = 0;
            }
            catch (JMSException e) {
                this.connection.onClientInternalException(e);
            }
        }
        final DataStructure o = md.getMessage().getDataStructure();
        if (o != null && o.getClass() == DestinationInfo.class) {
            this.processDestinationInfo((DestinationInfo)o);
        }
        else if (AdvisoryConsumer.LOG.isDebugEnabled()) {
            AdvisoryConsumer.LOG.debug("Unexpected message was dispatched to the AdvisoryConsumer: " + md);
        }
    }
    
    private void processDestinationInfo(final DestinationInfo dinfo) {
        final ActiveMQDestination dest = dinfo.getDestination();
        if (!dest.isTemporary()) {
            return;
        }
        ActiveMQTempDestination tempDest = (ActiveMQTempDestination)dest;
        if (dinfo.getOperationType() == 0) {
            if (tempDest.getConnection() != null) {
                tempDest = (ActiveMQTempDestination)tempDest.createDestination(tempDest.getPhysicalName());
            }
            this.connection.activeTempDestinations.put(tempDest, tempDest);
        }
        else if (dinfo.getOperationType() == 1) {
            this.connection.activeTempDestinations.remove(tempDest);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(AdvisoryConsumer.class);
    }
}
