// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.io.InterruptedIOException;
import org.apache.activemq.command.Response;
import java.util.concurrent.ArrayBlockingQueue;
import org.slf4j.Logger;

public class FutureResponse
{
    private static final Logger LOG;
    private final ResponseCallback responseCallback;
    private final ArrayBlockingQueue<Response> responseSlot;
    
    public FutureResponse(final ResponseCallback responseCallback) {
        this.responseSlot = new ArrayBlockingQueue<Response>(1);
        this.responseCallback = responseCallback;
    }
    
    public Response getResult() throws IOException {
        try {
            return this.responseSlot.take();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            if (FutureResponse.LOG.isDebugEnabled()) {
                FutureResponse.LOG.debug("Operation interupted: " + e, e);
            }
            throw new InterruptedIOException("Interrupted.");
        }
    }
    
    public Response getResult(final int timeout) throws IOException {
        try {
            final Response result = this.responseSlot.poll(timeout, TimeUnit.MILLISECONDS);
            if (result == null && timeout > 0) {
                throw new RequestTimedOutIOException();
            }
            return result;
        }
        catch (InterruptedException e) {
            throw new InterruptedIOException("Interrupted.");
        }
    }
    
    public void set(final Response result) {
        if (this.responseSlot.offer(result) && this.responseCallback != null) {
            this.responseCallback.onCompletion(this);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(FutureResponse.class);
    }
}
