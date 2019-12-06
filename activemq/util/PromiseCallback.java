// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

public abstract class PromiseCallback<T>
{
    public abstract void onComplete(final T p0, final Throwable p1);
}
