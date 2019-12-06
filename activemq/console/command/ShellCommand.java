// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.command;

import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import org.apache.activemq.console.formatter.OutputFormatter;
import java.io.OutputStream;
import org.apache.activemq.console.formatter.CommandShellOutputFormatter;
import org.apache.activemq.console.CommandContext;
import java.io.PrintStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;

public class ShellCommand extends AbstractCommand
{
    private boolean interactive;
    private String[] helpFile;
    
    public ShellCommand() {
        this(false);
    }
    
    public ShellCommand(final boolean interactive) {
        this.interactive = interactive;
        final ArrayList<String> help = new ArrayList<String>();
        help.addAll(Arrays.asList(interactive ? "Usage: [task] [task-options] [task data]" : "Usage: Main [--extdir <dir>] [task] [task-options] [task data]", "", "Tasks:"));
        final ArrayList<Command> commands = this.getCommands();
        Collections.sort(commands, new Comparator<Command>() {
            @Override
            public int compare(final Command command, final Command command1) {
                return command.getName().compareTo(command1.getName());
            }
        });
        for (final Command command : commands) {
            help.add(String.format("    %-24s - %s", command.getName(), command.getOneLineDescription()));
        }
        help.addAll(Arrays.asList("", "Task Options (Options specific to each task):", "    --extdir <dir>  - Add the jar files in the directory to the classpath.", "    --version       - Display the version information.", "    -h,-?,--help    - Display this help information. To display task specific help, use " + (interactive ? "" : "Main ") + "[task] -h,-?,--help", "", "Task Data:", "    - Information needed by each specific task.", "", "JMX system property options:", "    -Dactivemq.jmx.url=<jmx service uri> (default is: 'service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi')", "    -Dactivemq.jmx.user=<user name>", "    -Dactivemq.jmx.password=<password>", ""));
        this.helpFile = help.toArray(new String[help.size()]);
    }
    
    @Override
    public String getName() {
        return "shell";
    }
    
    @Override
    public String getOneLineDescription() {
        return "Runs the activemq sub shell";
    }
    
    public static int main(final String[] args, final InputStream in, final PrintStream out) {
        final CommandContext context = new CommandContext();
        context.setFormatter(new CommandShellOutputFormatter(out));
        final List<String> tokens = new ArrayList<String>(Arrays.asList(args));
        final ShellCommand main = new ShellCommand();
        try {
            main.setCommandContext(context);
            main.execute(tokens);
            return 0;
        }
        catch (Exception e) {
            context.printException(e);
            return -1;
        }
    }
    
    public boolean isInteractive() {
        return this.interactive;
    }
    
    public void setInteractive(final boolean interactive) {
        this.interactive = interactive;
    }
    
    @Override
    protected void runTask(final List<String> tokens) throws Exception {
        if (tokens.size() > 0) {
            Command command = null;
            final String taskToken = tokens.remove(0);
            for (final Command c : this.getCommands()) {
                if (taskToken.equals(c.getName())) {
                    command = c;
                    break;
                }
            }
            if (command == null) {
                if (taskToken.equals("help")) {
                    this.printHelp();
                }
                else {
                    this.printHelp();
                }
            }
            if (command != null) {
                command.setCommandContext(this.context);
                command.execute(tokens);
            }
        }
        else {
            this.printHelp();
        }
    }
    
    ArrayList<Command> getCommands() {
        final ServiceLoader<Command> loader = ServiceLoader.load(Command.class);
        final Iterator<Command> iterator = loader.iterator();
        final ArrayList<Command> rc = new ArrayList<Command>();
        boolean done = false;
        while (!done) {
            try {
                if (iterator.hasNext()) {
                    rc.add(iterator.next());
                }
                else {
                    done = true;
                }
            }
            catch (ServiceConfigurationError serviceConfigurationError) {}
        }
        return rc;
    }
    
    @Override
    protected void printHelp() {
        this.context.printHelp(this.helpFile);
    }
}
