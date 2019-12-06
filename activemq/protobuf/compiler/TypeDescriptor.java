// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf.compiler;

public interface TypeDescriptor
{
    String getName();
    
    String getQName();
    
    ProtoDescriptor getProtoDescriptor();
    
    boolean isEnum();
    
    void associate(final EnumFieldDescriptor p0);
}
