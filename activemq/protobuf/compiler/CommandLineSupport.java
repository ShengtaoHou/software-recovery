// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf.compiler;

import java.util.ArrayList;

public class CommandLineSupport
{
    public static String[] setOptions(final Object target, final String[] args) {
        final ArrayList rc = new ArrayList();
        for (int i = 0; i < args.length; ++i) {
            if (args[i] != null) {
                if (args[i].startsWith("--")) {
                    String value = "true";
                    String name = args[i].substring(2);
                    final int p = name.indexOf("=");
                    if (p > 0) {
                        value = name.substring(p + 1);
                        name = name.substring(0, p);
                    }
                    if (name.length() == 0) {
                        rc.add(args[i]);
                    }
                    else {
                        final String propName = convertOptionToPropertyName(name);
                        if (!IntrospectionSupport.setProperty(target, propName, value)) {
                            rc.add(args[i]);
                        }
                    }
                }
                else {
                    rc.add(args[i]);
                }
            }
        }
        final String[] r = new String[rc.size()];
        rc.toArray(r);
        return r;
    }
    
    private static String convertOptionToPropertyName(String name) {
        String rc = "";
        for (int p = name.indexOf("-"); p > 0; p = name.indexOf("-")) {
            rc += name.substring(0, p);
            name = name.substring(p + 1);
            if (name.length() > 0) {
                rc += name.substring(0, 1).toUpperCase();
                name = name.substring(1);
            }
        }
        return rc + name;
    }
}
