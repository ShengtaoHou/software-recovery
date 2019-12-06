// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.command;

import java.util.StringTokenizer;
import javax.jms.Destination;
import java.util.Iterator;
import java.util.Collection;
import org.apache.activemq.console.util.AmqMessagesUtil;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.command.ActiveMQQueue;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;

public class AmqBrowseCommand extends AbstractAmqCommand
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
    
    public AmqBrowseCommand() {
        this.helpFile = new String[] { "Task Usage: Main browse --amqurl <broker url> [browse-options] <destinations>", "Description: Display selected destination's messages.", "", "Browse Options:", "    --amqurl <url>                Set the broker URL to connect to.", "    --msgsel <msgsel1,msglsel2>   Add to the search list messages matched by the query similar to", "                                  the messages selector format.", "    --factory <className>         Load className as the javax.jms.ConnectionFactory to use for creating connections.", "    --passwordFactory <className> Load className as the org.apache.activemq.console.command.PasswordFactory", "                                  for retrieving the password from a keystore.", "    --user <username>             Username to use for JMS connections.", "    --password <password>         Password to use for JMS connections.", "    -V<header|custom|body>        Predefined view that allows you to view the message header, custom", "                                  message header, or the message body.", "    --view <attr1>,<attr2>,...    Select the specific attribute of the message to view.", "    --version                     Display the version information.", "    -h,-?,--help                  Display the browse broker help information.", "", "Examples:", "    Main browse --amqurl tcp://localhost:61616 FOO.BAR", "        - Print the message header, custom message header, and message body of all messages in the", "          queue FOO.BAR", "", "    Main browse --amqurl tcp://localhost:61616 -Vheader,body queue:FOO.BAR", "        - Print only the message header and message body of all messages in the queue FOO.BAR", "", "    Main browse --amqurl tcp://localhost:61616 -Vheader --view custom:MyField queue:FOO.BAR", "        - Print the message header and the custom field 'MyField' of all messages in the queue FOO.BAR", "", "    Main browse --amqurl tcp://localhost:61616 --msgsel JMSMessageID='*:10',JMSPriority>5 FOO.BAR", "        - Print all the message fields that has a JMSMessageID in the header field that matches the", "          wildcard *:10, and has a JMSPriority field > 5 in the queue FOO.BAR", "        * To use wildcard queries, the field must be a string and the query enclosed in ''", "", "    Main browse --amqurl tcp://localhost:61616 --user someUser --password somePass FOO.BAR", "        - Print the message header, custom message header, and message body of all messages in the", "          queue FOO.BAR, using someUser as the user name, and somePass as the password", "", "    Main browse --amqurl tcp://localhost:61616 --user someUser --password somePass --factory org.apache.activemq.ActiveMQConnectionFactory --passwordFactory org.apache.activemq.AMQPasswordFactory FOO.BAR", "        - Print the message header, custom message header, and message body of all messages in the", "          queue FOO.BAR, using someUser as the user name, org.apache.activemq.AMQFactorySubClass to create JMS connections,", "          and org.apache.activemq.console.command.DefaultPasswordFactory to turn somePass into the password to be used.", "" };
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
        return "Display selected messages in a specified destination.";
    }
    
    @Override
    protected void runTask(final List tokens) throws Exception {
        try {
            if (tokens.isEmpty()) {
                this.context.printException(new IllegalArgumentException("No JMS destination specified."));
                return;
            }
            if (this.getBrokerUrl() == null) {
                this.context.printException(new IllegalStateException("No broker url specified. Use the --amqurl option to specify a broker url."));
                return;
            }
            for (final String destName : tokens) {
                Destination dest;
                if (destName.startsWith("queue:")) {
                    dest = new ActiveMQQueue(destName.substring("queue:".length()));
                }
                else if (destName.startsWith("topic:")) {
                    dest = new ActiveMQTopic(destName.substring("topic:".length()));
                }
                else {
                    dest = new ActiveMQQueue(destName);
                }
                final List addMsgs = AmqMessagesUtil.getMessages(this.getConnectionFactory(), dest, this.queryAddObjects);
                if (this.querySubObjects.size() > 0) {
                    final List subMsgs = AmqMessagesUtil.getMessages(this.getConnectionFactory(), dest, this.querySubObjects);
                    addMsgs.removeAll(subMsgs);
                }
                this.context.printMessage(AmqMessagesUtil.filterMessagesView(addMsgs, this.groupViews, this.queryViews));
            }
        }
        catch (Exception e) {
            this.context.printException(new RuntimeException("Failed to execute browse task. Reason: " + e));
            throw new Exception(e);
        }
    }
    
    @Override
    protected void handleOption(final String token, final List tokens) throws Exception {
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
