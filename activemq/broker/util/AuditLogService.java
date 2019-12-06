// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.util;

import java.util.Iterator;

public class AuditLogService
{
    private AuditLogFactory factory;
    private static AuditLogService auditLog;
    
    public static AuditLogService getAuditLog() {
        if (AuditLogService.auditLog == null) {
            AuditLogService.auditLog = new AuditLogService();
        }
        return AuditLogService.auditLog;
    }
    
    private AuditLogService() {
        this.factory = new DefaultAuditLogFactory();
    }
    
    public void log(final AuditLogEntry entry) {
        for (final AuditLog log : this.factory.getAuditLogs()) {
            log.log(entry);
        }
    }
    
    public void setFactory(final AuditLogFactory factory) {
        this.factory = factory;
    }
}
