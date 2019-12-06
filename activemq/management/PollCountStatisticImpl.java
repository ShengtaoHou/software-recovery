// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.management;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import javax.management.j2ee.statistics.CountStatistic;

public class PollCountStatisticImpl extends StatisticImpl implements CountStatistic
{
    private PollCountStatisticImpl parent;
    private List<PollCountStatisticImpl> children;
    
    public PollCountStatisticImpl(final PollCountStatisticImpl parent, final String name, final String description) {
        this(name, description);
        this.setParent(parent);
    }
    
    public PollCountStatisticImpl(final String name, final String description) {
        this(name, "count", description);
    }
    
    public PollCountStatisticImpl(final String name, final String unit, final String description) {
        super(name, unit, description);
    }
    
    public PollCountStatisticImpl getParent() {
        return this.parent;
    }
    
    public void setParent(final PollCountStatisticImpl parent) {
        if (this.parent != null) {
            this.parent.removeChild(this);
        }
        this.parent = parent;
        if (this.parent != null) {
            this.parent.addChild(this);
        }
    }
    
    private synchronized void removeChild(final PollCountStatisticImpl child) {
        if (this.children != null) {
            this.children.remove(child);
        }
    }
    
    private synchronized void addChild(final PollCountStatisticImpl child) {
        if (this.children == null) {
            this.children = new ArrayList<PollCountStatisticImpl>();
        }
        this.children.add(child);
    }
    
    @Override
    public synchronized long getCount() {
        if (this.children == null) {
            return 0L;
        }
        long count = 0L;
        for (final PollCountStatisticImpl child : this.children) {
            count += child.getCount();
        }
        return count;
    }
    
    @Override
    protected void appendFieldDescription(final StringBuffer buffer) {
        buffer.append(" count: ");
        buffer.append(Long.toString(this.getCount()));
        super.appendFieldDescription(buffer);
    }
    
    public double getPeriod() {
        final double count = (double)this.getCount();
        if (count == 0.0) {
            return 0.0;
        }
        final double time = (double)(System.currentTimeMillis() - this.getStartTime());
        return time / (count * 1000.0);
    }
    
    public double getFrequency() {
        final double count = (double)this.getCount();
        final double time = (double)(System.currentTimeMillis() - this.getStartTime());
        return count * 1000.0 / time;
    }
}
