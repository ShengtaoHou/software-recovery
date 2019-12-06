// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.formatter;

import javax.jms.Message;
import java.util.Collection;
import java.util.Iterator;
import javax.management.Attribute;
import javax.management.AttributeList;
import java.util.Map;
import javax.management.ObjectName;
import javax.management.ObjectInstance;
import java.io.PrintStream;
import java.io.OutputStream;

public class CommandShellOutputFormatter implements OutputFormatter
{
    private OutputStream outputStream;
    private PrintStream out;
    
    public CommandShellOutputFormatter(final OutputStream out) {
        this.outputStream = out;
        if (out instanceof PrintStream) {
            this.out = (PrintStream)out;
        }
        else {
            this.out = new PrintStream(out);
        }
    }
    
    @Override
    public OutputStream getOutputStream() {
        return this.outputStream;
    }
    
    @Override
    public void printMBean(final ObjectInstance mbean) {
        this.printMBean(mbean.getObjectName());
    }
    
    @Override
    public void printMBean(final ObjectName mbean) {
        this.printMBean(mbean.getKeyPropertyList());
    }
    
    @Override
    public void printMBean(final AttributeList mbean) {
        for (final Attribute attrib : mbean) {
            if (attrib.getValue() instanceof ObjectName) {
                this.printMBean((ObjectName)attrib.getValue());
            }
            else if (attrib.getValue() instanceof ObjectInstance) {
                this.printMBean((ObjectInstance)attrib.getValue());
            }
            else {
                this.out.println(attrib.getName() + " = " + attrib.getValue().toString());
                this.out.println();
            }
        }
    }
    
    @Override
    public void printMBean(final Map mbean) {
        for (final String key : mbean.keySet()) {
            final String val = mbean.get(key).toString();
            this.out.println(key + " = " + val);
        }
        this.out.println();
    }
    
    @Override
    public void printMBean(final Collection mbean) {
        for (final Object obj : mbean) {
            if (obj instanceof ObjectInstance) {
                this.printMBean((ObjectInstance)obj);
            }
            else if (obj instanceof ObjectName) {
                this.printMBean((ObjectName)obj);
            }
            else if (obj instanceof Map) {
                this.printMBean((Map)obj);
            }
            else if (obj instanceof AttributeList) {
                this.printMBean((AttributeList)obj);
            }
            else if (obj instanceof Collection) {
                this.printMessage((Collection)obj);
            }
            else {
                this.printException(new UnsupportedOperationException("Unknown mbean type: " + obj.getClass().getName()));
            }
        }
    }
    
    @Override
    public void printMessage(final Map msg) {
        for (final String key : msg.keySet()) {
            final String val = msg.get(key).toString();
            this.out.println(key + " = " + val);
        }
        this.out.println();
    }
    
    @Override
    public void printMessage(final Message msg) {
    }
    
    @Override
    public void printMessage(final Collection msg) {
        for (final Object obj : msg) {
            if (obj instanceof Message) {
                this.printMessage((Message)obj);
            }
            else if (obj instanceof Map) {
                this.printMessage((Map)obj);
            }
            else if (obj instanceof Collection) {
                this.printMessage((Collection)obj);
            }
            else {
                this.printException(new UnsupportedOperationException("Unknown message type: " + obj.getClass().getName()));
            }
        }
    }
    
    @Override
    public void printHelp(final String[] helpMsgs) {
        for (int i = 0; i < helpMsgs.length; ++i) {
            this.out.println(helpMsgs[i]);
        }
        this.out.println();
    }
    
    @Override
    public void printInfo(final String info) {
        this.out.println("INFO: " + info);
    }
    
    @Override
    public void printException(final Exception e) {
        this.out.println("ERROR: " + e);
        e.printStackTrace(this.out);
    }
    
    @Override
    public void printVersion(final String version) {
        this.out.println("");
        this.out.println("ActiveMQ " + version);
        this.out.println("For help or more information please see: http://activemq.apache.org");
        this.out.println("");
    }
    
    @Override
    public void print(final Map map) {
        for (final String key : map.keySet()) {
            final String val = map.get(key).toString();
            this.out.println(key + " = " + val);
        }
        this.out.println();
    }
    
    @Override
    public void print(final String[] strings) {
        for (int i = 0; i < strings.length; ++i) {
            this.out.println(strings[i]);
        }
        this.out.println();
    }
    
    @Override
    public void print(final Collection collection) {
        final Iterator i = collection.iterator();
        while (i.hasNext()) {
            this.out.println(i.next().toString());
        }
        this.out.println();
    }
    
    @Override
    public void print(final String string) {
        this.out.println(string);
    }
}
