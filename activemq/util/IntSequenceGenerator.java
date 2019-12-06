// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

public class IntSequenceGenerator
{
    private int lastSequenceId;
    
    public synchronized int getNextSequenceId() {
        return ++this.lastSequenceId;
    }
    
    public synchronized int getLastSequenceId() {
        return this.lastSequenceId;
    }
    
    public synchronized void setLastSequenceId(final int l) {
        this.lastSequenceId = l;
    }
}
