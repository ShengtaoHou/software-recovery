// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.group;

import java.util.HashSet;
import java.util.Set;

public class SimpleMessageGroupSet implements MessageGroupSet
{
    private Set<String> set;
    
    public SimpleMessageGroupSet() {
        this.set = new HashSet<String>();
    }
    
    @Override
    public boolean contains(final String groupID) {
        return this.set.contains(groupID);
    }
    
    public void add(final String group) {
        this.set.add(group);
    }
    
    protected Set<String> getUnderlyingSet() {
        return this.set;
    }
}
