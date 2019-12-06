// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.camel.converter;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.camel.Converter;

@Converter
public class ActiveMQConverter
{
    @Converter
    public static ActiveMQDestination toDestination(final String name) {
        return ActiveMQDestination.createDestination(name, (byte)1);
    }
}
