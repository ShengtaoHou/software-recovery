// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.command;

import java.util.List;
import org.apache.activemq.console.CommandContext;

public interface Command
{
    String getName();
    
    String getOneLineDescription();
    
    void setCommandContext(final CommandContext p0);
    
    void execute(final List<String> p0) throws Exception;
}
