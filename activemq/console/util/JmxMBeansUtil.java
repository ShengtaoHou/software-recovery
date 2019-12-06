// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.util;

import org.apache.activemq.console.filter.GroupPropertiesViewFilter;
import org.apache.activemq.console.filter.WildcardToMsgSelectorTransformFilter;
import org.apache.activemq.console.filter.MessagesQueryFilter;
import javax.management.ObjectName;
import java.util.Iterator;
import org.apache.activemq.console.filter.PropertiesViewFilter;
import org.apache.activemq.console.filter.QueryFilter;
import org.apache.activemq.console.filter.MapTransformFilter;
import org.apache.activemq.console.filter.StubQueryFilter;
import org.apache.activemq.console.filter.MBeansAttributeQueryFilter;
import java.util.Set;
import org.apache.activemq.console.filter.MBeansObjectNameQueryFilter;
import java.util.List;
import javax.management.MBeanServerConnection;

public final class JmxMBeansUtil
{
    private JmxMBeansUtil() {
    }
    
    public static List getAllBrokers(final MBeanServerConnection jmxConnection) throws Exception {
        return new MBeansObjectNameQueryFilter(jmxConnection).query("type=Broker,brokerName=*");
    }
    
    public static List getBrokersByName(final MBeanServerConnection jmxConnection, final String brokerName) throws Exception {
        return new MBeansObjectNameQueryFilter(jmxConnection).query("type=Broker,brokerName=" + brokerName);
    }
    
    public static List getAllBrokers(final MBeanServerConnection jmxConnection, final Set attributes) throws Exception {
        return new MBeansAttributeQueryFilter(jmxConnection, attributes, new MBeansObjectNameQueryFilter(jmxConnection)).query("type=Broker");
    }
    
    public static List getBrokersByName(final MBeanServerConnection jmxConnection, final String brokerName, final Set attributes) throws Exception {
        return new MBeansAttributeQueryFilter(jmxConnection, attributes, new MBeansObjectNameQueryFilter(jmxConnection)).query("type=Broker,brokerName=" + brokerName);
    }
    
    public static List x_queryMBeans(final MBeanServerConnection jmxConnection, final List queryList) throws Exception {
        if (queryList == null || queryList.size() == 0) {
            return createMBeansObjectNameQuery(jmxConnection).query("");
        }
        return createMBeansObjectNameQuery(jmxConnection).query(queryList);
    }
    
    public static List queryMBeans(final MBeanServerConnection jmxConnection, final List queryList, final Set attributes) throws Exception {
        if (queryList == null || queryList.size() == 0) {
            return createMBeansAttributeQuery(jmxConnection, attributes).query("");
        }
        return createMBeansAttributeQuery(jmxConnection, attributes).query(queryList);
    }
    
    public static List queryMBeans(final MBeanServerConnection jmxConnection, final String queryString) throws Exception {
        return createMBeansObjectNameQuery(jmxConnection).query(queryString);
    }
    
    public static List queryMBeans(final MBeanServerConnection jmxConnection, final String queryString, final Set attributes) throws Exception {
        return createMBeansAttributeQuery(jmxConnection, attributes).query(queryString);
    }
    
    public static List filterMBeansView(final List mbeans, final Set viewFilter) throws Exception {
        return new PropertiesViewFilter(viewFilter, new MapTransformFilter(new StubQueryFilter(mbeans))).query("");
    }
    
    public static String createQueryString(final String query, final String param) {
        return query.replaceAll("%1", param);
    }
    
    public static String createQueryString(final String query, final List params) {
        String output = query;
        int count = 1;
        final Iterator i = params.iterator();
        while (i.hasNext()) {
            output = output.replaceAll("%" + count++, i.next().toString());
        }
        return output;
    }
    
    public static QueryFilter createMBeansObjectNameQuery(final MBeanServerConnection jmxConnection) {
        return new MBeansObjectNameQueryFilter(jmxConnection);
    }
    
    public static QueryFilter createMBeansAttributeQuery(final MBeanServerConnection jmxConnection, final Set attributes) {
        return new MBeansAttributeQueryFilter(jmxConnection, attributes, new MBeansObjectNameQueryFilter(jmxConnection));
    }
    
    public static QueryFilter createMessageQueryFilter(final MBeanServerConnection jmxConnection, final ObjectName destName) {
        return new WildcardToMsgSelectorTransformFilter(new MessagesQueryFilter(jmxConnection, destName));
    }
    
    public static List filterMessagesView(final List messages, final Set groupViews, final Set attributeViews) throws Exception {
        return new PropertiesViewFilter(attributeViews, new GroupPropertiesViewFilter(groupViews, new MapTransformFilter(new StubQueryFilter(messages)))).query("");
    }
}
