// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.inteceptor;

import org.apache.activemq.command.Message;
import org.apache.activemq.broker.ProducerBrokerExchange;

public interface MessageInterceptor
{
    void intercept(final ProducerBrokerExchange p0, final Message p1);
}
