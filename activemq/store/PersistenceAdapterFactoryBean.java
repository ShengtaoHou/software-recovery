// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store;

import org.springframework.beans.factory.FactoryBean;
import org.apache.activemq.store.journal.JournalPersistenceAdapterFactory;

public class PersistenceAdapterFactoryBean extends JournalPersistenceAdapterFactory implements FactoryBean
{
    private PersistenceAdapter persistenceAdaptor;
    
    public Object getObject() throws Exception {
        if (this.persistenceAdaptor == null) {
            this.persistenceAdaptor = this.createPersistenceAdapter();
        }
        return this.persistenceAdaptor;
    }
    
    public Class getObjectType() {
        return PersistenceAdapter.class;
    }
    
    public boolean isSingleton() {
        return false;
    }
}
