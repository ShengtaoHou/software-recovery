// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

import java.util.Collections;
import java.util.HashSet;
import org.slf4j.LoggerFactory;
import org.apache.activemq.util.JMXSupport;
import org.apache.activemq.broker.jmx.AnnotatedMBean;
import java.util.Iterator;
import javax.management.ObjectName;
import org.apache.activemq.broker.jmx.ManagementContext;
import java.lang.ref.WeakReference;
import java.util.Set;
import org.slf4j.Logger;

public class TransportLoggerView implements TransportLoggerViewMBean
{
    private static final Logger log;
    private static Set<TransportLoggerView> transportLoggerViews;
    private final WeakReference<TransportLogger> transportLogger;
    private final String nextTransportName;
    private final int id;
    private final ManagementContext managementContext;
    private final ObjectName name;
    
    public TransportLoggerView(final TransportLogger transportLogger, final String nextTransportName, final int id, final ManagementContext managementContext) {
        this.transportLogger = new WeakReference<TransportLogger>(transportLogger);
        this.nextTransportName = nextTransportName;
        this.id = id;
        this.managementContext = managementContext;
        this.name = this.createTransportLoggerObjectName();
        TransportLoggerView.transportLoggerViews.add(this);
        this.register();
    }
    
    public static void enableAllTransportLoggers() {
        for (final TransportLoggerView view : TransportLoggerView.transportLoggerViews) {
            view.enableLogging();
        }
    }
    
    public static void disableAllTransportLoggers() {
        for (final TransportLoggerView view : TransportLoggerView.transportLoggerViews) {
            view.disableLogging();
        }
    }
    
    @Override
    public void enableLogging() {
        this.setLogging(true);
    }
    
    @Override
    public void disableLogging() {
        this.setLogging(false);
    }
    
    @Override
    public boolean isLogging() {
        return this.transportLogger.get().isLogging();
    }
    
    @Override
    public void setLogging(final boolean logging) {
        this.transportLogger.get().setLogging(logging);
    }
    
    private void register() {
        try {
            AnnotatedMBean.registerMBean(this.managementContext, this, this.name);
        }
        catch (Exception e) {
            TransportLoggerView.log.error("Could not register MBean for TransportLoggerView " + this.id + "with name " + this.name.toString() + ", reason: " + e, e);
        }
    }
    
    public void unregister() {
        TransportLoggerView.transportLoggerViews.remove(this);
        try {
            this.managementContext.unregisterMBean(this.name);
        }
        catch (Exception e) {
            TransportLoggerView.log.error("Could not unregister MBean for TransportLoggerView " + this.id + "with name " + this.name.toString() + ", reason: " + e, e);
        }
    }
    
    private ObjectName createTransportLoggerObjectName() {
        try {
            return new ObjectName(createTransportLoggerObjectNameRoot(this.managementContext) + JMXSupport.encodeObjectNamePart(TransportLogger.class.getSimpleName() + " " + this.id + ";" + this.nextTransportName));
        }
        catch (Exception e) {
            TransportLoggerView.log.error("Could not create ObjectName for TransportLoggerView " + this.id + ", reason: " + e, e);
            return null;
        }
    }
    
    public static String createTransportLoggerObjectNameRoot(final ManagementContext managementContext) {
        return managementContext.getJmxDomainName() + ":Type=TransportLogger,TransportLoggerName=";
    }
    
    static {
        log = LoggerFactory.getLogger(TransportLoggerView.class);
        TransportLoggerView.transportLoggerViews = Collections.synchronizedSet(new HashSet<TransportLoggerView>());
    }
}
