// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import org.apache.activemq.command.MessageId;

public class ActiveMQMessageAudit extends ActiveMQMessageAuditNoSync
{
    private static final long serialVersionUID = 1L;
    
    public ActiveMQMessageAudit() {
    }
    
    public ActiveMQMessageAudit(final int auditDepth, final int maximumNumberOfProducersToTrack) {
        super(auditDepth, maximumNumberOfProducersToTrack);
    }
    
    @Override
    public boolean isDuplicate(final String id) {
        synchronized (this) {
            return super.isDuplicate(id);
        }
    }
    
    @Override
    public boolean isDuplicate(final MessageId id) {
        synchronized (this) {
            return super.isDuplicate(id);
        }
    }
    
    @Override
    public void rollback(final MessageId id) {
        synchronized (this) {
            super.rollback(id);
        }
    }
    
    @Override
    public boolean isInOrder(final String id) {
        synchronized (this) {
            return super.isInOrder(id);
        }
    }
    
    @Override
    public boolean isInOrder(final MessageId id) {
        synchronized (this) {
            return super.isInOrder(id);
        }
    }
    
    @Override
    public void setMaximumNumberOfProducersToTrack(final int maximumNumberOfProducersToTrack) {
        synchronized (this) {
            super.setMaximumNumberOfProducersToTrack(maximumNumberOfProducersToTrack);
        }
    }
}
