// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.filter;

public class WildcardToRegExTransformFilter extends WildcardTransformFilter
{
    public WildcardToRegExTransformFilter(final RegExQueryFilter next) {
        super(next);
    }
    
    @Override
    protected boolean isWildcardQuery(final String query) {
        final String key = query;
        String val = "";
        final int pos = key.indexOf("=");
        if (pos >= 0) {
            val = key.substring(pos + 1);
        }
        return val.indexOf("*") >= 0 || val.indexOf("?") >= 0;
    }
    
    @Override
    protected String transformWildcardQuery(final String query) {
        String key = query;
        String val = "";
        final int pos = key.indexOf("=");
        if (pos >= 0) {
            val = key.substring(pos + 1);
            key = key.substring(0, pos);
        }
        val = val.replaceAll("[.]", "\\\\.");
        val = val.replaceAll("[?]", ".");
        val = val.replaceAll("[*]", ".*?");
        val = "(" + val + ")";
        val = "REGEX:QUERY:" + val;
        return key + "=" + val;
    }
}
