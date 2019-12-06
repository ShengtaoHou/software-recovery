// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.util;

import org.apache.activemq.console.filter.WildcardToMsgSelectorTransformFilter;
import org.apache.activemq.console.filter.AmqMessagesQueryFilter;
import org.apache.activemq.console.filter.PropertiesViewFilter;
import org.apache.activemq.console.filter.GroupPropertiesViewFilter;
import org.apache.activemq.console.filter.QueryFilter;
import org.apache.activemq.console.filter.MapTransformFilter;
import org.apache.activemq.console.filter.StubQueryFilter;
import java.util.Set;
import javax.jms.ConnectionFactory;
import java.util.List;
import javax.jms.Destination;
import java.net.URI;

public final class AmqMessagesUtil
{
    public static final String JMS_MESSAGE_HEADER_PREFIX = "JMS_HEADER_FIELD:";
    public static final String JMS_MESSAGE_CUSTOM_PREFIX = "JMS_CUSTOM_FIELD:";
    public static final String JMS_MESSAGE_BODY_PREFIX = "JMS_BODY_FIELD:";
    
    private AmqMessagesUtil() {
    }
    
    public static List getAllMessages(final URI brokerUrl, final Destination dest) throws Exception {
        return getMessages(brokerUrl, dest, "");
    }
    
    public static List getMessages(final URI brokerUrl, final Destination dest, final String selector) throws Exception {
        return createMessageQueryFilter(brokerUrl, dest).query(selector);
    }
    
    public static List getMessages(final ConnectionFactory connectionFactory, final Destination dest, final String selector) throws Exception {
        return createMessageQueryFilter(connectionFactory, dest).query(selector);
    }
    
    public static List getMessages(final URI brokerUrl, final Destination dest, final List selectors) throws Exception {
        return createMessageQueryFilter(brokerUrl, dest).query(selectors);
    }
    
    public static List getMessages(final ConnectionFactory connectionFactory, final Destination dest, final List selectors) throws Exception {
        return createMessageQueryFilter(connectionFactory, dest).query(selectors);
    }
    
    public static List filterMessagesView(final List messages, final Set groupViews, final Set attributeViews) throws Exception {
        return new PropertiesViewFilter(attributeViews, new GroupPropertiesViewFilter(groupViews, new MapTransformFilter(new StubQueryFilter(messages)))).query("");
    }
    
    public static QueryFilter createMessageQueryFilter(final URI brokerUrl, final Destination dest) {
        return new WildcardToMsgSelectorTransformFilter(new AmqMessagesQueryFilter(brokerUrl, dest));
    }
    
    public static QueryFilter createMessageQueryFilter(final ConnectionFactory connectionFactory, final Destination dest) {
        return new WildcardToMsgSelectorTransformFilter(new AmqMessagesQueryFilter(connectionFactory, dest));
    }
}
