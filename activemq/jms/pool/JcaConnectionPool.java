// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.jms.pool;

import javax.jms.JMSException;
import org.apache.geronimo.transaction.manager.WrapperNamedXAResource;
import javax.jms.XASession;
import javax.transaction.xa.XAResource;
import javax.transaction.TransactionManager;
import javax.jms.Connection;

public class JcaConnectionPool extends XaConnectionPool
{
    private final String name;
    
    public JcaConnectionPool(final Connection connection, final TransactionManager transactionManager, final String name) {
        super(connection, transactionManager);
        this.name = name;
    }
    
    @Override
    protected XAResource createXaResource(final PooledSession session) throws JMSException {
        XAResource xares = ((XASession)session.getInternalSession()).getXAResource();
        if (this.name != null) {
            xares = (XAResource)new WrapperNamedXAResource(xares, this.name);
        }
        return xares;
    }
}
