// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.management;

public class SizeStatisticImpl extends StatisticImpl
{
    private long count;
    private long maxSize;
    private long minSize;
    private long totalSize;
    private SizeStatisticImpl parent;
    
    public SizeStatisticImpl(final String name, final String description) {
        this(name, "bytes", description);
    }
    
    public SizeStatisticImpl(final SizeStatisticImpl parent, final String name, final String description) {
        this(name, description);
        this.parent = parent;
    }
    
    public SizeStatisticImpl(final String name, final String unit, final String description) {
        super(name, unit, description);
    }
    
    @Override
    public synchronized void reset() {
        if (this.isDoReset()) {
            super.reset();
            this.count = 0L;
            this.maxSize = 0L;
            this.minSize = 0L;
            this.totalSize = 0L;
        }
    }
    
    public synchronized long getCount() {
        return this.count;
    }
    
    public synchronized void addSize(final long size) {
        ++this.count;
        this.totalSize += size;
        if (size > this.maxSize) {
            this.maxSize = size;
        }
        if (size < this.minSize || this.minSize == 0L) {
            this.minSize = size;
        }
        this.updateSampleTime();
        if (this.parent != null) {
            this.parent.addSize(size);
        }
    }
    
    public long getMaxSize() {
        return this.maxSize;
    }
    
    public synchronized long getMinSize() {
        return this.minSize;
    }
    
    public synchronized long getTotalSize() {
        return this.totalSize;
    }
    
    public synchronized double getAverageSize() {
        if (this.count == 0L) {
            return 0.0;
        }
        final double d = (double)this.totalSize;
        return d / this.count;
    }
    
    public synchronized double getAverageSizeExcludingMinMax() {
        if (this.count <= 2L) {
            return 0.0;
        }
        final double d = (double)(this.totalSize - this.minSize - this.maxSize);
        return d / (this.count - 2L);
    }
    
    public double getAveragePerSecond() {
        final double d = 1000.0;
        final double averageSize = this.getAverageSize();
        if (averageSize == 0.0) {
            return 0.0;
        }
        return d / averageSize;
    }
    
    public double getAveragePerSecondExcludingMinMax() {
        final double d = 1000.0;
        final double average = this.getAverageSizeExcludingMinMax();
        if (average == 0.0) {
            return 0.0;
        }
        return d / average;
    }
    
    public SizeStatisticImpl getParent() {
        return this.parent;
    }
    
    public void setParent(final SizeStatisticImpl parent) {
        this.parent = parent;
    }
    
    @Override
    protected synchronized void appendFieldDescription(final StringBuffer buffer) {
        buffer.append(" count: ");
        buffer.append(Long.toString(this.count));
        buffer.append(" maxSize: ");
        buffer.append(Long.toString(this.maxSize));
        buffer.append(" minSize: ");
        buffer.append(Long.toString(this.minSize));
        buffer.append(" totalSize: ");
        buffer.append(Long.toString(this.totalSize));
        buffer.append(" averageSize: ");
        buffer.append(Double.toString(this.getAverageSize()));
        buffer.append(" averageTimeExMinMax: ");
        buffer.append(Double.toString(this.getAveragePerSecondExcludingMinMax()));
        buffer.append(" averagePerSecond: ");
        buffer.append(Double.toString(this.getAveragePerSecond()));
        buffer.append(" averagePerSecondExMinMax: ");
        buffer.append(Double.toString(this.getAveragePerSecondExcludingMinMax()));
        super.appendFieldDescription(buffer);
    }
}
