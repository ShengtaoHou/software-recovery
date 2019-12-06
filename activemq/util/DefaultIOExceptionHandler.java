// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import org.slf4j.LoggerFactory;
import org.apache.activemq.broker.SuppressReplyException;
import java.util.Iterator;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.Map;
import org.apache.activemq.broker.region.Queue;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.broker.region.RegionBroker;
import java.util.concurrent.TimeUnit;
import java.sql.SQLException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;

public class DefaultIOExceptionHandler implements IOExceptionHandler
{
    private static final Logger LOG;
    protected BrokerService broker;
    private boolean ignoreAllErrors;
    private boolean ignoreNoSpaceErrors;
    private boolean ignoreSQLExceptions;
    private boolean stopStartConnectors;
    private String noSpaceMessage;
    private String sqlExceptionMessage;
    private long resumeCheckSleepPeriod;
    private AtomicBoolean handlingException;
    
    public DefaultIOExceptionHandler() {
        this.ignoreAllErrors = false;
        this.ignoreNoSpaceErrors = true;
        this.ignoreSQLExceptions = true;
        this.stopStartConnectors = false;
        this.noSpaceMessage = "space";
        this.sqlExceptionMessage = "";
        this.resumeCheckSleepPeriod = 5000L;
        this.handlingException = new AtomicBoolean(false);
    }
    
