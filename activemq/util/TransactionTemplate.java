// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.store.PersistenceAdapter;
import org.slf4j.Logger;

public class TransactionTemplate
{
    private static final Logger LOG;
    private PersistenceAdapter persistenceAdapter;
    private ConnectionContext context;
    
    public TransactionTemplate(final PersistenceAdapter persistenceAdapter, final ConnectionContext context) {
        this.persistenceAdapter = persistenceAdapter;
        this.context = context;
    }
    
    public void run(final Callback task) throws IOException {
        this.persistenceAdapter.beginTransaction(this.context);
        Throwable throwable = null;
        try {
            task.execute();
        }
        catch (IOException t) {
            throwable = t;
            throw t;
        }
        catch (RuntimeException t2) {
            throwable = t2;
            throw t2;
        }
        catch (Throwable t3) {
            throwable = t3;
            throw IOExceptionSupport.create("Persistence task failed: " + t3, t3);
        }
        finally {
            if (throwable == null) {
                this.persistenceAdapter.commitTransaction(this.context);
            }
            else {
                TransactionTemplate.LOG.error("Having to Rollback - caught an exception: " + throwable);
                this.persistenceAdapter.rollbackTransaction(this.context);
            }
        }
    }
    
    public ConnectionContext getContext() {
        return this.context;
    }
    
    public PersistenceAdapter getPersistenceAdapter() {
        return this.persistenceAdapter;
    }
    
    static {
        LOG = LoggerFactory.getLogger(TransactionTemplate.class);
    }
}
