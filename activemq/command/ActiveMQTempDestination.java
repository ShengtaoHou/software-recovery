// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import org.slf4j.LoggerFactory;
import javax.jms.JMSException;
import org.apache.activemq.ActiveMQConnection;
import org.slf4j.Logger;

public abstract class ActiveMQTempDestination extends ActiveMQDestination
{
    private static final Logger LOG;
    protected transient ActiveMQConnection connection;
    protected transient String connectionId;
    protected transient int sequenceId;
    
    public ActiveMQTempDestination() {
    }
    
    public ActiveMQTempDestination(final String name) {
        super(name);
    }
    
    public ActiveMQTempDestination(final String connectionId, final long sequenceId) {
        super(connectionId + ":" + sequenceId);
    }
    
    @Override
    public boolean isTemporary() {
        return true;
    }
    
    public void delete() throws JMSException {
        if (this.connection != null) {
            this.connection.deleteTempDestination(this);
        }
    }
    
    public ActiveMQConnection getConnection() {
        return this.connection;
    }
    
    public void setConnection(final ActiveMQConnection connection) {
        this.connection = connection;
    }
    
    @Override
    public void setPhysicalName(final String physicalName) {
        super.setPhysicalName(physicalName);
        if (!this.isComposite()) {
            final int p = this.physicalName.lastIndexOf(":");
            if (p >= 0) {
                final String seqStr = this.physicalName.substring(p + 1).trim();
                if (seqStr != null && seqStr.length() > 0) {
                    try {
                        this.sequenceId = Integer.parseInt(seqStr);
                    }
                    catch (NumberFormatException e) {
                        ActiveMQTempDestination.LOG.debug("Did not parse sequence Id from " + physicalName);
                    }
                    this.connectionId = this.physicalName.substring(0, p);
                }
            }
        }
    }
    
    public String getConnectionId() {
        return this.connectionId;
    }
    
    public void setConnectionId(final String connectionId) {
        this.connectionId = connectionId;
    }
    
    public int getSequenceId() {
        return this.sequenceId;
    }
    
    static {
        LOG = LoggerFactory.getLogger(ActiveMQTempDestination.class);
    }
}
