// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.filter;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public abstract class WildcardTransformFilter extends AbstractQueryFilter
{
    protected WildcardTransformFilter(final QueryFilter next) {
        super(next);
    }
    
    @Override
    public List query(final List queries) throws Exception {
        final List newQueries = new ArrayList();
        for (final String queryToken : queries) {
            if (this.isWildcardQuery(queryToken)) {
                newQueries.add(this.transformWildcardQuery(queryToken));
            }
            else {
                newQueries.add(queryToken);
            }
        }
        return this.next.query(newQueries);
    }
    
    protected abstract boolean isWildcardQuery(final String p0);
    
    protected abstract String transformWildcardQuery(final String p0);
}
