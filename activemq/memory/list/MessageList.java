// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.memory.list;

import org.apache.activemq.command.Message;
import java.util.List;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.region.MessageReference;

public interface MessageList
{
    void add(final MessageReference p0);
    
    List getMessages(final ActiveMQDestination p0);
    
    Message[] browse(final ActiveMQDestination p0);
    
    void clear();
}
