// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store;

import org.apache.activemq.command.MessageId;
import org.apache.activemq.command.Message;

public interface MessageRecoveryListener
{
    boolean recoverMessage(final Message p0) throws Exception;
    
    boolean recoverMessageReference(final MessageId p0) throws Exception;
    
    boolean hasSpace();
    
    boolean isDuplicate(final MessageId p0);
}
