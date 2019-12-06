// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import java.util.Map;
import java.io.OutputStream;
import javax.jms.Topic;
import javax.jms.JMSException;
import java.io.InputStream;
import javax.jms.Destination;
import javax.jms.Connection;

@Deprecated
public interface StreamConnection extends Connection
{
    InputStream createInputStream(final Destination p0) throws JMSException;
    
    InputStream createInputStream(final Destination p0, final String p1) throws JMSException;
    
    InputStream createInputStream(final Destination p0, final String p1, final boolean p2) throws JMSException;
    
    InputStream createInputStream(final Destination p0, final String p1, final boolean p2, final long p3) throws JMSException;
    
    InputStream createDurableInputStream(final Topic p0, final String p1) throws JMSException;
    
    InputStream createDurableInputStream(final Topic p0, final String p1, final String p2) throws JMSException;
    
    InputStream createDurableInputStream(final Topic p0, final String p1, final String p2, final boolean p3) throws JMSException;
    
    InputStream createDurableInputStream(final Topic p0, final String p1, final String p2, final boolean p3, final long p4) throws JMSException;
    
    OutputStream createOutputStream(final Destination p0) throws JMSException;
    
    OutputStream createOutputStream(final Destination p0, final Map<String, Object> p1, final int p2, final int p3, final long p4) throws JMSException;
    
    void unsubscribe(final String p0) throws JMSException;
}
