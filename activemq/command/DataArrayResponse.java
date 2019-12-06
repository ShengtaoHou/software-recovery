// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

public class DataArrayResponse extends Response
{
    public static final byte DATA_STRUCTURE_TYPE = 33;
    DataStructure[] data;
    
    public DataArrayResponse() {
    }
    
    public DataArrayResponse(final DataStructure[] data) {
        this.data = data;
    }
    
    @Override
    public byte getDataStructureType() {
        return 33;
    }
    
    public DataStructure[] getData() {
        return this.data;
    }
    
    public void setData(final DataStructure[] data) {
        this.data = data;
    }
}
