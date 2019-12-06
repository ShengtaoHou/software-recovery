// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import java.util.Map;
import org.apache.activemq.util.IntrospectionSupport;
import java.util.Random;
import java.io.Serializable;
import org.apache.activemq.filter.DestinationMapEntry;

public class RedeliveryPolicy extends DestinationMapEntry implements Cloneable, Serializable
{
    public static final int NO_MAXIMUM_REDELIVERIES = -1;
    public static final int DEFAULT_MAXIMUM_REDELIVERIES = 6;
    private static Random randomNumberGenerator;
    protected double collisionAvoidanceFactor;
    protected int maximumRedeliveries;
    protected long maximumRedeliveryDelay;
    protected long initialRedeliveryDelay;
    protected boolean useCollisionAvoidance;
    protected boolean useExponentialBackOff;
    protected double backOffMultiplier;
    protected long redeliveryDelay;
    
    public RedeliveryPolicy() {
        this.collisionAvoidanceFactor = 0.15;
        this.maximumRedeliveries = 6;
        this.maximumRedeliveryDelay = -1L;
        this.initialRedeliveryDelay = 1000L;
        this.backOffMultiplier = 5.0;
        this.redeliveryDelay = this.initialRedeliveryDelay;
    }
    
    public RedeliveryPolicy copy() {
        try {
            return (RedeliveryPolicy)this.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException("Could not clone: " + e, e);
        }
    }
    
    public double getBackOffMultiplier() {
        return this.backOffMultiplier;
    }
    
    public void setBackOffMultiplier(final double backOffMultiplier) {
        this.backOffMultiplier = backOffMultiplier;
    }
    
    public short getCollisionAvoidancePercent() {
        return (short)Math.round(this.collisionAvoidanceFactor * 100.0);
    }
    
    public void setCollisionAvoidancePercent(final short collisionAvoidancePercent) {
        this.collisionAvoidanceFactor = collisionAvoidancePercent * 0.01;
    }
    
    public long getInitialRedeliveryDelay() {
        return this.initialRedeliveryDelay;
    }
    
    public void setInitialRedeliveryDelay(final long initialRedeliveryDelay) {
        this.initialRedeliveryDelay = initialRedeliveryDelay;
    }
    
    public long getMaximumRedeliveryDelay() {
        return this.maximumRedeliveryDelay;
    }
    
    public void setMaximumRedeliveryDelay(final long maximumRedeliveryDelay) {
        this.maximumRedeliveryDelay = maximumRedeliveryDelay;
    }
    
    public int getMaximumRedeliveries() {
        return this.maximumRedeliveries;
    }
    
    public void setMaximumRedeliveries(final int maximumRedeliveries) {
        this.maximumRedeliveries = maximumRedeliveries;
    }
    
    public long getNextRedeliveryDelay(final long previousDelay) {
        long nextDelay = this.redeliveryDelay;
        if (previousDelay > 0L && this.useExponentialBackOff && this.backOffMultiplier > 1.0) {
            nextDelay = (long)(previousDelay * this.backOffMultiplier);
            if (this.maximumRedeliveryDelay != -1L && nextDelay > this.maximumRedeliveryDelay) {
                nextDelay = Math.max(this.maximumRedeliveryDelay, this.redeliveryDelay);
            }
        }
        if (this.useCollisionAvoidance) {
            final Random random = getRandomNumberGenerator();
            final double variance = (random.nextBoolean() ? this.collisionAvoidanceFactor : (-this.collisionAvoidanceFactor)) * random.nextDouble();
            nextDelay += (long)(nextDelay * variance);
        }
        return nextDelay;
    }
    
    public boolean isUseCollisionAvoidance() {
        return this.useCollisionAvoidance;
    }
    
    public void setUseCollisionAvoidance(final boolean useCollisionAvoidance) {
        this.useCollisionAvoidance = useCollisionAvoidance;
    }
    
    public boolean isUseExponentialBackOff() {
        return this.useExponentialBackOff;
    }
    
    public void setUseExponentialBackOff(final boolean useExponentialBackOff) {
        this.useExponentialBackOff = useExponentialBackOff;
    }
    
    protected static synchronized Random getRandomNumberGenerator() {
        if (RedeliveryPolicy.randomNumberGenerator == null) {
            RedeliveryPolicy.randomNumberGenerator = new Random();
        }
        return RedeliveryPolicy.randomNumberGenerator;
    }
    
    public void setRedeliveryDelay(final long redeliveryDelay) {
        this.redeliveryDelay = redeliveryDelay;
    }
    
    public long getRedeliveryDelay() {
        return this.redeliveryDelay;
    }
    
    @Override
    public String toString() {
        return IntrospectionSupport.toString(this, DestinationMapEntry.class, null);
    }
}
