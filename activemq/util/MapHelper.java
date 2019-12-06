// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import java.util.Map;

public final class MapHelper
{
    private MapHelper() {
    }
    
    public static String getString(final Map map, final String key) {
        final Object answer = map.get(key);
        return (answer != null) ? answer.toString() : null;
    }
    
    public static int getInt(final Map map, final String key, final int defaultValue) {
        final Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number)value).intValue();
        }
        if (value instanceof String) {
            return Integer.parseInt((String)value);
        }
        return defaultValue;
    }
}
