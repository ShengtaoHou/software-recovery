// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated;

import javax.management.openmbean.CompositeData;
import org.apache.activemq.broker.jmx.MBeanInfo;

public interface ReplicatedLevelDBStoreViewMBean
{
    @MBeanInfo("The address of the ZooKeeper server.")
    String getZkAddress();
    
    @MBeanInfo("The path in ZooKeeper to hold master elections.")
    String getZkPath();
    
    @MBeanInfo("The ZooKeeper session timeout.")
    String getZkSessionTmeout();
    
    @MBeanInfo("The address and port the master will bind for the replication protocol.")
    String getBind();
    
    @MBeanInfo("The number of replication nodes that will be part of the replication cluster.")
    int getReplicas();
    
    @MBeanInfo("The role of this node in the replication cluster.")
    String getNodeRole();
    
    @MBeanInfo("The replication status.")
    String getStatus();
    
    @MBeanInfo("The status of the connected slaves.")
    CompositeData[] getSlaves();
    
    @MBeanInfo("The current position of the replication log.")
    Long getPosition();
    
    @MBeanInfo("When the last entry was added to the replication log.")
    Long getPositionDate();
    
    @MBeanInfo("The directory holding the data.")
    String getDirectory();
    
    @MBeanInfo("The sync strategy to use.")
    String getSync();
    
    @MBeanInfo("The node id of this replication node.")
    String getNodeId();
}
