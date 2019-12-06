// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter;

import org.apache.activemq.command.ActiveMQDestination;

public class AnyDestination extends ActiveMQDestination
{
    public AnyDestination(final ActiveMQDestination[] destinations) {
        super(destinations);
        this.physicalName = "0";
    }
    
    @Override
    protected String getQualifiedPrefix() {
        return "Any://";
    }
    
    @Override
    public byte getDestinationType() {
        return 0;
    }
    
    @Override
    public byte getDataStructureType() {
        throw new IllegalStateException("not for marshalling");
    }
    
    @Override
    public boolean isQueue() {
        return true;
    }
    
    @Override
    public boolean isTopic() {
        return true;
    }
}
