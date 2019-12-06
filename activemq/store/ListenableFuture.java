// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store;

import java.util.concurrent.Future;

public interface ListenableFuture<T> extends Future<T>
{
    void addListener(final Runnable p0);
}
