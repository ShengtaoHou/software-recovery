// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf.compiler;

public class EnumFieldDescriptor
{
    private String name;
    private int value;
    private final EnumDescriptor parent;
    private TypeDescriptor associatedType;
    
    public EnumFieldDescriptor(final EnumDescriptor parent) {
        this.parent = parent;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setValue(final int value) {
        this.value = value;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public EnumDescriptor getParent() {
        return this.parent;
    }
    
    public TypeDescriptor getAssociatedType() {
        return this.associatedType;
    }
    
    public void associate(final TypeDescriptor associatedType) {
        this.associatedType = associatedType;
    }
}
