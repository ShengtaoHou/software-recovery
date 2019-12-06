// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.group;

public class SimpleMessageGroupMapFactory implements MessageGroupMapFactory
{
    @Override
    public MessageGroupMap createMessageGroupMap() {
        return new SimpleMessageGroupMap();
    }
}
