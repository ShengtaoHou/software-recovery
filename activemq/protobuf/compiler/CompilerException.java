// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf.compiler;

import java.util.List;

public class CompilerException extends Exception
{
    private final List<String> errors;
    
    public CompilerException(final List<String> errors) {
        this.errors = errors;
    }
    
    public List<String> getErrors() {
        return this.errors;
    }
}
