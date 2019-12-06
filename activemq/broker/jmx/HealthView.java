// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import org.apache.activemq.broker.scheduler.JobSchedulerStore;
import org.apache.activemq.usage.SystemUsage;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.broker.BrokerService;
import java.io.File;
import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.CompositeData;
import java.util.Map;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;
import javax.management.openmbean.TabularData;

public class HealthView implements HealthViewMBean
{
    ManagedRegionBroker broker;
    String currentState;
    
    public HealthView(final ManagedRegionBroker broker) {
        this.currentState = "Good";
        this.broker = broker;
    }
    
    @Override
    public TabularData health() throws Exception {
        final OpenTypeSupport.OpenTypeFactory factory = OpenTypeSupport.getFactory(HealthStatus.class);
        final CompositeType ct = factory.getCompositeType();
        final TabularType tt = new TabularType("HealthStatus", "HealthStatus", ct, new String[] { "healthId", "level", "message", "resource" });
        final TabularDataSupport rc = new TabularDataSupport(tt);
        final List<HealthStatus> list = this.healthList();
        for (final HealthStatus healthStatus : list) {
            rc.put(new CompositeDataSupport(ct, factory.getFields(healthStatus)));
        }
        return rc;
    }
    
    @Override
    public List<HealthStatus> healthList() throws Exception {
        final List<HealthStatus> answer = new ArrayList<HealthStatus>();
        final Map<ObjectName, DestinationView> queueViews = this.broker.getQueueViews();
        for (final Map.Entry<ObjectName, DestinationView> entry : queueViews.entrySet()) {
            final DestinationView queue = entry.getValue();
            if (queue.getConsumerCount() == 0L && queue.getProducerCount() > 0L) {
                final ObjectName key = entry.getKey();
                final String message = "Queue " + queue.getName() + " has no consumers";
                answer.add(new HealthStatus("org.apache.activemq.noConsumer", "WARNING", message, key.toString()));
            }
        }
        final BrokerService brokerService = this.broker.getBrokerService();
        if (brokerService != null && brokerService.getPersistenceAdapter() != null) {
            final PersistenceAdapter adapter = brokerService.getPersistenceAdapter();
            File dir = adapter.getDirectory();
            if (brokerService.isPersistent()) {
                final SystemUsage usage = brokerService.getSystemUsage();
                if (dir != null && usage != null) {
                    final String dirPath = dir.getAbsolutePath();
                    if (!dir.isAbsolute()) {
                        dir = new File(dirPath);
                    }
                    while (dir != null && !dir.isDirectory()) {
                        dir = dir.getParentFile();
                    }
                    final long storeSize = adapter.size();
                    final long storeLimit = usage.getStoreUsage().getLimit();
                    final long dirFreeSpace = dir.getUsableSpace();
                    if (storeSize != 0L && storeLimit != 0L) {
                        final int val = (int)(storeSize * 100L / storeLimit);
                        if (val > 90) {
                            answer.add(new HealthStatus("org.apache.activemq.StoreLimit", "WARNING", "Message Store size is within " + val + "% of its limit", adapter.toString()));
                        }
                    }
                    if (storeLimit - storeSize > dirFreeSpace) {
                        final String message2 = "Store limit is " + storeLimit / 1048576L + " mb, whilst the data directory: " + dir.getAbsolutePath() + " only has " + dirFreeSpace / 1048576L + " mb of usable space";
                        answer.add(new HealthStatus("org.apache.activemq.FreeDiskSpaceLeft", "WARNING", message2, adapter.toString()));
                    }
                }
                File tmpDir = brokerService.getTmpDataDirectory();
                if (tmpDir != null) {
                    final String tmpDirPath = tmpDir.getAbsolutePath();
                    if (!tmpDir.isAbsolute()) {
                        tmpDir = new File(tmpDirPath);
                    }
                    final long storeSize2 = usage.getTempUsage().getUsage();
                    final long storeLimit2 = usage.getTempUsage().getLimit();
                    while (tmpDir != null && !tmpDir.isDirectory()) {
                        tmpDir = tmpDir.getParentFile();
                    }
                    if (storeLimit2 != 0L) {
                        final int val2 = (int)(storeSize2 * 100L / storeLimit2);
                        if (val2 > 90) {
                            answer.add(new HealthStatus("org.apache.activemq.TempStoreLimit", "WARNING", "TempMessage Store size is within " + val2 + "% of its limit", adapter.toString()));
                        }
                    }
                }
            }
        }
        if (brokerService != null && brokerService.getJobSchedulerStore() != null) {
            final JobSchedulerStore scheduler = brokerService.getJobSchedulerStore();
            File dir = scheduler.getDirectory();
            if (brokerService.isPersistent()) {
                final SystemUsage usage = brokerService.getSystemUsage();
                if (dir != null && usage != null) {
                    final String dirPath = dir.getAbsolutePath();
                    if (!dir.isAbsolute()) {
                        dir = new File(dirPath);
                    }
                    while (dir != null && !dir.isDirectory()) {
                        dir = dir.getParentFile();
                    }
                    final long storeSize = scheduler.size();
                    final long storeLimit = usage.getJobSchedulerUsage().getLimit();
                    final long dirFreeSpace = dir.getUsableSpace();
                    if (storeSize != 0L && storeLimit != 0L) {
                        final int val = (int)(storeSize * 100L / storeLimit);
                        if (val > 90) {
                            answer.add(new HealthStatus("org.apache.activemq.JobSchedulerLimit", "WARNING", "JobSchedulerMessage Store size is within " + val + "% of its limit", scheduler.toString()));
                        }
                    }
                    if (storeLimit - storeSize > dirFreeSpace) {
                        final String message2 = "JobSchedulerStore limit is " + storeLimit / 1048576L + " mb, whilst the data directory: " + dir.getAbsolutePath() + " only has " + dirFreeSpace / 1048576L + " mb of usable space";
                        answer.add(new HealthStatus("org.apache.activemq.FreeDiskSpaceLeft", "WARNING", message2, scheduler.toString()));
                    }
                }
            }
        }
        if (answer != null && !answer.isEmpty()) {
            this.currentState = "Getting Worried {";
            for (final HealthStatus hs : answer) {
                this.currentState = this.currentState + hs + " , ";
            }
            this.currentState += " }";
        }
        else {
            this.currentState = "Good";
        }
        return answer;
    }
    
    @Override
    public String getCurrentStatus() {
        return this.currentState;
    }
}
