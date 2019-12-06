// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import java.net.MalformedURLException;
import java.net.URL;
import javax.jms.JMSException;
import java.io.IOException;
import java.io.InputStream;

public interface BlobMessage extends Message
{
    InputStream getInputStream() throws IOException, JMSException;
    
    URL getURL() throws MalformedURLException, JMSException;
    
    String getMimeType();
    
    void setMimeType(final String p0);
    
    String getName();
    
    void setName(final String p0);
}
