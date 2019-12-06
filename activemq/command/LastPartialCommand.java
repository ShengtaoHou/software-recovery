// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import org.apache.activemq.state.CommandVisitor;

public class LastPartialCommand extends PartialCommand
{
    public static final byte DATA_STRUCTURE_TYPE = 61;
    
    @Override
    public byte getDataStructureType() {
        return 61;
    }
    
    @Override
    public Response visit(final CommandVisitor visitor) throws Exception {
        throw new IllegalStateException("The transport layer should filter out LastPartialCommand instances but received: " + this);
    }
    
    public void configure(final Command completeCommand) {
        completeCommand.setFrom(this.getFrom());
    }
}
