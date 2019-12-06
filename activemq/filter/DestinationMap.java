// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.activemq.command.ActiveMQDestination;

public class DestinationMap
{
    protected static final String ANY_DESCENDENT = ">";
    protected static final String ANY_CHILD = "*";
    private DestinationMapNode queueRootNode;
    private DestinationMapNode tempQueueRootNode;
    private DestinationMapNode topicRootNode;
    private DestinationMapNode tempTopicRootNode;
    
    public DestinationMap() {
        this.queueRootNode = new DestinationMapNode(null);
        this.tempQueueRootNode = new DestinationMapNode(null);
        this.topicRootNode = new DestinationMapNode(null);
        this.tempTopicRootNode = new DestinationMapNode(null);
    }
    
    public synchronized Set get(final ActiveMQDestination key) {
        if (key.isComposite()) {
            final ActiveMQDestination[] destinations = key.getCompositeDestinations();
            final Set answer = new HashSet(destinations.length);
            for (int i = 0; i < destinations.length; ++i) {
                final ActiveMQDestination childDestination = destinations[i];
                final Object value = this.get(childDestination);
                if (value instanceof Set) {
                    answer.addAll((Collection)value);
                }
                else if (value != null) {
                    answer.add(value);
                }
            }
            return answer;
        }
        return this.findWildcardMatches(key);
    }
    
    public synchronized void put(final ActiveMQDestination key, final Object value) {
        if (key.isComposite()) {
            final ActiveMQDestination[] destinations = key.getCompositeDestinations();
            for (int i = 0; i < destinations.length; ++i) {
                final ActiveMQDestination childDestination = destinations[i];
                this.put(childDestination, value);
            }
            return;
        }
        final String[] paths = key.getDestinationPaths();
        this.getRootNode(key).add(paths, 0, value);
    }
    
    public synchronized void remove(final ActiveMQDestination key, final Object value) {
        if (key.isComposite()) {
            final ActiveMQDestination[] destinations = key.getCompositeDestinations();
            for (int i = 0; i < destinations.length; ++i) {
                final ActiveMQDestination childDestination = destinations[i];
                this.remove(childDestination, value);
            }
            return;
        }
        final String[] paths = key.getDestinationPaths();
        this.getRootNode(key).remove(paths, 0, value);
    }
    
    public int getTopicRootChildCount() {
        return this.topicRootNode.getChildCount();
    }
    
    public int getQueueRootChildCount() {
        return this.queueRootNode.getChildCount();
    }
    
    public DestinationMapNode getQueueRootNode() {
        return this.queueRootNode;
    }
    
    public DestinationMapNode getTopicRootNode() {
        return this.topicRootNode;
    }
    
    public DestinationMapNode getTempQueueRootNode() {
        return this.tempQueueRootNode;
    }
    
    public DestinationMapNode getTempTopicRootNode() {
        return this.tempTopicRootNode;
    }
    
    protected void setEntries(final List<DestinationMapEntry> entries) {
        for (final Object element : entries) {
            final Class<? extends DestinationMapEntry> type = this.getEntryClass();
            if (!type.isInstance(element)) {
                throw new IllegalArgumentException("Each entry must be an instance of type: " + type.getName() + " but was: " + element);
            }
            final DestinationMapEntry entry = (DestinationMapEntry)element;
            this.put(entry.getDestination(), entry.getValue());
        }
    }
    
    protected Class<? extends DestinationMapEntry> getEntryClass() {
        return DestinationMapEntry.class;
    }
    
    protected Set findWildcardMatches(final ActiveMQDestination key) {
        final String[] paths = key.getDestinationPaths();
        final Set answer = new HashSet();
        this.getRootNode(key).appendMatchingValues(answer, paths, 0);
        return answer;
    }
    
    public Set removeAll(final ActiveMQDestination key) {
        final Set rc = new HashSet();
        if (key.isComposite()) {
            final ActiveMQDestination[] destinations = key.getCompositeDestinations();
            for (int i = 0; i < destinations.length; ++i) {
                rc.add(this.removeAll(destinations[i]));
            }
            return rc;
        }
        final String[] paths = key.getDestinationPaths();
        this.getRootNode(key).removeAll(rc, paths, 0);
        return rc;
    }
    
    public Object chooseValue(final ActiveMQDestination destination) {
        final Set set = this.get(destination);
        if (set == null || set.isEmpty()) {
            return null;
        }
        final SortedSet sortedSet = new TreeSet(set);
        return sortedSet.last();
    }
    
    protected DestinationMapNode getRootNode(final ActiveMQDestination key) {
        if (key.isTemporary()) {
            if (key.isQueue()) {
                return this.tempQueueRootNode;
            }
            return this.tempTopicRootNode;
        }
        else {
            if (key.isQueue()) {
                return this.queueRootNode;
            }
            return this.topicRootNode;
        }
    }
    
    public void reset() {
        this.queueRootNode = new DestinationMapNode(null);
        this.tempQueueRootNode = new DestinationMapNode(null);
        this.topicRootNode = new DestinationMapNode(null);
        this.tempTopicRootNode = new DestinationMapNode(null);
    }
    
    public boolean isEmpty() {
        return this.queueRootNode.isEmpty() && this.topicRootNode.isEmpty() && this.tempQueueRootNode.isEmpty() && this.tempTopicRootNode.isEmpty();
    }
    
    public static Set union(Set existing, final Set candidates) {
        if (candidates != null) {
            if (existing != null) {
                final Iterator<Object> iterator = existing.iterator();
                while (iterator.hasNext()) {
                    final Object toMatch = iterator.next();
                    if (!candidates.contains(toMatch)) {
                        iterator.remove();
                    }
                }
            }
            else {
                existing = candidates;
            }
        }
        else if (existing != null) {
            existing.clear();
        }
        return existing;
    }
}
