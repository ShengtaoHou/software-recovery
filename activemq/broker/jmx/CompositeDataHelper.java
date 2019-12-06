// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import java.util.Iterator;
import java.util.Collection;
import javax.management.openmbean.TabularData;
import java.util.HashMap;
import java.util.Map;
import javax.management.openmbean.CompositeData;

public class CompositeDataHelper
{
    public static Map getTabularMap(final CompositeData cdata, final String fieldName) {
        final Map map = new HashMap();
        appendTabularMap(map, cdata, fieldName);
        return map;
    }
    
    public static void appendTabularMap(final Map map, final CompositeData cdata, final String fieldName) {
        final Object tabularObject = cdata.get(fieldName);
        if (tabularObject instanceof TabularData) {
            final TabularData tabularData = (TabularData)tabularObject;
            final Collection<CompositeData> values = (Collection<CompositeData>)tabularData.values();
            for (final CompositeData compositeData : values) {
                final Object key = compositeData.get("key");
                final Object value = compositeData.get("value");
                map.put(key, value);
            }
        }
    }
    
    public static Map getMessageUserProperties(final CompositeData cdata) {
        final Map map = new HashMap();
        appendTabularMap(map, cdata, "StringProperties");
        appendTabularMap(map, cdata, "BooleanProperties");
        appendTabularMap(map, cdata, "ByteProperties");
        appendTabularMap(map, cdata, "ShortProperties");
        appendTabularMap(map, cdata, "IntProperties");
        appendTabularMap(map, cdata, "LongProperties");
        appendTabularMap(map, cdata, "FloatProperties");
        appendTabularMap(map, cdata, "DoubleProperties");
        return map;
    }
}
