// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.scheduler;

import org.apache.activemq.util.ByteSequence;
import java.util.Collections;
import java.util.List;

public class JobSchedulerFacade implements JobScheduler
{
    private final SchedulerBroker broker;
    
    JobSchedulerFacade(final SchedulerBroker broker) {
        this.broker = broker;
    }
    
    @Override
    public void addListener(final JobListener l) throws Exception {
        final JobScheduler js = this.broker.getInternalScheduler();
        if (js != null) {
            js.addListener(l);
        }
    }
    
    @Override
    public List<Job> getAllJobs() throws Exception {
        final JobScheduler js = this.broker.getInternalScheduler();
        if (js != null) {
            return js.getAllJobs();
        }
        return Collections.emptyList();
    }
    
    @Override
    public List<Job> getAllJobs(final long start, final long finish) throws Exception {
        final JobScheduler js = this.broker.getInternalScheduler();
        if (js != null) {
            return js.getAllJobs(start, finish);
        }
        return Collections.emptyList();
    }
    
    @Override
    public String getName() throws Exception {
        final JobScheduler js = this.broker.getInternalScheduler();
        if (js != null) {
            return js.getName();
        }
        return "";
    }
    
    @Override
    public List<Job> getNextScheduleJobs() throws Exception {
        final JobScheduler js = this.broker.getInternalScheduler();
        if (js != null) {
            return js.getNextScheduleJobs();
        }
        return Collections.emptyList();
    }
    
    @Override
    public long getNextScheduleTime() throws Exception {
        final JobScheduler js = this.broker.getInternalScheduler();
        if (js != null) {
            return js.getNextScheduleTime();
        }
        return 0L;
    }
    
    @Override
    public void remove(final long time) throws Exception {
        final JobScheduler js = this.broker.getInternalScheduler();
        if (js != null) {
            js.remove(time);
        }
    }
    
    @Override
    public void remove(final String jobId) throws Exception {
        final JobScheduler js = this.broker.getInternalScheduler();
        if (js != null) {
            js.remove(jobId);
        }
    }
    
    @Override
    public void removeAllJobs() throws Exception {
        final JobScheduler js = this.broker.getInternalScheduler();
        if (js != null) {
            js.removeAllJobs();
        }
    }
    
    @Override
    public void removeAllJobs(final long start, final long finish) throws Exception {
        final JobScheduler js = this.broker.getInternalScheduler();
        if (js != null) {
            js.removeAllJobs(start, finish);
        }
    }
    
    @Override
    public void removeListener(final JobListener l) throws Exception {
        final JobScheduler js = this.broker.getInternalScheduler();
        if (js != null) {
            js.removeListener(l);
        }
    }
    
    @Override
    public void schedule(final String jobId, final ByteSequence payload, final long delay) throws Exception {
        final JobScheduler js = this.broker.getInternalScheduler();
        if (js != null) {
            js.schedule(jobId, payload, delay);
        }
    }
    
    @Override
    public void schedule(final String jobId, final ByteSequence payload, final String cronEntry, final long start, final long period, final int repeat) throws Exception {
        final JobScheduler js = this.broker.getInternalScheduler();
        if (js != null) {
            js.schedule(jobId, payload, cronEntry, start, period, repeat);
        }
    }
    
    @Override
    public void schedule(final String jobId, final ByteSequence payload, final String cronEntry) throws Exception {
        final JobScheduler js = this.broker.getInternalScheduler();
        if (js != null) {
            js.schedule(jobId, payload, cronEntry);
        }
    }
    
    @Override
    public void startDispatching() throws Exception {
        final JobScheduler js = this.broker.getInternalScheduler();
        if (js != null) {
            js.startDispatching();
        }
    }
    
    @Override
    public void stopDispatching() throws Exception {
        final JobScheduler js = this.broker.getInternalScheduler();
        if (js != null) {
            js.stopDispatching();
        }
    }
}
