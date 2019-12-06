// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.filter;

import java.util.Enumeration;
import java.util.Collections;
import java.util.StringTokenizer;
import java.util.List;

public abstract class AbstractQueryFilter implements QueryFilter
{
    protected QueryFilter next;
    
    protected AbstractQueryFilter(final QueryFilter next) {
        this.next = next;
    }
    
    @Override
    public List query(final String query) throws Exception {
        final StringTokenizer tokens = new StringTokenizer(query, ",");
        return this.query(Collections.list((Enumeration<Object>)tokens));
    }
}
