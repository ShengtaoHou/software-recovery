// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.filter;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GroupPropertiesViewFilter extends PropertiesViewFilter
{
    public GroupPropertiesViewFilter(final QueryFilter next) {
        super(next);
    }
    
    public GroupPropertiesViewFilter(final Set groupView, final QueryFilter next) {
        super(groupView, next);
    }
    
    @Override
    protected Map filterView(final Map data) {
        if (this.viewFilter == null || this.viewFilter.isEmpty()) {
            return data;
        }
        Map newData;
        try {
            newData = (Map)data.getClass().newInstance();
        }
        catch (Exception e) {
            newData = new HashMap();
        }
        for (final String key : data.keySet()) {
            for (final String group : this.viewFilter) {
                if (key.startsWith(group)) {
                    newData.put(key, data.get(key));
                    break;
                }
            }
        }
        return newData;
    }
}
