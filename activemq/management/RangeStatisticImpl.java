// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.management;

public class RangeStatisticImpl extends StatisticImpl
{
    private long highWaterMark;
    private long lowWaterMark;
    private long current;
    
    public RangeStatisticImpl(final String name, final String unit, final String description) {
        super(name, unit, description);
    }
    
    @Override
    public void reset() {
        if (this.isDoReset()) {
            super.reset();
            this.current = 0L;
            this.lowWaterMark = 0L;
            this.highWaterMark = 0L;
        }
    }
    
    public long getHighWaterMark() {
        return this.highWaterMark;
    }
    
    public long getLowWaterMark() {
        return this.lowWaterMark;
    }
    
    public long getCurrent() {
        return this.current;
    }
    
    public void setCurrent(final long current) {
        this.current = current;
        if (current > this.highWaterMark) {
            this.highWaterMark = current;
        }
        if (current < this.lowWaterMark || this.lowWaterMark == 0L) {
            this.lowWaterMark = current;
        }
        this.updateSampleTime();
    }
    
    @Override
    protected void appendFieldDescription(final StringBuffer buffer) {
        buffer.append(" current: ");
        buffer.append(Long.toString(this.current));
        buffer.append(" lowWaterMark: ");
        buffer.append(Long.toString(this.lowWaterMark));
        buffer.append(" highWaterMark: ");
        buffer.append(Long.toString(this.highWaterMark));
        super.appendFieldDescription(buffer);
    }
}
