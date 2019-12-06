// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter;

import org.apache.activemq.command.ActiveMQDestination;
import javax.jms.JMSException;
import org.apache.activemq.command.Message;
import java.util.List;
import java.util.ArrayList;

public final class DestinationPath
{
    protected static final char SEPARATOR = '.';
    
    private DestinationPath() {
    }
    
    public static String[] getDestinationPaths(final String subject) {
        final List<String> list = new ArrayList<String>();
        int previous = 0;
        final int lastIndex = subject.length() - 1;
        while (true) {
            final int idx = subject.indexOf(46, previous);
            if (idx < 0) {
                break;
            }
            list.add(subject.substring(previous, idx));
            previous = idx + 1;
        }
        list.add(subject.substring(previous, lastIndex + 1));
        final String[] answer = new String[list.size()];
        list.toArray(answer);
        return answer;
    }
    
    public static String[] getDestinationPaths(final Message message) throws JMSException {
        return getDestinationPaths(message.getDestination());
    }
    
    public static String[] getDestinationPaths(final ActiveMQDestination destination) {
        return getDestinationPaths(destination.getPhysicalName());
    }
    
    public static String toString(final String[] paths) {
        final StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < paths.length; ++i) {
            if (i > 0) {
                buffer.append('.');
            }
            final String path = paths[i];
            if (path == null) {
                buffer.append("*");
            }
            else {
                buffer.append(path);
            }
        }
        return buffer.toString();
    }
}
