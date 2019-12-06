// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.state;

import org.apache.activemq.command.ConsumerInfo;

public class ConsumerState
{
    final ConsumerInfo info;
    
    public ConsumerState(final ConsumerInfo info) {
        this.info = info;
    }
    
    @Override
    public String toString() {
        return this.info.toString();
    }
    
    public ConsumerInfo getInfo() {
        return this.info;
    }
}
