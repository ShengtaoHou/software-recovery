// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

public class ExceptionResponse extends Response
{
    public static final byte DATA_STRUCTURE_TYPE = 31;
    Throwable exception;
    
    public ExceptionResponse() {
    }
    
    public ExceptionResponse(final Throwable e) {
        this.setException(e);
    }
    
    @Override
    public byte getDataStructureType() {
        return 31;
    }
    
    public Throwable getException() {
        return this.exception;
    }
    
    public void setException(final Throwable exception) {
        this.exception = exception;
    }
    
    @Override
    public boolean isException() {
        return true;
    }
}
