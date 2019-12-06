// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.advisory;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.DestinationInfo;
import java.util.EventObject;

public class DestinationEvent extends EventObject
{
    private static final long serialVersionUID = 2442156576867593780L;
    private DestinationInfo destinationInfo;
    
    public DestinationEvent(final DestinationSource source, final DestinationInfo destinationInfo) {
        super(source);
        this.destinationInfo = destinationInfo;
    }
    
    public ActiveMQDestination getDestination() {
        return this.getDestinationInfo().getDestination();
    }
    
    public boolean isAddOperation() {
        return this.getDestinationInfo().isAddOperation();
    }
    
    public long getTimeout() {
        return this.getDestinationInfo().getTimeout();
    }
    
    public boolean isRemoveOperation() {
        return this.getDestinationInfo().isRemoveOperation();
    }
    
    public DestinationInfo getDestinationInfo() {
        return this.destinationInfo;
    }
}
