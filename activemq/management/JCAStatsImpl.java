// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.management;

public class JCAStatsImpl extends StatsImpl
{
    private JCAConnectionStatsImpl[] connectionStats;
    private JCAConnectionPoolStatsImpl[] connectionPoolStats;
    
    public JCAStatsImpl(final JCAConnectionStatsImpl[] connectionStats, final JCAConnectionPoolStatsImpl[] connectionPoolStats) {
        this.connectionStats = connectionStats;
        this.connectionPoolStats = connectionPoolStats;
    }
    
    public JCAConnectionStatsImpl[] getConnections() {
        return this.connectionStats;
    }
    
    public JCAConnectionPoolStatsImpl[] getConnectionPools() {
        return this.connectionPoolStats;
    }
}
