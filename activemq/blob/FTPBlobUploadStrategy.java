// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.blob;

import org.apache.commons.net.ftp.FTPClient;
import java.io.IOException;
import javax.jms.JMSException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.net.URL;
import java.io.File;
import org.apache.activemq.command.ActiveMQBlobMessage;
import java.net.MalformedURLException;

public class FTPBlobUploadStrategy extends FTPStrategy implements BlobUploadStrategy
{
    public FTPBlobUploadStrategy(final BlobTransferPolicy transferPolicy) throws MalformedURLException {
        super(transferPolicy);
    }
    
    @Override
    public URL uploadFile(final ActiveMQBlobMessage message, final File file) throws JMSException, IOException {
        return this.uploadStream(message, new FileInputStream(file));
    }
    
    @Override
    public URL uploadStream(final ActiveMQBlobMessage message, final InputStream in) throws JMSException, IOException {
        final FTPClient ftp = this.createFTP();
        try {
            final String path = this.url.getPath();
            final String workingDir = path.substring(0, path.lastIndexOf("/"));
            final String filename = message.getMessageId().toString().replaceAll(":", "_");
            ftp.setFileType(2);
            String url;
            if (!ftp.changeWorkingDirectory(workingDir)) {
                url = this.url.toString().replaceFirst(this.url.getPath(), "") + "/";
            }
            else {
                url = this.url.toString();
            }
            if (!ftp.storeFile(filename, in)) {
                throw new JMSException("FTP store failed: " + ftp.getReplyString());
            }
            return new URL(url + filename);
        }
        finally {
            ftp.quit();
            ftp.disconnect();
        }
    }
}
