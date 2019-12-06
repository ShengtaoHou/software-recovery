// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter;

import org.apache.activemq.command.ActiveMQDestination;

public class PrefixDestinationFilter extends DestinationFilter
{
    private String[] prefixes;
    private byte destinationType;
    
    public PrefixDestinationFilter(final String[] prefixes, final byte destinationType) {
        int lastIndex;
        for (lastIndex = prefixes.length - 1; lastIndex >= 0 && ">".equals(prefixes[lastIndex]); --lastIndex) {}
        System.arraycopy(prefixes, 0, this.prefixes = new String[lastIndex + 2], 0, this.prefixes.length);
        this.destinationType = destinationType;
    }
    
    @Override
    public boolean matches(final ActiveMQDestination destination) {
        if (destination.getDestinationType() != this.destinationType) {
            return false;
        }
        final String[] path = DestinationPath.getDestinationPaths(destination.getPhysicalName());
        final int length = this.prefixes.length;
        if (path.length >= length) {
            for (int size = length - 1, i = 0; i < size; ++i) {
                if (!this.matches(this.prefixes[i], path[i])) {
                    return false;
                }
            }
            return true;
        }
        boolean match = true;
        for (int i = 0; i < path.length && match; match = this.matches(this.prefixes[i], path[i]), ++i) {}
        return match && this.prefixes.length == path.length + 1;
    }
    
    private boolean matches(final String prefix, final String path) {
        return path.equals("*") || prefix.equals("*") || prefix.equals(path);
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
