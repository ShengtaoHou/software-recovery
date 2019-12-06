// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf.compiler;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.LinkedHashMap;
import java.net.URISyntaxException;
import java.beans.PropertyEditor;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.io.File;
import java.net.URI;
import java.beans.PropertyEditorManager;
import java.util.HashMap;
import java.util.Iterator;
import java.lang.reflect.Method;
import java.util.Map;

public final class IntrospectionSupport
{
    private IntrospectionSupport() {
    }
    
    public static boolean getProperties(final Object target, final Map props, String optionPrefix) {
        boolean rc = false;
        if (target == null) {
            throw new IllegalArgumentException("target was null.");
        }
        if (props == null) {
            throw new IllegalArgumentException("props was null.");
        }
        if (optionPrefix == null) {
            optionPrefix = "";
        }
        final Class clazz = target.getClass();
        final Method[] methods = clazz.getMethods();
        for (int i = 0; i < methods.length; ++i) {
            final Method method = methods[i];
            String name = method.getName();
            final Class type = method.getReturnType();
            final Class[] params = method.getParameterTypes();
            if (name.startsWith("get") && params.length == 0 && type != null && isSettableType(type)) {
                try {
                    final Object value = method.invoke(target, new Object[0]);
                    if (value != null) {
                        final String strValue = convertToString(value, type);
                        if (strValue != null) {
                            name = name.substring(3, 4).toLowerCase() + name.substring(4);
                            props.put(optionPrefix + name, strValue);
                            rc = true;
                        }
                    }
                }
                catch (Throwable t) {}
            }
        }
        return rc;
    }
    
    public static boolean setProperties(final Object target, final Map<String, ?> props, final String optionPrefix) {
        boolean rc = false;
        if (target == null) {
            throw new IllegalArgumentException("target was null.");
        }
        if (props == null) {
            throw new IllegalArgumentException("props was null.");
        }
        final Iterator<String> iter = props.keySet().iterator();
        while (iter.hasNext()) {
            String name = iter.next();
            if (name.startsWith(optionPrefix)) {
                final Object value = props.get(name);
                name = name.substring(optionPrefix.length());
                if (!setProperty(target, name, value)) {
                    continue;
                }
                iter.remove();
                rc = true;
            }
        }
        return rc;
    }
    
    public static Map<String, Object> extractProperties(final Map props, final String optionPrefix) {
        if (props == null) {
            throw new IllegalArgumentException("props was null.");
        }
        final HashMap<String, Object> rc = new HashMap<String, Object>(props.size());
        final Iterator iter = props.keySet().iterator();
        while (iter.hasNext()) {
            String name = iter.next();
            if (name.startsWith(optionPrefix)) {
                final Object value = props.get(name);
                name = name.substring(optionPrefix.length());
                rc.put(name, value);
                iter.remove();
            }
        }
        return rc;
    }
    
    public static boolean setProperties(final Object target, final Map props) {
        boolean rc = false;
        if (target == null) {
            throw new IllegalArgumentException("target was null.");
        }
        if (props == null) {
            throw new IllegalArgumentException("props was null.");
        }
        final Iterator iter = props.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry entry = iter.next();
            if (setProperty(target, entry.getKey(), entry.getValue())) {
                iter.remove();
                rc = true;
            }
        }
        return rc;
    }
    
    public static boolean setProperty(final Object target, final String name, final Object value) {
        try {
            final Class clazz = target.getClass();
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
        catch (Throwable ignore) {
            return false;
        }
    }
    
    private static Object convert(final Object value, final Class type) throws URISyntaxException {
        final PropertyEditor editor = PropertyEditorManager.findEditor(type);
        if (editor != null) {
            editor.setAsText(value.toString());
            return editor.getValue();
        }
        if (type == URI.class) {
            return new URI(value.toString());
        }
        if (type == File.class) {
            return new File(value.toString());
        }
        if (type == File[].class) {
            final ArrayList<File> files = new ArrayList<File>();
            final StringTokenizer st = new StringTokenizer(value.toString(), ":");
            while (st.hasMoreTokens()) {
                final String t = st.nextToken();
                if (t != null && t.trim().length() > 0) {
                    files.add(new File(t.trim()));
                }
            }
            final File[] rc = new File[files.size()];
            files.toArray(rc);
            return rc;
        }
        return null;
    }
    
    private static String convertToString(final Object value, final Class type) throws URISyntaxException {
        final PropertyEditor editor = PropertyEditorManager.findEditor(type);
        if (editor != null) {
            editor.setValue(value);
            return editor.getAsText();
        }
        if (type == URI.class) {
            return ((URI)value).toString();
        }
        return null;
    }
    
    private static Method findSetterMethod(final Class clazz, String name) {
        name = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
        final Method[] methods = clazz.getMethods();
        for (int i = 0; i < methods.length; ++i) {
            final Method method = methods[i];
            final Class[] params = method.getParameterTypes();
            if (method.getName().equals(name) && params.length == 1 && isSettableType(params[0])) {
                return method;
            }
        }
        return null;
    }
    
    private static boolean isSettableType(final Class clazz) {
        return PropertyEditorManager.findEditor(clazz) != null || clazz == URI.class || clazz == File.class || clazz == File[].class || clazz == Boolean.class;
    }
    
    public static String toString(final Object target) {
        return toString(target, Object.class);
    }
    
    public static String toString(final Object target, final Class stopClass) {
        final LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        addFields(target, target.getClass(), stopClass, map);
        final StringBuffer buffer = new StringBuffer(simpleName(target.getClass()));
        buffer.append(" {");
        final Set entrySet = map.entrySet();
        boolean first = true;
        for (final Map.Entry entry : entrySet) {
            if (first) {
                first = false;
            }
            else {
                buffer.append(", ");
            }
            buffer.append(entry.getKey());
            buffer.append(" = ");
            appendToString(buffer, entry.getValue());
        }
        buffer.append("}");
        return buffer.toString();
    }
    
    protected static void appendToString(final StringBuffer buffer, final Object value) {
        buffer.append(value);
    }
    
    public static String simpleName(final Class clazz) {
        String name = clazz.getName();
        final int p = name.lastIndexOf(".");
        if (p >= 0) {
            name = name.substring(p + 1);
        }
        return name;
    }
    
    private static void addFields(final Object target, final Class startClass, final Class<Object> stopClass, final LinkedHashMap<String, Object> map) {
        if (startClass != stopClass) {
            addFields(target, startClass.getSuperclass(), stopClass, map);
        }
        final Field[] fields = startClass.getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            final Field field = fields[i];
            if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers())) {
                if (!Modifier.isPrivate(field.getModifiers())) {
                    try {
                        field.setAccessible(true);
                        Object o = field.get(target);
                        if (o != null && o.getClass().isArray()) {
                            try {
                                o = Arrays.asList((Object[])o);
                            }
                            catch (Throwable t) {}
                        }
                        map.put(field.getName(), o);
                    }
                    catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
