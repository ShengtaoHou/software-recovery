// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.jdbc;

public interface JDBCMessageRecoveryListener
{
    boolean recoverMessage(final long p0, final byte[] p1) throws Exception;
    
    boolean recoverMessageReference(final String p0) throws Exception;
}
