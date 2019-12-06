// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.filter;

import java.util.regex.Pattern;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class RegExQueryFilter extends AbstractQueryFilter
{
    public static final String REGEX_PREFIX = "REGEX:QUERY:";
    
    protected RegExQueryFilter(final QueryFilter next) {
        super(next);
    }
    
    @Override
    public List query(final List queries) throws Exception {
        final Map regex = new HashMap();
        final List newQueries = new ArrayList();
        for (final String token : queries) {
            String key = "";
            String val = "";
            final int pos = token.indexOf("=");
            if (pos >= 0) {
                val = token.substring(pos + 1);
                key = token.substring(0, pos);
            }
            if (this.isRegularExpression(val)) {
                regex.put(key, this.compileQuery(val));
            }
            else {
                newQueries.add(token);
            }
        }
        return this.filterCollectionUsingRegEx(regex, this.next.query(newQueries));
    }
    
    protected boolean isRegularExpression(final String query) {
        return query.startsWith("REGEX:QUERY:");
    }
    
    protected Pattern compileQuery(final String query) {
        return Pattern.compile(query.substring("REGEX:QUERY:".length()));
    }
    
    protected List filterCollectionUsingRegEx(final Map regex, final List data) throws Exception {
        if (regex == null || regex.isEmpty()) {
            return data;
        }
        final List filteredElems = new ArrayList();
        for (final Object dataElem : data) {
            if (this.matches(dataElem, regex)) {
                filteredElems.add(dataElem);
            }
        }
        return filteredElems;
    }
    
    protected abstract boolean matches(final Object p0, final Map p1) throws Exception;
}
