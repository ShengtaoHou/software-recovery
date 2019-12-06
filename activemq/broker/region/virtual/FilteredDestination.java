// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.virtual;

import javax.jms.InvalidSelectorException;
import org.apache.activemq.selector.SelectorParser;
import javax.jms.JMSException;
import org.apache.activemq.filter.MessageEvaluationContext;
import org.apache.activemq.filter.BooleanExpression;
import org.apache.activemq.command.ActiveMQDestination;

public class FilteredDestination
{
    private ActiveMQDestination destination;
    private String selector;
    private BooleanExpression filter;
    
    public boolean matches(final MessageEvaluationContext context) throws JMSException {
        final BooleanExpression booleanExpression = this.getFilter();
        return booleanExpression != null && booleanExpression.matches(context);
    }
    
    public ActiveMQDestination getDestination() {
        return this.destination;
    }
    
    public void setDestination(final ActiveMQDestination destination) {
        this.destination = destination;
    }
    
    public String getSelector() {
        return this.selector;
    }
    
    public void setSelector(final String selector) throws InvalidSelectorException {
        this.selector = selector;
        this.setFilter(SelectorParser.parse(selector));
    }
    
    public BooleanExpression getFilter() {
        return this.filter;
    }
    
    public void setFilter(final BooleanExpression filter) {
        this.filter = filter;
    }
    
    public void setQueue(final String queue) {
        this.setDestination(ActiveMQDestination.createDestination(queue, (byte)1));
    }
    
    public void setTopic(final String topic) {
        this.setDestination(ActiveMQDestination.createDestination(topic, (byte)2));
    }
}
