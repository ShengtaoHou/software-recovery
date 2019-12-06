// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.management;

public class BoundaryStatisticImpl extends StatisticImpl
{
    private long lowerBound;
    private long upperBound;
    
    public BoundaryStatisticImpl(final String name, final String unit, final String description, final long lowerBound, final long upperBound) {
        super(name, unit, description);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }
    
    public long getLowerBound() {
        return this.lowerBound;
    }
    
    public long getUpperBound() {
        return this.upperBound;
    }
    
    @Override
    protected void appendFieldDescription(final StringBuffer buffer) {
        buffer.append(" lowerBound: ");
        buffer.append(Long.toString(this.lowerBound));
        buffer.append(" upperBound: ");
        buffer.append(Long.toString(this.upperBound));
        super.appendFieldDescription(buffer);
    }
}
