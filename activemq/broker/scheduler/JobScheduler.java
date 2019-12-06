// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.scheduler;

import java.util.List;
import org.apache.activemq.util.ByteSequence;

public interface JobScheduler
{
    String getName() throws Exception;
    
    void startDispatching() throws Exception;
    
    void stopDispatching() throws Exception;
    
    void addListener(final JobListener p0) throws Exception;
    
    void removeListener(final JobListener p0) throws Exception;
    
    void schedule(final String p0, final ByteSequence p1, final long p2) throws Exception;
    
    void schedule(final String p0, final ByteSequence p1, final String p2) throws Exception;
    
    void schedule(final String p0, final ByteSequence p1, final String p2, final long p3, final long p4, final int p5) throws Exception;
    
    void remove(final long p0) throws Exception;
    
    void remove(final String p0) throws Exception;
    
    void removeAllJobs() throws Exception;
    
    void removeAllJobs(final long p0, final long p1) throws Exception;
    
    long getNextScheduleTime() throws Exception;
    
    List<Job> getNextScheduleJobs() throws Exception;
    
    List<Job> getAllJobs() throws Exception;
    
    List<Job> getAllJobs(final long p0, final long p1) throws Exception;
}
