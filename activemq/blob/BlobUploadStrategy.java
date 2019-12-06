// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.blob;

import java.io.InputStream;
import java.io.IOException;
import javax.jms.JMSException;
import java.net.URL;
import java.io.File;
import org.apache.activemq.command.ActiveMQBlobMessage;

public interface BlobUploadStrategy
{
    URL uploadFile(final ActiveMQBlobMessage p0, final File p1) throws JMSException, IOException;
    
    URL uploadStream(final ActiveMQBlobMessage p0, final InputStream p1) throws JMSException, IOException;
}
