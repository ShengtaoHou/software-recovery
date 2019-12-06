// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.command;

import java.util.Collection;
import java.util.Set;
import org.apache.activemq.console.util.JmxMBeansUtil;
import java.util.HashSet;
import java.util.List;

public class ListCommand extends AbstractJmxCommand
{
    protected String[] helpFile;
    
    public ListCommand() {
        this.helpFile = new String[] { "Task Usage: Main list [list-options]", "Description:  Lists all available broker in the specified JMX context.", "", "List Options:", "    --jmxurl <url>             Set the JMX URL to connect to.", "    --pid <pid>                Set the pid to connect to (only on Sun JVM).", "    --jmxuser <user>           Set the JMX user used for authenticating.", "    --jmxpassword <password>   Set the JMX password used for authenticating.", "    --jmxlocal                 Use the local JMX server instead of a remote one.", "    --version                  Display the version information.", "    -h,-?,--help               Display the stop broker help information.", "" };
    }
    
    @Override
    public String getName() {
        return "list";
    }
    
    @Override
    public String getOneLineDescription() {
        return "Lists all available brokers in the specified JMX context";
    }
    
    @Override
    protected void runTask(final List tokens) throws Exception {
        try {
            final Set<String> propsView = new HashSet<String>();
            propsView.add("brokerName");
            this.context.printMBean(JmxMBeansUtil.filterMBeansView(JmxMBeansUtil.getAllBrokers(this.createJmxConnection()), propsView));
        }
        catch (Exception e) {
            this.context.printException(new RuntimeException("Failed to execute list task. Reason: " + e));
            throw new Exception(e);
        }
    }
    
    @Override
    protected void printHelp() {
        this.context.printHelp(this.helpFile);
    }
}
