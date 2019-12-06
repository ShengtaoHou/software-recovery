// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import java.util.Map;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.CompositeData;

public interface QueueViewMBean extends DestinationViewMBean
{
    @MBeanInfo("View a message from the destination by JMS message ID.")
    CompositeData getMessage(@MBeanInfo("messageId") final String p0) throws OpenDataException;
    
    @MBeanInfo("Remove a message from the destination by JMS message ID.  If the message has been dispatched, it cannot be deleted and false is returned.")
    boolean removeMessage(@MBeanInfo("messageId") final String p0) throws Exception;
    
    @MBeanInfo("Removes messages from the destination based on an SQL-92 selection on the message headers or XPATH on the body.")
    int removeMatchingMessages(@MBeanInfo("selector") final String p0) throws Exception;
    
    @MBeanInfo("Removes up to a specified number of messages from the destination based on an SQL-92 selection on the message headers or XPATH on the body.")
    int removeMatchingMessages(@MBeanInfo("selector") final String p0, @MBeanInfo("maximumMessages") final int p1) throws Exception;
    
    @MBeanInfo("Removes all of the messages in the queue.")
    void purge() throws Exception;
    
    @MBeanInfo("Copies a message with the given JMS message ID into the specified destination.")
    boolean copyMessageTo(@MBeanInfo("messageId") final String p0, @MBeanInfo("destinationName") final String p1) throws Exception;
    
    @MBeanInfo("Copies messages based on an SQL-92 selecton on the message headers or XPATH on the body into the specified destination.")
    int copyMatchingMessagesTo(@MBeanInfo("selector") final String p0, @MBeanInfo("destinationName") final String p1) throws Exception;
    
    @MBeanInfo("Copies up to a specified number of messages based on an SQL-92 selecton on the message headers or XPATH on the body into the specified destination.")
    int copyMatchingMessagesTo(@MBeanInfo("selector") final String p0, @MBeanInfo("destinationName") final String p1, @MBeanInfo("maximumMessages") final int p2) throws Exception;
    
    @MBeanInfo("Moves a message with the given JMS message ID into the specified destination.")
    boolean moveMessageTo(@MBeanInfo("messageId") final String p0, @MBeanInfo("destinationName") final String p1) throws Exception;
    
    @MBeanInfo("Moves a message with the given JMS message back to its original destination")
    boolean retryMessage(@MBeanInfo("messageId") final String p0) throws Exception;
    
    @MBeanInfo("Moves messages based on an SQL-92 selecton on the message headers or XPATH on the body into the specified destination.")
    int moveMatchingMessagesTo(@MBeanInfo("selector") final String p0, @MBeanInfo("destinationName") final String p1) throws Exception;
    
    @MBeanInfo("Moves up to a specified number of messages based on an SQL-92 selecton on the message headers or XPATH on the body into the specified destination.")
    int moveMatchingMessagesTo(@MBeanInfo("selector") final String p0, @MBeanInfo("destinationName") final String p1, @MBeanInfo("maximumMessages") final int p2) throws Exception;
    
    @MBeanInfo("Retries messages sent to the DLQ")
    int retryMessages() throws Exception;
    
    @MBeanInfo("Message cursor has memory space available")
    boolean doesCursorHaveSpace();
    
    @MBeanInfo("Message cusor has reached its memory limit for paged in messages")
    boolean isCursorFull();
    
    @MBeanInfo("Message cursor has buffered messages to deliver")
    boolean doesCursorHaveMessagesBuffered();
    
    @MBeanInfo("Message cursor memory usage, in bytes.")
    long getCursorMemoryUsage();
    
    @MBeanInfo("Percentage of memory limit used")
    int getCursorPercentUsage();
    
    @MBeanInfo("Number of messages available to be paged in by the cursor.")
    int cursorSize();
    
    @MBeanInfo("Caching is enabled")
    boolean isCacheEnabled();
    
    @MBeanInfo("Map of groupNames and ConsumerIds")
    Map<String, String> getMessageGroups();
    
    @MBeanInfo("group  implementation (simple,bucket,cached)")
    String getMessageGroupType();
    
    @MBeanInfo("remove a message group by its groupName")
    void removeMessageGroup(@MBeanInfo("groupName") final String p0);
    
    @MBeanInfo("emove all the message groups - will rebalance all message groups across consumers")
    void removeAllMessageGroups();
}
