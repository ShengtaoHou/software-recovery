// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.view;

import java.util.Collection;
import org.apache.activemq.filter.DestinationMapNode;
import org.apache.activemq.broker.jmx.SubscriptionViewMBean;
import javax.management.ObjectName;
import java.util.Iterator;
import java.io.PrintWriter;
import java.util.HashSet;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.broker.ConnectionContext;
import java.io.IOException;
import java.util.HashMap;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.Set;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.command.ProducerId;
import java.util.Map;
import org.apache.activemq.broker.jmx.BrokerViewMBean;

public class ConnectionDotFileInterceptor extends DotFileInterceptorSupport
{
    protected static final String ID_SEPARATOR = "_";
    private final boolean redrawOnRemove;
    private boolean clearProducerCacheAfterRender;
    private final String domain = "org.apache.activemq";
    private BrokerViewMBean brokerView;
    private final Map<ProducerId, ProducerInfo> producers;
    private final Map<ProducerId, Set<ActiveMQDestination>> producerDestinations;
    private final Object lock;
    
    public ConnectionDotFileInterceptor(final Broker next, final String file, final boolean redrawOnRemove) throws IOException {
        super(next, file);
        this.producers = new HashMap<ProducerId, ProducerInfo>();
        this.producerDestinations = new HashMap<ProducerId, Set<ActiveMQDestination>>();
        this.lock = new Object();
        this.redrawOnRemove = redrawOnRemove;
    }
    
    @Override
    public Subscription addConsumer(final ConnectionContext context, final ConsumerInfo info) throws Exception {
        final Subscription answer = super.addConsumer(context, info);
        this.generateFile();
        return answer;
    }
    
    @Override
    public void addProducer(final ConnectionContext context, final ProducerInfo info) throws Exception {
        super.addProducer(context, info);
        final ProducerId producerId = info.getProducerId();
        synchronized (this.lock) {
            this.producers.put(producerId, info);
        }
        this.generateFile();
    }
    
    @Override
    public void removeConsumer(final ConnectionContext context, final ConsumerInfo info) throws Exception {
        super.removeConsumer(context, info);
        if (this.redrawOnRemove) {
            this.generateFile();
        }
    }
    
    @Override
    public void removeProducer(final ConnectionContext context, final ProducerInfo info) throws Exception {
        super.removeProducer(context, info);
        final ProducerId producerId = info.getProducerId();
        if (this.redrawOnRemove) {
            synchronized (this.lock) {
                this.producerDestinations.remove(producerId);
                this.producers.remove(producerId);
            }
            this.generateFile();
        }
    }
    
    @Override
    public void send(final ProducerBrokerExchange producerExchange, final Message messageSend) throws Exception {
        super.send(producerExchange, messageSend);
        final ProducerId producerId = messageSend.getProducerId();
        final ActiveMQDestination destination = messageSend.getDestination();
        synchronized (this.lock) {
            Set<ActiveMQDestination> destinations = this.producerDestinations.get(producerId);
            if (destinations == null) {
                destinations = new HashSet<ActiveMQDestination>();
            }
            this.producerDestinations.put(producerId, destinations);
            destinations.add(destination);
        }
    }
    
    @Override
    protected void generateFile(final PrintWriter writer) throws Exception {
        writer.println("digraph \"ActiveMQ Connections\" {");
        writer.println();
        writer.println("label=\"ActiveMQ Broker: " + this.getBrokerView().getBrokerId() + "\"];");
        writer.println();
        writer.println("node [style = \"rounded,filled\", fillcolor = yellow, fontname=\"Helvetica-Oblique\"];");
        writer.println();
        final Map<String, String> clients = new HashMap<String, String>();
        final Map<String, String> queues = new HashMap<String, String>();
        final Map<String, String> topics = new HashMap<String, String>();
        this.printSubscribers(writer, clients, queues, "queue_", this.getBrokerView().getQueueSubscribers());
        writer.println();
        this.printSubscribers(writer, clients, topics, "topic_", this.getBrokerView().getTopicSubscribers());
        writer.println();
        this.printProducers(writer, clients, queues, topics);
        writer.println();
        this.writeLabels(writer, "green", "Client: ", clients);
        writer.println();
        this.writeLabels(writer, "red", "Queue: ", queues);
        this.writeLabels(writer, "blue", "Topic: ", topics);
        writer.println("}");
        if (this.clearProducerCacheAfterRender) {
            this.producerDestinations.clear();
        }
    }
    
