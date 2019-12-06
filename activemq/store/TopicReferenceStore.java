// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store;

import org.apache.activemq.command.SubscriptionInfo;
import java.io.IOException;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.broker.ConnectionContext;

public interface TopicReferenceStore extends ReferenceStore, TopicMessageStore
{
    boolean acknowledgeReference(final ConnectionContext p0, final String p1, final String p2, final MessageId p3) throws IOException;
    
    void deleteSubscription(final String p0, final String p1) throws IOException;
    
    void recoverSubscription(final String p0, final String p1, final MessageRecoveryListener p2) throws Exception;
    
    void recoverNextMessages(final String p0, final String p1, final int p2, final MessageRecoveryListener p3) throws Exception;
    
    void resetBatching(final String p0, final String p1);
    
    int getMessageCount(final String p0, final String p1) throws IOException;
    
    SubscriptionInfo lookupSubscription(final String p0, final String p1) throws IOException;
    
    SubscriptionInfo[] getAllSubscriptions() throws IOException;
    
    void addSubsciption(final SubscriptionInfo p0, final boolean p1) throws IOException;
}
