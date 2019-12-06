// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.util;

public class Sequence extends LinkedNode<Sequence>
{
    long first;
    long last;
    
    public Sequence(final long value) {
        this.last = value;
        this.first = value;
    }
    
    public Sequence(final long first, final long last) {
        this.first = first;
        this.last = last;
    }
    
    public boolean isAdjacentToLast(final long value) {
        return this.last + 1L == value;
    }
    
    public boolean isAdjacentToFirst(final long value) {
        return this.first - 1L == value;
    }
    
    public boolean contains(final long value) {
        return this.first <= value && value <= this.last;
    }
    
    public long range() {
        return (this.first == this.last) ? 1L : (this.last - this.first + 1L);
    }
    
    @Override
    public String toString() {
        return (this.first == this.last) ? ("" + this.first) : (this.first + ".." + this.last);
    }
    
    public long getFirst() {
        return this.first;
    }
    
    public void setFirst(final long first) {
        this.first = first;
    }
    
    public long getLast() {
        return this.last;
    }
    
    public void setLast(final long last) {
        this.last = last;
    }
    
    public <T extends Throwable> void each(final Closure<T> closure) throws T, Throwable {
        for (long i = this.first; i <= this.last; ++i) {
            closure.execute(i);
        }
    }
    
    public interface Closure<T extends Throwable>
    {
        void execute(final long p0) throws T, Throwable;
    }
}
