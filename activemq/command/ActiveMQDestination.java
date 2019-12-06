// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import org.apache.activemq.util.IntrospectionSupport;
import java.util.Properties;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.HashSet;
import java.net.URISyntaxException;
import org.apache.activemq.util.URISupport;
import javax.jms.JMSException;
import javax.jms.TemporaryTopic;
import javax.jms.TemporaryQueue;
import javax.jms.Topic;
import javax.jms.Queue;
import java.util.Map;
import java.io.Externalizable;
import javax.jms.Destination;
import org.apache.activemq.jndi.JNDIBaseStorable;

public abstract class ActiveMQDestination extends JNDIBaseStorable implements DataStructure, Destination, Externalizable, Comparable<Object>
{
    public static final String PATH_SEPERATOR = ".";
    public static final char COMPOSITE_SEPERATOR = ',';
    public static final byte QUEUE_TYPE = 1;
    public static final byte TOPIC_TYPE = 2;
    public static final byte TEMP_MASK = 4;
    public static final byte TEMP_TOPIC_TYPE = 6;
    public static final byte TEMP_QUEUE_TYPE = 5;
    public static final String QUEUE_QUALIFIED_PREFIX = "queue://";
    public static final String TOPIC_QUALIFIED_PREFIX = "topic://";
    public static final String TEMP_QUEUE_QUALIFED_PREFIX = "temp-queue://";
    public static final String TEMP_TOPIC_QUALIFED_PREFIX = "temp-topic://";
    public static final String TEMP_DESTINATION_NAME_PREFIX = "ID:";
    private static final long serialVersionUID = -3885260014960795889L;
    protected String physicalName;
    protected transient ActiveMQDestination[] compositeDestinations;
    protected transient String[] destinationPaths;
    protected transient boolean isPattern;
    protected transient int hashValue;
    protected Map<String, String> options;
    protected static UnresolvedDestinationTransformer unresolvableDestinationTransformer;
    
    public ActiveMQDestination() {
    }
    
    protected ActiveMQDestination(final String name) {
        this.setPhysicalName(name);
    }
    
    public ActiveMQDestination(final ActiveMQDestination[] composites) {
        this.setCompositeDestinations(composites);
    }
    
    public static ActiveMQDestination createDestination(final String name, final byte defaultType) {
        if (name.startsWith("queue://")) {
            return new ActiveMQQueue(name.substring("queue://".length()));
        }
        if (name.startsWith("topic://")) {
            return new ActiveMQTopic(name.substring("topic://".length()));
        }
        if (name.startsWith("temp-queue://")) {
            return new ActiveMQTempQueue(name.substring("temp-queue://".length()));
        }
        if (name.startsWith("temp-topic://")) {
            return new ActiveMQTempTopic(name.substring("temp-topic://".length()));
        }
        switch (defaultType) {
            case 1: {
                return new ActiveMQQueue(name);
            }
            case 2: {
                return new ActiveMQTopic(name);
            }
            case 5: {
                return new ActiveMQTempQueue(name);
            }
            case 6: {
                return new ActiveMQTempTopic(name);
            }
            default: {
                throw new IllegalArgumentException("Invalid default destination type: " + defaultType);
            }
        }
    }
    
    public static ActiveMQDestination transform(final Destination dest) throws JMSException {
        if (dest == null) {
            return null;
        }
        if (dest instanceof ActiveMQDestination) {
            return (ActiveMQDestination)dest;
        }
        if (dest instanceof Queue && dest instanceof Topic) {
            final String queueName = ((Queue)dest).getQueueName();
            final String topicName = ((Topic)dest).getTopicName();
            if (queueName != null && topicName == null) {
                return new ActiveMQQueue(queueName);
            }
            if (queueName == null && topicName != null) {
                return new ActiveMQTopic(topicName);
            }
            return ActiveMQDestination.unresolvableDestinationTransformer.transform(dest);
        }
        else {
            if (dest instanceof TemporaryQueue) {
                return new ActiveMQTempQueue(((TemporaryQueue)dest).getQueueName());
            }
            if (dest instanceof TemporaryTopic) {
                return new ActiveMQTempTopic(((TemporaryTopic)dest).getTopicName());
            }
            if (dest instanceof Queue) {
                return new ActiveMQQueue(((Queue)dest).getQueueName());
            }
            if (dest instanceof Topic) {
                return new ActiveMQTopic(((Topic)dest).getTopicName());
            }
            throw new JMSException("Could not transform the destination into a ActiveMQ destination: " + dest);
        }
    }
    
    public static int compare(final ActiveMQDestination destination, final ActiveMQDestination destination2) {
        if (destination == destination2) {
            return 0;
        }
        if (destination == null) {
            return -1;
        }
        if (destination2 == null) {
            return 1;
        }
        if (destination.isQueue() == destination2.isQueue()) {
            return destination.getPhysicalName().compareTo(destination2.getPhysicalName());
        }
        return destination.isQueue() ? -1 : 1;
    }
    
    @Override
    public int compareTo(final Object that) {
        if (that instanceof ActiveMQDestination) {
            return compare(this, (ActiveMQDestination)that);
        }
        if (that == null) {
            return 1;
        }
        return this.getClass().getName().compareTo(that.getClass().getName());
    }
    
    public boolean isComposite() {
        return this.compositeDestinations != null;
    }
    
