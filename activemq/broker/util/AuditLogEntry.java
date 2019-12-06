// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;

public class AuditLogEntry
{
    protected String user;
    protected long timestamp;
    protected String operation;
    protected String remoteAddr;
    SimpleDateFormat formatter;
    protected Map<String, Object> parameters;
    
    public AuditLogEntry() {
        this.user = "anonymous";
        this.formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss,SSS");
        this.parameters = new HashMap<String, Object>();
    }
    
    public String getUser() {
        return this.user;
    }
    
    public void setUser(final String user) {
        this.user = user;
    }
    
    public long getTimestamp() {
        return this.timestamp;
    }
    
    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getFormattedTime() {
        return this.formatter.format(new Date(this.timestamp));
    }
    
    public String getOperation() {
        return this.operation;
    }
    
    public void setOperation(final String operation) {
        this.operation = operation;
    }
    
    public String getRemoteAddr() {
        return this.remoteAddr;
    }
    
    public void setRemoteAddr(final String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }
    
    public Map<String, Object> getParameters() {
        return this.parameters;
    }
    
    public void setParameters(final Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
