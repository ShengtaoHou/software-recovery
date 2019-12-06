// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.security;

import org.apache.activemq.command.ActiveMQDestination;
import java.util.Set;
import org.apache.activemq.filter.DestinationMap;

public class SimpleAuthorizationMap implements AuthorizationMap
{
    private DestinationMap writeACLs;
    private DestinationMap readACLs;
    private DestinationMap adminACLs;
    private TempDestinationAuthorizationEntry tempDestinationAuthorizationEntry;
    
    public SimpleAuthorizationMap() {
    }
    
    public SimpleAuthorizationMap(final DestinationMap writeACLs, final DestinationMap readACLs, final DestinationMap adminACLs) {
        this.writeACLs = writeACLs;
        this.readACLs = readACLs;
        this.adminACLs = adminACLs;
    }
    
    public void setTempDestinationAuthorizationEntry(final TempDestinationAuthorizationEntry tempDestinationAuthorizationEntry) {
        this.tempDestinationAuthorizationEntry = tempDestinationAuthorizationEntry;
    }
    
    public TempDestinationAuthorizationEntry getTempDestinationAuthorizationEntry() {
        return this.tempDestinationAuthorizationEntry;
    }
    
    @Override
    public Set<Object> getTempDestinationAdminACLs() {
        if (this.tempDestinationAuthorizationEntry != null) {
            return this.tempDestinationAuthorizationEntry.getAdminACLs();
        }
        return null;
    }
    
    @Override
    public Set<Object> getTempDestinationReadACLs() {
        if (this.tempDestinationAuthorizationEntry != null) {
            return this.tempDestinationAuthorizationEntry.getReadACLs();
        }
        return null;
    }
    
    @Override
    public Set<Object> getTempDestinationWriteACLs() {
        if (this.tempDestinationAuthorizationEntry != null) {
            return this.tempDestinationAuthorizationEntry.getWriteACLs();
        }
        return null;
    }
    
    @Override
    public Set<Object> getAdminACLs(final ActiveMQDestination destination) {
        return (Set<Object>)this.adminACLs.get(destination);
    }
    
    @Override
    public Set<Object> getReadACLs(final ActiveMQDestination destination) {
        return (Set<Object>)this.readACLs.get(destination);
    }
    
    @Override
    public Set<Object> getWriteACLs(final ActiveMQDestination destination) {
        return (Set<Object>)this.writeACLs.get(destination);
    }
    
    public DestinationMap getAdminACLs() {
        return this.adminACLs;
    }
    
    public void setAdminACLs(final DestinationMap adminACLs) {
        this.adminACLs = adminACLs;
    }
    
    public DestinationMap getReadACLs() {
        return this.readACLs;
    }
    
    public void setReadACLs(final DestinationMap readACLs) {
        this.readACLs = readACLs;
    }
    
    public DestinationMap getWriteACLs() {
        return this.writeACLs;
    }
    
    public void setWriteACLs(final DestinationMap writeACLs) {
        this.writeACLs = writeACLs;
    }
}
