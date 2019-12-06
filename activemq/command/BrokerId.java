// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

public class BrokerId implements DataStructure
{
    public static final byte DATA_STRUCTURE_TYPE = 124;
    protected String value;
    
    public BrokerId() {
    }
    
    public BrokerId(final String brokerId) {
        this.value = brokerId;
    }
    
    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || o.getClass() != BrokerId.class) {
            return false;
        }
        final BrokerId id = (BrokerId)o;
        return this.value.equals(id.value);
    }
    
    @Override
    public byte getDataStructureType() {
        return 124;
    }
    
    @Override
    public String toString() {
        return this.value;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public void setValue(final String brokerId) {
        this.value = brokerId;
    }
    
    @Override
    public boolean isMarshallAware() {
        return false;
    }
}
