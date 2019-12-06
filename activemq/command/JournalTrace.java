// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import org.apache.activemq.util.IntrospectionSupport;

public class JournalTrace implements DataStructure
{
    public static final byte DATA_STRUCTURE_TYPE = 53;
    private String message;
    
    public JournalTrace() {
    }
    
    public JournalTrace(final String message) {
        this.message = message;
    }
    
    @Override
    public byte getDataStructureType() {
        return 53;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(final String message) {
        this.message = message;
    }
    
    @Override
    public boolean isMarshallAware() {
        return false;
    }
    
    @Override
    public String toString() {
        return IntrospectionSupport.toString(this, JournalTrace.class);
    }
}
