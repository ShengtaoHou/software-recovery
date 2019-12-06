// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import javax.management.ObjectName;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.TabularData;

public interface AbortSlowConsumerStrategyViewMBean
{
    @MBeanInfo("returns the current max slow count, -1 disables")
    long getMaxSlowCount();
    
    @MBeanInfo("sets the count after which a slow consumer will be aborted, -1 disables")
    void setMaxSlowCount(final long p0);
    
    @MBeanInfo("returns the current max slow (milliseconds) duration")
    long getMaxSlowDuration();
    
    @MBeanInfo("sets the duration (milliseconds) after which a continually slow consumer will be aborted")
    void setMaxSlowDuration(final long p0);
    
    @MBeanInfo("returns the check period at which a sweep of consumers is done to determine continued slowness")
    long getCheckPeriod();
    
    @MBeanInfo("returns the current list of slow consumers, Not HTML friendly")
    TabularData getSlowConsumers() throws OpenDataException;
    
    @MBeanInfo("aborts the slow consumer gracefully by sending a shutdown control message to just that consumer")
    void abortConsumer(final ObjectName p0);
    
    @MBeanInfo("aborts the slow consumer forcefully by shutting down it's connection, note: all other users of the connection will be affected")
    void abortConnection(final ObjectName p0);
    
    @MBeanInfo("aborts the slow consumer gracefully by sending a shutdown control message to just that consumer")
    void abortConsumer(final String p0);
    
    @MBeanInfo("aborts the slow consumer forcefully by shutting down it's connection, note: all other users of the connection will be affected")
    void abortConnection(final String p0);
}
