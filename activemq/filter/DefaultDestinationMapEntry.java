// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter;

public class DefaultDestinationMapEntry extends DestinationMapEntry
{
    private DestinationMapEntry value;
    
    @Override
    public DestinationMapEntry getValue() {
        return this.value;
    }
    
    public void setValue(final DestinationMapEntry value) {
        this.value = value;
    }
}
