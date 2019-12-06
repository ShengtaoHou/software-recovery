// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

import java.io.IOException;
import org.slf4j.Logger;

public class TransportLogger extends TransportFilter
{
    private final Logger log;
    private boolean logging;
    private final LogWriter logWriter;
    private TransportLoggerView view;
    
    public TransportLogger(final Transport next, final Logger log, final boolean startLogging, final LogWriter logWriter) {
        super(next);
        this.log = log;
        this.logging = startLogging;
        this.logWriter = logWriter;
    }
    
    public boolean isLogging() {
        return this.logging;
    }
    
    public void setLogging(final boolean logging) {
        this.logging = logging;
    }
    
    @Override
    public Object request(final Object command) throws IOException {
        if (this.logging) {
            this.logWriter.logRequest(this.log, command);
        }
        final Object rc = super.request(command);
        if (this.logging) {
            this.logWriter.logResponse(this.log, command);
        }
        return rc;
    }
    
    @Override
    public Object request(final Object command, final int timeout) throws IOException {
        if (this.logging) {
            this.logWriter.logRequest(this.log, command);
        }
        final Object rc = super.request(command, timeout);
        if (this.logging) {
            this.logWriter.logResponse(this.log, command);
        }
        return rc;
    }
    
    @Override
    public FutureResponse asyncRequest(final Object command, final ResponseCallback responseCallback) throws IOException {
        if (this.logging) {
            this.logWriter.logAsyncRequest(this.log, command);
        }
        final FutureResponse rc = this.next.asyncRequest(command, responseCallback);
        return rc;
    }
    
    @Override
    public void oneway(final Object command) throws IOException {
        if (this.logging && this.log.isDebugEnabled()) {
            this.logWriter.logOneWay(this.log, command);
        }
        this.next.oneway(command);
    }
    
    @Override
    public void onCommand(final Object command) {
        if (this.logging && this.log.isDebugEnabled()) {
            this.logWriter.logReceivedCommand(this.log, command);
        }
        this.getTransportListener().onCommand(command);
    }
    
    @Override
    public void onException(final IOException error) {
        if (this.logging && this.log.isDebugEnabled()) {
            this.logWriter.logReceivedException(this.log, error);
        }
        this.getTransportListener().onException(error);
    }
    
    public TransportLoggerView getView() {
        return this.view;
    }
    
    public void setView(final TransportLoggerView view) {
        this.view = view;
    }
    
    @Override
    public String toString() {
        return this.next.toString();
    }
    
    @Override
    public void stop() throws Exception {
        super.stop();
        if (this.view != null) {
            this.view.unregister();
        }
    }
}
