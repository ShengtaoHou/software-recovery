// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.filter;

import java.util.List;

public class StubQueryFilter implements QueryFilter
{
    private List data;
    
    public StubQueryFilter(final List data) {
        this.data = data;
    }
    
    @Override
    public List query(final String queryStr) throws Exception {
        return this.data;
    }
    
    @Override
    public List query(final List queries) throws Exception {
        return this.data;
    }
}
