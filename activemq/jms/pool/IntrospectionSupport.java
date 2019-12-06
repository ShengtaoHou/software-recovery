// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.jms.pool;

import org.slf4j.LoggerFactory;
import java.lang.reflect.Method;
import javax.net.ssl.SSLServerSocket;
import java.util.Iterator;
import java.util.Map;
import org.slf4j.Logger;

public final class IntrospectionSupport
{
    private static final Logger LOG;
    
    private IntrospectionSupport() {
    }
    
    public static boolean setProperties(final Object target, final Map props) {
        boolean rc = false;
        if (target == null) {
            throw new IllegalArgumentException("target was null.");
        }
        if (props == null) {
            throw new IllegalArgumentException("props was null.");
        }
        final Iterator<?> iter = (Iterator<?>)props.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)iter.next();
            if (setProperty(target, (String)entry.getKey(), entry.getValue())) {
                iter.remove();
                rc = true;
            }
        }
        return rc;
    }
    
    public static boolean setProperty(final Object target, final String name, final Object value) {
        try {
            Class<?> clazz = target.getClass();
            if (target instanceof SSLServerSocket) {
                clazz = SSLServerSocket.class;
            }
            final Method setter = findSetterMethod(clazz, name);
            if (setter == null) {
                return false;
            }
            if (value == null || value.getClass() == setter.getParameterTypes()[0]) {
                setter.invoke(target, value);
            }
            else {
                setter.invoke(target, convert(value, setter.getParameterTypes()[0]));
            }
            return true;
        }
        catch (Exception e) {
            IntrospectionSupport.LOG.error(String.format("Could not set property %s on %s", name, target), e);
            return false;
        }
    }
    
    private static Object convert(final Object value, final Class to) {
        if (value == null) {
            if (Boolean.TYPE.isAssignableFrom(to)) {
                return Boolean.FALSE;
            }
            return null;
        }
        else {
            if (to.isAssignableFrom(value.getClass())) {
                return to.cast(value);
            }
            if (Boolean.TYPE.isAssignableFrom(to) && value instanceof String) {
                return Boolean.valueOf((String)value);
            }
            throw new IllegalArgumentException("Cannot convert from " + value.getClass() + " to " + to + " with value " + value);
        }
    }
    
    private static Method findSetterMethod(final Class clazz, String name) {
        name = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
        final Method[] methods2;
        final Method[] methods = methods2 = clazz.getMethods();
        for (final Method method : methods2) {
            final Class<?>[] params = method.getParameterTypes();
            if (method.getName().equals(name) && params.length == 1) {
                return method;
            }
        }
        return null;
    }
    
    static {
        LOG = LoggerFactory.getLogger(IntrospectionSupport.class);
    }
}
