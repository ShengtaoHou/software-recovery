// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import org.slf4j.LoggerFactory;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.lang.reflect.Modifier;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.List;
import javax.net.ssl.SSLServerSocket;
import java.util.HashMap;
import java.util.Iterator;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;

public final class IntrospectionSupport
{
    private static final Logger LOG;
    
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
        final Class<?> clazz = target.getClass();
        final Method[] methods2;
        final Method[] methods = methods2 = clazz.getMethods();
        for (final Method method : methods2) {
            String name = method.getName();
            final Class<?> type = method.getReturnType();
            final Class<?>[] params = method.getParameterTypes();
            if ((name.startsWith("is") || name.startsWith("get")) && params.length == 0 && type != null) {
                try {
                    final Object value = method.invoke(target, new Object[0]);
                    if (value != null) {
                        final String strValue = convertToString(value, type);
                        if (strValue != null) {
                            if (name.startsWith("get")) {
                                name = name.substring(3, 4).toLowerCase(Locale.ENGLISH) + name.substring(4);
                            }
                            else {
                                name = name.substring(2, 3).toLowerCase(Locale.ENGLISH) + name.substring(3);
                            }
                            props.put(optionPrefix + name, strValue);
                            rc = true;
                        }
                    }
                }
                catch (Exception ex) {}
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
        final Iterator<?> iter = props.keySet().iterator();
        while (iter.hasNext()) {
            String name = (String)iter.next();
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
            if (to.isAssignableFrom(String[].class)) {
                return StringArrayConverter.convertToStringArray(value);
            }
            if (value.getClass().equals(String.class) && to.equals(List.class)) {
                final Object answer = StringToListOfActiveMQDestinationConverter.convertToActiveMQDestination(value);
                if (answer != null) {
                    return answer;
                }
            }
            final TypeConversionSupport.Converter converter = TypeConversionSupport.lookupConverter(value.getClass(), to);
            if (converter != null) {
                return converter.convert(value);
            }
            throw new IllegalArgumentException("Cannot convert from " + value.getClass() + " to " + to + " with value " + value);
        }
    }
    
    public static String convertToString(final Object value, final Class to) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return (String)value;
        }
        if (String[].class.isInstance(value)) {
            final String[] array = (String[])value;
            return StringArrayConverter.convertToString(array);
        }
        if (List.class.isInstance(value)) {
            final String answer = StringToListOfActiveMQDestinationConverter.convertFromActiveMQDestination(value);
            if (answer != null) {
                return answer;
            }
        }
        final TypeConversionSupport.Converter converter = TypeConversionSupport.lookupConverter(value.getClass(), String.class);
        if (converter != null) {
            return (String)converter.convert(value);
        }
        throw new IllegalArgumentException("Cannot convert from " + value.getClass() + " to " + to + " with value " + value);
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
    
    public static String toString(final Object target) {
        return toString(target, Object.class, null);
    }
    
    public static String toString(final Object target, final Class stopClass) {
        return toString(target, stopClass, null);
    }
    
    public static String toString(final Object target, final Class stopClass, final Map<String, Object> overrideFields) {
        final LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        addFields(target, target.getClass(), stopClass, map);
        if (overrideFields != null) {
            for (final String key : overrideFields.keySet()) {
                final Object value = overrideFields.get(key);
                map.put(key, value);
            }
        }
        final StringBuffer buffer = new StringBuffer(simpleName(target.getClass()));
        buffer.append(" {");
        final Set<Map.Entry<String, Object>> entrySet = map.entrySet();
        boolean first = true;
        for (final Map.Entry<String, Object> entry : entrySet) {
            final Object value2 = entry.getValue();
            final Object key2 = entry.getKey();
            if (first) {
                first = false;
            }
            else {
                buffer.append(", ");
            }
            buffer.append(key2);
            buffer.append(" = ");
            appendToString(buffer, key2, value2);
        }
        buffer.append("}");
        return buffer.toString();
    }
    
    protected static void appendToString(final StringBuffer buffer, final Object key, final Object value) {
        if (value instanceof ActiveMQDestination) {
            final ActiveMQDestination destination = (ActiveMQDestination)value;
            buffer.append(destination.getQualifiedName());
        }
        else if (key.toString().toLowerCase(Locale.ENGLISH).contains("password")) {
            buffer.append("*****");
        }
        else {
            buffer.append(value);
        }
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
        final Field[] declaredFields;
        final Field[] fields = declaredFields = startClass.getDeclaredFields();
        for (final Field field : declaredFields) {
            if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers())) {
                if (!Modifier.isPrivate(field.getModifiers())) {
                    try {
                        field.setAccessible(true);
                        Object o = field.get(target);
                        if (o != null && o.getClass().isArray()) {
                            try {
                                o = Arrays.asList((Object[])o);
                            }
                            catch (Exception ex) {}
                        }
                        map.put(field.getName(), o);
                    }
                    catch (Exception e) {
                        IntrospectionSupport.LOG.debug("Error getting field " + field + " on class " + startClass + ". This exception is ignored.", e);
                    }
                }
            }
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(IntrospectionSupport.class);
    }
}
