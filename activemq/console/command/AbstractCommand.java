// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.command;

import org.apache.activemq.ActiveMQConnectionMetaData;
import java.util.List;
import org.apache.activemq.console.CommandContext;

public abstract class AbstractCommand implements Command
{
    public static final String COMMAND_OPTION_DELIMETER = ",";
    private boolean isPrintHelp;
    private boolean isPrintVersion;
    protected CommandContext context;
    
    @Override
    public void setCommandContext(final CommandContext context) {
        this.context = context;
    }
    
    @Override
    public void execute(final List<String> tokens) throws Exception {
        this.parseOptions(tokens);
        if (this.isPrintHelp) {
            this.printHelp();
        }
        else if (this.isPrintVersion) {
            this.context.printVersion(ActiveMQConnectionMetaData.PROVIDER_VERSION);
        }
        else {
            this.runTask(tokens);
        }
    }
    
    protected void parseOptions(final List<String> tokens) throws Exception {
        while (!tokens.isEmpty()) {
            final String token = tokens.remove(0);
            if (!token.startsWith("-")) {
                tokens.add(0, token);
                return;
            }
            this.handleOption(token, tokens);
        }
    }
    
    protected void handleOption(final String token, final List<String> tokens) throws Exception {
        this.isPrintHelp = false;
        this.isPrintVersion = false;
        if (token.equals("-h") || token.equals("-?") || token.equals("--help")) {
            this.isPrintHelp = true;
            tokens.clear();
        }
        else if (token.equals("--version")) {
            this.isPrintVersion = true;
            tokens.clear();
        }
        else if (token.startsWith("-D")) {
            String key = token.substring(2);
            String value = "";
            final int pos = key.indexOf("=");
            if (pos >= 0) {
                value = key.substring(pos + 1);
                key = key.substring(0, pos);
            }
            System.setProperty(key, value);
        }
        else {
            this.context.printInfo("Unrecognized option: " + token);
            this.isPrintHelp = true;
        }
    }
    
    protected abstract void runTask(final List<String> p0) throws Exception;
    
    protected abstract void printHelp();
}
