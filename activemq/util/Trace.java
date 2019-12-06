// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

class Trace extends Throwable
{
    public int count;
    public final long id;
    
    Trace() {
        this.count = 1;
        this.id = this.calculateIdentifier();
    }
    
    private long calculateIdentifier() {
        int len = 0;
        for (int i = 0; i < this.getStackTrace().length; ++i) {
            len += this.getStackTrace()[i].toString().intern().hashCode();
        }
        return len;
    }
}
