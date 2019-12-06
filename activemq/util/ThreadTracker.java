// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import org.slf4j.Logger;

public class ThreadTracker
{
    static final Logger LOG;
    static HashMap<String, Tracker> trackers;
    
    public static void track(final String name) {
        final String key = name.intern();
        Tracker t;
        synchronized (ThreadTracker.trackers) {
            t = ThreadTracker.trackers.get(key);
            if (t == null) {
                t = new Tracker();
                ThreadTracker.trackers.put(key, t);
            }
        }
        t.track();
    }
    
    public static void result() {
        synchronized (ThreadTracker.trackers) {
            for (final Map.Entry<String, Tracker> t : ThreadTracker.trackers.entrySet()) {
                ThreadTracker.LOG.info("Tracker: " + t.getKey() + ", " + t.getValue().size() + " entry points...");
                for (final Trace trace : ((HashMap<K, Trace>)t.getValue()).values()) {
                    ThreadTracker.LOG.info("count: " + trace.count, trace);
                }
                ThreadTracker.LOG.info("Tracker: " + t.getKey() + ", done.");
            }
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(ThreadTracker.class);
        ThreadTracker.trackers = new HashMap<String, Tracker>();
    }
}
