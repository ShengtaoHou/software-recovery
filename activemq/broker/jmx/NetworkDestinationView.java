// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import org.slf4j.LoggerFactory;
import org.apache.activemq.management.TimeStatisticImpl;
import org.slf4j.Logger;

public class NetworkDestinationView implements NetworkDestinationViewMBean
{
    private static final Logger LOG;
    private TimeStatisticImpl timeStatistic;
    private final String name;
    private final NetworkBridgeView networkBridgeView;
    private long lastTime;
    
    public NetworkDestinationView(final NetworkBridgeView networkBridgeView, final String name) {
        this.timeStatistic = new TimeStatisticImpl("networkEnqueue", "network messages enqueued");
        this.lastTime = -1L;
        this.networkBridgeView = networkBridgeView;
        this.name = name;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public void resetStats() {
        this.timeStatistic.reset();
        this.lastTime = -1L;
    }
    
    @Override
    public long getCount() {
        return this.timeStatistic.getCount();
    }
    
    @Override
    public double getRate() {
        return this.timeStatistic.getAveragePerSecond();
    }
    
    public void messageSent() {
        final long currentTime = System.currentTimeMillis();
        long time = 0L;
        if (this.lastTime < 0L) {
            time = 0L;
            this.lastTime = currentTime;
        }
        else {
            time = currentTime - this.lastTime;
        }
        this.timeStatistic.addTime(time);
        this.lastTime = currentTime;
    }
    
    public long getLastAccessTime() {
        return this.timeStatistic.getLastSampleTime();
    }
    
    public void close() {
        this.networkBridgeView.removeNetworkDestinationView(this);
    }
    
    static {
        LOG = LoggerFactory.getLogger(NetworkDestinationViewMBean.class);
    }
}
