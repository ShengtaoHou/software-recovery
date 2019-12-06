// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.command;

import org.apache.activemq.broker.jmx.TopicView;
import org.apache.activemq.broker.jmx.QueueView;
import org.apache.activemq.broker.jmx.TopicViewMBean;
import javax.management.ObjectName;
import java.util.Iterator;
import javax.management.MBeanServerInvocationHandler;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import javax.management.ObjectInstance;
import java.util.Locale;
import java.util.Comparator;
import java.util.Collections;
import org.apache.activemq.console.util.JmxMBeansUtil;
import java.util.List;

public class DstatCommand extends AbstractJmxCommand
{
    private static final String queryString = "type=Broker,brokerName=*,destinationType=%1,destinationName=*,*";
    protected String[] helpFile;
    
    public DstatCommand() {
        this.helpFile = new String[] { "Task Usage: activemq-admin dstat [dstat-options] [destination-type]", "Description: Performs a predefined query that displays useful statistics regarding the specified .", "             destination type (Queues or Topics) and displays those results in a tabular format.", "             If no broker name is specified, it will try and select from all registered brokers.", "", "dstat Options:", "    --jmxurl <url>                Set the JMX URL to connect to.", "    --pid <pid>                   Set the pid to connect to (only on Sun JVM).", "    --jmxuser <user>              Set the JMX user used for authenticating.", "    --jmxpassword <password>      Set the JMX password used for authenticating.", "    --jmxlocal                    Use the local JMX server instead of a remote one.", "    --version                     Display the version information.", "    -h,-?,--help                  Display the query broker help information.", "", "Examples:", "    activemq-admin dstat queues", "        - Display a tabular summary of statistics for the queues on the broker.", "    activemq-admin dstat topics", "        - Display a tabular summary of statistics for the queues on the broker." };
    }
    
    @Override
    protected void runTask(final List<String> tokens) throws Exception {
        try {
            if (tokens.contains("topics")) {
                this.displayTopicStats();
            }
            else if (tokens.contains("queues")) {
                this.displayQueueStats();
            }
            else {
                this.displayAllDestinations();
            }
        }
        catch (Exception e) {
            this.context.printException(new RuntimeException("Failed to execute dstat task. Reason: " + e.getMessage(), e));
            throw new Exception(e);
        }
    }
    
    private void displayAllDestinations() throws Exception {
        final String query = JmxMBeansUtil.createQueryString("type=Broker,brokerName=*,destinationType=%1,destinationName=*,*", "*");
        final List queueList = JmxMBeansUtil.queryMBeans(this.createJmxConnection(), query);
        final String header = "%-50s  %10s  %10s  %10s  %10s  %10s  %10s";
        final String tableRow = "%-50s  %10d  %10d  %10d  %10d  %10d  %10d";
        Collections.sort((List<Object>)queueList, (Comparator<? super Object>)new ObjectInstanceComparator());
        this.context.print(String.format(Locale.US, "%-50s  %10s  %10s  %10s  %10s  %10s  %10s", "Name", "Queue Size", "Producer #", "Consumer #", "Enqueue #", "Dequeue #", "Memory %"));
        for (final Object view : queueList) {
            final ObjectInstance obj = (ObjectInstance)view;
            if (!this.filterMBeans(obj)) {
                continue;
            }
            final ObjectName queueName = obj.getObjectName();
            final QueueViewMBean queueView = MBeanServerInvocationHandler.newProxyInstance(this.createJmxConnection(), queueName, QueueViewMBean.class, true);
            this.context.print(String.format(Locale.US, "%-50s  %10d  %10d  %10d  %10d  %10d  %10d", queueView.getName(), queueView.getQueueSize(), queueView.getProducerCount(), queueView.getConsumerCount(), queueView.getEnqueueCount(), queueView.getDequeueCount(), queueView.getMemoryPercentUsage()));
        }
    }
    
