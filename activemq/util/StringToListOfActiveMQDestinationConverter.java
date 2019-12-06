// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import java.util.ArrayList;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.List;

public class StringToListOfActiveMQDestinationConverter
{
    public static List<ActiveMQDestination> convertToActiveMQDestination(final Object value) {
        if (value == null) {
            return null;
        }
        String text = value.toString();
        if (!text.startsWith("[") || !text.endsWith("]")) {
            return null;
        }
        text = text.substring(1, text.length() - 1).trim();
        if (text.isEmpty()) {
            return null;
        }
        final String[] array = text.split(",");
        final List<ActiveMQDestination> list = new ArrayList<ActiveMQDestination>();
        for (final String item : array) {
            list.add(ActiveMQDestination.createDestination(item.trim(), (byte)1));
        }
        return list;
    }
    
    public static String convertFromActiveMQDestination(final Object value) {
        if (value == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder("[");
        if (value instanceof List) {
            final List list = (List)value;
            for (int i = 0; i < list.size(); ++i) {
                final Object e = list.get(i);
                if (e instanceof ActiveMQDestination) {
                    final ActiveMQDestination destination = (ActiveMQDestination)e;
                    sb.append(destination);
                    if (i < list.size() - 1) {
                        sb.append(", ");
                    }
                }
            }
        }
        sb.append("]");
        if (sb.length() > 2) {
            return sb.toString();
        }
        return null;
    }
}
