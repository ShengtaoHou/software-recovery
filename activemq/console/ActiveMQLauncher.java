// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console;

import java.util.Collection;
import java.util.ArrayList;
import org.apache.activemq.console.command.ShutdownCommand;
import org.apache.activemq.console.command.Command;
import org.apache.activemq.console.command.StartCommand;
import org.apache.activemq.console.formatter.OutputFormatter;
import java.io.OutputStream;
import org.apache.activemq.console.formatter.CommandShellOutputFormatter;
import java.util.Arrays;
import org.apache.commons.daemon.DaemonContext;
import java.util.List;
import org.apache.commons.daemon.Daemon;

public class ActiveMQLauncher implements Daemon
{
    private List<String> args;
    
    public void destroy() {
    }
    
    public void init(final DaemonContext arg0) throws Exception {
        this.args = Arrays.asList(arg0.getArguments());
    }
    
    public void start() throws Exception {
        final CommandContext context = new CommandContext();
        context.setFormatter(new CommandShellOutputFormatter(System.out));
        final Command command = new StartCommand();
        command.setCommandContext(context);
        command.execute(this.args);
    }
    
    public void stop() throws Exception {
        final CommandContext context = new CommandContext();
        context.setFormatter(new CommandShellOutputFormatter(System.out));
        final Command command = new ShutdownCommand();
        command.setCommandContext(context);
        final List<String> tokens = new ArrayList<String>(Arrays.asList("--jmxlocal", "--all"));
        command.execute(tokens);
    }
}
