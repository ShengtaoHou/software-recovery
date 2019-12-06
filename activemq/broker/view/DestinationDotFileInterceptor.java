// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.view;

import java.util.Iterator;
import java.util.Collection;
import org.apache.activemq.filter.DestinationMapNode;
import org.apache.activemq.filter.DestinationMap;
import java.io.PrintWriter;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.Broker;

public class DestinationDotFileInterceptor extends DotFileInterceptorSupport
{
    protected static final String ID_SEPARATOR = "_";
    
    public DestinationDotFileInterceptor(final Broker next, final String file) {
        super(next, file);
    }
    
    @Override
    public Destination addDestination(final ConnectionContext context, final ActiveMQDestination destination, final boolean create) throws Exception {
        final Destination answer = super.addDestination(context, destination, create);
        this.generateFile();
        return answer;
    }
    
    @Override
    public void removeDestination(final ConnectionContext context, final ActiveMQDestination destination, final long timeout) throws Exception {
        super.removeDestination(context, destination, timeout);
        this.generateFile();
    }
    
    @Override
    protected void generateFile(final PrintWriter writer) throws Exception {
        final ActiveMQDestination[] destinations = this.getDestinations();
        final DestinationMap map = new DestinationMap();
        for (int i = 0; i < destinations.length; ++i) {
            final ActiveMQDestination destination = destinations[i];
            map.put(destination, destination);
        }
        writer.println("digraph \"ActiveMQ Destinations\" {");
        writer.println();
        writer.println("node [style = \"rounded,filled\", fontname=\"Helvetica-Oblique\"];");
        writer.println();
        writer.println("topic_root [fillcolor = deepskyblue, label = \"Topics\" ];");
        writer.println("queue_root [fillcolor = deepskyblue, label = \"Queues\" ];");
        writer.println();
        writer.println("subgraph queues {");
        writer.println("  node [fillcolor=red];     ");
        writer.println("  label = \"Queues\"");
        writer.println();
        this.printNodeLinks(writer, map.getQueueRootNode(), "queue");
        writer.println("}");
        writer.println();
        writer.println("subgraph temp queues {");
        writer.println("  node [fillcolor=red];     ");
        writer.println("  label = \"TempQueues\"");
        writer.println();
        this.printNodeLinks(writer, map.getTempQueueRootNode(), "tempqueue");
        writer.println("}");
        writer.println();
        writer.println("subgraph topics {");
        writer.println("  node [fillcolor=green];     ");
        writer.println("  label = \"Topics\"");
        writer.println();
        this.printNodeLinks(writer, map.getTopicRootNode(), "topic");
        writer.println("}");
        writer.println();
        writer.println("subgraph temp topics {");
        writer.println("  node [fillcolor=green];     ");
        writer.println("  label = \"TempTopics\"");
        writer.println();
        this.printNodeLinks(writer, map.getTempTopicRootNode(), "temptopic");
        writer.println("}");
        writer.println();
        this.printNodes(writer, map.getQueueRootNode(), "queue");
        writer.println();
        this.printNodes(writer, map.getTempQueueRootNode(), "tempqueue");
        writer.println();
        this.printNodes(writer, map.getTopicRootNode(), "topic");
        writer.println();
        this.printNodes(writer, map.getTempTopicRootNode(), "temptopic");
        writer.println();
        writer.println("}");
    }
    
    protected void printNodes(final PrintWriter writer, final DestinationMapNode node, final String prefix) {
        final String path = this.getPath(node);
        writer.print("  ");
        writer.print(prefix);
        writer.print("_");
        writer.print(path);
        String label = path;
        if (prefix.equals("topic")) {
            label = "Topics";
        }
        else if (prefix.equals("queue")) {
            label = "Queues";
        }
        writer.print("[ label = \"");
        writer.print(label);
        writer.println("\" ];");
        final Collection children = node.getChildren();
        for (final DestinationMapNode child : children) {
            this.printNodes(writer, child, prefix + "_" + path);
        }
    }
    
    protected void printNodeLinks(final PrintWriter writer, final DestinationMapNode node, final String prefix) {
        final String path = this.getPath(node);
        final Collection children = node.getChildren();
        for (final DestinationMapNode child : children) {
            writer.print("  ");
            writer.print(prefix);
            writer.print("_");
            writer.print(path);
            writer.print(" -> ");
            writer.print(prefix);
            writer.print("_");
            writer.print(path);
            writer.print("_");
            writer.print(this.getPath(child));
            writer.println(";");
            this.printNodeLinks(writer, child, prefix + "_" + path);
        }
    }
    
    protected String getPath(final DestinationMapNode node) {
        final String path = node.getPath();
        if (path.equals("*")) {
            return "root";
        }
        return path;
    }
}
