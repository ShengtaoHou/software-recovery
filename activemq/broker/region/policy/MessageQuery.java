// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.apache.activemq.command.Message;
import javax.jms.MessageListener;
import org.apache.activemq.command.ActiveMQDestination;

public interface MessageQuery
{
    void execute(final ActiveMQDestination p0, final MessageListener p1) throws Exception;
    
    boolean validateUpdate(final Message p0);
}
