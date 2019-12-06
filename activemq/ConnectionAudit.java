// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.command.Message;
import org.apache.activemq.util.LRUCache;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.LinkedHashMap;

class ConnectionAudit
{
    private boolean checkForDuplicates;
    private LinkedHashMap<ActiveMQDestination, ActiveMQMessageAudit> destinations;
    private LinkedHashMap<ActiveMQDispatcher, ActiveMQMessageAudit> dispatchers;
    private int auditDepth;
    private int auditMaximumProducerNumber;
    
    ConnectionAudit() {
        this.destinations = new LRUCache<ActiveMQDestination, ActiveMQMessageAudit>(1000);
        this.dispatchers = new LRUCache<ActiveMQDispatcher, ActiveMQMessageAudit>(1000);
        this.auditDepth = 2048;
        this.auditMaximumProducerNumber = 64;
    }
    
    synchronized void removeDispatcher(final ActiveMQDispatcher dispatcher) {
        this.dispatchers.remove(dispatcher);
    }
    
    synchronized boolean isDuplicate(final ActiveMQDispatcher dispatcher, final Message message) {
        if (this.checkForDuplicates && message != null) {
            final ActiveMQDestination destination = message.getDestination();
            if (destination != null) {
                if (destination.isQueue()) {
                    ActiveMQMessageAudit audit = this.destinations.get(destination);
                    if (audit == null) {
                        audit = new ActiveMQMessageAudit(this.auditDepth, this.auditMaximumProducerNumber);
                        this.destinations.put(destination, audit);
                    }
                    final boolean result = audit.isDuplicate(message);
                    return result;
                }
                ActiveMQMessageAudit audit = this.dispatchers.get(dispatcher);
                if (audit == null) {
                    audit = new ActiveMQMessageAudit(this.auditDepth, this.auditMaximumProducerNumber);
                    this.dispatchers.put(dispatcher, audit);
                }
                final boolean result = audit.isDuplicate(message);
                return result;
            }
        }
        return false;
    }
    
    protected synchronized void rollbackDuplicate(final ActiveMQDispatcher dispatcher, final Message message) {
        if (this.checkForDuplicates && message != null) {
            final ActiveMQDestination destination = message.getDestination();
            if (destination != null) {
                if (destination.isQueue()) {
                    final ActiveMQMessageAudit audit = this.destinations.get(destination);
                    if (audit != null) {
                        audit.rollback(message);
                    }
                }
                else {
                    final ActiveMQMessageAudit audit = this.dispatchers.get(dispatcher);
                    if (audit != null) {
                        audit.rollback(message);
                    }
                }
            }
        }
    }
    
    boolean isCheckForDuplicates() {
        return this.checkForDuplicates;
    }
    
    void setCheckForDuplicates(final boolean checkForDuplicates) {
        this.checkForDuplicates = checkForDuplicates;
    }
    
    public int getAuditDepth() {
        return this.auditDepth;
    }
    
    public void setAuditDepth(final int auditDepth) {
        this.auditDepth = auditDepth;
    }
    
    public int getAuditMaximumProducerNumber() {
        return this.auditMaximumProducerNumber;
    }
    
    public void setAuditMaximumProducerNumber(final int auditMaximumProducerNumber) {
        this.auditMaximumProducerNumber = auditMaximumProducerNumber;
    }
}
