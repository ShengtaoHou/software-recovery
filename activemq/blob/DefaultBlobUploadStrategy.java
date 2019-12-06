// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.blob;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.io.IOException;
import javax.jms.JMSException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.net.URL;
import java.io.File;
import org.apache.activemq.command.ActiveMQBlobMessage;

public class DefaultBlobUploadStrategy extends DefaultStrategy implements BlobUploadStrategy
{
    public DefaultBlobUploadStrategy(final BlobTransferPolicy transferPolicy) {
        super(transferPolicy);
    }
    
    @Override
    public URL uploadFile(final ActiveMQBlobMessage message, final File file) throws JMSException, IOException {
        return this.uploadStream(message, new FileInputStream(file));
    }
    
    @Override
    public URL uploadStream(final ActiveMQBlobMessage message, final InputStream fis) throws JMSException, IOException {
        final URL url = this.createMessageURL(message);
        final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);
        connection.setChunkedStreamingMode(this.transferPolicy.getBufferSize());
        final OutputStream os = connection.getOutputStream();
        final byte[] buf = new byte[this.transferPolicy.getBufferSize()];
        for (int c = fis.read(buf); c != -1; c = fis.read(buf)) {
            os.write(buf, 0, c);
            os.flush();
        }
        os.close();
        fis.close();
        if (!this.isSuccessfulCode(connection.getResponseCode())) {
            throw new IOException("PUT was not successful: " + connection.getResponseCode() + " " + connection.getResponseMessage());
        }
        return url;
    }
}
