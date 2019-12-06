// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.command;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Collection;
import org.apache.activemq.console.util.JmxMBeansUtil;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;
import java.util.Properties;

public class QueryCommand extends AbstractJmxCommand
{
    private static final Properties PREDEFINED_OBJNAME_QUERY;
    protected String[] helpFile;
    private final List<String> queryAddObjects;
    private final List<String> querySubObjects;
    private final Set queryViews;
    
    public QueryCommand() {
        this.helpFile = new String[] { "Task Usage: Main query [query-options]", "Description: Display selected broker component's attributes and statistics.", "", "Query Options:", "    -Q<type>=<name>               Add to the search list the specific object type matched", "                                  by the defined object identifier.", "    -xQ<type>=<name>              Remove from the search list the specific object type", "                                  matched by the object identifier.", "    --objname <query>             Add to the search list objects matched by the query similar", "                                  to the JMX object name format.", "    --xobjname <query>            Remove from the search list objects matched by the query", "                                  similar to the JMX object name format.", "    --view <attr1>,<attr2>,...    Select the specific attribute of the object to view.", "                                  By default all attributes will be displayed.", "    --jmxurl <url>                Set the JMX URL to connect to.", "    --pid <pid>                   Set the pid to connect to (only on Sun JVM).", "    --jmxuser <user>              Set the JMX user used for authenticating.", "    --jmxpassword <password>      Set the JMX password used for authenticating.", "    --jmxlocal                    Use the local JMX server instead of a remote one.", "    --version                     Display the version information.", "    -h,-?,--help                  Display the query broker help information.", "", "Examples:", "    query", "        - Print all the attributes of all registered objects queues, topics, connections, etc).", "", "    query -QQueue=TEST.FOO", "        - Print all the attributes of the queue with destination name TEST.FOO.", "", "    query -QTopic=*", "        - Print all the attributes of all registered topics.", "", "    query --view EnqueueCount,DequeueCount", "        - Print the attributes EnqueueCount and DequeueCount of all registered objects.", "", "    query -QTopic=* --view EnqueueCount,DequeueCount", "        - Print the attributes EnqueueCount and DequeueCount of all registered topics.", "", "    query -QTopic=* -QQueue=* --view EnqueueCount,DequeueCount", "        - Print the attributes EnqueueCount and DequeueCount of all registered topics and", "          queues.", "", "    query -QTopic=* -xQTopic=ActiveMQ.Advisory.*", "        - Print all attributes of all topics except those that has a name that begins", "          with \"ActiveMQ.Advisory\".", "", "    query --objname Type=*Connect*,BrokerName=local* -xQNetworkConnector=*", "        - Print all attributes of all connectors, connections excluding network connectors", "          that belongs to the broker that begins with local.", "", "    query -QQueue=* -xQQueue=????", "        - Print all attributes of all queues except those that are 4 letters long.", "" };
        this.queryAddObjects = new ArrayList<String>(10);
        this.querySubObjects = new ArrayList<String>(10);
        this.queryViews = new HashSet(10);
    }
    
    @Override
    public String getName() {
        return "query";
    }
    
    @Override
    public String getOneLineDescription() {
        return "Display selected broker component's attributes and statistics.";
    }
    
    @Override
    protected void runTask(final List<String> tokens) throws Exception {
        try {
            final List addMBeans = JmxMBeansUtil.queryMBeans(this.createJmxConnection(), this.queryAddObjects, this.queryViews);
            if (this.querySubObjects.size() > 0) {
                final List subMBeans = JmxMBeansUtil.queryMBeans(this.createJmxConnection(), this.querySubObjects, this.queryViews);
                addMBeans.removeAll(subMBeans);
            }
            this.context.printMBean(JmxMBeansUtil.filterMBeansView(addMBeans, this.queryViews));
        }
        catch (Exception e) {
            this.context.printException(new RuntimeException("Failed to execute query task. Reason: " + e));
            throw new Exception(e);
        }
    }
    
