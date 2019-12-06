// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.blob;

import javax.jms.JMSException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.activemq.command.ActiveMQBlobMessage;

public interface BlobDownloadStrategy
{
    InputStream getInputStream(final ActiveMQBlobMessage p0) throws IOException, JMSException;
    
    void deleteFile(final ActiveMQBlobMessage p0) throws IOException, JMSException;
}
