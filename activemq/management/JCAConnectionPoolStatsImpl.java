// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.management;

public class JCAConnectionPoolStatsImpl extends JCAConnectionStatsImpl
{
    private CountStatisticImpl closeCount;
    private CountStatisticImpl createCount;
    private BoundedRangeStatisticImpl freePoolSize;
    private BoundedRangeStatisticImpl poolSize;
    private RangeStatisticImpl waitingThreadCount;
    
    public JCAConnectionPoolStatsImpl(final String connectionFactory, final String managedConnectionFactory, final TimeStatisticImpl waitTime, final TimeStatisticImpl useTime, final CountStatisticImpl closeCount, final CountStatisticImpl createCount, final BoundedRangeStatisticImpl freePoolSize, final BoundedRangeStatisticImpl poolSize, final RangeStatisticImpl waitingThreadCount) {
        super(connectionFactory, managedConnectionFactory, waitTime, useTime);
        this.closeCount = closeCount;
        this.createCount = createCount;
        this.freePoolSize = freePoolSize;
        this.poolSize = poolSize;
        this.waitingThreadCount = waitingThreadCount;
        this.addStatistic("freePoolSize", freePoolSize);
        this.addStatistic("poolSize", poolSize);
        this.addStatistic("waitingThreadCount", waitingThreadCount);
    }
    
    public CountStatisticImpl getCloseCount() {
        return this.closeCount;
    }
    
    public CountStatisticImpl getCreateCount() {
        return this.createCount;
    }
    
    public BoundedRangeStatisticImpl getFreePoolSize() {
        return this.freePoolSize;
    }
    
    public BoundedRangeStatisticImpl getPoolSize() {
        return this.poolSize;
    }
    
    public RangeStatisticImpl getWaitingThreadCount() {
        return this.waitingThreadCount;
    }
}
