// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.util;

import java.util.Arrays;

public class JMXAuditLogEntry extends AuditLogEntry
{
    @Override
    public String toString() {
        return this.user.trim() + " called " + this.operation + Arrays.toString(this.parameters.get("arguments")) + " at " + this.getFormattedTime();
    }
}
