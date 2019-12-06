// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import java.io.IOException;
import java.util.Iterator;
import java.util.HashMap;
import org.apache.activemq.broker.region.MessageReference;
import java.util.LinkedList;

public class UniquePropertyMessageEvictionStrategy extends MessageEvictionStrategySupport
{
    protected String propertyName;
    
    public String getPropertyName() {
        return this.propertyName;
    }
    
    public void setPropertyName(final String propertyName) {
        this.propertyName = propertyName;
    }
    
    @Override
    public MessageReference[] evictMessages(final LinkedList messages) throws IOException {
        final MessageReference oldest = messages.getFirst();
        final HashMap<Object, MessageReference> pivots = new HashMap<Object, MessageReference>();
        final Iterator iter = messages.iterator();
        int i = 0;
        while (iter.hasNext()) {
            final MessageReference reference = iter.next();
            if (this.propertyName != null && reference.getMessage().getProperty(this.propertyName) != null) {
                final Object key = reference.getMessage().getProperty(this.propertyName);
                if (pivots.containsKey(key)) {
                    final MessageReference pivot = pivots.get(key);
                    if (reference.getMessage().getTimestamp() > pivot.getMessage().getTimestamp()) {
                        pivots.put(key, reference);
                    }
                }
                else {
                    pivots.put(key, reference);
                }
            }
            ++i;
        }
        if (!pivots.isEmpty()) {
            for (final MessageReference ref : pivots.values()) {
                messages.remove(ref);
            }
            if (messages.size() != 0) {
                return messages.toArray(new MessageReference[messages.size()]);
            }
        }
        return new MessageReference[] { oldest };
    }
}
