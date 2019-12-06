// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter;

import org.apache.activemq.command.ActiveMQDestination;

public class CompositeDestinationFilter extends DestinationFilter
{
    private DestinationFilter[] filters;
    
    public CompositeDestinationFilter(final ActiveMQDestination destination) {
        final ActiveMQDestination[] destinations = destination.getCompositeDestinations();
        this.filters = new DestinationFilter[destinations.length];
        for (int i = 0; i < destinations.length; ++i) {
            final ActiveMQDestination childDestination = destinations[i];
            this.filters[i] = DestinationFilter.parseFilter(childDestination);
        }
    }
    
    @Override
    public boolean matches(final ActiveMQDestination destination) {
        for (int i = 0; i < this.filters.length; ++i) {
            if (this.filters[i].matches(destination)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isWildcard() {
        for (final DestinationFilter filter : this.filters) {
            if (filter.isWildcard()) {
                return true;
            }
        }
        return false;
    }
}
