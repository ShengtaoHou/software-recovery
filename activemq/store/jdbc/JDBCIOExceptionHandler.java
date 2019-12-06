// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.jdbc;

import org.slf4j.LoggerFactory;
import org.apache.activemq.broker.Locker;
import java.io.IOException;
import org.apache.activemq.broker.SuppressReplyException;
import org.slf4j.Logger;
import org.apache.activemq.util.DefaultIOExceptionHandler;

public class JDBCIOExceptionHandler extends DefaultIOExceptionHandler
{
    private static final Logger LOG;
    
    public JDBCIOExceptionHandler() {
        this.setIgnoreSQLExceptions(false);
        this.setStopStartConnectors(true);
    }
    
    @Override
    protected boolean hasLockOwnership() throws IOException {
        boolean hasLock = true;
        if (this.broker.getPersistenceAdapter() instanceof JDBCPersistenceAdapter) {
            final JDBCPersistenceAdapter jdbcPersistenceAdapter = (JDBCPersistenceAdapter)this.broker.getPersistenceAdapter();
            final Locker locker = jdbcPersistenceAdapter.getLocker();
            if (locker != null) {
                try {
                    if (!locker.keepAlive()) {
                        hasLock = false;
                    }
                }
                catch (SuppressReplyException ex) {}
                catch (IOException ex2) {}
                if (!hasLock) {
                    JDBCIOExceptionHandler.LOG.warn("Lock keepAlive failed, no longer lock owner with: {}", locker);
                    throw new IOException("Lock keepAlive failed, no longer lock owner with: " + locker);
                }
            }
        }
        return hasLock;
    }
    
    static {
        LOG = LoggerFactory.getLogger(JDBCIOExceptionHandler.class);
    }
}
