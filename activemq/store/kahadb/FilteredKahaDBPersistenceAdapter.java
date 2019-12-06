// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.filter.DestinationMapEntry;

public class FilteredKahaDBPersistenceAdapter extends DestinationMapEntry
{
    private PersistenceAdapter persistenceAdapter;
    private boolean perDestination;
    
    public FilteredKahaDBPersistenceAdapter() {
    }
    
    public FilteredKahaDBPersistenceAdapter(final ActiveMQDestination destination, final PersistenceAdapter adapter) {
        this.setDestination(destination);
        this.persistenceAdapter = adapter;
    }
    
    public PersistenceAdapter getPersistenceAdapter() {
        return this.persistenceAdapter;
    }
    
    public void setPersistenceAdapter(final PersistenceAdapter persistenceAdapter) {
        this.persistenceAdapter = persistenceAdapter;
    }
    
    public boolean isPerDestination() {
        return this.perDestination;
    }
    
    public void setPerDestination(final boolean perDestination) {
        this.perDestination = perDestination;
    }
}
