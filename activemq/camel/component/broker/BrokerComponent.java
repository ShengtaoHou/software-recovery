// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.camel.component.broker;

import java.util.Iterator;
import java.util.Set;
import org.apache.activemq.broker.view.MessageBrokerView;
import java.util.ArrayList;
import org.apache.activemq.broker.view.MessageBrokerViewRegistry;
import java.util.List;
import org.apache.camel.ComponentConfiguration;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.camel.util.ObjectHelper;
import org.apache.camel.Endpoint;
import java.util.Map;
import org.apache.camel.spi.EndpointCompleter;
import org.apache.camel.impl.UriEndpointComponent;

public class BrokerComponent extends UriEndpointComponent implements EndpointCompleter
{
    public BrokerComponent() {
        super((Class)BrokerEndpoint.class);
    }
    
    protected Endpoint createEndpoint(final String uri, String remaining, final Map<String, Object> parameters) throws Exception {
        final BrokerConfiguration brokerConfiguration = new BrokerConfiguration();
        this.setProperties((Object)brokerConfiguration, (Map)parameters);
        byte destinationType = 1;
        if (remaining.startsWith("queue:")) {
            remaining = ObjectHelper.removeStartingCharacters(remaining.substring("queue:".length()), '/');
        }
        else if (remaining.startsWith("topic:")) {
            destinationType = 2;
            remaining = ObjectHelper.removeStartingCharacters(remaining.substring("topic:".length()), '/');
        }
        else if (remaining.startsWith("temp:queue:")) {
            destinationType = 5;
            remaining = ObjectHelper.removeStartingCharacters(remaining.substring("temp:queue:".length()), '/');
        }
        else if (remaining.startsWith("temp:topic:")) {
            destinationType = 6;
            remaining = ObjectHelper.removeStartingCharacters(remaining.substring("temp:topic:".length()), '/');
        }
        final ActiveMQDestination destination = ActiveMQDestination.createDestination(remaining, destinationType);
        final BrokerEndpoint brokerEndpoint = new BrokerEndpoint(uri, this, destination, brokerConfiguration);
        this.setProperties((Object)brokerEndpoint, (Map)parameters);
        return (Endpoint)brokerEndpoint;
    }
    
    public List<String> completeEndpointPath(final ComponentConfiguration componentConfiguration, final String completionText) {
        final String brokerName = String.valueOf(componentConfiguration.getParameter("brokerName"));
        final MessageBrokerView messageBrokerView = MessageBrokerViewRegistry.getInstance().lookup(brokerName);
        if (messageBrokerView != null) {
            String destinationName = completionText;
            Set<? extends ActiveMQDestination> set = messageBrokerView.getQueues();
            if (completionText.startsWith("topic:")) {
                set = messageBrokerView.getTopics();
                destinationName = completionText.substring(6);
            }
            else if (completionText.startsWith("queue:")) {
                destinationName = completionText.substring(6);
            }
            final ArrayList<String> answer = new ArrayList<String>();
            for (final ActiveMQDestination destination : set) {
                if (destination.getPhysicalName().startsWith(destinationName)) {
                    answer.add(destination.getPhysicalName());
                }
            }
            return answer;
        }
        return null;
    }
}
