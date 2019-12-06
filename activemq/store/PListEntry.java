// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store;

import org.apache.activemq.util.ByteSequence;

public class PListEntry
{
    private final ByteSequence byteSequence;
    private final String entry;
    private final Object locator;
    
    public PListEntry(final String entry, final ByteSequence bs, final Object locator) {
        this.entry = entry;
        this.byteSequence = bs;
        this.locator = locator;
    }
    
    public ByteSequence getByteSequence() {
        return this.byteSequence;
    }
    
    public String getId() {
        return this.entry;
    }
    
    public Object getLocator() {
        return this.locator;
    }
    
    public PListEntry copy() {
        return new PListEntry(this.entry, this.byteSequence, this.locator);
    }
}
