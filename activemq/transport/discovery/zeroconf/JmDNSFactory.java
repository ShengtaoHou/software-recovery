// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.discovery.zeroconf;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.HashMap;
import java.io.IOException;
import javax.jmdns.JmDNS;
import java.net.InetAddress;
import java.util.Map;

public final class JmDNSFactory
{
    static Map<InetAddress, UsageTracker> registry;
    
    private JmDNSFactory() {
    }
    
    static synchronized JmDNS create(final InetAddress address) throws IOException {
        UsageTracker tracker = JmDNSFactory.registry.get(address);
        if (tracker == null) {
            tracker = new UsageTracker();
            tracker.jmDNS = JmDNS.create(address);
            JmDNSFactory.registry.put(address, tracker);
        }
        tracker.count.incrementAndGet();
        return tracker.jmDNS;
    }
    
    static synchronized boolean onClose(final InetAddress address) {
        final UsageTracker tracker = JmDNSFactory.registry.get(address);
        if (tracker != null && tracker.count.decrementAndGet() == 0) {
            JmDNSFactory.registry.remove(address);
            return true;
        }
        return false;
    }
    
    static {
        JmDNSFactory.registry = new HashMap<InetAddress, UsageTracker>();
    }
    
    static class UsageTracker
    {
        AtomicInteger count;
        JmDNS jmDNS;
        
        UsageTracker() {
            this.count = new AtomicInteger(0);
        }
    }
}
