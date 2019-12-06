// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.jndi;

import org.slf4j.LoggerFactory;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import java.util.Enumeration;
import javax.naming.StringRefAddr;
import java.util.Properties;
import javax.naming.Reference;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import org.slf4j.Logger;
import javax.naming.spi.ObjectFactory;

public class JNDIReferenceFactory implements ObjectFactory
{
    static Logger log;
    
    @Override
    public Object getObjectInstance(final Object object, final Name name, final Context nameCtx, final Hashtable environment) throws Exception {
        Object result = null;
        if (object instanceof Reference) {
            final Reference reference = (Reference)object;
            if (JNDIReferenceFactory.log.isTraceEnabled()) {
                JNDIReferenceFactory.log.trace("Getting instance of " + reference.getClassName());
            }
            final Class theClass = loadClass(this, reference.getClassName());
            if (JNDIStorableInterface.class.isAssignableFrom(theClass)) {
                final JNDIStorableInterface store = theClass.newInstance();
                final Properties properties = new Properties();
                final Enumeration iter = reference.getAll();
                while (iter.hasMoreElements()) {
                    final StringRefAddr addr = iter.nextElement();
                    properties.put(addr.getType(), (addr.getContent() == null) ? "" : addr.getContent());
                }
                store.setProperties(properties);
                result = store;
            }
            return result;
        }
        JNDIReferenceFactory.log.error("Object " + object + " is not a reference - cannot load");
        throw new RuntimeException("Object " + object + " is not a reference");
    }
    
    public static Reference createReference(final String instanceClassName, final JNDIStorableInterface po) throws NamingException {
        if (JNDIReferenceFactory.log.isTraceEnabled()) {
            JNDIReferenceFactory.log.trace("Creating reference: " + instanceClassName + "," + po);
        }
        final Reference result = new Reference(instanceClassName, JNDIReferenceFactory.class.getName(), null);
        try {
            final Properties props = po.getProperties();
            final Enumeration iter = props.propertyNames();
            while (iter.hasMoreElements()) {
                final String key = iter.nextElement();
                final String value = props.getProperty(key);
                final StringRefAddr addr = new StringRefAddr(key, value);
                result.add(addr);
            }
        }
        catch (Exception e) {
            JNDIReferenceFactory.log.error(e.getMessage(), e);
            throw new NamingException(e.getMessage());
        }
        return result;
    }
    
    public static Class loadClass(final Object thisObj, final String className) throws ClassNotFoundException {
        final ClassLoader loader = thisObj.getClass().getClassLoader();
        Class theClass;
        if (loader != null) {
            theClass = loader.loadClass(className);
        }
        else {
            theClass = Class.forName(className);
        }
        return theClass;
    }
    
    static {
        JNDIReferenceFactory.log = LoggerFactory.getLogger(JNDIReferenceFactory.class);
    }
}
