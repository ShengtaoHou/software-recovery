// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import java.io.IOException;
import org.apache.activemq.broker.scheduler.JobSupport;
import java.util.Iterator;
import java.util.List;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.CompositeData;
import java.util.Map;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;
import org.apache.activemq.broker.scheduler.Job;
import javax.management.openmbean.TabularData;
import org.apache.activemq.broker.scheduler.JobScheduler;

public class JobSchedulerView implements JobSchedulerViewMBean
{
    private final JobScheduler jobScheduler;
    
    public JobSchedulerView(final JobScheduler jobScheduler) {
        this.jobScheduler = jobScheduler;
    }
    
    @Override
    public TabularData getAllJobs() throws Exception {
        final OpenTypeSupport.OpenTypeFactory factory = OpenTypeSupport.getFactory(Job.class);
        final CompositeType ct = factory.getCompositeType();
        final TabularType tt = new TabularType("Scheduled Jobs", "Scheduled Jobs", ct, new String[] { "jobId" });
        final TabularDataSupport rc = new TabularDataSupport(tt);
        final List<Job> jobs = this.jobScheduler.getAllJobs();
        for (final Job job : jobs) {
            rc.put(new CompositeDataSupport(ct, factory.getFields(job)));
        }
        return rc;
    }
    
    @Override
    public TabularData getAllJobs(final String startTime, final String finishTime) throws Exception {
        final OpenTypeSupport.OpenTypeFactory factory = OpenTypeSupport.getFactory(Job.class);
        final CompositeType ct = factory.getCompositeType();
        final TabularType tt = new TabularType("Scheduled Jobs", "Scheduled Jobs", ct, new String[] { "jobId" });
        final TabularDataSupport rc = new TabularDataSupport(tt);
        final long start = JobSupport.getDataTime(startTime);
        final long finish = JobSupport.getDataTime(finishTime);
        final List<Job> jobs = this.jobScheduler.getAllJobs(start, finish);
        for (final Job job : jobs) {
            rc.put(new CompositeDataSupport(ct, factory.getFields(job)));
        }
        return rc;
    }
    
    @Override
    public TabularData getNextScheduleJobs() throws Exception {
        final OpenTypeSupport.OpenTypeFactory factory = OpenTypeSupport.getFactory(Job.class);
        final CompositeType ct = factory.getCompositeType();
        final TabularType tt = new TabularType("Scheduled Jobs", "Scheduled Jobs", ct, new String[] { "jobId" });
        final TabularDataSupport rc = new TabularDataSupport(tt);
        final List<Job> jobs = this.jobScheduler.getNextScheduleJobs();
        for (final Job job : jobs) {
            rc.put(new CompositeDataSupport(ct, factory.getFields(job)));
        }
        return rc;
    }
    
    @Override
    public String getNextScheduleTime() throws Exception {
        final long time = this.jobScheduler.getNextScheduleTime();
        return JobSupport.getDateTime(time);
    }
    
    @Override
    public void removeAllJobs() throws Exception {
        this.jobScheduler.removeAllJobs();
    }
    
    @Override
    public void removeAllJobs(final String startTime, final String finishTime) throws Exception {
        final long start = JobSupport.getDataTime(startTime);
        final long finish = JobSupport.getDataTime(finishTime);
        this.jobScheduler.removeAllJobs(start, finish);
    }
    
    @Override
    public void removeJob(final String jobId) throws Exception {
        this.jobScheduler.remove(jobId);
    }
    
    @Override
    public void removeJobAtScheduledTime(final String time) throws IOException {
    }
}
