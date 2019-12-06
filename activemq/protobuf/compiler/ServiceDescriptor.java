// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf.compiler;

import java.util.ArrayList;
import java.util.List;

public class ServiceDescriptor
{
    private final ProtoDescriptor protoDescriptor;
    private List<MethodDescriptor> methods;
    private String name;
    
    public ServiceDescriptor(final ProtoDescriptor protoDescriptor) {
        this.methods = new ArrayList<MethodDescriptor>();
        this.protoDescriptor = protoDescriptor;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setMethods(final List<MethodDescriptor> methods) {
        this.methods = methods;
    }
    
    public ProtoDescriptor getProtoDescriptor() {
        return this.protoDescriptor;
    }
    
    public List<MethodDescriptor> getMethods() {
        return this.methods;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void validate(final List<String> errors) {
    }
}
