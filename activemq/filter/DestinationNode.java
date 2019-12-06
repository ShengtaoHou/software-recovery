// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter;

import java.util.Collection;
import java.util.Set;

public interface DestinationNode
{
    void appendMatchingValues(final Set<DestinationNode> p0, final String[] p1, final int p2);
    
    void appendMatchingWildcards(final Set<DestinationNode> p0, final String[] p1, final int p2);
    
    void appendDescendantValues(final Set<DestinationNode> p0);
    
    Collection<DestinationNode> getDesendentValues();
    
    DestinationNode getChild(final String p0);
    
    Collection<DestinationNode> getValues();
    
    Collection<DestinationNode> getChildren();
    
    Collection<DestinationNode> removeDesendentValues();
    
    Collection<DestinationNode> removeValues();
}
