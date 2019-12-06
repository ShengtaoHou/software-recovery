// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.command;

import javax.management.ObjectName;
import java.util.Iterator;
import javax.management.ObjectInstance;
import javax.management.MBeanServerConnection;
import java.util.Collection;
import java.util.HashSet;
import org.apache.activemq.console.util.JmxMBeansUtil;
import java.util.List;

public class ShutdownCommand extends AbstractJmxCommand
{
    protected String[] helpFile;
    private boolean isStopAllBrokers;
    
    public ShutdownCommand() {
        this.helpFile = new String[] { "Task Usage: Main stop [stop-options] [broker-name1] [broker-name2] ...", "Description: Stops a running broker.", "", "Stop Options:", "    --jmxurl <url>             Set the JMX URL to connect to.", "    --pid <pid>                   Set the pid to connect to (only on Sun JVM).", "    --jmxuser <user>           Set the JMX user used for authenticating.", "    --jmxpassword <password>   Set the JMX password used for authenticating.", "    --jmxlocal                 Use the local JMX server instead of a remote one.", "    --all                      Stop all brokers.", "    --version                  Display the version information.", "    -h,-?,--help               Display the stop broker help information.", "", "Broker Names:", "    Name of the brokers that will be stopped.", "    If omitted, it is assumed that there is only one broker running, and it will be stopped.", "    Use -all to stop all running brokers.", "" };
    }
    
    @Override
    public String getName() {
        return "stop";
    }
    
    @Override
    public String getOneLineDescription() {
        return "Stops a running broker specified by the broker name.";
    }
    
    @Override
    protected void runTask(final List brokerNames) throws Exception {
        try {
            Collection mbeans;
            if (this.isStopAllBrokers) {
                mbeans = JmxMBeansUtil.getAllBrokers(this.createJmxConnection());
                brokerNames.clear();
            }
            else if (brokerNames.isEmpty()) {
                mbeans = JmxMBeansUtil.getAllBrokers(this.createJmxConnection());
                if (mbeans.isEmpty()) {
                    this.context.printInfo("There are no brokers to stop.");
                    return;
                }
                if (mbeans.size() > 1) {
                    this.context.printInfo("There are multiple brokers to stop. Please select the broker(s) to stop or use --all to stop all brokers.");
                    return;
                }
                final Object firstBroker = mbeans.iterator().next();
                mbeans.clear();
                mbeans.add(firstBroker);
            }
            else {
                mbeans = new HashSet();
                while (!brokerNames.isEmpty()) {
                    final String brokerName = brokerNames.remove(0);
                    final Collection matchedBrokers = JmxMBeansUtil.getBrokersByName(this.createJmxConnection(), brokerName);
                    if (matchedBrokers.isEmpty()) {
                        this.context.printInfo(brokerName + " did not match any running brokers.");
                    }
                    else {
                        mbeans.addAll(matchedBrokers);
                    }
                }
            }
            this.stopBrokers(this.createJmxConnection(), mbeans);
        }
        catch (Exception e) {
            this.context.printException(new RuntimeException("Failed to execute stop task. Reason: " + e));
            throw new Exception(e);
        }
    }
    
    protected void stopBrokers(final MBeanServerConnection jmxConnection, final Collection brokerBeans) throws Exception {
        final Iterator i = brokerBeans.iterator();
        while (i.hasNext()) {
            final ObjectName brokerObjName = i.next().getObjectName();
            final String brokerName = brokerObjName.getKeyProperty("brokerName");
            this.context.print("Stopping broker: " + brokerName);
            try {
                jmxConnection.invoke(brokerObjName, "terminateJVM", new Object[] { 0 }, new String[] { "int" });
                this.context.print("Succesfully stopped broker: " + brokerName);
            }
            catch (Exception ex) {}
        }
        this.closeJmxConnection();
    }
    
    @Override
    protected void handleOption(final String token, final List<String> tokens) throws Exception {
        if (token.equals("--all")) {
            this.isStopAllBrokers = true;
        }
        else {
            super.handleOption(token, tokens);
        }
    }
    
    @Override
    protected void printHelp() {
        this.context.printHelp(this.helpFile);
    }
}
