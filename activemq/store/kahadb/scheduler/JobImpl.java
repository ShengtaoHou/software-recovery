// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.scheduler;

import org.apache.activemq.broker.scheduler.JobSupport;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.broker.scheduler.Job;

public class JobImpl implements Job
{
    private final JobLocation jobLocation;
    private final byte[] payload;
    
    protected JobImpl(final JobLocation location, final ByteSequence bs) {
        this.jobLocation = location;
        this.payload = new byte[bs.getLength()];
        System.arraycopy(bs.getData(), bs.getOffset(), this.payload, 0, bs.getLength());
    }
    
    @Override
    public String getJobId() {
        return this.jobLocation.getJobId();
    }
    
    @Override
    public byte[] getPayload() {
        return this.payload;
    }
    
    @Override
    public long getPeriod() {
        return this.jobLocation.getPeriod();
    }
    
    @Override
    public int getRepeat() {
        return this.jobLocation.getRepeat();
    }
    
    @Override
    public long getStart() {
        return this.jobLocation.getStartTime();
    }
    
    @Override
    public long getDelay() {
        return this.jobLocation.getDelay();
    }
    
    @Override
    public String getCronEntry() {
        return this.jobLocation.getCronEntry();
    }
    
    @Override
    public String getNextExecutionTime() {
        return JobSupport.getDateTime(this.jobLocation.getNextTime());
    }
    
    @Override
    public String getStartTime() {
        return JobSupport.getDateTime(this.getStart());
    }
}
