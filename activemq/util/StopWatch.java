// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

public final class StopWatch
{
    private long start;
    private long stop;
    
    public StopWatch() {
        this(true);
    }
    
    public StopWatch(final boolean started) {
        if (started) {
            this.restart();
        }
    }
    
    public void restart() {
        this.start = System.currentTimeMillis();
        this.stop = 0L;
    }
    
    public long stop() {
        this.stop = System.currentTimeMillis();
        return this.taken();
    }
    
    public long taken() {
        if (this.start > 0L && this.stop > 0L) {
            return this.stop - this.start;
        }
        if (this.start > 0L) {
            return System.currentTimeMillis() - this.start;
        }
        return 0L;
    }
}
