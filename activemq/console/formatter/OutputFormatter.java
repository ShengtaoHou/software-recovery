// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.formatter;

import javax.jms.Message;
import java.util.Collection;
import java.util.Map;
import javax.management.AttributeList;
import javax.management.ObjectName;
import javax.management.ObjectInstance;
import java.io.OutputStream;

public interface OutputFormatter
{
    OutputStream getOutputStream();
    
    void printMBean(final ObjectInstance p0);
    
    void printMBean(final ObjectName p0);
    
    void printMBean(final AttributeList p0);
    
    void printMBean(final Map p0);
    
    void printMBean(final Collection p0);
    
    void printMessage(final Map p0);
    
    void printMessage(final Message p0);
    
    void printMessage(final Collection p0);
    
    void printHelp(final String[] p0);
    
    void printInfo(final String p0);
    
    void printException(final Exception p0);
    
    void printVersion(final String p0);
    
    void print(final Map p0);
    
    void print(final String[] p0);
    
    void print(final Collection p0);
    
    void print(final String p0);
}
