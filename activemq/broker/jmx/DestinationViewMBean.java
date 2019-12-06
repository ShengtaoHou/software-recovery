// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import javax.management.MalformedObjectNameException;
import java.io.IOException;
import javax.management.ObjectName;
import java.util.List;
import java.util.Map;
import javax.jms.InvalidSelectorException;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.CompositeData;

public interface DestinationViewMBean
{
    @MBeanInfo("Name of this destination.")
    String getName();
    
    @MBeanInfo("Resets statistics.")
    void resetStatistics();
    
    @MBeanInfo("Number of messages that have been sent to the destination.")
    long getEnqueueCount();
    
    @MBeanInfo("Number of messages that have been delivered (but potentially not acknowledged) to consumers.")
    long getDispatchCount();
    
    @MBeanInfo("Number of messages that have been acknowledged (and removed from) from the destination.")
    long getDequeueCount();
    
    @MBeanInfo("Number of messages that have been dispatched to, but not acknowledged by, consumers.")
    long getInFlightCount();
    
    @MBeanInfo("Number of messages that have been expired.")
    long getExpiredCount();
    
    @MBeanInfo("Number of consumers subscribed to this destination.")
    long getConsumerCount();
    
    @MBeanInfo("Number of producers publishing to this destination")
    long getProducerCount();
    
    @MBeanInfo("Number of messages in the destination which are yet to be consumed.  Potentially dispatched but unacknowledged.")
    long getQueueSize();
    
    @MBeanInfo("An array of all messages in the destination. Not HTML friendly.")
    CompositeData[] browse() throws OpenDataException;
    
    @MBeanInfo("A list of all messages in the destination. Not HTML friendly.")
    TabularData browseAsTable() throws OpenDataException;
    
    @MBeanInfo("An array of all messages in the destination based on an SQL-92 selection on the message headers or XPATH on the body. Not HTML friendly.")
    CompositeData[] browse(@MBeanInfo("selector") final String p0) throws OpenDataException, InvalidSelectorException;
    
    @MBeanInfo("A list of all messages in the destination based on an SQL-92 selection on the message headers or XPATH on the body. Not HTML friendly.")
    TabularData browseAsTable(@MBeanInfo("selector") final String p0) throws OpenDataException, InvalidSelectorException;
    
    @MBeanInfo("Sends a TextMessage to the destination.")
    String sendTextMessage(@MBeanInfo("body") final String p0) throws Exception;
    
    @MBeanInfo("Sends a TextMessage to the destination.")
    String sendTextMessageWithProperties(final String p0) throws Exception;
    
    @MBeanInfo("Sends a TextMessage to the destination.")
    String sendTextMessage(@MBeanInfo("headers") final Map<?, ?> p0, @MBeanInfo("body") final String p1) throws Exception;
    
    @MBeanInfo("Sends a TextMessage to a password-protected destination.")
    String sendTextMessage(@MBeanInfo("body") final String p0, @MBeanInfo("user") final String p1, @MBeanInfo("password") final String p2) throws Exception;
    
    @MBeanInfo("Sends a TextMessage to a password-protected destination.")
    String sendTextMessage(@MBeanInfo("headers") final Map<String, String> p0, @MBeanInfo("body") final String p1, @MBeanInfo("user") final String p2, @MBeanInfo("password") final String p3) throws Exception;
    
    @MBeanInfo("The percentage of the memory limit used")
    int getMemoryPercentUsage();
    
    @MBeanInfo("Memory usage, in bytes, used by undelivered messages")
    long getMemoryUsageByteCount();
    
    @MBeanInfo("Memory limit, in bytes, used for holding undelivered messages before paging to temporary storage.")
    long getMemoryLimit();
    
    void setMemoryLimit(final long p0);
    
    @MBeanInfo("Portion of memory from the broker memory limit for this destination")
    float getMemoryUsagePortion();
    
    void setMemoryUsagePortion(@MBeanInfo("bytes") final float p0);
    
    @MBeanInfo("A list of all messages in the destination. Not HTML friendly.")
    List<?> browseMessages() throws InvalidSelectorException;
    
    @MBeanInfo("A list of all messages in the destination based on an SQL-92 selection on the message headers or XPATH on the body. Not HTML friendly.")
    List<?> browseMessages(final String p0) throws InvalidSelectorException;
    
    @MBeanInfo("The longest time a message has been held this destination.")
    long getMaxEnqueueTime();
    
    @MBeanInfo("The shortest time a message has been held this destination.")
    long getMinEnqueueTime();
    
    @MBeanInfo("Average time a message has been held this destination.")
    double getAverageEnqueueTime();
    
    @MBeanInfo("Average message size on this destination")
    double getAverageMessageSize();
    
    @MBeanInfo("Max message size on this destination")
    long getMaxMessageSize();
    
    @MBeanInfo("Min message size on this destination")
    long getMinMessageSize();
    
    @MBeanInfo("Producers are flow controlled")
    boolean isProducerFlowControl();
    
    void setProducerFlowControl(@MBeanInfo("producerFlowControl") final boolean p0);
    
    @MBeanInfo("Always treat consumers as retroActive")
    boolean isAlwaysRetroactive();
    
    void setAlwaysRetroactive(@MBeanInfo("alwaysRetroactive") final boolean p0);
    
    void setBlockedProducerWarningInterval(@MBeanInfo("blockedProducerWarningInterval") final long p0);
    
    @MBeanInfo("Blocked Producer Warning Interval")
    long getBlockedProducerWarningInterval();
    
    @MBeanInfo("Maximum number of producers to audit")
    int getMaxProducersToAudit();
    
    void setMaxProducersToAudit(@MBeanInfo("maxProducersToAudit") final int p0);
    
    @MBeanInfo("Max audit depth")
    int getMaxAuditDepth();
    
    void setMaxAuditDepth(@MBeanInfo("maxAuditDepth") final int p0);
    
    @MBeanInfo("Maximum number of messages to be paged in")
    int getMaxPageSize();
    
    void setMaxPageSize(@MBeanInfo("pageSize") final int p0);
    
    @MBeanInfo("Caching is allowed")
    boolean isUseCache();
    
    @MBeanInfo("Prioritized messages is enabled")
    boolean isPrioritizedMessages();
    
    void setUseCache(@MBeanInfo("cache") final boolean p0);
    
    @MBeanInfo("returns all the current subscription MBeans matching this destination")
    ObjectName[] getSubscriptions() throws IOException, MalformedObjectNameException;
    
    @MBeanInfo("returns the optional slowConsumer handler MBeans for this destination")
    ObjectName getSlowConsumerStrategy() throws IOException, MalformedObjectNameException;
    
    @MBeanInfo("returns the destination options, name value pairs as URL queryString")
    String getOptions();
    
    @MBeanInfo("Dead Letter Queue")
    boolean isDLQ();
    
    @MBeanInfo("Get number of messages blocked for Flow Control")
    long getBlockedSends();
    
    @MBeanInfo("get the average time (ms) a message is blocked for Flow Control")
    double getAverageBlockedTime();
    
    @MBeanInfo("Get the total time (ms) messages are blocked for Flow Control")
    long getTotalBlockedTime();
}
