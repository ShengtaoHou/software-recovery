// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;

public class InlineListenableFuture implements ListenableFuture<Object>
{
    public Object call() throws Exception {
        return null;
    }
    
    @Override
    public void addListener(final Runnable listener) {
        listener.run();
    }
    
    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        return false;
    }
    
    @Override
    public boolean isCancelled() {
        return false;
    }
    
    @Override
    public boolean isDone() {
        return true;
    }
    
    @Override
    public Object get() throws InterruptedException, ExecutionException {
        return null;
    }
    
    @Override
    public Object get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }
}
