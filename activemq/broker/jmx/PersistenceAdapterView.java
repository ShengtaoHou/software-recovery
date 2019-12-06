// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import java.util.concurrent.Callable;
import org.apache.activemq.store.PersistenceAdapter;

public class PersistenceAdapterView implements PersistenceAdapterViewMBean
{
    private final String name;
    private final PersistenceAdapter persistenceAdapter;
    private Callable<String> inflightTransactionViewCallable;
    private Callable<String> dataViewCallable;
    
    public PersistenceAdapterView(final PersistenceAdapter adapter) {
        this.name = adapter.toString();
        this.persistenceAdapter = adapter;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String getTransactions() {
        return this.invoke(this.inflightTransactionViewCallable);
    }
    
    @Override
    public String getData() {
        return this.invoke(this.dataViewCallable);
    }
    
    @Override
    public long getSize() {
        return this.persistenceAdapter.size();
    }
    
    private String invoke(final Callable<String> callable) {
        String result = null;
        if (callable != null) {
            try {
                result = callable.call();
            }
            catch (Exception e) {
                result = e.toString();
            }
        }
        return result;
    }
    
    public void setDataViewCallable(final Callable<String> dataViewCallable) {
        this.dataViewCallable = dataViewCallable;
    }
    
    public void setInflightTransactionViewCallable(final Callable<String> inflightTransactionViewCallable) {
        this.inflightTransactionViewCallable = inflightTransactionViewCallable;
    }
}
