// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.blob;

import java.net.HttpURLConnection;
import javax.jms.JMSException;
import java.io.IOException;
import java.net.URL;
import java.io.InputStream;
import org.apache.activemq.command.ActiveMQBlobMessage;

public class DefaultBlobDownloadStrategy extends DefaultStrategy implements BlobDownloadStrategy
{
    public DefaultBlobDownloadStrategy(final BlobTransferPolicy transferPolicy) {
        super(transferPolicy);
    }
    
    @Override
    public InputStream getInputStream(final ActiveMQBlobMessage message) throws IOException, JMSException {
        final URL value = message.getURL();
        if (value == null) {
            return null;
        }
        return value.openStream();
    }
    
    @Override
    public void deleteFile(final ActiveMQBlobMessage message) throws IOException, JMSException {
        final URL url = this.createMessageURL(message);
        final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.connect();
        connection.disconnect();
        if (!this.isSuccessfulCode(connection.getResponseCode())) {
            throw new IOException("DELETE was not successful: " + connection.getResponseCode() + " " + connection.getResponseMessage());
        }
    }
}
