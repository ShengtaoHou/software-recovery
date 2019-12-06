// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.apache.activemq.filter.DestinationMapEntry;
import java.util.List;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.filter.DestinationMap;

public class PolicyMap extends DestinationMap
{
    private PolicyEntry defaultEntry;
    
    public PolicyEntry getEntryFor(final ActiveMQDestination destination) {
        PolicyEntry answer = (PolicyEntry)this.chooseValue(destination);
        if (answer == null) {
            answer = this.getDefaultEntry();
        }
        return answer;
    }
    
    public void setPolicyEntries(final List entries) {
        super.setEntries(entries);
    }
    
    public PolicyEntry getDefaultEntry() {
        return this.defaultEntry;
    }
    
    public void setDefaultEntry(final PolicyEntry defaultEntry) {
        this.defaultEntry = defaultEntry;
    }
    
    @Override
    protected Class<? extends DestinationMapEntry> getEntryClass() {
        return PolicyEntry.class;
    }
}
