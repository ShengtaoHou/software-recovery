// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import org.apache.activemq.state.CommandVisitor;

public class Response extends BaseCommand
{
    public static final byte DATA_STRUCTURE_TYPE = 30;
    int correlationId;
    
    @Override
    public byte getDataStructureType() {
        return 30;
    }
    
    public int getCorrelationId() {
        return this.correlationId;
    }
    
    public void setCorrelationId(final int responseId) {
        this.correlationId = responseId;
    }
    
    @Override
    public boolean isResponse() {
        return true;
    }
    
    public boolean isException() {
        return false;
    }
    
    @Override
    public Response visit(final CommandVisitor visitor) throws Exception {
        return null;
    }
}
