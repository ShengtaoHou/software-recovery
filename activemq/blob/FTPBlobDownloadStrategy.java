// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.blob;

import javax.jms.JMSException;
import java.io.IOException;
import org.apache.commons.net.ftp.FTPClient;
import java.io.FilterInputStream;
import java.io.InputStream;
import org.apache.activemq.command.ActiveMQBlobMessage;
import java.net.MalformedURLException;

public class FTPBlobDownloadStrategy extends FTPStrategy implements BlobDownloadStrategy
{
    public FTPBlobDownloadStrategy(final BlobTransferPolicy transferPolicy) throws MalformedURLException {
        super(transferPolicy);
    }
    
    @Override
    public InputStream getInputStream(final ActiveMQBlobMessage message) throws IOException, JMSException {
        this.url = message.getURL();
        final FTPClient ftp = this.createFTP();
        final String path = this.url.getPath();
        final String workingDir = path.substring(0, path.lastIndexOf("/"));
        final String file = path.substring(path.lastIndexOf("/") + 1);
        ftp.changeWorkingDirectory(workingDir);
        ftp.setFileType(2);
        final InputStream input = new FilterInputStream(ftp.retrieveFileStream(file)) {
            @Override
            public void close() throws IOException {
                this.in.close();
                ftp.quit();
                ftp.disconnect();
            }
        };
        return input;
    }
    
    @Override
    public void deleteFile(final ActiveMQBlobMessage message) throws IOException, JMSException {
        this.url = message.getURL();
        final FTPClient ftp = this.createFTP();
        final String path = this.url.getPath();
        try {
            if (!ftp.deleteFile(path)) {
                throw new JMSException("Delete file failed: " + ftp.getReplyString());
            }
        }
        finally {
            ftp.quit();
            ftp.disconnect();
        }
    }
}
