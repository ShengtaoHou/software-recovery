// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import java.util.List;
import javax.management.openmbean.TabularData;

public interface HealthViewMBean
{
    TabularData health() throws Exception;
    
    @MBeanInfo("List of warnings and errors about the current health of the Broker - empty list is Good!")
    List<HealthStatus> healthList() throws Exception;
    
    @MBeanInfo("String representation of current Broker state")
    String getCurrentStatus();
}
