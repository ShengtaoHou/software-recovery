// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import javax.jms.MessageConsumer;

public interface MessageAvailableListener
{
    void onMessageAvailable(final MessageConsumer p0);
}
