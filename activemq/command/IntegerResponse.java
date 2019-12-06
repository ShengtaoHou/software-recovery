// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

public class IntegerResponse extends Response
{
    public static final byte DATA_STRUCTURE_TYPE = 34;
    int result;
    
    public IntegerResponse() {
    }
    
    public IntegerResponse(final int result) {
        this.result = result;
    }
    
    @Override
    public byte getDataStructureType() {
        return 34;
    }
    
    public int getResult() {
        return this.result;
    }
    
    public void setResult(final int result) {
        this.result = result;
    }
}