    public ActiveMQDestination[] getCompositeDestinations() {
        return this.compositeDestinations;
    }
    
    public void setCompositeDestinations(final ActiveMQDestination[] destinations) {
        this.compositeDestinations = destinations;
        this.destinationPaths = null;
        this.hashValue = 0;
        this.isPattern = false;
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < destinations.length; ++i) {
            if (i != 0) {
                sb.append(',');
            }
            if (this.getDestinationType() == destinations[i].getDestinationType()) {
                sb.append(destinations[i].getPhysicalName());
            }
            else {
                sb.append(destinations[i].getQualifiedName());
            }
        }
        this.physicalName = sb.toString();
    }
    
    public String getQualifiedName() {
        if (this.isComposite()) {
            return this.physicalName;
        }
        return this.getQualifiedPrefix() + this.physicalName;
    }
    
    protected abstract String getQualifiedPrefix();
    
    public String getPhysicalName() {
        return this.physicalName;
    }
    
    public void setPhysicalName(String physicalName) {
        physicalName = physicalName.trim();
        final int len = physicalName.length();
        int p = -1;
        boolean composite = false;
        for (int i = 0; i < len; ++i) {
            final char c = physicalName.charAt(i);
            if (c == '?') {
                p = i;
                break;
            }
            if (c == ',') {
                this.isPattern = false;
                composite = true;
            }
            else if (!composite && (c == '*' || c == '>')) {
                this.isPattern = true;
            }
        }
        if (p >= 0) {
            final String optstring = physicalName.substring(p + 1);
            physicalName = physicalName.substring(0, p);
            try {
                this.options = URISupport.parseQuery(optstring);
            }
            catch (URISyntaxException e) {
                throw new IllegalArgumentException("Invalid destination name: " + physicalName + ", it's options are not encoded properly: " + e);
            }
        }
        this.physicalName = physicalName;
        this.destinationPaths = null;
        this.hashValue = 0;
        if (composite) {
            final Set<String> l = new HashSet<String>();
            final StringTokenizer iter = new StringTokenizer(physicalName, ",");
            while (iter.hasMoreTokens()) {
                final String name = iter.nextToken().trim();
                if (name.length() == 0) {
                    continue;
                }
                l.add(name);
            }
            this.compositeDestinations = new ActiveMQDestination[l.size()];
            int counter = 0;
            for (final String dest : l) {
                this.compositeDestinations[counter++] = this.createDestination(dest);
            }
        }
    }
    
    public ActiveMQDestination createDestination(final String name) {
        return createDestination(name, this.getDestinationType());
    }
    
    public String[] getDestinationPaths() {
        if (this.destinationPaths != null) {
            return this.destinationPaths;
        }
        final List<String> l = new ArrayList<String>();
        final StringBuilder level = new StringBuilder();
        final char separator = ".".charAt(0);
        for (final char c : this.physicalName.toCharArray()) {
            if (c == separator) {
                l.add(level.toString());
                level.delete(0, level.length());
            }
            else {
                level.append(c);
            }
        }
        l.add(level.toString());
        l.toArray(this.destinationPaths = new String[l.size()]);
        return this.destinationPaths;
    }
    
    public abstract byte getDestinationType();
    
    public boolean isQueue() {
        return false;
    }
    
    public boolean isTopic() {
        return false;
    }
    
    public boolean isTemporary() {
        return false;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final ActiveMQDestination d = (ActiveMQDestination)o;
        return this.physicalName.equals(d.physicalName);
    }
    
    @Override
    public int hashCode() {
        if (this.hashValue == 0) {
            this.hashValue = this.physicalName.hashCode();
        }
        return this.hashValue;
    }
    
    @Override
    public String toString() {
        return this.getQualifiedName();
    }
    
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeUTF(this.getPhysicalName());
        out.writeObject(this.options);
    }
    
    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        this.setPhysicalName(in.readUTF());
        this.options = (Map<String, String>)in.readObject();
    }
    
    public String getDestinationTypeAsString() {
        switch (this.getDestinationType()) {
            case 1: {
                return "Queue";
            }
            case 2: {
                return "Topic";
            }
            case 5: {
                return "TempQueue";
            }
            case 6: {
                return "TempTopic";
            }
            default: {
                throw new IllegalArgumentException("Invalid destination type: " + this.getDestinationType());
            }
        }
    }
    
    public Map<String, String> getOptions() {
        return this.options;
    }
    
    @Override
    public boolean isMarshallAware() {
        return false;
    }
    
    public void buildFromProperties(Properties properties) {
        if (properties == null) {
            properties = new Properties();
        }
        IntrospectionSupport.setProperties(this, properties);
    }
    
    public void populateProperties(final Properties props) {
        props.setProperty("physicalName", this.getPhysicalName());
    }
    
    public boolean isPattern() {
        return this.isPattern;
    }
    
    public static UnresolvedDestinationTransformer getUnresolvableDestinationTransformer() {
        return ActiveMQDestination.unresolvableDestinationTransformer;
    }
    
    public static void setUnresolvableDestinationTransformer(final UnresolvedDestinationTransformer unresolvableDestinationTransformer) {
        ActiveMQDestination.unresolvableDestinationTransformer = unresolvableDestinationTransformer;
    }
    
    static {
        ActiveMQDestination.unresolvableDestinationTransformer = new DefaultUnresolvedDestinationTransformer();
    }
}
