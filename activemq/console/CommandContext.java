// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console;

import javax.jms.Message;
import java.util.Collection;
import java.util.Map;
import javax.management.AttributeList;
import javax.management.ObjectName;
import javax.management.ObjectInstance;
import java.io.OutputStream;
import org.apache.activemq.console.formatter.OutputFormatter;

public final class CommandContext
{
    private OutputFormatter formatter;
    
    public OutputStream getOutputStream() {
        if (this.formatter == null) {
            throw new IllegalStateException("No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter).");
        }
        return this.formatter.getOutputStream();
    }
    
    public void printMBean(final ObjectInstance mbean) {
        if (this.formatter == null) {
            throw new IllegalStateException("No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter).");
        }
        this.formatter.printMBean(mbean);
    }
    
    public void printMBean(final ObjectName mbean) {
        if (this.formatter == null) {
            throw new IllegalStateException("No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter).");
        }
        this.formatter.printMBean(mbean);
    }
    
    public void printMBean(final AttributeList mbean) {
        if (this.formatter == null) {
            throw new IllegalStateException("No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter).");
        }
        this.formatter.printMBean(mbean);
    }
    
    public void printMBean(final Map mbean) {
        if (this.formatter == null) {
            throw new IllegalStateException("No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter).");
        }
        this.formatter.printMBean(mbean);
    }
    
    public void printMBean(final Collection mbean) {
        if (this.formatter == null) {
            throw new IllegalStateException("No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter).");
        }
        this.formatter.printMBean(mbean);
    }
    
    public void printMessage(final Map msg) {
        if (this.formatter == null) {
            throw new IllegalStateException("No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter).");
        }
        this.formatter.printMessage(msg);
    }
    
    public void printMessage(final Message msg) {
        if (this.formatter == null) {
            throw new IllegalStateException("No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter).");
        }
        this.formatter.printMessage(msg);
    }
    
    public void printMessage(final Collection msg) {
        if (this.formatter == null) {
            throw new IllegalStateException("No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter).");
        }
        this.formatter.printMessage(msg);
    }
    
    public void printHelp(final String[] helpMsgs) {
        if (this.formatter == null) {
            throw new IllegalStateException("No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter).");
        }
        this.formatter.printHelp(helpMsgs);
    }
    
    public void printInfo(final String info) {
        if (this.formatter == null) {
            throw new IllegalStateException("No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter).");
        }
        this.formatter.printInfo(info);
    }
    
    public void printException(final Exception e) {
        if (this.formatter == null) {
            throw new IllegalStateException("No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter).");
        }
        this.formatter.printException(e);
    }
    
    public void printVersion(final String version) {
        if (this.formatter == null) {
            throw new IllegalStateException("No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter).");
        }
        this.formatter.printVersion(version);
    }
    
    public void print(final Map map) {
        if (this.formatter == null) {
            throw new IllegalStateException("No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter).");
        }
        this.formatter.print(map);
    }
    
    public void print(final String[] strings) {
        if (this.formatter == null) {
            throw new IllegalStateException("No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter).");
        }
        this.formatter.print(strings);
    }
    
    public void print(final Collection collection) {
        if (this.formatter == null) {
            throw new IllegalStateException("No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter).");
        }
        this.formatter.print(collection);
    }
    
    public void print(final String string) {
        if (this.formatter == null) {
            throw new IllegalStateException("No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter).");
        }
        this.formatter.print(string);
    }
    
    public OutputFormatter getFormatter() {
        return this.formatter;
    }
    
    public void setFormatter(final OutputFormatter formatter) {
        this.formatter = formatter;
    }
}
