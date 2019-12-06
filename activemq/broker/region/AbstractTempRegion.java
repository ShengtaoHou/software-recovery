// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.ConnectionContext;
import java.util.HashMap;
import org.apache.activemq.thread.TaskRunnerFactory;
import org.apache.activemq.usage.SystemUsage;
import java.util.TimerTask;
import java.util.Timer;
import java.util.Map;
import org.slf4j.Logger;

public abstract class AbstractTempRegion extends AbstractRegion
{
    private static final Logger LOG;
    private Map<CachedDestination, Destination> cachedDestinations;
    private final boolean doCacheTempDestinations;
    private final int purgeTime;
    private Timer purgeTimer;
    private TimerTask purgeTask;
    
    public AbstractTempRegion(final RegionBroker broker, final DestinationStatistics destinationStatistics, final SystemUsage memoryManager, final TaskRunnerFactory taskRunnerFactory, final DestinationFactory destinationFactory) {
        super(broker, destinationStatistics, memoryManager, taskRunnerFactory, destinationFactory);
        this.cachedDestinations = new HashMap<CachedDestination, Destination>();
        this.doCacheTempDestinations = broker.getBrokerService().isCacheTempDestinations();
        this.purgeTime = broker.getBrokerService().getTimeBeforePurgeTempDestinations();
        if (this.doCacheTempDestinations) {
            this.purgeTimer = new Timer("ActiveMQ Temp destination purge timer", true);
            this.purgeTask = new TimerTask() {
                @Override
                public void run() {
                    AbstractTempRegion.this.doPurge();
                }
            };
            this.purgeTimer.schedule(this.purgeTask, this.purgeTime, this.purgeTime);
        }
    }
    
    @Override
    public void stop() throws Exception {
        super.stop();
        if (this.purgeTimer != null) {
            this.purgeTimer.cancel();
        }
    }
    
    @Override
    protected synchronized Destination createDestination(final ConnectionContext context, final ActiveMQDestination destination) throws Exception {
        Destination result = this.cachedDestinations.remove(new CachedDestination(destination));
        if (result == null) {
            result = this.destinationFactory.createDestination(context, destination, this.destinationStatistics);
        }
        return result;
    }
    
    @Override
    protected final synchronized void dispose(final ConnectionContext context, final Destination dest) throws Exception {
        if (this.doCacheTempDestinations) {
            this.cachedDestinations.put(new CachedDestination(dest.getActiveMQDestination()), dest);
        }
        else {
            try {
                dest.dispose(context);
                dest.stop();
            }
            catch (Exception e) {
                AbstractTempRegion.LOG.warn("Failed to dispose of {}", dest, e);
            }
        }
    }
    
    private void doDispose(final Destination dest) {
        final ConnectionContext context = new ConnectionContext();
        try {
            dest.dispose(context);
            dest.stop();
        }
        catch (Exception e) {
            AbstractTempRegion.LOG.warn("Failed to dispose of {}", dest, e);
        }
    }
    
    private synchronized void doPurge() {
        final long currentTime = System.currentTimeMillis();
        if (this.cachedDestinations.size() > 0) {
            final Set<CachedDestination> tmp = new HashSet<CachedDestination>(this.cachedDestinations.keySet());
            for (final CachedDestination key : tmp) {
                if (key.timeStamp + this.purgeTime < currentTime) {
                    final Destination dest = this.cachedDestinations.remove(key);
                    if (dest == null) {
                        continue;
                    }
                    this.doDispose(dest);
                }
            }
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(TempQueueRegion.class);
    }
    
    static class CachedDestination
    {
        long timeStamp;
        ActiveMQDestination destination;
        
        CachedDestination(final ActiveMQDestination destination) {
            this.destination = destination;
            this.timeStamp = System.currentTimeMillis();
        }
        
        @Override
        public int hashCode() {
            return this.destination.hashCode();
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o instanceof CachedDestination) {
                final CachedDestination other = (CachedDestination)o;
                return other.destination.equals(this.destination);
            }
            return false;
        }
    }
}
