// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;

public class FactoryFinder
{
    private static ObjectFactory objectFactory;
    private final String path;
    
    public static ObjectFactory getObjectFactory() {
        return FactoryFinder.objectFactory;
    }
    
    public static void setObjectFactory(final ObjectFactory objectFactory) {
        FactoryFinder.objectFactory = objectFactory;
    }
    
    public FactoryFinder(final String path) {
        this.path = path;
    }
    
    public Object newInstance(final String key) throws IllegalAccessException, InstantiationException, IOException, ClassNotFoundException {
        return FactoryFinder.objectFactory.create(this.path + key);
    }
    
    static {
        FactoryFinder.objectFactory = new StandaloneObjectFactory();
    }
    
    protected static class StandaloneObjectFactory implements ObjectFactory
    {
        final ConcurrentHashMap<String, Class> classMap;
        
        protected StandaloneObjectFactory() {
            this.classMap = new ConcurrentHashMap<String, Class>();
        }
        
        @Override
        public Object create(final String path) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
            Class clazz = this.classMap.get(path);
            if (clazz == null) {
                clazz = loadClass(loadProperties(path));
                this.classMap.put(path, clazz);
            }
            return clazz.newInstance();
        }
        
        public static Class loadClass(final Properties properties) throws ClassNotFoundException, IOException {
            final String className = properties.getProperty("class");
            if (className == null) {
                throw new IOException("Expected property is missing: class");
            }
            Class clazz = null;
            final ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (loader != null) {
                try {
                    clazz = loader.loadClass(className);
                }
                catch (ClassNotFoundException ex) {}
            }
            if (clazz == null) {
                clazz = FactoryFinder.class.getClassLoader().loadClass(className);
            }
            return clazz;
        }
        
        public static Properties loadProperties(final String uri) throws IOException {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null) {
                classLoader = StandaloneObjectFactory.class.getClassLoader();
            }
            InputStream in = classLoader.getResourceAsStream(uri);
            if (in == null) {
                in = FactoryFinder.class.getClassLoader().getResourceAsStream(uri);
                if (in == null) {
                    throw new IOException("Could not find factory class for resource: " + uri);
                }
            }
            BufferedInputStream reader = null;
            try {
                reader = new BufferedInputStream(in);
                final Properties properties = new Properties();
                properties.load(reader);
                return properties;
            }
            finally {
                try {
                    reader.close();
                }
                catch (Exception ex) {}
            }
        }
    }
    
    public interface ObjectFactory
    {
        Object create(final String p0) throws IllegalAccessException, InstantiationException, IOException, ClassNotFoundException;
    }
}
