// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import java.util.HashMap;

class Tracker extends HashMap<Long, Trace>
{
    public void track() {
        final Trace current = new Trace();
        synchronized (this) {
            final Trace exist = ((HashMap<K, Trace>)this).get(current.id);
            if (exist != null) {
                final Trace trace = exist;
                ++trace.count;
            }
            else {
                this.put(current.id, current);
            }
        }
    }
}
