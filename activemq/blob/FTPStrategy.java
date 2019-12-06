// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.blob;

import java.io.IOException;
import java.net.ConnectException;
import javax.jms.JMSException;
import org.apache.commons.net.ftp.FTPClient;
import java.net.MalformedURLException;
import java.net.URL;

public class FTPStrategy
{
    protected BlobTransferPolicy transferPolicy;
    protected URL url;
    protected String ftpUser;
    protected String ftpPass;
    
    public FTPStrategy(final BlobTransferPolicy transferPolicy) throws MalformedURLException {
        this.ftpUser = "";
        this.ftpPass = "";
        this.transferPolicy = transferPolicy;
        this.url = new URL(this.transferPolicy.getUploadUrl());
    }
    
    protected void setUserInformation(final String userInfo) {
        if (userInfo != null) {
            final String[] userPass = userInfo.split(":");
            if (userPass.length > 0) {
                this.ftpUser = userPass[0];
            }
            if (userPass.length > 1) {
                this.ftpPass = userPass[1];
            }
        }
        else {
            this.ftpUser = "anonymous";
            this.ftpPass = "anonymous";
        }
    }
    
    protected FTPClient createFTP() throws IOException, JMSException {
        final String connectUrl = this.url.getHost();
        this.setUserInformation(this.url.getUserInfo());
        final int port = (this.url.getPort() < 1) ? 21 : this.url.getPort();
        final FTPClient ftp = new FTPClient();
        try {
            ftp.connect(connectUrl, port);
        }
        catch (ConnectException e) {
            throw new JMSException("Problem connecting the FTP-server");
        }
        if (!ftp.login(this.ftpUser, this.ftpPass)) {
            ftp.quit();
            ftp.disconnect();
            throw new JMSException("Cant Authentificate to FTP-Server");
        }
        return ftp;
    }
}
