// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

public interface DataStructure
{
    byte getDataStructureType();
    
    boolean isMarshallAware();
}
