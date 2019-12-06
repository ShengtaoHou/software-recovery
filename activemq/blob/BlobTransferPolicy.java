// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.blob;

import java.net.URISyntaxException;
import java.net.MalformedURLException;
import java.net.URL;

public class BlobTransferPolicy
{
    private String defaultUploadUrl;
    private String brokerUploadUrl;
    private String uploadUrl;
    private int bufferSize;
    private BlobUploadStrategy uploadStrategy;
    private BlobDownloadStrategy downloadStrategy;
    
    public BlobTransferPolicy() {
        this.defaultUploadUrl = "http://localhost:8080/uploads/";
        this.bufferSize = 131072;
    }
    
    public BlobTransferPolicy copy() {
        final BlobTransferPolicy that = new BlobTransferPolicy();
        that.defaultUploadUrl = this.defaultUploadUrl;
        that.brokerUploadUrl = this.brokerUploadUrl;
        that.uploadUrl = this.uploadUrl;
        that.bufferSize = this.bufferSize;
        that.uploadStrategy = this.uploadStrategy;
        that.downloadStrategy = this.downloadStrategy;
        return that;
    }
    
    public String getUploadUrl() {
        if (this.uploadUrl == null) {
            this.uploadUrl = this.getBrokerUploadUrl();
            if (this.uploadUrl == null) {
                this.uploadUrl = this.getDefaultUploadUrl();
            }
        }
        return this.uploadUrl;
    }
    
    public void setUploadUrl(final String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }
    
    public String getBrokerUploadUrl() {
        return this.brokerUploadUrl;
    }
    
    public void setBrokerUploadUrl(final String brokerUploadUrl) {
        this.brokerUploadUrl = brokerUploadUrl;
    }
    
    public String getDefaultUploadUrl() {
        return this.defaultUploadUrl;
    }
    
    public void setDefaultUploadUrl(final String defaultUploadUrl) {
        this.defaultUploadUrl = defaultUploadUrl;
    }
    
    public BlobUploadStrategy getUploadStrategy() {
        if (this.uploadStrategy == null) {
            this.uploadStrategy = this.createUploadStrategy();
        }
        return this.uploadStrategy;
    }
    
    public BlobDownloadStrategy getDownloadStrategy() {
        if (this.downloadStrategy == null) {
            this.downloadStrategy = this.createDownloadStrategy();
        }
        return this.downloadStrategy;
    }
    
    public void setUploadStrategy(final BlobUploadStrategy uploadStrategy) {
        this.uploadStrategy = uploadStrategy;
    }
    
    public int getBufferSize() {
        return this.bufferSize;
    }
    
    public void setBufferSize(final int bufferSize) {
        this.bufferSize = bufferSize;
    }
    
    protected BlobUploadStrategy createUploadStrategy() {
        BlobUploadStrategy strategy;
        try {
            final URL url = new URL(this.getUploadUrl());
            if (url.getProtocol().equalsIgnoreCase("FTP")) {
                strategy = new FTPBlobUploadStrategy(this);
            }
            else if (url.getProtocol().equalsIgnoreCase("FILE")) {
                strategy = new FileSystemBlobStrategy(this);
            }
            else {
                strategy = new DefaultBlobUploadStrategy(this);
            }
        }
        catch (MalformedURLException e) {
            strategy = new DefaultBlobUploadStrategy(this);
        }
        catch (URISyntaxException e2) {
            strategy = new DefaultBlobUploadStrategy(this);
        }
        return strategy;
    }
    
    protected BlobDownloadStrategy createDownloadStrategy() {
        BlobDownloadStrategy strategy;
        try {
            final URL url = new URL(this.getUploadUrl());
            if (url.getProtocol().equalsIgnoreCase("FTP")) {
                strategy = new FTPBlobDownloadStrategy(this);
            }
            else if (url.getProtocol().equalsIgnoreCase("FILE")) {
                strategy = new FileSystemBlobStrategy(this);
            }
            else {
                strategy = new DefaultBlobDownloadStrategy(this);
            }
        }
        catch (MalformedURLException e) {
            strategy = new DefaultBlobDownloadStrategy(this);
        }
        catch (URISyntaxException e2) {
            strategy = new DefaultBlobDownloadStrategy(this);
        }
        return strategy;
    }
}
