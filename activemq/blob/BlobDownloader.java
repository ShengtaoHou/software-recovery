// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.blob;

import javax.jms.JMSException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.activemq.command.ActiveMQBlobMessage;

public class BlobDownloader
{
    private final BlobTransferPolicy blobTransferPolicy;
    
    public BlobDownloader(final BlobTransferPolicy transferPolicy) {
        this.blobTransferPolicy = transferPolicy.copy();
    }
    
    public InputStream getInputStream(final ActiveMQBlobMessage message) throws IOException, JMSException {
        return this.getStrategy().getInputStream(message);
    }
    
    public void deleteFile(final ActiveMQBlobMessage message) throws IOException, JMSException {
        this.getStrategy().deleteFile(message);
    }
    
    public BlobTransferPolicy getBlobTransferPolicy() {
        return this.blobTransferPolicy;
    }
    
    public BlobDownloadStrategy getStrategy() {
        return this.getBlobTransferPolicy().getDownloadStrategy();
    }
}
