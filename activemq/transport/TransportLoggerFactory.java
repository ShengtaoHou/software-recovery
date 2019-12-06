// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

import org.apache.activemq.broker.jmx.AnnotatedMBean;
import org.slf4j.LoggerFactory;
import org.apache.activemq.util.IOExceptionSupport;
import java.io.IOException;
import javax.management.ObjectName;
import org.apache.activemq.broker.jmx.ManagementContext;
import org.apache.activemq.util.LogWriterFinder;
import org.slf4j.Logger;

public class TransportLoggerFactory
{
    private static final Logger LOG;
    private static TransportLoggerFactory instance;
    private static int lastId;
    private static final LogWriterFinder logWriterFinder;
    public static String defaultLogWriterName;
    private static boolean defaultDynamicManagement;
    private static boolean defaultInitialBehavior;
    private static int defaultJmxPort;
    private boolean transportLoggerControlCreated;
    private ManagementContext managementContext;
    private ObjectName objectName;
    
    private TransportLoggerFactory() {
        this.transportLoggerControlCreated = false;
    }
    
    public static synchronized TransportLoggerFactory getInstance() {
        if (TransportLoggerFactory.instance == null) {
            TransportLoggerFactory.instance = new TransportLoggerFactory();
        }
        return TransportLoggerFactory.instance;
    }
    
    public void stop() {
        try {
            if (this.transportLoggerControlCreated) {
                this.managementContext.unregisterMBean(this.objectName);
                this.managementContext.stop();
                this.managementContext = null;
            }
        }
        catch (Exception e) {
            TransportLoggerFactory.LOG.error("TransportLoggerFactory could not be stopped, reason: " + e, e);
        }
    }
    
    public TransportLogger createTransportLogger(final Transport next) throws IOException {
        final int id = getNextId();
        return this.createTransportLogger(next, id, createLog(id), TransportLoggerFactory.defaultLogWriterName, TransportLoggerFactory.defaultDynamicManagement, TransportLoggerFactory.defaultInitialBehavior, TransportLoggerFactory.defaultJmxPort);
    }
    
    public TransportLogger createTransportLogger(final Transport next, final Logger log) throws IOException {
        return this.createTransportLogger(next, getNextId(), log, TransportLoggerFactory.defaultLogWriterName, TransportLoggerFactory.defaultDynamicManagement, TransportLoggerFactory.defaultInitialBehavior, TransportLoggerFactory.defaultJmxPort);
    }
    
    public TransportLogger createTransportLogger(final Transport next, final String logWriterName, final boolean useJmx, final boolean startLogging, final int jmxport) throws IOException {
        final int id = getNextId();
        return this.createTransportLogger(next, id, createLog(id), logWriterName, useJmx, startLogging, jmxport);
    }
    
    public TransportLogger createTransportLogger(final Transport next, final int id, final Logger log, final String logWriterName, final boolean dynamicManagement, final boolean startLogging, final int jmxport) throws IOException {
        try {
            final LogWriter logWriter = TransportLoggerFactory.logWriterFinder.newInstance(logWriterName);
            final TransportLogger tl = new TransportLogger(next, log, startLogging, logWriter);
            if (dynamicManagement) {
                synchronized (this) {
                    if (!this.transportLoggerControlCreated) {
                        this.createTransportLoggerControl(jmxport);
                    }
                }
                final TransportLoggerView tlv = new TransportLoggerView(tl, next.toString(), id, this.managementContext);
                tl.setView(tlv);
            }
            return tl;
        }
        catch (Throwable e) {
            throw IOExceptionSupport.create("Could not create log writer object for: " + logWriterName + ", reason: " + e, e);
        }
    }
    
    private static synchronized int getNextId() {
        return ++TransportLoggerFactory.lastId;
    }
    
    private static Logger createLog(final int id) {
        return LoggerFactory.getLogger(TransportLogger.class.getName() + ".Connection:" + id);
    }
    
    private void createTransportLoggerControl(final int port) {
        try {
            (this.managementContext = new ManagementContext()).setConnectorPort(port);
            this.managementContext.start();
        }
        catch (Exception e) {
            TransportLoggerFactory.LOG.error("Management context could not be started, reason: " + e, e);
        }
        try {
            this.objectName = new ObjectName(this.managementContext.getJmxDomainName() + ":Type=TransportLoggerControl");
            AnnotatedMBean.registerMBean(this.managementContext, new TransportLoggerControl(this.managementContext), this.objectName);
            this.transportLoggerControlCreated = true;
        }
        catch (Exception e) {
            TransportLoggerFactory.LOG.error("TransportLoggerControlMBean could not be registered, reason: " + e, e);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(TransportLoggerFactory.class);
        TransportLoggerFactory.lastId = 0;
        logWriterFinder = new LogWriterFinder("META-INF/services/org/apache/activemq/transport/logwriters/");
        TransportLoggerFactory.defaultLogWriterName = "default";
        TransportLoggerFactory.defaultDynamicManagement = false;
        TransportLoggerFactory.defaultInitialBehavior = true;
        TransportLoggerFactory.defaultJmxPort = 1099;
    }
}
