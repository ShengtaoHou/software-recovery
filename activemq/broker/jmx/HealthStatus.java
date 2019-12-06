// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import java.io.Serializable;

public class HealthStatus implements Serializable
{
    private final String healthId;
    private final String level;
    private final String message;
    private final String resource;
    
    public HealthStatus(final String healthId, final String level, final String message, final String resource) {
        this.healthId = healthId;
        this.level = level;
        this.message = message;
        this.resource = resource;
    }
    
    public String getHealthId() {
        return this.healthId;
    }
    
    public String getLevel() {
        return this.level;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public String getResource() {
        return this.resource;
    }
    
    @Override
    public String toString() {
        return this.healthId + ": " + this.level + " " + this.message + " from " + this.resource;
    }
}
