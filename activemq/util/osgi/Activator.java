// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util.osgi;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.transport.discovery.DiscoveryAgent;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.Service;
import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;
import java.net.URL;
import java.io.IOException;
import java.util.Iterator;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleListener;
import org.osgi.framework.BundleContext;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.apache.activemq.util.FactoryFinder;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.framework.BundleActivator;

public class Activator implements BundleActivator, SynchronousBundleListener, FactoryFinder.ObjectFactory
{
    private static final Logger LOG;
    private final ConcurrentHashMap<String, Class> serviceCache;
    private final ConcurrentMap<Long, BundleWrapper> bundleWrappers;
    private BundleContext bundleContext;
    
    public Activator() {
        this.serviceCache = new ConcurrentHashMap<String, Class>();
        this.bundleWrappers = new ConcurrentHashMap<Long, BundleWrapper>();
    }
    
    public synchronized void start(final BundleContext bundleContext) throws Exception {
        FactoryFinder.setObjectFactory(this);
        this.debug("activating");
        this.bundleContext = bundleContext;
        this.debug("checking existing bundles");
        bundleContext.addBundleListener((BundleListener)this);
        for (final Bundle bundle : bundleContext.getBundles()) {
            if (bundle.getState() == 4 || bundle.getState() == 8 || bundle.getState() == 32 || bundle.getState() == 16) {
                this.register(bundle);
            }
        }
        this.debug("activated");
    }
    
    public synchronized void stop(final BundleContext bundleContext) throws Exception {
        this.debug("deactivating");
        bundleContext.removeBundleListener((BundleListener)this);
        while (!this.bundleWrappers.isEmpty()) {
            this.unregister(this.bundleWrappers.keySet().iterator().next());
        }
        this.debug("deactivated");
        this.bundleContext = null;
    }
    
    public void bundleChanged(final BundleEvent event) {
        if (event.getType() == 32) {
            this.register(event.getBundle());
        }
        else if (event.getType() == 64 || event.getType() == 16) {
            this.unregister(event.getBundle().getBundleId());
        }
    }
    
    protected void register(final Bundle bundle) {
        this.debug("checking bundle " + bundle.getBundleId());
        if (!this.isImportingUs(bundle)) {
            this.debug("The bundle does not import us: " + bundle.getBundleId());
            return;
        }
        this.bundleWrappers.put(bundle.getBundleId(), new BundleWrapper(bundle));
    }
    
    protected void unregister(final long bundleId) {
        final BundleWrapper bundle = this.bundleWrappers.remove(bundleId);
        if (bundle != null) {
            for (final String path : bundle.cachedServices) {
                this.debug("unregistering service for key: " + path);
                this.serviceCache.remove(path);
            }
        }
    }
    
    public Object create(final String path) throws IllegalAccessException, InstantiationException, IOException, ClassNotFoundException {
        Class clazz = this.serviceCache.get(path);
        if (clazz == null) {
            final StringBuffer warnings = new StringBuffer();
            int wrrningCounter = 1;
            for (final BundleWrapper wrapper : this.bundleWrappers.values()) {
                final URL resource = wrapper.bundle.getResource(path);
                if (resource == null) {
                    continue;
                }
                final Properties properties = this.loadProperties(resource);
                final String className = properties.getProperty("class");
                if (className != null) {
                    try {
                        clazz = wrapper.bundle.loadClass(className);
                    }
                    catch (ClassNotFoundException e) {
                        warnings.append("(" + wrrningCounter++ + ") Bundle " + wrapper + " could not load " + className + ": " + e);
                        continue;
                    }
                    this.serviceCache.put(path, clazz);
                    wrapper.cachedServices.add(path);
                    break;
                }
                warnings.append("(" + wrrningCounter++ + ") Invalid service file in bundle " + wrapper + ": 'class' property not defined.");
            }
            if (clazz == null) {
                String msg = "Service not found: '" + path + "'";
                if (warnings.length() != 0) {
                    msg = msg + ", " + (Object)warnings;
                }
                throw new IOException(msg);
            }
        }
        return clazz.newInstance();
    }
    
    private void debug(final Object msg) {
        Activator.LOG.debug(msg.toString());
    }
    
    private Properties loadProperties(final URL resource) throws IOException {
        final InputStream in = resource.openStream();
        try {
            final BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            final Properties properties = new Properties();
            properties.load(in);
            return properties;
        }
        finally {
            try {
                in.close();
            }
            catch (Exception ex) {}
        }
    }
    
    private boolean isImportingUs(final Bundle bundle) {
        return this.isImportingClass(bundle, Service.class) || this.isImportingClass(bundle, Transport.class) || this.isImportingClass(bundle, DiscoveryAgent.class) || this.isImportingClass(bundle, PersistenceAdapter.class);
    }
    
    private boolean isImportingClass(final Bundle bundle, final Class clazz) {
        try {
            return bundle.loadClass(clazz.getName()) == clazz;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(Activator.class);
    }
    
    private static class BundleWrapper
    {
        private final Bundle bundle;
        private final List<String> cachedServices;
        
        public BundleWrapper(final Bundle bundle) {
            this.cachedServices = new ArrayList<String>();
            this.bundle = bundle;
        }
    }
}
