// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.filter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.util.Set;

public class PropertiesViewFilter implements QueryFilter
{
    protected QueryFilter next;
    protected Set viewFilter;
    
    public PropertiesViewFilter(final QueryFilter next) {
        this(null, next);
    }
    
    public PropertiesViewFilter(final Set viewFilter, final QueryFilter next) {
        this.next = next;
        this.viewFilter = viewFilter;
    }
    
    @Override
    public List<Map<Object, Object>> query(final String query) throws Exception {
        return this.filterViewCollection(this.next.query(query), this.viewFilter);
    }
    
    @Override
    public List<Map<Object, Object>> query(final List queries) throws Exception {
        return this.filterViewCollection(this.next.query(queries), this.viewFilter);
    }
    
    protected List<Map<Object, Object>> filterViewCollection(final Collection<Map<Object, Object>> result, final Set viewFilter) {
        final List<Map<Object, Object>> newCollection = new ArrayList<Map<Object, Object>>();
        final Iterator<Map<Object, Object>> i = result.iterator();
        while (i.hasNext()) {
            newCollection.add(this.filterView(i.next()));
        }
        return newCollection;
    }
    
    protected Map<Object, Object> filterView(final Map<Object, Object> data) {
        if (this.viewFilter == null || this.viewFilter.isEmpty()) {
            return data;
        }
        Map<Object, Object> newData;
        try {
            newData = (Map<Object, Object>)data.getClass().newInstance();
        }
        catch (Exception e) {
            newData = new HashMap<Object, Object>();
        }
        for (final Object key : this.viewFilter) {
            final Object val = data.get(key);
            if (val != null) {
                newData.put(key, val);
            }
        }
        return newData;
    }
}
