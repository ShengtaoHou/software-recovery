// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.util;

import java.util.List;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.ArrayList;

public class DefaultAuditLogFactory implements AuditLogFactory
{
    private ArrayList<AuditLog> auditLogs;
    
    public DefaultAuditLogFactory() {
        this.auditLogs = new ArrayList<AuditLog>();
        final ServiceLoader<AuditLog> logs = ServiceLoader.load(AuditLog.class);
        for (final AuditLog log : logs) {
            this.auditLogs.add(log);
        }
        if (this.auditLogs.size() == 0) {
            this.auditLogs.add(new DefaultAuditLog());
        }
    }
    
    @Override
    public List<AuditLog> getAuditLogs() {
        return this.auditLogs;
    }
}
