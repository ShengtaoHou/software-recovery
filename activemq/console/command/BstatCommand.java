// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.command;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class BstatCommand extends QueryCommand
{
    protected String[] helpFile;
    
    public BstatCommand() {
        this.helpFile = new String[] { "Task Usage: activemq-admin bstat [bstat-options] [broker-name]", "Description: Performs a predefined query that displays useful statistics regarding the specified broker.", "             If no broker name is specified, it will try and select from all registered brokers.", "", "Bstat Options:", "    --jmxurl <url>                Set the JMX URL to connect to.", "    --pid <pid>                   Set the pid to connect to (only on Sun JVM).", "    --jmxuser <user>              Set the JMX user used for authenticating.", "    --jmxpassword <password>      Set the JMX password used for authenticating.", "    --jmxlocal                    Use the local JMX server instead of a remote one.", "    --version                     Display the version information.", "    -h,-?,--help                  Display the query broker help information.", "", "Examples:", "    activemq-admin bstat localhost", "        - Display a summary of statistics for the broker 'localhost'" };
    }
    
    @Override
    public String getName() {
        return "bstat";
    }
    
    @Override
    public String getOneLineDescription() {
        return "Performs a predefined query that displays useful statistics regarding the specified broker";
    }
    
    @Override
    protected void runTask(final List<String> tokens) throws Exception {
        final List<String> queryTokens = new ArrayList<String>();
        String brokerName = "*";
        for (final String token : tokens) {
            if (!token.startsWith("-")) {
                brokerName = token;
                break;
            }
            queryTokens.add(token);
        }
        queryTokens.add("--objname");
        queryTokens.add("type=*,brokerName=" + brokerName + ",*");
        queryTokens.add("-xQTopic=ActiveMQ.Advisory.*");
        queryTokens.add("--view");
        queryTokens.add("BrokerName,Name,connectorName,networkConnectorName,destinationName,destinationType,EnqueueCount,DequeueCount,TotalEnqueueCount,TotalDequeueCount,Messages,TotalMessageCount,ConsumerCount,TotalConsumerCount,DispatchCount,Duplex,NetworkTTL,Uptime");
        super.parseOptions(queryTokens);
        super.runTask(queryTokens);
    }
    
    @Override
    protected void printHelp() {
        this.context.printHelp(this.helpFile);
    }
}
