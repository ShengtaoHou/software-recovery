// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region;

import org.apache.activemq.command.Message;
import org.apache.activemq.usage.MemoryUsage;

public class PendingMarshalUsageTracker implements Runnable
{
    final MemoryUsage usage;
    int messageSize;
    
    public PendingMarshalUsageTracker(final Message message) {
        this.usage = message.getMemoryUsage();
        if (this.usage != null) {
            this.messageSize = message.getSize();
            this.usage.increaseUsage(this.messageSize);
        }
    }
    
    @Override
    public void run() {
        if (this.usage != null) {
            this.usage.decreaseUsage(this.messageSize);
        }
    }
}
