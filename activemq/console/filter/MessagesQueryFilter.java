// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.filter;

import java.util.Arrays;
import javax.management.openmbean.CompositeData;
import java.util.Iterator;
import java.util.List;
import javax.management.ObjectName;
import javax.management.MBeanServerConnection;

public class MessagesQueryFilter extends AbstractQueryFilter
{
    private MBeanServerConnection jmxConnection;
    private ObjectName destName;
    
    public MessagesQueryFilter(final MBeanServerConnection jmxConnection, final ObjectName destName) {
        super(null);
        this.jmxConnection = jmxConnection;
        this.destName = destName;
    }
    
    @Override
    public List query(final List queries) throws Exception {
        String selector = "";
        final Iterator i = queries.iterator();
        while (i.hasNext()) {
            selector = selector + "(" + i.next().toString() + ") AND ";
        }
        if (!selector.equals("")) {
            selector = selector.substring(0, selector.length() - 5);
        }
        return this.queryMessages(selector);
    }
    
    protected List queryMessages(final String selector) throws Exception {
        final CompositeData[] messages = (CompositeData[])this.jmxConnection.invoke(this.destName, "browse", new Object[] { selector }, new String[0]);
        return Arrays.asList(messages);
    }
}
