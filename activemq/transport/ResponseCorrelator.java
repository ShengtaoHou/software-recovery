// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.activemq.command.Response;
import org.apache.activemq.command.ExceptionResponse;
import org.apache.activemq.command.Command;
import java.util.HashMap;
import java.io.IOException;
import org.apache.activemq.util.IntSequenceGenerator;
import java.util.Map;
import org.slf4j.Logger;

public class ResponseCorrelator extends TransportFilter
{
    private static final Logger LOG;
    private final Map<Integer, FutureResponse> requestMap;
    private IntSequenceGenerator sequenceGenerator;
    private final boolean debug;
    private IOException error;
    
    public ResponseCorrelator(final Transport next) {
        this(next, new IntSequenceGenerator());
    }
    
    public ResponseCorrelator(final Transport next, final IntSequenceGenerator sequenceGenerator) {
        super(next);
        this.requestMap = new HashMap<Integer, FutureResponse>();
        this.debug = ResponseCorrelator.LOG.isDebugEnabled();
        this.sequenceGenerator = sequenceGenerator;
    }
    
    @Override
    public void oneway(final Object o) throws IOException {
        final Command command = (Command)o;
        command.setCommandId(this.sequenceGenerator.getNextSequenceId());
        command.setResponseRequired(false);
        this.next.oneway(command);
    }
    
    @Override
    public FutureResponse asyncRequest(final Object o, final ResponseCallback responseCallback) throws IOException {
        final Command command = (Command)o;
        command.setCommandId(this.sequenceGenerator.getNextSequenceId());
        command.setResponseRequired(true);
        final FutureResponse future = new FutureResponse(responseCallback);
        IOException priorError = null;
        synchronized (this.requestMap) {
            priorError = this.error;
            if (priorError == null) {
                this.requestMap.put(new Integer(command.getCommandId()), future);
            }
        }
        if (priorError != null) {
            future.set(new ExceptionResponse(priorError));
            throw priorError;
        }
        this.next.oneway(command);
        return future;
    }
    
    @Override
    public Object request(final Object command) throws IOException {
        final FutureResponse response = this.asyncRequest(command, null);
        return response.getResult();
    }
    
    @Override
    public Object request(final Object command, final int timeout) throws IOException {
        final FutureResponse response = this.asyncRequest(command, null);
        return response.getResult(timeout);
    }
    
    @Override
    public void onCommand(final Object o) {
        Command command = null;
        if (o instanceof Command) {
            command = (Command)o;
            if (command.isResponse()) {
                final Response response = (Response)command;
                FutureResponse future = null;
                synchronized (this.requestMap) {
                    future = this.requestMap.remove(response.getCorrelationId());
                }
                if (future != null) {
                    future.set(response);
                }
                else if (this.debug) {
                    ResponseCorrelator.LOG.debug("Received unexpected response: {" + command + "}for command id: " + response.getCorrelationId());
                }
            }
            else {
                this.getTransportListener().onCommand(command);
            }
            return;
        }
        throw new ClassCastException("Object cannot be converted to a Command,  Object: " + o);
    }
    
    @Override
    public void onException(final IOException error) {
        this.dispose(error);
        super.onException(error);
    }
    
    @Override
    public void stop() throws Exception {
        this.dispose(new IOException("Stopped."));
        super.stop();
    }
    
    private void dispose(final IOException error) {
        ArrayList<FutureResponse> requests = null;
        synchronized (this.requestMap) {
            if (this.error == null) {
                this.error = error;
                requests = new ArrayList<FutureResponse>(this.requestMap.values());
                this.requestMap.clear();
            }
        }
        if (requests != null) {
            for (final FutureResponse fr : requests) {
                fr.set(new ExceptionResponse(error));
            }
        }
    }
    
    public IntSequenceGenerator getSequenceGenerator() {
        return this.sequenceGenerator;
    }
    
    @Override
    public String toString() {
        return this.next.toString();
    }
    
    static {
        LOG = LoggerFactory.getLogger(ResponseCorrelator.class);
    }
}
