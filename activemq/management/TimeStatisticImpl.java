// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.management;

public class TimeStatisticImpl extends StatisticImpl
{
    private long count;
    private long maxTime;
    private long minTime;
    private long totalTime;
    private TimeStatisticImpl parent;
    
    public TimeStatisticImpl(final String name, final String description) {
        this(name, "millis", description);
    }
    
    public TimeStatisticImpl(final TimeStatisticImpl parent, final String name, final String description) {
        this(name, description);
        this.parent = parent;
    }
    
    public TimeStatisticImpl(final String name, final String unit, final String description) {
        super(name, unit, description);
    }
    
    @Override
    public synchronized void reset() {
        if (this.isDoReset()) {
            super.reset();
            this.count = 0L;
            this.maxTime = 0L;
            this.minTime = 0L;
            this.totalTime = 0L;
        }
    }
    
    public synchronized long getCount() {
        return this.count;
    }
    
    public synchronized void addTime(final long time) {
        ++this.count;
        this.totalTime += time;
        if (time > this.maxTime) {
            this.maxTime = time;
        }
        if (time < this.minTime || this.minTime == 0L) {
            this.minTime = time;
        }
        this.updateSampleTime();
        if (this.parent != null) {
            this.parent.addTime(time);
        }
    }
    
    public long getMaxTime() {
        return this.maxTime;
    }
    
    public synchronized long getMinTime() {
        return this.minTime;
    }
    
    public synchronized long getTotalTime() {
        return this.totalTime;
    }
    
    public synchronized double getAverageTime() {
        if (this.count == 0L) {
            return 0.0;
        }
        final double d = (double)this.totalTime;
        return d / this.count;
    }
    
    public synchronized double getAverageTimeExcludingMinMax() {
        if (this.count <= 2L) {
            return 0.0;
        }
        final double d = (double)(this.totalTime - this.minTime - this.maxTime);
        return d / (this.count - 2L);
    }
    
    public double getAveragePerSecond() {
        final double d = 1000.0;
        final double averageTime = this.getAverageTime();
        if (averageTime == 0.0) {
            return 0.0;
        }
        return d / averageTime;
    }
    
    public double getAveragePerSecondExcludingMinMax() {
        final double d = 1000.0;
        final double average = this.getAverageTimeExcludingMinMax();
        if (average == 0.0) {
            return 0.0;
        }
        return d / average;
    }
    
    public TimeStatisticImpl getParent() {
        return this.parent;
    }
    
    public void setParent(final TimeStatisticImpl parent) {
        this.parent = parent;
    }
    
    @Override
    protected synchronized void appendFieldDescription(final StringBuffer buffer) {
        buffer.append(" count: ");
        buffer.append(Long.toString(this.count));
        buffer.append(" maxTime: ");
        buffer.append(Long.toString(this.maxTime));
        buffer.append(" minTime: ");
        buffer.append(Long.toString(this.minTime));
        buffer.append(" totalTime: ");
        buffer.append(Long.toString(this.totalTime));
        buffer.append(" averageTime: ");
        buffer.append(Double.toString(this.getAverageTime()));
        buffer.append(" averageTimeExMinMax: ");
        buffer.append(Double.toString(this.getAverageTimeExcludingMinMax()));
        buffer.append(" averagePerSecond: ");
        buffer.append(Double.toString(this.getAveragePerSecond()));
        buffer.append(" averagePerSecondExMinMax: ");
        buffer.append(Double.toString(this.getAveragePerSecondExcludingMinMax()));
        super.appendFieldDescription(buffer);
    }
}
