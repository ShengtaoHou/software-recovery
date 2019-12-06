// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.blob;

import java.net.MalformedURLException;
import javax.jms.JMSException;
import java.net.URL;
import org.apache.activemq.command.ActiveMQBlobMessage;

public class DefaultStrategy
{
    protected BlobTransferPolicy transferPolicy;
    
    public DefaultStrategy(final BlobTransferPolicy transferPolicy) {
        this.transferPolicy = transferPolicy;
    }
    
    protected boolean isSuccessfulCode(final int responseCode) {
        return responseCode >= 200 && responseCode < 300;
    }
    
    protected URL createMessageURL(final ActiveMQBlobMessage message) throws JMSException, MalformedURLException {
        return new URL(this.transferPolicy.getUploadUrl() + message.getMessageId().toString());
    }
}
