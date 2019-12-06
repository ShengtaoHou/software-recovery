// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store;

import java.io.IOException;
import java.util.Iterator;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.ArrayList;
import org.apache.activemq.command.SubscriptionInfo;
import java.util.List;

public class PersistenceAdapterSupport
{
    public static List<SubscriptionInfo> listSubscriptions(final PersistenceAdapter pa, final String clientId) throws IOException {
        final ArrayList<SubscriptionInfo> rc = new ArrayList<SubscriptionInfo>();
        for (final ActiveMQDestination destination : pa.getDestinations()) {
            if (destination.isTopic()) {
                final TopicMessageStore store = pa.createTopicMessageStore((ActiveMQTopic)destination);
                for (final SubscriptionInfo sub : store.getAllSubscriptions()) {
                    if (clientId == sub.getClientId() || clientId.equals(sub.getClientId())) {
                        rc.add(sub);
                    }
                }
            }
        }
        return rc;
    }
}
