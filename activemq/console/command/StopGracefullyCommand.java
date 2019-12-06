// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.command;

import java.util.List;
import javax.management.ObjectName;
import java.util.Iterator;
import javax.management.ObjectInstance;
import java.util.Collection;
import javax.management.MBeanServerConnection;

public class StopGracefullyCommand extends ShutdownCommand
{
    protected String connectorName;
    protected String queueName;
    protected long timeout;
    protected long pollInterval;
    
    @Override
    public String getName() {
        return "stop-gracefully";
    }
    
    @Override
    public String getOneLineDescription() {
        return "Stops a running broker gracefully.";
    }
    
    public StopGracefullyCommand() {
        this.helpFile = new String[] { "Task Usage: Main stopGracefully [stop-options] [broker-name1] [broker-name2] ...", "Description: Stops a running broker if there is no pending messages in the queues. It first stops the connector for client connection, then check queuesize until it becomes 0 before stop the broker.", "", "Stop Options:", "    --connectorName <connectorName> connectorName to stop", "    --queueName <queueName>         check the queuesize of the queueName for pending message", "    --timeout <timeout>             periodically check the queuesize before the timeout expires", "    --pollInterval <pollInterval>   the time interval it checks the queuesize", "    --jmxurl <url>             Set the JMX URL to connect to.", "    --jmxuser <user>           Set the JMX user used for authenticating.", "    --jmxpassword <password>   Set the JMX password used for authenticating.", "    --jmxlocal                 Use the local JMX server instead of a remote one.", "    --localProcessId           Use the local process id to connect( ignore jmxurl, jmxuser, jmxpassword), need to be root to use this option", "    --all                      Stop all brokers.", "    --version                  Display the version information.", "    -h,-?,--help               Display the stop broker help information.", "", "Broker Names:", "    Name of the brokers that will be stopped.", "    If omitted, it is assumed that there is only one broker running, and it will be stopped.", "    Use -all to stop all running brokers.", "" };
    }
    
    @Override
    protected void stopBrokers(final MBeanServerConnection jmxConnection, final Collection brokerBeans) throws Exception {
        final Iterator i = brokerBeans.iterator();
        while (i.hasNext()) {
            final ObjectName brokerObjName = i.next().getObjectName();
            final String brokerName = brokerObjName.getKeyProperty("BrokerName");
            this.context.print("Stopping broker: " + brokerName);
            try {
                jmxConnection.invoke(brokerObjName, "stopGracefully", new Object[] { this.connectorName, this.queueName, this.timeout, this.pollInterval }, new String[] { "java.lang.String", "java.lang.String", "long", "long" });
                this.context.print("Succesfully stopped broker: " + brokerName);
            }
            catch (Exception e) {
                if (e.getMessage().startsWith("Error unmarshaling return header")) {
                    continue;
                }
                this.context.print("Exception:" + e.getMessage());
            }
        }
        this.closeJmxConnection();
    }
    
    @Override
    protected void handleOption(final String token, final List<String> tokens) throws Exception {
        if (token.equals("--connectorName")) {
            if (tokens.isEmpty() || tokens.get(0).startsWith("-")) {
                this.context.printException(new IllegalArgumentException("connectorName not specified"));
                return;
            }
            this.connectorName = tokens.remove(0);
        }
        else if (token.equals("--timeout")) {
            if (tokens.isEmpty() || tokens.get(0).startsWith("-")) {
                this.context.printException(new IllegalArgumentException("timeout not specified"));
                return;
            }
            this.timeout = Long.parseLong(tokens.remove(0));
        }
        else if (token.equals("--pollInterval")) {
            if (tokens.isEmpty() || tokens.get(0).startsWith("-")) {
                this.context.printException(new IllegalArgumentException("pollInterval not specified"));
                return;
            }
            this.pollInterval = Long.parseLong(tokens.remove(0));
        }
        else if (token.equals("--queueName")) {
            if (tokens.isEmpty() || tokens.get(0).startsWith("-")) {
                this.context.printException(new IllegalArgumentException("queueName not specified"));
                return;
            }
            this.queueName = tokens.remove(0);
        }
        else {
            super.handleOption(token, tokens);
        }
    }
}
