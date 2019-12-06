// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.util;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class DefaultAuditLog implements AuditLog
{
    private static final Logger LOG;
    
    @Override
    public void log(final AuditLogEntry entry) {
        DefaultAuditLog.LOG.info(entry.toString());
    }
    
    static {
        LOG = LoggerFactory.getLogger("org.apache.activemq.audit");
    }
}
