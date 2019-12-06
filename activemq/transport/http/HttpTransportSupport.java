// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.http;

import java.net.URI;
import org.apache.activemq.transport.util.TextWireFormat;
import org.apache.activemq.transport.TransportThreadSupport;

public abstract class HttpTransportSupport extends TransportThreadSupport
{
    private TextWireFormat textWireFormat;
    private URI remoteUrl;
    private String proxyHost;
    private int proxyPort;
    private String proxyUser;
    private String proxyPassword;
    
    public HttpTransportSupport(final TextWireFormat textWireFormat, final URI remoteUrl) {
        this.proxyPort = 8080;
        this.textWireFormat = textWireFormat;
        this.remoteUrl = remoteUrl;
    }
    
    @Override
    public String toString() {
        return "HTTP Reader " + this.getRemoteUrl();
    }
    
    @Override
    public String getRemoteAddress() {
        return this.remoteUrl.toString();
    }
    
    public URI getRemoteUrl() {
        return this.remoteUrl;
    }
    
    public TextWireFormat getTextWireFormat() {
        return this.textWireFormat;
    }
    
    public void setTextWireFormat(final TextWireFormat textWireFormat) {
        this.textWireFormat = textWireFormat;
    }
    
    public String getProxyHost() {
        return this.proxyHost;
    }
    
    public void setProxyHost(final String proxyHost) {
        this.proxyHost = proxyHost;
    }
    
    public int getProxyPort() {
        return this.proxyPort;
    }
    
    public void setProxyPort(final int proxyPort) {
        this.proxyPort = proxyPort;
    }
    
    public String getProxyUser() {
        return this.proxyUser;
    }
    
    public void setProxyUser(final String proxyUser) {
        this.proxyUser = proxyUser;
    }
    
    public String getProxyPassword() {
        return this.proxyPassword;
    }
    
    public void setProxyPassword(final String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }
}
