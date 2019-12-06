// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.proxy;

import org.slf4j.LoggerFactory;
import org.apache.activemq.util.ServiceStopper;
import org.apache.activemq.transport.TransportListener;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.command.WireFormatInfo;
import org.apache.activemq.command.ShutdownInfo;
import org.apache.activemq.transport.DefaultTransportListener;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.transport.Transport;
import org.slf4j.Logger;
import org.apache.activemq.Service;

class ProxyConnection implements Service
{
    private static final Logger LOG;
    protected final Transport localTransport;
    protected final Transport remoteTransport;
    private final AtomicBoolean shuttingDown;
    private final AtomicBoolean running;
    
    public ProxyConnection(final Transport localTransport, final Transport remoteTransport) {
        this.shuttingDown = new AtomicBoolean(false);
        this.running = new AtomicBoolean(false);
        this.localTransport = localTransport;
        this.remoteTransport = remoteTransport;
    }
    
    public void onFailure(final IOException e) {
        if (!this.shuttingDown.get()) {
            ProxyConnection.LOG.debug("Transport error: {}", e.getMessage(), e);
            try {
                this.stop();
            }
            catch (Exception ex) {}
        }
    }
    
    @Override
    public void start() throws Exception {
        if (!this.running.compareAndSet(false, true)) {
            return;
        }
        this.localTransport.setTransportListener(new DefaultTransportListener() {
            @Override
            public void onCommand(final Object command) {
                boolean shutdown = false;
                if (command.getClass() == ShutdownInfo.class) {
                    ProxyConnection.this.shuttingDown.set(true);
                    shutdown = true;
                }
                if (command.getClass() == WireFormatInfo.class) {
                    return;
                }
                try {
                    ProxyConnection.this.remoteTransport.oneway(command);
                    if (shutdown) {
                        ProxyConnection.this.stop();
                    }
                }
                catch (IOException error) {
                    ProxyConnection.this.onFailure(error);
                }
                catch (Exception error2) {
                    ProxyConnection.this.onFailure(IOExceptionSupport.create(error2));
                }
            }
            
            @Override
            public void onException(final IOException error) {
                ProxyConnection.this.onFailure(error);
            }
        });
        this.remoteTransport.setTransportListener(new DefaultTransportListener() {
            @Override
            public void onCommand(final Object command) {
                try {
                    if (command.getClass() == WireFormatInfo.class) {
                        return;
                    }
                    ProxyConnection.this.localTransport.oneway(command);
                }
                catch (IOException error) {
                    ProxyConnection.this.onFailure(error);
                }
            }
            
            @Override
            public void onException(final IOException error) {
                ProxyConnection.this.onFailure(error);
            }
        });
        this.localTransport.start();
        this.remoteTransport.start();
    }
    
    @Override
    public void stop() throws Exception {
        if (!this.running.compareAndSet(true, false)) {
            return;
        }
        this.shuttingDown.set(true);
        final ServiceStopper ss = new ServiceStopper();
        ss.stop(this.remoteTransport);
        ss.stop(this.localTransport);
        ss.throwFirstException();
    }
    
    @Override
    public boolean equals(final Object arg) {
        if (arg == null || !(arg instanceof ProxyConnection)) {
            return false;
        }
        final ProxyConnection other = (ProxyConnection)arg;
        String otherRemote = "";
        String otherLocal = "";
        String thisRemote = "";
        String thisLocal = "";
        if (other.localTransport != null && other.localTransport.getRemoteAddress() != null) {
            otherLocal = other.localTransport.getRemoteAddress();
        }
        if (other.remoteTransport != null && other.remoteTransport.getRemoteAddress() != null) {
            otherRemote = other.remoteTransport.getRemoteAddress();
        }
        if (this.remoteTransport != null && this.remoteTransport.getRemoteAddress() != null) {
            thisRemote = this.remoteTransport.getRemoteAddress();
        }
        if (this.localTransport != null && this.localTransport.getRemoteAddress() != null) {
            thisLocal = this.localTransport.getRemoteAddress();
        }
        return otherRemote.equals(thisRemote) && otherLocal.equals(thisLocal);
    }
    
    @Override
    public int hashCode() {
        int hash = 17;
        if (this.localTransport != null && this.localTransport.getRemoteAddress() != null) {
            hash += 31 * hash + this.localTransport.getRemoteAddress().hashCode();
        }
        if (this.remoteTransport != null && this.remoteTransport.getRemoteAddress() != null) {
            hash = 31 * hash + this.remoteTransport.hashCode();
        }
        return hash;
    }
    
    @Override
    public String toString() {
        return "ProxyConnection [localTransport=" + this.localTransport + ", remoteTransport=" + this.remoteTransport + ", shuttingDown=" + this.shuttingDown.get() + ", running=" + this.running.get() + "]";
    }
    
    static {
        LOG = LoggerFactory.getLogger(ProxyConnection.class);
    }
}
