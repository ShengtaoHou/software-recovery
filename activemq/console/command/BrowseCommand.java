// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.command;

import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.Collection;
import javax.management.ObjectInstance;
import org.apache.activemq.console.util.JmxMBeansUtil;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;

public class BrowseCommand extends AbstractJmxCommand
{
    public static final String QUEUE_PREFIX = "queue:";
    public static final String TOPIC_PREFIX = "topic:";
    public static final String VIEW_GROUP_HEADER = "header:";
    public static final String VIEW_GROUP_CUSTOM = "custom:";
    public static final String VIEW_GROUP_BODY = "body:";
    protected String[] helpFile;
    private final List<String> queryAddObjects;
    private final List<String> querySubObjects;
    private final Set<String> groupViews;
    private final Set queryViews;
    
    public BrowseCommand() {
        this.helpFile = new String[] { "Task Usage: Main browse [browse-options] <destinations>", "Description: Display selected destination's messages.", "", "Browse Options:", "    --msgsel <msgsel1,msglsel2>   Add to the search list messages matched by the query similar to", "                                  the messages selector format.", "    -V<header|custom|body>        Predefined view that allows you to view the message header, custom", "                                  message header, or the message body.", "    --view <attr1>,<attr2>,...    Select the specific attribute of the message to view.", "    --jmxurl <url>                Set the JMX URL to connect to.", "    --pid <pid>                   Set the pid to connect to (only on Sun JVM).", "    --jmxuser <user>              Set the JMX user used for authenticating.", "    --jmxpassword <password>      Set the JMX password used for authenticating.", "    --jmxlocal                    Use the local JMX server instead of a remote one.", "    --version                     Display the version information.", "    -h,-?,--help                  Display the browse broker help information.", "", "Examples:", "    Main browse FOO.BAR", "        - Print the message header, custom message header, and message body of all messages in the", "          queue FOO.BAR", "", "    Main browse -Vheader,body queue:FOO.BAR", "        - Print only the message header and message body of all messages in the queue FOO.BAR", "", "    Main browse -Vheader --view custom:MyField queue:FOO.BAR", "        - Print the message header and the custom field 'MyField' of all messages in the queue FOO.BAR", "", "    Main browse --msgsel \"JMSMessageID='*:10',JMSPriority>5\" FOO.BAR", "        - Print all the message fields that has a JMSMessageID in the header field that matches the", "          wildcard *:10, and has a JMSPriority field > 5 in the queue FOO.BAR.", "          SLQ92 syntax is also supported.", "        * To use wildcard queries, the field must be a string and the query enclosed in ''", "          Use double quotes \"\" around the entire message selector string.", "" };
        this.queryAddObjects = new ArrayList<String>(10);
        this.querySubObjects = new ArrayList<String>(10);
        this.groupViews = new HashSet<String>(10);
        this.queryViews = new HashSet(10);
    }
    
    @Override
    public String getName() {
        return "browse";
    }
    
    @Override
    public String getOneLineDescription() {
        return "Used to browse a destination";
    }
    
    @Override
    protected void runTask(final List<String> tokens) throws Exception {
        try {
            if (tokens.isEmpty()) {
                tokens.add("*");
            }
            final Iterator<String> i = tokens.iterator();
            while (i.hasNext()) {
                final List queueList = JmxMBeansUtil.queryMBeans(this.createJmxConnection(), "Type=Queue,Destination=" + i.next() + ",*");
                final Iterator j = queueList.iterator();
                while (j.hasNext()) {
                    final List messages = JmxMBeansUtil.createMessageQueryFilter(this.createJmxConnection(), j.next().getObjectName()).query(this.queryAddObjects);
                    this.context.printMessage(JmxMBeansUtil.filterMessagesView(messages, this.groupViews, this.queryViews));
                }
            }
        }
        catch (Exception e) {
            this.context.printException(new RuntimeException("Failed to execute browse task. Reason: " + e));
            throw new Exception(e);
        }
    }
    
    @Override
    protected void handleOption(final String token, final List<String> tokens) throws Exception {
        if (token.startsWith("--msgsel")) {
            if (tokens.isEmpty() || tokens.get(0).startsWith("-")) {
                this.context.printException(new IllegalArgumentException("Message selector not specified"));
                return;
            }
            final StringTokenizer queryTokens = new StringTokenizer(tokens.remove(0), ",");
            while (queryTokens.hasMoreTokens()) {
                this.queryAddObjects.add(queryTokens.nextToken());
            }
        }
        else if (token.startsWith("--xmsgsel")) {
            if (tokens.isEmpty() || tokens.get(0).startsWith("-")) {
                this.context.printException(new IllegalArgumentException("Message selector not specified"));
                return;
            }
            final StringTokenizer queryTokens = new StringTokenizer(tokens.remove(0), ",");
            while (queryTokens.hasMoreTokens()) {
                this.querySubObjects.add(queryTokens.nextToken());
            }
        }
        else if (token.startsWith("--view")) {
            if (tokens.isEmpty() || tokens.get(0).startsWith("-")) {
                this.context.printException(new IllegalArgumentException("Attributes to view not specified"));
                return;
            }
            final StringTokenizer viewTokens = new StringTokenizer(tokens.remove(0), ",");
            while (viewTokens.hasMoreTokens()) {
                final String viewToken = viewTokens.nextToken();
                if (viewToken.equals("header:")) {
                    this.queryViews.add("JMS_HEADER_FIELD:" + viewToken.substring("header:".length()));
                }
                else if (viewToken.equals("custom:")) {
                    this.queryViews.add("JMS_CUSTOM_FIELD:" + viewToken.substring("custom:".length()));
                }
                else if (viewToken.equals("body:")) {
                    this.queryViews.add("JMS_BODY_FIELD:" + viewToken.substring("body:".length()));
                }
                else {
                    this.queryViews.add("JMS_HEADER_FIELD:" + viewToken);
                    this.queryViews.add("JMS_CUSTOM_FIELD:" + viewToken);
                    this.queryViews.add("JMS_BODY_FIELD:" + viewToken);
                }
            }
        }
        else if (token.startsWith("-V")) {
            final String viewGroup = token.substring(2);
            if (viewGroup.equals("header")) {
                this.groupViews.add("JMS_HEADER_FIELD:");
            }
            else if (viewGroup.equals("custom")) {
                this.groupViews.add("JMS_CUSTOM_FIELD:");
            }
            else if (viewGroup.equals("body")) {
                this.groupViews.add("JMS_BODY_FIELD:");
            }
            else {
                this.context.printInfo("Unknown group view: " + viewGroup + ". Ignoring group view option.");
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
}
