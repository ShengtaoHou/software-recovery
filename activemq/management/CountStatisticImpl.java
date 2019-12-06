// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.management;

import java.util.concurrent.atomic.AtomicLong;
import javax.management.j2ee.statistics.CountStatistic;

public class CountStatisticImpl extends StatisticImpl implements CountStatistic
{
    private final AtomicLong counter;
    private CountStatisticImpl parent;
    
    public CountStatisticImpl(final CountStatisticImpl parent, final String name, final String description) {
        this(name, description);
        this.parent = parent;
    }
    
    public CountStatisticImpl(final String name, final String description) {
        this(name, "count", description);
    }
    
    public CountStatisticImpl(final String name, final String unit, final String description) {
        super(name, unit, description);
        this.counter = new AtomicLong(0L);
    }
    
    @Override
    public void reset() {
        if (this.isDoReset()) {
            super.reset();
            this.counter.set(0L);
        }
    }
    
    @Override
    public long getCount() {
        return this.counter.get();
    }
    
    public void setCount(final long count) {
        if (this.isEnabled()) {
            this.counter.set(count);
        }
    }
    
    public void add(final long amount) {
        if (this.isEnabled()) {
            this.counter.addAndGet(amount);
            this.updateSampleTime();
            if (this.parent != null) {
                this.parent.add(amount);
            }
        }
    }
    
    public void increment() {
        if (this.isEnabled()) {
            this.counter.incrementAndGet();
            this.updateSampleTime();
            if (this.parent != null) {
                this.parent.increment();
            }
        }
    }
    
    public void subtract(final long amount) {
        if (this.isEnabled()) {
            this.counter.addAndGet(-amount);
            this.updateSampleTime();
            if (this.parent != null) {
                this.parent.subtract(amount);
            }
        }
    }
    
    public void decrement() {
        if (this.isEnabled()) {
            this.counter.decrementAndGet();
            this.updateSampleTime();
            if (this.parent != null) {
                this.parent.decrement();
            }
        }
    }
    
    public CountStatisticImpl getParent() {
        return this.parent;
    }
    
    public void setParent(final CountStatisticImpl parent) {
        if (this.parent != null) {
            this.parent.subtract(this.getCount());
        }
        this.parent = parent;
    }
    
    @Override
    protected void appendFieldDescription(final StringBuffer buffer) {
        buffer.append(" count: ");
        buffer.append(Long.toString(this.counter.get()));
        super.appendFieldDescription(buffer);
    }
    
    public double getPeriod() {
        final double count = (double)this.counter.get();
        if (count == 0.0) {
            return 0.0;
        }
        final double time = (double)(System.currentTimeMillis() - this.getStartTime());
        return time / (count * 1000.0);
    }
    
    public double getFrequency() {
        final double count = (double)this.counter.get();
        final double time = (double)(System.currentTimeMillis() - this.getStartTime());
        return count * 1000.0 / time;
    }
}
