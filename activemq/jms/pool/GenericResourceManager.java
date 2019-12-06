// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.jms.pool;

import javax.transaction.xa.XAResource;
import org.apache.geronimo.transaction.manager.WrapperNamedXAResource;
import java.io.IOException;
import javax.jms.XASession;
import javax.jms.XAConnection;
import javax.transaction.SystemException;
import javax.jms.Connection;
import org.apache.geronimo.transaction.manager.NamedXAResource;
import org.apache.geronimo.transaction.manager.NamedXAResourceFactory;
import org.apache.geronimo.transaction.manager.RecoverableTransactionManager;
import javax.jms.XAConnectionFactory;
import org.slf4j.LoggerFactory;
import javax.jms.ConnectionFactory;
import javax.transaction.TransactionManager;
import org.slf4j.Logger;

public class GenericResourceManager
{
    private static final Logger LOGGER;
    private String resourceName;
    private String userName;
    private String password;
    private TransactionManager transactionManager;
    private ConnectionFactory connectionFactory;
    
    public void recoverResource() {
        try {
            if (!Recovery.recover(this)) {
                GenericResourceManager.LOGGER.info("Resource manager is unrecoverable");
            }
        }
        catch (NoClassDefFoundError e) {
            GenericResourceManager.LOGGER.info("Resource manager is unrecoverable due to missing classes: " + e);
        }
        catch (Throwable e2) {
            GenericResourceManager.LOGGER.warn("Error while recovering resource manager", e2);
        }
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public void setUserName(final String userName) {
        this.userName = userName;
    }
    
    public String getResourceName() {
        return this.resourceName;
    }
    
    public void setResourceName(final String resourceName) {
        this.resourceName = resourceName;
    }
    
    public TransactionManager getTransactionManager() {
        return this.transactionManager;
    }
    
    public void setTransactionManager(final TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
    
    public ConnectionFactory getConnectionFactory() {
        return this.connectionFactory;
    }
    
    public void setConnectionFactory(final ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(GenericResourceManager.class);
    }
    
    public static class Recovery
    {
        public static boolean isRecoverable(final GenericResourceManager rm) {
            return rm.getConnectionFactory() instanceof XAConnectionFactory && rm.getTransactionManager() instanceof RecoverableTransactionManager && rm.getResourceName() != null && !"".equals(rm.getResourceName());
        }
        
        public static boolean recover(final GenericResourceManager rm) throws IOException {
            if (isRecoverable(rm)) {
                final XAConnectionFactory connFactory = (XAConnectionFactory)rm.getConnectionFactory();
                final RecoverableTransactionManager rtxManager = (RecoverableTransactionManager)rm.getTransactionManager();
                rtxManager.registerNamedXAResourceFactory((NamedXAResourceFactory)new NamedXAResourceFactory() {
                    public String getName() {
                        return rm.getResourceName();
                    }
                    
                    public NamedXAResource getNamedXAResource() throws SystemException {
                        try {
                            final XAConnection xaConnection = connFactory.createXAConnection(rm.getUserName(), rm.getPassword());
                            final XASession session = xaConnection.createXASession();
                            xaConnection.start();
                            GenericResourceManager.LOGGER.debug("new namedXAResource's connection: " + xaConnection);
                            return (NamedXAResource)new ConnectionAndWrapperNamedXAResource(session.getXAResource(), this.getName(), xaConnection);
                        }
                        catch (Exception e) {
                            final SystemException se = new SystemException("Failed to create ConnectionAndWrapperNamedXAResource, " + e.getLocalizedMessage());
                            se.initCause(e);
                            GenericResourceManager.LOGGER.error(se.getLocalizedMessage(), se);
                            throw se;
                        }
                    }
                    
                    public void returnNamedXAResource(final NamedXAResource namedXaResource) {
                        if (namedXaResource instanceof ConnectionAndWrapperNamedXAResource) {
                            try {
                                GenericResourceManager.LOGGER.debug("closing returned namedXAResource's connection: " + ((ConnectionAndWrapperNamedXAResource)namedXaResource).connection);
                                ((ConnectionAndWrapperNamedXAResource)namedXaResource).connection.close();
                            }
                            catch (Exception ignored) {
                                GenericResourceManager.LOGGER.debug("failed to close returned namedXAResource: " + namedXaResource, ignored);
                            }
                        }
                    }
                });
                return true;
            }
            return false;
        }
    }
    
    public static class ConnectionAndWrapperNamedXAResource extends WrapperNamedXAResource
    {
        final Connection connection;
        
        public ConnectionAndWrapperNamedXAResource(final XAResource xaResource, final String name, final Connection connection) {
            super(xaResource, name);
            this.connection = connection;
        }
    }
}
