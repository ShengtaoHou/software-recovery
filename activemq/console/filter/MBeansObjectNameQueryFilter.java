// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.filter;

import javax.management.QueryExp;
import java.util.Collection;
import java.util.ArrayList;
import java.io.IOException;
import javax.management.MalformedObjectNameException;
import java.util.Iterator;
import javax.management.ObjectName;
import java.util.List;
import javax.management.MBeanServerConnection;

public class MBeansObjectNameQueryFilter extends AbstractQueryFilter
{
    public static final String DEFAULT_JMX_DOMAIN = "org.apache.activemq";
    public static final String QUERY_EXP_PREFIX = "MBeans.QueryExp.";
    private MBeanServerConnection jmxConnection;
    
    public MBeansObjectNameQueryFilter(final MBeanServerConnection jmxConnection) {
        super(null);
        this.jmxConnection = jmxConnection;
    }
    
    @Override
    public List query(final List queries) throws MalformedObjectNameException, IOException {
        if (queries == null || queries.isEmpty()) {
            return this.queryMBeans(new ObjectName("org.apache.activemq:*"), null);
        }
        String objNameQuery = "";
        final String queryExp = "";
        String delimiter = "";
        for (String key : queries) {
            String val = "";
            final int pos = key.indexOf("=");
            if (pos >= 0) {
                val = key.substring(pos + 1);
                key = key.substring(0, pos);
            }
            else {
                objNameQuery = objNameQuery + delimiter + key;
            }
            if (val.startsWith("MBeans.QueryExp.")) {
                continue;
            }
            if (key.equals("") || val.equals("")) {
                continue;
            }
            objNameQuery = objNameQuery + delimiter + key + "=" + val;
            delimiter = ",";
        }
        return this.queryMBeans(new ObjectName("org.apache.activemq:" + objNameQuery), queryExp);
    }
    
    protected List queryMBeans(final ObjectName objName, final String queryExpStr) throws IOException {
        final QueryExp queryExp = this.createQueryExp(queryExpStr);
        final List mbeans = new ArrayList(this.jmxConnection.queryMBeans(objName, queryExp));
        return mbeans;
    }
    
    protected QueryExp createQueryExp(final String queryExpStr) {
        return null;
    }
}
