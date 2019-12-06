// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.scheduler;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import org.apache.activemq.store.kahadb.disk.util.VariableMarshaller;
import java.util.Date;
import java.io.DataOutput;
import java.io.IOException;
import java.io.DataInput;
import org.apache.activemq.store.kahadb.disk.journal.Location;

class JobLocation
{
    private String jobId;
    private int repeat;
    private long startTime;
    private long delay;
    private long nextTime;
    private long period;
    private String cronEntry;
    private final Location location;
    
    public JobLocation(final Location location) {
        this.location = location;
    }
    
    public JobLocation() {
        this(new Location());
    }
    
    public void readExternal(final DataInput in) throws IOException {
        this.jobId = in.readUTF();
        this.repeat = in.readInt();
        this.startTime = in.readLong();
        this.delay = in.readLong();
        this.nextTime = in.readLong();
        this.period = in.readLong();
        this.cronEntry = in.readUTF();
        this.location.readExternal(in);
    }
    
    public void writeExternal(final DataOutput out) throws IOException {
        out.writeUTF(this.jobId);
        out.writeInt(this.repeat);
        out.writeLong(this.startTime);
        out.writeLong(this.delay);
        out.writeLong(this.nextTime);
        out.writeLong(this.period);
        if (this.cronEntry == null) {
            this.cronEntry = "";
        }
        out.writeUTF(this.cronEntry);
        this.location.writeExternal(out);
    }
    
    public String getJobId() {
        return this.jobId;
    }
    
    public void setJobId(final String jobId) {
        this.jobId = jobId;
    }
    
    public int getRepeat() {
        return this.repeat;
    }
    
    public void setRepeat(final int repeat) {
        this.repeat = repeat;
    }
    
    public long getStartTime() {
        return this.startTime;
    }
    
    public void setStartTime(final long start) {
        this.startTime = start;
    }
    
    public synchronized long getNextTime() {
        return this.nextTime;
    }
    
    public synchronized void setNextTime(final long nextTime) {
        this.nextTime = nextTime;
    }
    
    public long getPeriod() {
        return this.period;
    }
    
    public void setPeriod(final long period) {
        this.period = period;
    }
    
    public synchronized String getCronEntry() {
        return this.cronEntry;
    }
    
    public synchronized void setCronEntry(final String cronEntry) {
        this.cronEntry = cronEntry;
    }
    
    public boolean isCron() {
        return this.getCronEntry() != null && this.getCronEntry().length() > 0;
    }
    
    public long getDelay() {
        return this.delay;
    }
    
    public void setDelay(final long delay) {
        this.delay = delay;
    }
    
    public Location getLocation() {
        return this.location;
    }
    
    @Override
    public String toString() {
        return "Job [id=" + this.jobId + ", startTime=" + new Date(this.startTime) + ", delay=" + this.delay + ", period=" + this.period + ", repeat=" + this.repeat + ", nextTime=" + new Date(this.nextTime) + "]";
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.cronEntry == null) ? 0 : this.cronEntry.hashCode());
        result = 31 * result + (int)(this.delay ^ this.delay >>> 32);
        result = 31 * result + ((this.jobId == null) ? 0 : this.jobId.hashCode());
        result = 31 * result + ((this.location == null) ? 0 : this.location.hashCode());
        result = 31 * result + (int)(this.nextTime ^ this.nextTime >>> 32);
        result = 31 * result + (int)(this.period ^ this.period >>> 32);
        result = 31 * result + this.repeat;
        result = 31 * result + (int)(this.startTime ^ this.startTime >>> 32);
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final JobLocation other = (JobLocation)obj;
        if (this.cronEntry == null) {
            if (other.cronEntry != null) {
                return false;
            }
        }
        else if (!this.cronEntry.equals(other.cronEntry)) {
            return false;
        }
        if (this.delay != other.delay) {
            return false;
        }
        if (this.jobId == null) {
            if (other.jobId != null) {
                return false;
            }
        }
        else if (!this.jobId.equals(other.jobId)) {
            return false;
        }
        if (this.location == null) {
            if (other.location != null) {
                return false;
            }
        }
        else if (!this.location.equals(other.location)) {
            return false;
        }
        return this.nextTime == other.nextTime && this.period == other.period && this.repeat == other.repeat && this.startTime == other.startTime;
    }
    
    static class JobLocationMarshaller extends VariableMarshaller<List<JobLocation>>
    {
        static final JobLocationMarshaller INSTANCE;
        
        @Override
        public List<JobLocation> readPayload(final DataInput dataIn) throws IOException {
            final List<JobLocation> result = new ArrayList<JobLocation>();
            for (int size = dataIn.readInt(), i = 0; i < size; ++i) {
                final JobLocation jobLocation = new JobLocation();
                jobLocation.readExternal(dataIn);
                result.add(jobLocation);
            }
            return result;
        }
        
        @Override
        public void writePayload(final List<JobLocation> value, final DataOutput dataOut) throws IOException {
            dataOut.writeInt(value.size());
            for (final JobLocation jobLocation : value) {
                jobLocation.writeExternal(dataOut);
            }
        }
        
        static {
            INSTANCE = new JobLocationMarshaller();
        }
    }
}
