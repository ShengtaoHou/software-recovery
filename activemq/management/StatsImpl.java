// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.management;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import javax.management.j2ee.statistics.Statistic;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.Set;
import javax.management.j2ee.statistics.Stats;

public class StatsImpl extends StatisticImpl implements Stats, Resettable
{
    private Set<StatisticImpl> set;
    
    public StatsImpl() {
        this(new CopyOnWriteArraySet<StatisticImpl>());
    }
    
    public StatsImpl(final Set<StatisticImpl> set) {
        super("stats", "many", "Used only as container, not Statistic");
        this.set = set;
    }
    
    @Override
    public void reset() {
        for (final Statistic stat : this.getStatistics()) {
            if (stat instanceof Resettable) {
                final Resettable r = (Resettable)stat;
                r.reset();
            }
        }
    }
    
    @Override
    public Statistic getStatistic(final String name) {
        for (final StatisticImpl stat : this.set) {
            if (stat.getName() != null && stat.getName().equals(name)) {
                return stat;
            }
        }
        return null;
    }
    
    @Override
    public String[] getStatisticNames() {
        final List<String> names = new ArrayList<String>();
        for (final StatisticImpl stat : this.set) {
            names.add(stat.getName());
        }
        final String[] answer = new String[names.size()];
        names.toArray(answer);
        return answer;
    }
    
    @Override
    public Statistic[] getStatistics() {
        final Statistic[] answer = new Statistic[this.set.size()];
        this.set.toArray(answer);
        return answer;
    }
    
    protected void addStatistic(final String name, final StatisticImpl statistic) {
        this.set.add(statistic);
    }
}
