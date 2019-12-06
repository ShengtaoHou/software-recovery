// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import org.slf4j.LoggerFactory;
import org.apache.activemq.transport.TransportLoggerView;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.util.Properties;
import java.io.IOException;
import org.apache.activemq.transport.LogWriter;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;

public class LogWriterFinder
{
    private static final Logger log;
    private final String path;
    private final ConcurrentHashMap classMap;
    
    public LogWriterFinder(final String path) {
        this.classMap = new ConcurrentHashMap();
        this.path = path;
    }
    
    public LogWriter newInstance(final String logWriterName) throws IllegalAccessException, InstantiationException, IOException, ClassNotFoundException {
        Class clazz = this.classMap.get(logWriterName);
        if (clazz == null) {
            clazz = this.newInstance(this.doFindLogWriterProperties(logWriterName));
            this.classMap.put(logWriterName, clazz);
        }
        return clazz.newInstance();
    }
    
    private Class newInstance(final Properties properties) throws ClassNotFoundException, IOException {
        final String className = properties.getProperty("class");
        if (className == null) {
            throw new IOException("Expected property is missing: class");
        }
        Class clazz;
        try {
            clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
        }
        catch (ClassNotFoundException e) {
            clazz = LogWriterFinder.class.getClassLoader().loadClass(className);
        }
        return clazz;
    }
    
    protected Properties doFindLogWriterProperties(final String logWriterName) throws IOException {
        final String uri = this.path + logWriterName;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = this.getClass().getClassLoader();
        }
        InputStream in = classLoader.getResourceAsStream(uri);
        if (in == null) {
            in = LogWriterFinder.class.getClassLoader().getResourceAsStream(uri);
            if (in == null) {
                LogWriterFinder.log.error("Could not find log writer for resource: " + uri);
                throw new IOException("Could not find log writer for resource: " + uri);
            }
        }
        BufferedInputStream reader = null;
        final Properties properties = new Properties();
        try {
            reader = new BufferedInputStream(in);
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
    
    static {
        log = LoggerFactory.getLogger(TransportLoggerView.class);
    }
}
