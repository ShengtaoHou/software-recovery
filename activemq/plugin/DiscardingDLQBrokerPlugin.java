// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.plugin;

import org.slf4j.LoggerFactory;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.apache.activemq.broker.Broker;
import org.slf4j.Logger;
import org.apache.activemq.broker.BrokerPlugin;

public class DiscardingDLQBrokerPlugin implements BrokerPlugin
{
    public static Logger log;
    private boolean dropTemporaryTopics;
    private boolean dropTemporaryQueues;
    private boolean dropAll;
    private String dropOnly;
    private int reportInterval;
    
    public DiscardingDLQBrokerPlugin() {
        this.dropTemporaryTopics = true;
        this.dropTemporaryQueues = true;
        this.dropAll = true;
        this.reportInterval = 1000;
    }
    
    @Override
    public Broker installPlugin(final Broker broker) throws Exception {
        DiscardingDLQBrokerPlugin.log.info("Installing Discarding Dead Letter Queue broker plugin[dropAll={}; dropTemporaryTopics={}; dropTemporaryQueues={}; dropOnly={}; reportInterval={}]", this.isDropAll(), this.isDropTemporaryTopics(), this.isDropTemporaryQueues(), this.getDropOnly(), this.reportInterval);
        final DiscardingDLQBroker cb = new DiscardingDLQBroker(broker);
        cb.setDropAll(this.isDropAll());
        cb.setDropTemporaryQueues(this.isDropTemporaryQueues());
        cb.setDropTemporaryTopics(this.isDropTemporaryTopics());
        cb.setDestFilter(this.getDestFilter());
        cb.setReportInterval(this.getReportInterval());
        return cb;
    }
    
    public boolean isDropAll() {
        return this.dropAll;
    }
    
    public boolean isDropTemporaryQueues() {
        return this.dropTemporaryQueues;
    }
    
    public boolean isDropTemporaryTopics() {
        return this.dropTemporaryTopics;
    }
    
    public String getDropOnly() {
        return this.dropOnly;
    }
    
    public int getReportInterval() {
        return this.reportInterval;
    }
    
    public void setDropTemporaryTopics(final boolean dropTemporaryTopics) {
        this.dropTemporaryTopics = dropTemporaryTopics;
    }
    
    public void setDropTemporaryQueues(final boolean dropTemporaryQueues) {
        this.dropTemporaryQueues = dropTemporaryQueues;
    }
    
    public void setDropAll(final boolean dropAll) {
        this.dropAll = dropAll;
    }
    
    public void setDropOnly(final String dropOnly) {
        this.dropOnly = dropOnly;
    }
    
    public void setReportInterval(final int reportInterval) {
        this.reportInterval = reportInterval;
    }
    
    public Pattern[] getDestFilter() {
        if (this.getDropOnly() == null) {
            return null;
        }
        final ArrayList<Pattern> list = new ArrayList<Pattern>();
        final StringTokenizer t = new StringTokenizer(this.getDropOnly(), " ");
        while (t.hasMoreTokens()) {
            final String s = t.nextToken();
            if (s != null && s.trim().length() > 0) {
                list.add(Pattern.compile(s));
            }
        }
        if (list.size() == 0) {
            return null;
        }
        return list.toArray(new Pattern[0]);
    }
    
    static {
        DiscardingDLQBrokerPlugin.log = LoggerFactory.getLogger(DiscardingDLQBrokerPlugin.class);
    }
}
