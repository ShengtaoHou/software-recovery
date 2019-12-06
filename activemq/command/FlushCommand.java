// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import org.apache.activemq.state.CommandVisitor;

public class FlushCommand extends BaseCommand
{
    public static final byte DATA_STRUCTURE_TYPE = 15;
    public static final Command COMMAND;
    
    @Override
    public byte getDataStructureType() {
        return 15;
    }
    
    @Override
    public Response visit(final CommandVisitor visitor) throws Exception {
        return visitor.processFlush(this);
    }
    
    static {
        COMMAND = new FlushCommand();
    }
}