    @Override
    protected void handleOption(final String token, final List<String> tokens) throws Exception {
        if (token.startsWith("-Q")) {
            String key = token.substring(2);
            String value = "";
            final int pos = key.indexOf("=");
            if (pos >= 0) {
                value = key.substring(pos + 1);
                key = key.substring(0, pos);
            }
            final String predefQuery = QueryCommand.PREDEFINED_OBJNAME_QUERY.getProperty(key);
            if (predefQuery == null) {
                this.context.printException(new IllegalArgumentException("Unknown query object type: " + key));
                return;
            }
            final String queryStr = JmxMBeansUtil.createQueryString(predefQuery, value);
            final StringTokenizer queryTokens = new StringTokenizer(queryStr, ",");
            while (queryTokens.hasMoreTokens()) {
                this.queryAddObjects.add(queryTokens.nextToken());
            }
        }
        else if (token.startsWith("-xQ")) {
            String key = token.substring(3);
            String value = "";
            final int pos = key.indexOf("=");
            if (pos >= 0) {
                value = key.substring(pos + 1);
                key = key.substring(0, pos);
            }
            final String predefQuery = QueryCommand.PREDEFINED_OBJNAME_QUERY.getProperty(key);
            if (predefQuery == null) {
                this.context.printException(new IllegalArgumentException("Unknown query object type: " + key));
                return;
            }
            final String queryStr = JmxMBeansUtil.createQueryString(predefQuery, value);
            final StringTokenizer queryTokens = new StringTokenizer(queryStr, ",");
            while (queryTokens.hasMoreTokens()) {
                this.querySubObjects.add(queryTokens.nextToken());
            }
        }
        else if (token.startsWith("--objname")) {
            if (tokens.isEmpty() || tokens.get(0).startsWith("-")) {
                this.context.printException(new IllegalArgumentException("Object name query not specified"));
                return;
            }
            final StringTokenizer queryTokens2 = new StringTokenizer(tokens.remove(0), ",");
            while (queryTokens2.hasMoreTokens()) {
                this.queryAddObjects.add(queryTokens2.nextToken());
            }
        }
        else if (token.startsWith("--xobjname")) {
            if (tokens.isEmpty() || tokens.get(0).startsWith("-")) {
                this.context.printException(new IllegalArgumentException("Object name query not specified"));
                return;
            }
            final StringTokenizer queryTokens2 = new StringTokenizer(tokens.remove(0), ",");
            while (queryTokens2.hasMoreTokens()) {
                this.querySubObjects.add(queryTokens2.nextToken());
            }
        }
        else if (token.startsWith("--view")) {
            if (tokens.isEmpty() || tokens.get(0).startsWith("-")) {
                this.context.printException(new IllegalArgumentException("Attributes to view not specified"));
                return;
            }
            final Enumeration viewTokens = new StringTokenizer(tokens.remove(0), ",");
            while (viewTokens.hasMoreElements()) {
                this.queryViews.add(viewTokens.nextElement());
            }
        }
        else {
            super.handleOption(token, tokens);
        }
    }
    
    @Override
    protected void printHelp() {
        this.context.printHelp(this.helpFile);
    }
    
    static {
        (PREDEFINED_OBJNAME_QUERY = new Properties()).setProperty("Broker", "type=Broker,brokerName=%1");
        QueryCommand.PREDEFINED_OBJNAME_QUERY.setProperty("Connection", "type=Broker,connector=clientConnectors,connectionName=%1,*");
        QueryCommand.PREDEFINED_OBJNAME_QUERY.setProperty("Connector", "type=Broker,brokerName=*,connector=clientConnectors,connectorName=%1");
        QueryCommand.PREDEFINED_OBJNAME_QUERY.setProperty("NetworkConnector", "type=Broker,brokerName=%1,connector=networkConnectors,networkConnectorName=*");
        QueryCommand.PREDEFINED_OBJNAME_QUERY.setProperty("Queue", "type=Broker,brokerName=*,destinationType=Queue,destinationName=%1");
        QueryCommand.PREDEFINED_OBJNAME_QUERY.setProperty("Topic", "type=Broker,brokerName=*,destinationType=Topic,destinationName=%1,*");
    }
}
