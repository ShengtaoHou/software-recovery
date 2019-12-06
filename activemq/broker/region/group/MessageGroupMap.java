// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.group;

import java.util.Map;
import org.apache.activemq.command.ConsumerId;

public interface MessageGroupMap
{
    void put(final String p0, final ConsumerId p1);
    
    ConsumerId get(final String p0);
    
    ConsumerId removeGroup(final String p0);
    
    MessageGroupSet removeConsumer(final ConsumerId p0);
    
    void removeAll();
    
    Map<String, String> getGroups();
    
    String getType();
}
