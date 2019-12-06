// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class AnyChildDestinationNode implements DestinationNode
{
    private DestinationNode node;
    
    public AnyChildDestinationNode(final DestinationNode node) {
        this.node = node;
    }
    
    @Override
    public void appendMatchingValues(final Set answer, final String[] paths, final int startIndex) {
        for (final DestinationNode child : this.getChildNodes()) {
            child.appendMatchingValues(answer, paths, startIndex);
        }
    }
    
    @Override
    public void appendMatchingWildcards(final Set answer, final String[] paths, final int startIndex) {
        for (final DestinationNode child : this.getChildNodes()) {
            child.appendMatchingWildcards(answer, paths, startIndex);
        }
    }
    
    @Override
    public void appendDescendantValues(final Set answer) {
        for (final DestinationNode child : this.getChildNodes()) {
            child.appendDescendantValues(answer);
        }
    }
    
    @Override
    public DestinationNode getChild(final String path) {
        final Collection list = new ArrayList();
        for (final DestinationNode child : this.getChildNodes()) {
            final DestinationNode answer = child.getChild(path);
            if (answer != null) {
                list.add(answer);
            }
        }
        if (!list.isEmpty()) {
            return new AnyChildDestinationNode(this) {
                @Override
                protected Collection getChildNodes() {
                    return list;
                }
            };
        }
        return null;
    }
    
    @Override
    public Collection getDesendentValues() {
        final Collection answer = new ArrayList();
        for (final DestinationNode child : this.getChildNodes()) {
            answer.addAll(child.getDesendentValues());
        }
        return answer;
    }
    
    @Override
    public Collection getValues() {
        final Collection answer = new ArrayList();
        for (final DestinationNode child : this.getChildNodes()) {
            answer.addAll(child.getValues());
        }
        return answer;
    }
    
    @Override
    public Collection getChildren() {
        final Collection answer = new ArrayList();
        for (final DestinationNode child : this.getChildNodes()) {
            answer.addAll(child.getChildren());
        }
        return answer;
    }
    
    @Override
    public Collection removeDesendentValues() {
        final Collection answer = new ArrayList();
        for (final DestinationNode child : this.getChildNodes()) {
            answer.addAll(child.removeDesendentValues());
        }
        return answer;
    }
    
    @Override
    public Collection removeValues() {
        final Collection answer = new ArrayList();
        for (final DestinationNode child : this.getChildNodes()) {
            answer.addAll(child.removeValues());
        }
        return answer;
    }
    
    protected Collection getChildNodes() {
        return this.node.getChildren();
    }
}
