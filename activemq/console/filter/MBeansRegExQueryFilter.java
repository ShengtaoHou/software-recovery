// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.filter;

import javax.management.Attribute;
import javax.management.AttributeList;
import java.util.Iterator;
import java.util.regex.Pattern;
import javax.management.ObjectName;
import javax.management.ObjectInstance;
import java.lang.reflect.Method;
import java.util.Map;

public class MBeansRegExQueryFilter extends RegExQueryFilter
{
    public MBeansRegExQueryFilter(final QueryFilter next) {
        super(next);
    }
    
    @Override
    protected boolean matches(final Object data, final Map regex) throws Exception {
        try {
            final Method method = this.getClass().getDeclaredMethod("matches", data.getClass(), Map.class);
            return (boolean)method.invoke(this, data, regex);
        }
        catch (NoSuchMethodException e) {
            return false;
        }
    }
    
    protected boolean matches(final ObjectInstance data, final Map regex) {
        return this.matches(data.getObjectName(), regex);
    }
    
    protected boolean matches(final ObjectName data, final Map regex) {
        for (final String key : regex.keySet()) {
            final String target = data.getKeyProperty(key);
            if (target != null && !regex.get(key).matcher(target).matches()) {
                return false;
            }
        }
        return true;
    }
    
    protected boolean matches(final AttributeList data, final Map regex) {
        for (final String key : regex.keySet()) {
            final Iterator j = data.iterator();
            if (j.hasNext()) {
                final Attribute attrib = j.next();
                if (attrib.getName().equals("Attribute:ObjectName:")) {
                    final String target = ((ObjectName)attrib.getValue()).getKeyProperty(key);
                    if (target == null || !regex.get(key).matcher(target).matches()) {
                        return false;
                    }
                    continue;
                }
                else {
                    if (!attrib.getName().equals(key)) {
                        return false;
                    }
                    if (!regex.get(key).matcher(attrib.getValue().toString()).matches()) {
                        return false;
                    }
                    continue;
                }
            }
        }
        return true;
    }
}