    @Override
    public void handle(final IOException exception) {
        if (this.ignoreAllErrors) {
            DefaultIOExceptionHandler.LOG.info("Ignoring IO exception, " + exception, exception);
            return;
        }
        if (this.ignoreNoSpaceErrors) {
            for (Throwable cause = exception; cause != null && cause instanceof IOException; cause = cause.getCause()) {
                final String message = cause.getMessage();
                if (message != null && message.contains(this.noSpaceMessage)) {
                    DefaultIOExceptionHandler.LOG.info("Ignoring no space left exception, " + exception, exception);
                    return;
                }
            }
        }
        if (this.ignoreSQLExceptions) {
            for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
                final String message = cause.getMessage();
                if (cause instanceof SQLException && message.contains(this.sqlExceptionMessage)) {
                    DefaultIOExceptionHandler.LOG.info("Ignoring SQLException, " + exception, cause);
                    return;
                }
            }
        }
        if (this.stopStartConnectors) {
            if (this.handlingException.compareAndSet(false, true)) {
                DefaultIOExceptionHandler.LOG.info("Initiating stop/restart of transports on " + this.broker + " due to IO exception, " + exception, exception);
                new Thread("IOExceptionHandler: stop transports") {
                    @Override
                    public void run() {
                        try {
                            final ServiceStopper stopper = new ServiceStopper();
                            DefaultIOExceptionHandler.this.broker.stopAllConnectors(stopper);
                            DefaultIOExceptionHandler.LOG.info("Successfully stopped transports on " + DefaultIOExceptionHandler.this.broker);
                        }
                        catch (Exception e) {
                            DefaultIOExceptionHandler.LOG.warn("Failure occurred while stopping broker connectors", e);
                        }
                        finally {
                            new Thread("IOExceptionHandler: restart transports") {
                                @Override
                                public void run() {
                                    try {
                                        while (DefaultIOExceptionHandler.this.hasLockOwnership() && this.isPersistenceAdapterDown()) {
                                            DefaultIOExceptionHandler.LOG.info("waiting for broker persistence adapter checkpoint to succeed before restarting transports");
                                            TimeUnit.MILLISECONDS.sleep(DefaultIOExceptionHandler.this.resumeCheckSleepPeriod);
                                        }
                                        if (DefaultIOExceptionHandler.this.hasLockOwnership()) {
                                            final Map<ActiveMQDestination, Destination> destinations = ((RegionBroker)DefaultIOExceptionHandler.this.broker.getRegionBroker()).getDestinationMap();
                                            for (final Destination destination : destinations.values()) {
                                                if (destination instanceof Queue) {
                                                    final Queue queue = (Queue)destination;
                                                    if (!queue.isResetNeeded()) {
                                                        continue;
                                                    }
                                                    queue.clearPendingMessages();
                                                }
                                            }
                                            DefaultIOExceptionHandler.this.broker.startAllConnectors();
                                            DefaultIOExceptionHandler.LOG.info("Successfully restarted transports on " + DefaultIOExceptionHandler.this.broker);
                                        }
                                    }
                                    catch (Exception e) {
                                        DefaultIOExceptionHandler.LOG.warn("Stopping " + DefaultIOExceptionHandler.this.broker + " due to failure restarting transports", e);
                                        DefaultIOExceptionHandler.this.stopBroker(e);
                                    }
                                    finally {
                                        DefaultIOExceptionHandler.this.handlingException.compareAndSet(true, false);
                                    }
                                }
                                
                                private boolean isPersistenceAdapterDown() {
                                    boolean checkpointSuccess = false;
                                    try {
                                        DefaultIOExceptionHandler.this.broker.getPersistenceAdapter().checkpoint(true);
                                        checkpointSuccess = true;
                                    }
                                    catch (Throwable t) {}
                                    return !checkpointSuccess;
                                }
                            }.start();
                        }
                    }
                }.start();
            }
            throw new SuppressReplyException("Stop/RestartTransportsInitiated", exception);
        }
        if (this.handlingException.compareAndSet(false, true)) {
            this.stopBroker(exception);
        }
        throw new SuppressReplyException("ShutdownBrokerInitiated", exception);
    }
    
    private void stopBroker(final Exception exception) {
        DefaultIOExceptionHandler.LOG.info("Stopping " + this.broker + " due to exception, " + exception, exception);
        new Thread("IOExceptionHandler: stopping " + this.broker) {
            @Override
            public void run() {
                try {
                    if (DefaultIOExceptionHandler.this.broker.isRestartAllowed()) {
                        DefaultIOExceptionHandler.this.broker.requestRestart();
                    }
                    DefaultIOExceptionHandler.this.broker.stop();
                }
                catch (Exception e) {
                    DefaultIOExceptionHandler.LOG.warn("Failure occurred while stopping broker", e);
                }
            }
        }.start();
    }
    
    protected boolean hasLockOwnership() throws IOException {
        return true;
    }
    
    @Override
    public void setBrokerService(final BrokerService broker) {
        this.broker = broker;
    }
    
    public boolean isIgnoreAllErrors() {
        return this.ignoreAllErrors;
    }
    
    public void setIgnoreAllErrors(final boolean ignoreAllErrors) {
        this.ignoreAllErrors = ignoreAllErrors;
    }
    
    public boolean isIgnoreNoSpaceErrors() {
        return this.ignoreNoSpaceErrors;
    }
    
    public void setIgnoreNoSpaceErrors(final boolean ignoreNoSpaceErrors) {
        this.ignoreNoSpaceErrors = ignoreNoSpaceErrors;
    }
    
    public String getNoSpaceMessage() {
        return this.noSpaceMessage;
    }
    
    public void setNoSpaceMessage(final String noSpaceMessage) {
        this.noSpaceMessage = noSpaceMessage;
    }
    
    public boolean isIgnoreSQLExceptions() {
        return this.ignoreSQLExceptions;
    }
    
    public void setIgnoreSQLExceptions(final boolean ignoreSQLExceptions) {
        this.ignoreSQLExceptions = ignoreSQLExceptions;
    }
    
    public String getSqlExceptionMessage() {
        return this.sqlExceptionMessage;
    }
    
    public void setSqlExceptionMessage(final String sqlExceptionMessage) {
        this.sqlExceptionMessage = sqlExceptionMessage;
    }
    
    public boolean isStopStartConnectors() {
        return this.stopStartConnectors;
    }
    
    public void setStopStartConnectors(final boolean stopStartConnectors) {
        this.stopStartConnectors = stopStartConnectors;
    }
    
    public long getResumeCheckSleepPeriod() {
        return this.resumeCheckSleepPeriod;
    }
    
    public void setResumeCheckSleepPeriod(final long resumeCheckSleepPeriod) {
        this.resumeCheckSleepPeriod = resumeCheckSleepPeriod;
    }
    
    static {
        LOG = LoggerFactory.getLogger(DefaultIOExceptionHandler.class);
    }
}
