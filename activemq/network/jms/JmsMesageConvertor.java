// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network.jms;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

public interface JmsMesageConvertor
{
    Message convert(final Message p0) throws JMSException;
    
    Message convert(final Message p0, final Destination p1) throws JMSException;
    
    void setConnection(final Connection p0);
}
