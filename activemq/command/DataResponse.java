// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

public class DataResponse extends Response
{
    public static final byte DATA_STRUCTURE_TYPE = 32;
    DataStructure data;
    
    public DataResponse() {
    }
    
    public DataResponse(final DataStructure data) {
        this.data = data;
    }
    
    @Override
    public byte getDataStructureType() {
        return 32;
    }
    
    public DataStructure getData() {
        return this.data;
    }
    
    public void setData(final DataStructure data) {
        this.data = data;
    }
}
