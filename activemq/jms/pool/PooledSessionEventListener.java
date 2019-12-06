// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.jms.pool;

import javax.jms.TemporaryTopic;
import javax.jms.TemporaryQueue;

interface PooledSessionEventListener
{
    void onTemporaryQueueCreate(final TemporaryQueue p0);
    
    void onTemporaryTopicCreate(final TemporaryTopic p0);
    
    void onSessionClosed(final PooledSession p0);
}
