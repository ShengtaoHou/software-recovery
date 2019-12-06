// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store;

import org.apache.activemq.command.TransactionId;

public interface TransactionIdTransformer
{
    TransactionId transform(final TransactionId p0);
}
