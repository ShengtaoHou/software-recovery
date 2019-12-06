// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.scheduler;

import java.io.File;
import org.apache.activemq.Service;

public interface JobSchedulerStore extends Service
{
    File getDirectory();
    
    void setDirectory(final File p0);
    
    long size();
    
    JobScheduler getJobScheduler(final String p0) throws Exception;
    
    boolean removeJobScheduler(final String p0) throws Exception;
}
