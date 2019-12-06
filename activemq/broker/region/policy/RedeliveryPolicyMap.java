// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.apache.activemq.filter.DestinationMapEntry;
import java.util.List;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.filter.DestinationMap;

public class RedeliveryPolicyMap extends DestinationMap
{
    private RedeliveryPolicy defaultEntry;
    
    public RedeliveryPolicy getEntryFor(final ActiveMQDestination destination) {
        RedeliveryPolicy answer = (RedeliveryPolicy)this.chooseValue(destination);
        if (answer == null) {
            answer = this.getDefaultEntry();
        }
        return answer;
    }
    
    public void setRedeliveryPolicyEntries(final List entries) {
        super.setEntries(entries);
    }
    
    public RedeliveryPolicy getDefaultEntry() {
        return this.defaultEntry;
    }
    
    public void setDefaultEntry(final RedeliveryPolicy defaultEntry) {
        this.defaultEntry = defaultEntry;
    }
    
    @Override
    protected Class<? extends DestinationMapEntry> getEntryClass() {
        return RedeliveryPolicy.class;
    }
}
