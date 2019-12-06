// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.data;

import org.apache.activemq.protobuf.Buffer;
import org.apache.activemq.protobuf.BaseMessage;

abstract class KahaAddMessageCommandBase<T> extends BaseMessage<T>
{
    private KahaTransactionInfo f_transactionInfo;
    private KahaDestination f_destination;
    private String f_messageId;
    private boolean b_messageId;
    private Buffer f_message;
    private boolean b_message;
    private int f_priority;
    private boolean b_priority;
    private boolean f_prioritySupported;
    private boolean b_prioritySupported;
    
    KahaAddMessageCommandBase() {
        this.f_transactionInfo = null;
        this.f_destination = null;
        this.f_messageId = null;
        this.f_message = null;
        this.f_priority = 4;
        this.f_prioritySupported = false;
    }
    
    public boolean hasTransactionInfo() {
        return this.f_transactionInfo != null;
    }
    
    public KahaTransactionInfo getTransactionInfo() {
        if (this.f_transactionInfo == null) {
            this.f_transactionInfo = new KahaTransactionInfo();
        }
        return this.f_transactionInfo;
    }
    
    public T setTransactionInfo(final KahaTransactionInfo transactionInfo) {
        this.loadAndClear();
        this.f_transactionInfo = transactionInfo;
        return (T)this;
    }
    
    public void clearTransactionInfo() {
        this.loadAndClear();
        this.f_transactionInfo = null;
    }
    
    public boolean hasDestination() {
        return this.f_destination != null;
    }
    
    public KahaDestination getDestination() {
        if (this.f_destination == null) {
            this.f_destination = new KahaDestination();
        }
        return this.f_destination;
    }
    
    public T setDestination(final KahaDestination destination) {
        this.loadAndClear();
        this.f_destination = destination;
        return (T)this;
    }
    
    public void clearDestination() {
        this.loadAndClear();
        this.f_destination = null;
    }
    
    public boolean hasMessageId() {
        return this.b_messageId;
    }
    
    public String getMessageId() {
        return this.f_messageId;
    }
    
    public T setMessageId(final String messageId) {
        this.loadAndClear();
        this.b_messageId = true;
        this.f_messageId = messageId;
        return (T)this;
    }
    
    public void clearMessageId() {
        this.loadAndClear();
        this.b_messageId = false;
        this.f_messageId = null;
    }
    
    public boolean hasMessage() {
        return this.b_message;
    }
    
    public Buffer getMessage() {
        return this.f_message;
    }
    
    public T setMessage(final Buffer message) {
        this.loadAndClear();
        this.b_message = true;
        this.f_message = message;
        return (T)this;
    }
    
    public void clearMessage() {
        this.loadAndClear();
        this.b_message = false;
        this.f_message = null;
    }
    
    public boolean hasPriority() {
        return this.b_priority;
    }
    
    public int getPriority() {
        return this.f_priority;
    }
    
    public T setPriority(final int priority) {
        this.loadAndClear();
        this.b_priority = true;
        this.f_priority = priority;
        return (T)this;
    }
    
    public void clearPriority() {
        this.loadAndClear();
        this.b_priority = false;
        this.f_priority = 4;
    }
    
    public boolean hasPrioritySupported() {
        return this.b_prioritySupported;
    }
    
    public boolean getPrioritySupported() {
        return this.f_prioritySupported;
    }
    
    public T setPrioritySupported(final boolean prioritySupported) {
        this.loadAndClear();
        this.b_prioritySupported = true;
        this.f_prioritySupported = prioritySupported;
        return (T)this;
    }
    
    public void clearPrioritySupported() {
        this.loadAndClear();
        this.b_prioritySupported = false;
        this.f_prioritySupported = false;
    }
}
