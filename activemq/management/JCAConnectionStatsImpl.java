// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.management;

public class JCAConnectionStatsImpl extends StatsImpl
{
    private String connectionFactory;
    private String managedConnectionFactory;
    private TimeStatisticImpl waitTime;
    private TimeStatisticImpl useTime;
    
    public JCAConnectionStatsImpl(final String connectionFactory, final String managedConnectionFactory, final TimeStatisticImpl waitTime, final TimeStatisticImpl useTime) {
        this.connectionFactory = connectionFactory;
        this.managedConnectionFactory = managedConnectionFactory;
        this.waitTime = waitTime;
        this.useTime = useTime;
        this.addStatistic("waitTime", waitTime);
        this.addStatistic("useTime", useTime);
    }
    
    public String getConnectionFactory() {
        return this.connectionFactory;
    }
    
    public String getManagedConnectionFactory() {
        return this.managedConnectionFactory;
    }
    
    public TimeStatisticImpl getWaitTime() {
        return this.waitTime;
    }
    
    public TimeStatisticImpl getUseTime() {
        return this.useTime;
    }
}
