// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class StringArrayConverter
{
    public static String[] convertToStringArray(final Object value) {
        if (value == null) {
            return null;
        }
        final String text = value.toString();
        if (text == null || text.length() == 0) {
            return null;
        }
        final StringTokenizer stok = new StringTokenizer(text, ",");
        final List<String> list = new ArrayList<String>();
        while (stok.hasMoreTokens()) {
            list.add(stok.nextToken());
        }
        final String[] array = list.toArray(new String[list.size()]);
        return array;
    }
    
    public static String convertToString(final String[] value) {
        if (value == null || value.length == 0) {
            return null;
        }
        final StringBuffer result = new StringBuffer(String.valueOf(value[0]));
        for (int i = 1; i < value.length; ++i) {
            result.append(",").append(value[i]);
        }
        return result.toString();
    }
}
