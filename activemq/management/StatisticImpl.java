// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.management;

import javax.management.j2ee.statistics.Statistic;

public class StatisticImpl implements Statistic, Resettable
{
    protected boolean enabled;
    private String name;
    private String unit;
    private String description;
    private long startTime;
    private long lastSampleTime;
    private boolean doReset;
    
    public StatisticImpl(final String name, final String unit, final String description) {
        this.doReset = true;
        this.name = name;
        this.unit = unit;
        this.description = description;
        this.startTime = System.currentTimeMillis();
        this.lastSampleTime = this.startTime;
    }
    
    @Override
    public synchronized void reset() {
        if (this.isDoReset()) {
            this.startTime = System.currentTimeMillis();
            this.lastSampleTime = this.startTime;
        }
    }
    
    protected synchronized void updateSampleTime() {
        this.lastSampleTime = System.currentTimeMillis();
    }
    
    @Override
    public synchronized String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(this.name);
        buffer.append("{");
        this.appendFieldDescription(buffer);
        buffer.append(" }");
        return buffer.toString();
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String getUnit() {
        return this.unit;
    }
    
    @Override
    public String getDescription() {
        return this.description;
    }
    
    @Override
    public synchronized long getStartTime() {
        return this.startTime;
    }
    
    @Override
    public synchronized long getLastSampleTime() {
        return this.lastSampleTime;
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isDoReset() {
        return this.doReset;
    }
    
    public void setDoReset(final boolean doReset) {
        this.doReset = doReset;
    }
    
    protected synchronized void appendFieldDescription(final StringBuffer buffer) {
        buffer.append(" unit: ");
        buffer.append(this.unit);
        buffer.append(" startTime: ");
        buffer.append(this.startTime);
        buffer.append(" lastSampleTime: ");
        buffer.append(this.lastSampleTime);
        buffer.append(" description: ");
        buffer.append(this.description);
    }
}
