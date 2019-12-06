// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.group;

public class EmptyMessageGroupSet implements MessageGroupSet
{
    public static final MessageGroupSet INSTANCE;
    
    @Override
    public boolean contains(final String groupID) {
        return false;
    }
    
    static {
        INSTANCE = new EmptyMessageGroupSet();
    }
}
