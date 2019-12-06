// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import java.io.IOException;
import org.apache.activemq.broker.region.MessageReference;
import java.util.LinkedList;

public interface MessageEvictionStrategy
{
    MessageReference[] evictMessages(final LinkedList p0) throws IOException;
    
    int getEvictExpiredMessagesHighWatermark();
}
