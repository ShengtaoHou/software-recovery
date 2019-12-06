// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.filter;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public abstract class ResultTransformFilter implements QueryFilter
{
    private QueryFilter next;
    
    protected ResultTransformFilter(final QueryFilter next) {
        this.next = next;
    }
    
    @Override
    public List query(final String query) throws Exception {
        return this.transformList(this.next.query(query));
    }
    
    @Override
    public List<Object> query(final List queries) throws Exception {
        return this.transformList(this.next.query(queries));
    }
    
    protected List<Object> transformList(final List<Object> result) throws Exception {
        final List<Object> props = new ArrayList<Object>();
        final Iterator<Object> i = result.iterator();
        while (i.hasNext()) {
            props.add(this.transformElement(i.next()));
        }
        return props;
    }
    
    protected abstract Object transformElement(final Object p0) throws Exception;
}
