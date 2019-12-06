// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb;

public final class UowManagerConstants$
{
    public static final UowManagerConstants$ MODULE$;
    private final int QUEUE_COLLECTION_TYPE;
    private final int TOPIC_COLLECTION_TYPE;
    private final int TRANSACTION_COLLECTION_TYPE;
    private final int SUBSCRIPTION_COLLECTION_TYPE;
    
    static {
        new UowManagerConstants$();
    }
    
    public int QUEUE_COLLECTION_TYPE() {
        return this.QUEUE_COLLECTION_TYPE;
    }
    
    public int TOPIC_COLLECTION_TYPE() {
        return this.TOPIC_COLLECTION_TYPE;
    }
    
    public int TRANSACTION_COLLECTION_TYPE() {
        return this.TRANSACTION_COLLECTION_TYPE;
    }
    
    public int SUBSCRIPTION_COLLECTION_TYPE() {
        return this.SUBSCRIPTION_COLLECTION_TYPE;
    }
    
    public UowManagerConstants.QueueEntryKey key(final QueueEntryRecord x) {
        return new UowManagerConstants.QueueEntryKey(x.queueKey(), x.queueSeq());
    }
    
    private UowManagerConstants$() {
        MODULE$ = this;
        this.QUEUE_COLLECTION_TYPE = 1;
        this.TOPIC_COLLECTION_TYPE = 2;
        this.TRANSACTION_COLLECTION_TYPE = 3;
        this.SUBSCRIPTION_COLLECTION_TYPE = 4;
    }
}
