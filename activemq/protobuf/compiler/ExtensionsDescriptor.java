// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf.compiler;

public class ExtensionsDescriptor
{
    private int first;
    private int last;
    private final MessageDescriptor parent;
    
    public ExtensionsDescriptor(final MessageDescriptor parent) {
        this.parent = parent;
    }
    
    public void setFirst(final int first) {
        this.first = first;
    }
    
    public void setLast(final int last) {
        this.last = last;
    }
    
    public int getFirst() {
        return this.first;
    }
    
    public int getLast() {
        return this.last;
    }
    
    public MessageDescriptor getParent() {
        return this.parent;
    }
}
