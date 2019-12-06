// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.failover;

import org.apache.activemq.transport.TransportListener;
import java.io.IOException;
import java.net.URI;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.transport.DefaultTransportListener;

class BackupTransport extends DefaultTransportListener
{
    private final FailoverTransport failoverTransport;
    private Transport transport;
    private URI uri;
    private boolean disposed;
    
    BackupTransport(final FailoverTransport ft) {
        this.failoverTransport = ft;
    }
    
    @Override
    public void onException(final IOException error) {
        this.disposed = true;
        if (this.failoverTransport != null) {
            this.failoverTransport.reconnect(false);
        }
    }
    
    public Transport getTransport() {
        return this.transport;
    }
    
    public void setTransport(final Transport transport) {
        (this.transport = transport).setTransportListener(this);
    }
    
    public URI getUri() {
        return this.uri;
    }
    
    public void setUri(final URI uri) {
        this.uri = uri;
    }
    
    public boolean isDisposed() {
        return this.disposed || (this.transport != null && this.transport.isDisposed());
    }
    
    public void setDisposed(final boolean disposed) {
        this.disposed = disposed;
    }
    
    @Override
    public int hashCode() {
        return (this.uri != null) ? this.uri.hashCode() : -1;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof BackupTransport) {
            final BackupTransport other = (BackupTransport)obj;
            return (this.uri == null && other.uri == null) || (this.uri != null && other.uri != null && this.uri.equals(other.uri));
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "Backup transport: " + this.uri;
    }
}
