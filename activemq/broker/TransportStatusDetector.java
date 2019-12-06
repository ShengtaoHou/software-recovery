// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Set;
import org.slf4j.Logger;
import org.apache.activemq.Service;

public class TransportStatusDetector implements Service, Runnable
{
    private static final Logger LOG;
    private TransportConnector connector;
    private Set<TransportConnection> collectionCandidates;
    private AtomicBoolean started;
    private Thread runner;
    private int sweepInterval;
    
    TransportStatusDetector(final TransportConnector connector) {
        this.collectionCandidates = new CopyOnWriteArraySet<TransportConnection>();
        this.started = new AtomicBoolean(false);
        this.sweepInterval = 5000;
        this.connector = connector;
    }
    
    public int getSweepInterval() {
        return this.sweepInterval;
    }
    
    public void setSweepInterval(final int sweepInterval) {
        this.sweepInterval = sweepInterval;
    }
    
    protected void doCollection() {
        for (final TransportConnection tc : this.collectionCandidates) {
            if (tc.isMarkedCandidate()) {
                if (tc.isBlockedCandidate()) {
                    this.collectionCandidates.remove(tc);
                    this.doCollection(tc);
                }
                else {
                    tc.doMark();
                }
            }
            else {
                this.collectionCandidates.remove(tc);
            }
        }
    }
    
    protected void doSweep() {
        for (final TransportConnection connection : this.connector.getConnections()) {
            if (connection.isMarkedCandidate()) {
                connection.doMark();
                this.collectionCandidates.add(connection);
            }
        }
    }
    
    protected void doCollection(final TransportConnection tc) {
        TransportStatusDetector.LOG.warn("found a blocked client - stopping: {}", tc);
        try {
            tc.stop();
        }
        catch (Exception e) {
            TransportStatusDetector.LOG.error("Error stopping {}", tc, e);
        }
    }
    
    @Override
    public void run() {
        while (this.started.get()) {
            try {
                this.doCollection();
                this.doSweep();
                Thread.sleep(this.sweepInterval);
            }
            catch (Throwable e) {
                TransportStatusDetector.LOG.error("failed to complete a sweep for blocked clients", e);
            }
        }
    }
    
    @Override
    public void start() throws Exception {
        if (this.started.compareAndSet(false, true)) {
            (this.runner = new Thread(this, "ActiveMQ Transport Status Monitor: " + this.connector)).setDaemon(true);
            this.runner.setPriority(9);
            this.runner.start();
        }
    }
    
    @Override
    public void stop() throws Exception {
        this.started.set(false);
        if (this.runner != null) {
            this.runner.join(this.getSweepInterval() * 5);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(TransportStatusDetector.class);
    }
}
