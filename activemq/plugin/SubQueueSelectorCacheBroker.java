// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.plugin;

import org.slf4j.LoggerFactory;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.HashSet;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.Broker;
import java.io.File;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.apache.activemq.broker.BrokerFilter;

public class SubQueueSelectorCacheBroker extends BrokerFilter implements Runnable
{
    private static final Logger LOG;
    private ConcurrentHashMap<String, Set<String>> subSelectorCache;
    private final File persistFile;
    private boolean running;
    private Thread persistThread;
    private static final long MAX_PERSIST_INTERVAL = 600000L;
    private static final String SELECTOR_CACHE_PERSIST_THREAD_NAME = "SelectorCachePersistThread";
    
    public SubQueueSelectorCacheBroker(final Broker next, final File persistFile) {
        super(next);
        this.subSelectorCache = new ConcurrentHashMap<String, Set<String>>();
        this.running = true;
        this.persistFile = persistFile;
        SubQueueSelectorCacheBroker.LOG.info("Using persisted selector cache from[{}]", persistFile);
        this.readCache();
        (this.persistThread = new Thread(this, "SelectorCachePersistThread")).start();
    }
    
    @Override
    public void stop() throws Exception {
        this.running = false;
        if (this.persistThread != null) {
            this.persistThread.interrupt();
            this.persistThread.join();
        }
    }
    
    @Override
    public Subscription addConsumer(final ConnectionContext context, final ConsumerInfo info) throws Exception {
        final String destinationName = info.getDestination().getQualifiedName();
        SubQueueSelectorCacheBroker.LOG.debug("Caching consumer selector [{}] on a {}", info.getSelector(), destinationName);
        String selector = info.getSelector();
        if (selector == null) {
            selector = "TRUE";
        }
        Set<String> selectors = this.subSelectorCache.get(destinationName);
        if (selectors == null) {
            selectors = Collections.synchronizedSet(new HashSet<String>());
        }
        selectors.add(selector);
        this.subSelectorCache.put(destinationName, selectors);
        return super.addConsumer(context, info);
    }
    
    private void readCache() {
        if (this.persistFile != null && this.persistFile.exists()) {
            try {
                final FileInputStream fis = new FileInputStream(this.persistFile);
                try {
                    final ObjectInputStream in = new ObjectInputStream(fis);
                    try {
                        this.subSelectorCache = (ConcurrentHashMap<String, Set<String>>)in.readObject();
                    }
                    catch (ClassNotFoundException ex) {
                        SubQueueSelectorCacheBroker.LOG.error("Invalid selector cache data found. Please remove file.", ex);
                    }
                    finally {
                        in.close();
                    }
                }
                finally {
                    fis.close();
                }
            }
            catch (IOException ex2) {
                SubQueueSelectorCacheBroker.LOG.error("Unable to read persisted selector cache...it will be ignored!", ex2);
            }
        }
    }
    
    private void persistCache() {
        SubQueueSelectorCacheBroker.LOG.debug("Persisting selector cache....");
        try {
            final FileOutputStream fos = new FileOutputStream(this.persistFile);
            try {
                final ObjectOutputStream out = new ObjectOutputStream(fos);
                try {
                    out.writeObject(this.subSelectorCache);
                }
                finally {
                    out.flush();
                    out.close();
                }
            }
            catch (IOException ex) {
                SubQueueSelectorCacheBroker.LOG.error("Unable to persist selector cache", ex);
            }
            finally {
                fos.close();
            }
        }
        catch (IOException ex2) {
            SubQueueSelectorCacheBroker.LOG.error("Unable to access file[{}]", this.persistFile, ex2);
        }
    }
    
    public Set<String> getSelector(final String destination) {
        return this.subSelectorCache.get(destination);
    }
    
    @Override
    public void run() {
        while (this.running) {
            try {
                Thread.sleep(600000L);
            }
            catch (InterruptedException ex) {}
            this.persistCache();
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(SubQueueSelectorCacheBroker.class);
    }
}
