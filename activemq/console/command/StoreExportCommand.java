// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.command;

import java.util.Arrays;
import org.apache.activemq.console.command.store.amq.CommandLineSupport;
import org.apache.activemq.console.command.store.StoreExporter;
import java.util.List;
import org.apache.activemq.console.CommandContext;

public class StoreExportCommand implements Command
{
    private CommandContext context;
    
    @Override
    public void setCommandContext(final CommandContext context) {
        this.context = context;
    }
    
    @Override
    public String getName() {
        return "export";
    }
    
    @Override
    public String getOneLineDescription() {
        return "Exports a stopped brokers data files to an archive file";
    }
    
    @Override
    public void execute(final List<String> tokens) throws Exception {
        final StoreExporter exporter = new StoreExporter();
        final String[] remaining = CommandLineSupport.setOptions(exporter, tokens.toArray(new String[tokens.size()]));
        if (remaining.length > 0) {
            throw new Exception("Unexpected arguments: " + Arrays.asList(remaining));
        }
        exporter.execute();
    }
}
