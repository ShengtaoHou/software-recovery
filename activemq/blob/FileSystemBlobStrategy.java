// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.blob;

import java.io.FileOutputStream;
import java.io.IOException;
import javax.jms.JMSException;
import java.io.InputStream;
import java.io.FileInputStream;
import org.apache.activemq.command.ActiveMQBlobMessage;
import java.net.URL;
import java.net.URISyntaxException;
import java.net.MalformedURLException;
import java.io.File;

public class FileSystemBlobStrategy implements BlobUploadStrategy, BlobDownloadStrategy
{
    private final BlobTransferPolicy policy;
    private File rootFile;
    
    public FileSystemBlobStrategy(final BlobTransferPolicy policy) throws MalformedURLException, URISyntaxException {
        this.policy = policy;
        this.createRootFolder();
    }
    
    protected void createRootFolder() throws MalformedURLException, URISyntaxException {
        this.rootFile = new File(new URL(this.policy.getUploadUrl()).toURI());
        if (!this.rootFile.exists()) {
            this.rootFile.mkdirs();
        }
        else if (!this.rootFile.isDirectory()) {
            throw new IllegalArgumentException("Given url is not a directory " + this.rootFile);
        }
    }
    
    @Override
    public URL uploadFile(final ActiveMQBlobMessage message, final File file) throws JMSException, IOException {
        return this.uploadStream(message, new FileInputStream(file));
    }
    
    @Override
    public URL uploadStream(final ActiveMQBlobMessage message, final InputStream in) throws JMSException, IOException {
        final File f = this.getFile(message);
        final FileOutputStream out = new FileOutputStream(f);
        final byte[] buffer = new byte[this.policy.getBufferSize()];
        for (int c = in.read(buffer); c != -1; c = in.read(buffer)) {
            out.write(buffer, 0, c);
            out.flush();
        }
        out.flush();
        out.close();
        return f.toURI().toURL();
    }
    
    @Override
    public void deleteFile(final ActiveMQBlobMessage message) throws IOException, JMSException {
        final File f = this.getFile(message);
        if (f.exists() && !f.delete()) {
            throw new IOException("Unable to delete file " + f);
        }
    }
    
    @Override
    public InputStream getInputStream(final ActiveMQBlobMessage message) throws IOException, JMSException {
        return new FileInputStream(this.getFile(message));
    }
    
    protected File getFile(final ActiveMQBlobMessage message) throws JMSException, IOException {
        if (message.getURL() != null) {
            try {
                return new File(message.getURL().toURI());
            }
            catch (URISyntaxException e) {
                final IOException ioe = new IOException("Unable to open file for message " + message);
                ioe.initCause(e);
            }
        }
        final String fileName = message.getJMSMessageID().replaceAll(":", "_");
        return new File(this.rootFile, fileName);
    }
}
