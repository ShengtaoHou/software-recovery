// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import javax.management.openmbean.TabularData;

public interface JobSchedulerViewMBean
{
    @MBeanInfo("remove jobs with matching execution time")
    void removeJobAtScheduledTime(@MBeanInfo("time: yyyy-MM-dd hh:mm:ss") final String p0) throws Exception;
    
    @MBeanInfo("remove jobs with matching jobId")
    void removeJob(@MBeanInfo("jobId") final String p0) throws Exception;
    
    @MBeanInfo("remove all scheduled jobs")
    void removeAllJobs() throws Exception;
    
    @MBeanInfo("remove all scheduled jobs between time ranges ")
    void removeAllJobs(@MBeanInfo("start: yyyy-MM-dd hh:mm:ss") final String p0, @MBeanInfo("finish: yyyy-MM-dd hh:mm:ss") final String p1) throws Exception;
    
    @MBeanInfo("get the next time a job is due to be scheduled ")
    String getNextScheduleTime() throws Exception;
    
    @MBeanInfo("get the next job(s) to be scheduled. Not HTML friendly ")
    TabularData getNextScheduleJobs() throws Exception;
    
    @MBeanInfo("get the scheduled Jobs in the Store. Not HTML friendly ")
    TabularData getAllJobs() throws Exception;
    
    @MBeanInfo("get the scheduled Jobs in the Store within the time range. Not HTML friendly ")
    TabularData getAllJobs(@MBeanInfo("start: yyyy-MM-dd hh:mm:ss") final String p0, @MBeanInfo("finish: yyyy-MM-dd hh:mm:ss") final String p1) throws Exception;
}
