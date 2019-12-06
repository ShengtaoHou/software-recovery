// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.slf4j.LoggerFactory;
import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.command.Message;
import org.apache.activemq.ActiveMQMessageAudit;
import org.slf4j.Logger;

public abstract class AbstractDeadLetterStrategy implements DeadLetterStrategy
{
    private static final Logger LOG;
    private boolean processNonPersistent;
    private boolean processExpired;
    private boolean enableAudit;
    private final ActiveMQMessageAudit messageAudit;
    
    public AbstractDeadLetterStrategy() {
        this.processNonPersistent = false;
        this.processExpired = true;
        this.enableAudit = true;
        this.messageAudit = new ActiveMQMessageAudit();
    }
    
    @Override
    public void rollback(final Message message) {
        if (message != null && this.enableAudit) {
            this.messageAudit.rollback(message);
        }
    }
    
    @Override
    public boolean isSendToDeadLetterQueue(final Message message) {
        boolean result = false;
        if (message != null) {
            result = true;
            if (this.enableAudit && this.messageAudit.isDuplicate(message)) {
                result = false;
                AbstractDeadLetterStrategy.LOG.debug("Not adding duplicate to DLQ: {}, dest: {}", message.getMessageId(), message.getDestination());
            }
            if (!message.isPersistent() && !this.processNonPersistent) {
                result = false;
            }
            if (message.isExpired() && !this.processExpired) {
                result = false;
            }
        }
        return result;
    }
    
    @Override
    public boolean isProcessExpired() {
        return this.processExpired;
    }
    
    @Override
    public void setProcessExpired(final boolean processExpired) {
        this.processExpired = processExpired;
    }
    
    @Override
    public boolean isProcessNonPersistent() {
        return this.processNonPersistent;
    }
    
    @Override
    public void setProcessNonPersistent(final boolean processNonPersistent) {
        this.processNonPersistent = processNonPersistent;
    }
    
    public boolean isEnableAudit() {
        return this.enableAudit;
    }
    
    public void setEnableAudit(final boolean enableAudit) {
        this.enableAudit = enableAudit;
    }
    
    static {
        LOG = LoggerFactory.getLogger(AbstractDeadLetterStrategy.class);
    }
}
