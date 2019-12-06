// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import javax.management.openmbean.TabularData;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.CompositeData;

public interface DurableSubscriptionViewMBean extends SubscriptionViewMBean
{
    String getSubscriptionName();
    
    CompositeData[] browse() throws OpenDataException;
    
    TabularData browseAsTable() throws OpenDataException;
    
    void destroy() throws Exception;
    
    boolean doesCursorHaveSpace();
    
    boolean isCursorFull();
    
    boolean doesCursorHaveMessagesBuffered();
    
    long getCursorMemoryUsage();
    
    int getCursorPercentUsage();
    
    int cursorSize();
}
