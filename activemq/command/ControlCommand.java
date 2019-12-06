// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import org.apache.activemq.state.CommandVisitor;

public class ControlCommand extends BaseCommand
{
    public static final byte DATA_STRUCTURE_TYPE = 14;
    private String command;
    
    @Override
    public byte getDataStructureType() {
        return 14;
    }
    
    public String getCommand() {
        return this.command;
    }
    
    public void setCommand(final String command) {
        this.command = command;
    }
    
    @Override
    public Response visit(final CommandVisitor visitor) throws Exception {
        return visitor.processControlCommand(this);
    }
}
