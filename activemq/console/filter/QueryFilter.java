// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.filter;

import java.util.List;

public interface QueryFilter
{
    public static final String QUERY_DELIMETER = ",";
    
    List query(final String p0) throws Exception;
    
    List query(final List p0) throws Exception;
}
