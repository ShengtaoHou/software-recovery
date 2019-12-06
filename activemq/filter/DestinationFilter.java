// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter;

import org.apache.activemq.command.ActiveMQDestination;
import java.io.IOException;
import org.apache.activemq.util.JMSExceptionSupport;
import javax.jms.JMSException;

public abstract class DestinationFilter implements BooleanExpression
{
    public static final String ANY_DESCENDENT = ">";
    public static final String ANY_CHILD = "*";
    
    @Override
    public Object evaluate(final MessageEvaluationContext message) throws JMSException {
        return this.matches(message) ? Boolean.TRUE : Boolean.FALSE;
    }
    
    @Override
    public boolean matches(final MessageEvaluationContext message) throws JMSException {
        try {
            return !message.isDropped() && this.matches(message.getMessage().getDestination());
        }
        catch (IOException e) {
            throw JMSExceptionSupport.create(e);
        }
    }
    
    public abstract boolean matches(final ActiveMQDestination p0);
    
    public static DestinationFilter parseFilter(final ActiveMQDestination destination) {
        if (destination.isComposite()) {
            return new CompositeDestinationFilter(destination);
        }
        final String[] paths = DestinationPath.getDestinationPaths(destination);
        int idx = paths.length - 1;
        if (idx >= 0) {
            String lastPath = paths[idx];
            if (lastPath.equals(">")) {
                return new PrefixDestinationFilter(paths, destination.getDestinationType());
            }
            while (idx >= 0) {
                lastPath = paths[idx--];
                if (lastPath.equals("*")) {
                    return new WildcardDestinationFilter(paths, destination.getDestinationType());
                }
            }
        }
        return new SimpleDestinationFilter(destination);
    }
    
    public abstract boolean isWildcard();
}
