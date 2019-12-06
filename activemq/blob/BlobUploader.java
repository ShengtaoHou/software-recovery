// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.blob;

import java.io.IOException;
import javax.jms.JMSException;
import java.net.URL;
import org.apache.activemq.command.ActiveMQBlobMessage;
import java.io.InputStream;
import java.io.File;

public class BlobUploader
{
    private final BlobTransferPolicy blobTransferPolicy;
    private File file;
    private InputStream in;
    
    public BlobUploader(final BlobTransferPolicy blobTransferPolicy, final InputStream in) {
        this.blobTransferPolicy = blobTransferPolicy.copy();
        this.in = in;
    }
    
    public BlobUploader(final BlobTransferPolicy blobTransferPolicy, final File file) {
        this.blobTransferPolicy = blobTransferPolicy.copy();
        this.file = file;
    }
    
    public URL upload(final ActiveMQBlobMessage message) throws JMSException, IOException {
        if (this.file != null) {
            return this.getStrategy().uploadFile(message, this.file);
        }
        return this.getStrategy().uploadStream(message, this.in);
    }
    
    public BlobTransferPolicy getBlobTransferPolicy() {
        return this.blobTransferPolicy;
    }
    
    public BlobUploadStrategy getStrategy() {
        return this.getBlobTransferPolicy().getUploadStrategy();
    }
}