    private void displayQueueStats() throws Exception {
        final String query = JmxMBeansUtil.createQueryString("type=Broker,brokerName=*,destinationType=%1,destinationName=*,*", "Queue");
        final List queueList = JmxMBeansUtil.queryMBeans(this.createJmxConnection(), query);
        final String header = "%-50s  %10s  %10s  %10s  %10s  %10s  %10s";
        final String tableRow = "%-50s  %10d  %10d  %10d  %10d  %10d  %10d";
        this.context.print(String.format(Locale.US, "%-50s  %10s  %10s  %10s  %10s  %10s  %10s", "Name", "Queue Size", "Producer #", "Consumer #", "Enqueue #", "Dequeue #", "Memory %"));
        Collections.sort((List<Object>)queueList, (Comparator<? super Object>)new ObjectInstanceComparator());
        for (final Object view : queueList) {
            final ObjectInstance obj = (ObjectInstance)view;
            if (!this.filterMBeans(obj)) {
                continue;
            }
            final ObjectName queueName = obj.getObjectName();
            final QueueViewMBean queueView = MBeanServerInvocationHandler.newProxyInstance(this.createJmxConnection(), queueName, QueueViewMBean.class, true);
            this.context.print(String.format(Locale.US, "%-50s  %10d  %10d  %10d  %10d  %10d  %10d", queueView.getName(), queueView.getQueueSize(), queueView.getProducerCount(), queueView.getConsumerCount(), queueView.getEnqueueCount(), queueView.getDequeueCount(), queueView.getMemoryPercentUsage()));
        }
    }
    
    private void displayTopicStats() throws Exception {
        final String query = JmxMBeansUtil.createQueryString("type=Broker,brokerName=*,destinationType=%1,destinationName=*,*", "Topic");
        final List topicsList = JmxMBeansUtil.queryMBeans(this.createJmxConnection(), query);
        final String header = "%-50s  %10s  %10s  %10s  %10s  %10s  %10s";
        final String tableRow = "%-50s  %10d  %10d  %10d  %10d  %10d  %10d";
        Collections.sort((List<Object>)topicsList, (Comparator<? super Object>)new ObjectInstanceComparator());
        this.context.print(String.format(Locale.US, "%-50s  %10s  %10s  %10s  %10s  %10s  %10s", "Name", "Queue Size", "Producer #", "Consumer #", "Enqueue #", "Dequeue #", "Memory %"));
        for (final Object view : topicsList) {
            final ObjectInstance obj = (ObjectInstance)view;
            if (!this.filterMBeans(obj)) {
                continue;
            }
            final ObjectName topicName = obj.getObjectName();
            final TopicViewMBean topicView = MBeanServerInvocationHandler.newProxyInstance(this.createJmxConnection(), topicName, TopicViewMBean.class, true);
            this.context.print(String.format(Locale.US, "%-50s  %10d  %10d  %10d  %10d  %10d  %10d", topicView.getName(), topicView.getQueueSize(), topicView.getProducerCount(), topicView.getConsumerCount(), topicView.getEnqueueCount(), topicView.getDequeueCount(), topicView.getMemoryPercentUsage()));
        }
    }
    
    @Override
    public String getName() {
        return "dstat";
    }
    
    @Override
    public String getOneLineDescription() {
        return "Performs a predefined query that displays useful tabular statistics regarding the specified destination type";
    }
    
    @Override
    protected void printHelp() {
        this.context.printHelp(this.helpFile);
    }
    
    protected boolean filterMBeans(final ObjectInstance obj) {
        final String className = obj.getClassName();
        return className.equals(QueueView.class.getName()) || className.equals(TopicView.class.getName());
    }
    
    private static class ObjectInstanceComparator implements Comparator<ObjectInstance>
    {
        @Override
        public int compare(final ObjectInstance o1, final ObjectInstance o2) {
            return o1.getObjectName().compareTo(o2.getObjectName());
        }
    }
}
