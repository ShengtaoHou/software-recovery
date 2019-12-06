// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network.jms;

public class ReconnectionPolicy
{
    private int maxSendRetries;
    private long sendRetryDelay;
    private int maxReconnectAttempts;
    private int maxInitialConnectAttempts;
    private long maximumReconnectDelay;
    private long initialReconnectDelay;
    private boolean useExponentialBackOff;
    private double backOffMultiplier;
    
    public ReconnectionPolicy() {
        this.maxSendRetries = 10;
        this.sendRetryDelay = 1000L;
        this.maxReconnectAttempts = -1;
        this.maxInitialConnectAttempts = -1;
        this.maximumReconnectDelay = 30000L;
        this.initialReconnectDelay = 1000L;
        this.useExponentialBackOff = false;
        this.backOffMultiplier = 2.0;
    }
    
    public int getMaxSendRetries() {
        return this.maxSendRetries;
    }
    
    public void setMaxSendRetries(final int maxSendRetries) {
        this.maxSendRetries = maxSendRetries;
    }
    
    public long getSendRetryDelay() {
        return this.sendRetryDelay;
    }
    
    public void setSendRetyDelay(final long sendRetryDelay) {
        if (sendRetryDelay < 1000L) {
            this.sendRetryDelay = 1000L;
        }
        this.sendRetryDelay = sendRetryDelay;
    }
    
    public int getMaxReconnectAttempts() {
        return this.maxReconnectAttempts;
    }
    
    public void setMaxReconnectAttempts(final int maxReconnectAttempts) {
        this.maxReconnectAttempts = maxReconnectAttempts;
    }
    
    public int getMaxInitialConnectAttempts() {
        return this.maxInitialConnectAttempts;
    }
    
    public void setMaxInitialConnectAttempts(final int maxAttempts) {
        this.maxInitialConnectAttempts = maxAttempts;
    }
    
    public long getMaximumReconnectDelay() {
        return this.maximumReconnectDelay;
    }
    
    public void setMaximumReconnectDelay(final long maximumReconnectDelay) {
        this.maximumReconnectDelay = maximumReconnectDelay;
    }
    
    public long getInitialReconnectDelay() {
        return this.initialReconnectDelay;
    }
    
    public void setInitialReconnectDelay(final long initialReconnectDelay) {
        this.initialReconnectDelay = initialReconnectDelay;
    }
    
    public boolean isUseExponentialBackOff() {
        return this.useExponentialBackOff;
    }
    
    public void setUseExponentialBackOff(final boolean useExponentialBackOff) {
        this.useExponentialBackOff = useExponentialBackOff;
    }
    
    public double getBackOffMultiplier() {
        return this.backOffMultiplier;
    }
    
    public void setBackOffMultiplier(final double backOffMultiplier) {
        this.backOffMultiplier = backOffMultiplier;
    }
    
    public long getNextDelay(final int attempt) {
        if (attempt == 0) {
            return 0L;
        }
        long nextDelay = this.initialReconnectDelay;
        if (this.useExponentialBackOff) {
            nextDelay *= (long)(attempt * this.backOffMultiplier);
        }
        if (this.maximumReconnectDelay > 0L && nextDelay > this.maximumReconnectDelay) {
            nextDelay = this.maximumReconnectDelay;
        }
        return nextDelay;
    }
}
