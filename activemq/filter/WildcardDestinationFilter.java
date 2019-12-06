// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter;

import org.apache.activemq.command.ActiveMQDestination;

public class WildcardDestinationFilter extends DestinationFilter
{
    private String[] prefixes;
    private byte destinationType;
    
    public WildcardDestinationFilter(final String[] prefixes, final byte destinationType) {
        this.prefixes = new String[prefixes.length];
        for (int i = 0; i < prefixes.length; ++i) {
            final String prefix = prefixes[i];
            if (!prefix.equals("*")) {
                this.prefixes[i] = prefix;
            }
        }
        this.destinationType = destinationType;
    }
    
    @Override
    public boolean matches(final ActiveMQDestination destination) {
        if (destination.getDestinationType() != this.destinationType) {
            return false;
        }
        final String[] path = DestinationPath.getDestinationPaths(destination);
        final int length = this.prefixes.length;
        if (path.length == length) {
            for (int i = 0; i < length; ++i) {
                final String prefix = this.prefixes[i];
                if (prefix != null && !prefix.equals(path[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public String getText() {
        return DestinationPath.toString(this.prefixes);
    }
    
    @Override
    public String toString() {
        return super.toString() + "[destination: " + this.getText() + "]";
    }
    
    @Override
    public boolean isWildcard() {
        return true;
    }
}
