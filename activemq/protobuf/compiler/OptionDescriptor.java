// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf.compiler;

import java.util.List;

public class OptionDescriptor
{
    private String name;
    private String value;
    
    public String getName() {
        return this.name;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
    
    public void validate(final List<String> errors) {
    }
}
