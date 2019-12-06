// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.CountDownLatch;
import java.util.Iterator;
import java.util.concurrent.Future;
import java.util.ArrayList;

public class Promise<T> extends PromiseCallback<T>
{
    ArrayList<PromiseCallback<T>> callbacks;
    T value;
    Throwable error;
    Future<T> future;
    
    public Promise() {
        this.callbacks = new ArrayList<PromiseCallback<T>>(1);
        this.future = null;
    }
    
    public Future<T> future() {
        if (this.future == null) {
            final PromiseFuture future = new PromiseFuture();
            this.watch(future);
            this.future = future;
        }
        return this.future;
    }
    
    public void watch(final PromiseCallback<T> callback) {
        if (callback == null) {
            throw new IllegalArgumentException("callback cannot be null");
        }
        boolean queued = false;
        synchronized (this) {
            if (this.callbacks != null) {
                this.callbacks.add(callback);
                queued = true;
            }
        }
        if (!queued) {
            callback.onComplete(this.value, this.error);
        }
    }
    
    @Override
    public void onComplete(final T value, final Throwable error) {
        if (value != null && error != null) {
            throw new IllegalArgumentException("You can not have both a vaule and error");
        }
        final ArrayList<PromiseCallback<T>> callbacks;
        synchronized (this) {
            callbacks = this.callbacks;
            if (callbacks != null) {
                this.value = value;
                this.error = error;
                this.callbacks = null;
            }
        }
        if (callbacks != null) {
            for (final PromiseCallback callback : callbacks) {
                callback.onComplete(this.value, this.error);
            }
        }
    }
    
    private class PromiseFuture extends PromiseCallback<T> implements Future<T>
    {
        CountDownLatch latch;
        
        private PromiseFuture() {
            this.latch = new CountDownLatch(1);
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
            return this.latch.getCount() == 0L;
        }
        
        @Override
        public T get() throws InterruptedException, ExecutionException {
            this.latch.await();
            return this.value();
        }
        
        @Override
        public T get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            if (this.latch.await(timeout, unit)) {
                return this.value();
            }
            throw new TimeoutException();
        }
        
        @Override
        public void onComplete(final T value, final Throwable error) {
            this.latch.countDown();
        }
        
        private T value() throws ExecutionException {
            if (Promise.this.error != null) {
                throw new ExecutionException(Promise.this.error);
            }
            return Promise.this.value;
        }
    }
}