    protected void printProducers(final PrintWriter writer, final Map<String, String> clients, final Map<String, String> queues, final Map<String, String> topics) {
        synchronized (this.lock) {
            for (final Map.Entry entry : this.producerDestinations.entrySet()) {
                final ProducerId producerId = entry.getKey();
                final Set destinationSet = entry.getValue();
                this.printProducers(writer, clients, queues, topics, producerId, destinationSet);
            }
        }
    }
    
    protected void printProducers(final PrintWriter writer, final Map<String, String> clients, final Map<String, String> queues, final Map<String, String> topics, final ProducerId producerId, final Set destinationSet) {
        for (final ActiveMQDestination destination : destinationSet) {
            final String clientId = producerId.getConnectionId();
            final String safeClientId = this.asID(clientId);
            clients.put(safeClientId, clientId);
            final String physicalName = destination.getPhysicalName();
            String safeDestinationId = this.asID(physicalName);
            if (destination.isTopic()) {
                safeDestinationId = "topic_" + safeDestinationId;
                topics.put(safeDestinationId, physicalName);
            }
            else {
                safeDestinationId = "queue_" + safeDestinationId;
                queues.put(safeDestinationId, physicalName);
            }
            final String safeProducerId = this.asID(producerId.toString());
            writer.print(safeClientId);
            writer.print(" -> ");
            writer.print(safeProducerId);
            writer.println(";");
            writer.print(safeProducerId);
            writer.print(" -> ");
            writer.print(safeDestinationId);
            writer.println(";");
            writer.print(safeProducerId);
            writer.print(" [label = \"");
            final String label = "Producer: " + producerId.getSessionId() + "-" + producerId.getValue();
            writer.print(label);
            writer.println("\"];");
        }
    }
    
    protected void printSubscribers(final PrintWriter writer, final Map<String, String> clients, final Map<String, String> destinations, final String type, final ObjectName[] subscribers) {
        for (int i = 0; i < subscribers.length; ++i) {
            final ObjectName name = subscribers[i];
            final SubscriptionViewMBean subscriber = (SubscriptionViewMBean)this.getBrokerService().getManagementContext().newProxyInstance(name, SubscriptionViewMBean.class, true);
            final String clientId = subscriber.getClientId();
            final String safeClientId = this.asID(clientId);
            clients.put(safeClientId, clientId);
            final String destination = subscriber.getDestinationName();
            final String safeDestinationId = type + this.asID(destination);
            destinations.put(safeDestinationId, destination);
            final String selector = subscriber.getSelector();
            final String subscriberId = safeClientId + "_" + subscriber.getSessionId() + "_" + subscriber.getSubscriptionId();
            writer.print(subscriberId);
            writer.print(" -> ");
            writer.print(safeClientId);
            writer.println(";");
            writer.print(safeDestinationId);
            writer.print(" -> ");
            writer.print(subscriberId);
            writer.println(";");
            writer.print(subscriberId);
            writer.print(" [label = \"");
            String label = "Subscription: " + subscriber.getSessionId() + "-" + subscriber.getSubscriptionId();
            if (selector != null && selector.length() > 0) {
                label = label + "\\nSelector: " + selector;
            }
            writer.print(label);
            writer.println("\"];");
        }
    }
    
    protected void writeLabels(final PrintWriter writer, final String color, final String prefix, final Map<String, String> map) {
        for (final Map.Entry entry : map.entrySet()) {
            final String id = entry.getKey();
            final String label = entry.getValue();
            writer.print(id);
            writer.print(" [ fillcolor = ");
            writer.print(color);
            writer.print(", label = \"");
            writer.print(prefix);
            writer.print(label);
            writer.println("\"];");
        }
    }
    
    protected String asID(final String name) {
        final StringBuffer buffer = new StringBuffer();
        for (int size = name.length(), i = 0; i < size; ++i) {
            final char ch = name.charAt(i);
            if (Character.isLetterOrDigit(ch) || ch == '_') {
                buffer.append(ch);
            }
            else {
                buffer.append('_');
            }
        }
        return buffer.toString();
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
    
    BrokerViewMBean getBrokerView() throws Exception {
        if (this.brokerView == null) {
            final ObjectName brokerName = this.getBrokerService().getBrokerObjectName();
            this.brokerView = (BrokerViewMBean)this.getBrokerService().getManagementContext().newProxyInstance(brokerName, BrokerViewMBean.class, true);
        }
        return this.brokerView;
    }
}
