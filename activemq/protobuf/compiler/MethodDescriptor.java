// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf.compiler;

public class MethodDescriptor
{
    private final ProtoDescriptor protoDescriptor;
    private String name;
    private String parameter;
    private String returns;
    
    public MethodDescriptor(final ProtoDescriptor protoDescriptor) {
        this.protoDescriptor = protoDescriptor;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setParameter(final String parameter) {
        this.parameter = parameter;
    }
    
    public void setReturns(final String returns) {
        this.returns = returns;
    }
    
    public ProtoDescriptor getProtoDescriptor() {
        return this.protoDescriptor;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getParameter() {
        return this.parameter;
    }
    
    public String getReturns() {
        return this.returns;
    }
}
