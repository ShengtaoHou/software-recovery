// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import org.slf4j.LoggerFactory;
import java.lang.reflect.Proxy;
import java.io.ObjectStreamClass;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import java.io.ObjectInputStream;

public class ClassLoadingAwareObjectInputStream extends ObjectInputStream
{
    private static final Logger LOG;
    private static final ClassLoader FALLBACK_CLASS_LOADER;
    private final ClassLoader inLoader;
    
    public ClassLoadingAwareObjectInputStream(final InputStream in) throws IOException {
        super(in);
        this.inLoader = in.getClass().getClassLoader();
    }
    
    @Override
    protected Class<?> resolveClass(final ObjectStreamClass classDesc) throws IOException, ClassNotFoundException {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return this.load(classDesc.getName(), cl, this.inLoader);
    }
    
    @Override
    protected Class<?> resolveProxyClass(final String[] interfaces) throws IOException, ClassNotFoundException {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        final Class[] cinterfaces = new Class[interfaces.length];
        for (int i = 0; i < interfaces.length; ++i) {
            cinterfaces[i] = this.load(interfaces[i], cl);
        }
        try {
            return Proxy.getProxyClass(cl, (Class<?>[])cinterfaces);
        }
        catch (IllegalArgumentException e) {
            try {
                return Proxy.getProxyClass(this.inLoader, (Class<?>[])cinterfaces);
            }
            catch (IllegalArgumentException ex) {
                try {
                    return Proxy.getProxyClass(ClassLoadingAwareObjectInputStream.FALLBACK_CLASS_LOADER, (Class<?>[])cinterfaces);
                }
                catch (IllegalArgumentException ex2) {
                    throw new ClassNotFoundException(null, e);
                }
            }
        }
    }
    
    private Class<?> load(final String className, final ClassLoader... cl) throws ClassNotFoundException {
        final Class<?> clazz = loadSimpleType(className);
        if (clazz != null) {
            ClassLoadingAwareObjectInputStream.LOG.trace("Loaded class: {} as simple type -> ", className, clazz);
            return clazz;
        }
        final int length = cl.length;
        int i = 0;
        while (i < length) {
            final ClassLoader loader = cl[i];
            ClassLoadingAwareObjectInputStream.LOG.trace("Attempting to load class: {} using classloader: {}", className, cl);
            try {
                final Class<?> answer = Class.forName(className, false, loader);
                if (ClassLoadingAwareObjectInputStream.LOG.isTraceEnabled()) {
                    ClassLoadingAwareObjectInputStream.LOG.trace("Loaded class: {} using classloader: {} -> ", className, cl, answer);
                }
                return answer;
            }
            catch (ClassNotFoundException e) {
                ClassLoadingAwareObjectInputStream.LOG.trace("Class not found: {} using classloader: {}", className, cl);
                ++i;
                continue;
            }
            break;
        }
        return Class.forName(className, false, ClassLoadingAwareObjectInputStream.FALLBACK_CLASS_LOADER);
    }
    
    public static Class<?> loadSimpleType(final String name) {
        if ("java.lang.byte[]".equals(name) || "byte[]".equals(name)) {
            return byte[].class;
        }
        if ("java.lang.Byte[]".equals(name) || "Byte[]".equals(name)) {
            return Byte[].class;
        }
        if ("java.lang.Object[]".equals(name) || "Object[]".equals(name)) {
            return Object[].class;
        }
        if ("java.lang.String[]".equals(name) || "String[]".equals(name)) {
            return String[].class;
        }
        if ("java.lang.String".equals(name) || "String".equals(name)) {
            return String.class;
        }
        if ("java.lang.Boolean".equals(name) || "Boolean".equals(name)) {
            return Boolean.class;
        }
        if ("boolean".equals(name)) {
            return Boolean.TYPE;
        }
        if ("java.lang.Integer".equals(name) || "Integer".equals(name)) {
            return Integer.class;
        }
        if ("int".equals(name)) {
            return Integer.TYPE;
        }
        if ("java.lang.Long".equals(name) || "Long".equals(name)) {
            return Long.class;
        }
        if ("long".equals(name)) {
            return Long.TYPE;
        }
        if ("java.lang.Short".equals(name) || "Short".equals(name)) {
            return Short.class;
        }
        if ("short".equals(name)) {
            return Short.TYPE;
        }
        if ("java.lang.Byte".equals(name) || "Byte".equals(name)) {
            return Byte.class;
        }
        if ("byte".equals(name)) {
            return Byte.TYPE;
        }
        if ("java.lang.Float".equals(name) || "Float".equals(name)) {
            return Float.class;
        }
        if ("float".equals(name)) {
            return Float.TYPE;
        }
        if ("java.lang.Double".equals(name) || "Double".equals(name)) {
            return Double.class;
        }
        if ("double".equals(name)) {
            return Double.TYPE;
        }
        return null;
    }
    
    static {
        LOG = LoggerFactory.getLogger(ClassLoadingAwareObjectInputStream.class);
        FALLBACK_CLASS_LOADER = ClassLoadingAwareObjectInputStream.class.getClassLoader();
    }
}
