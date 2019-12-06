// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

import java.io.IOException;
import org.apache.activemq.broker.SslContext;
import org.apache.activemq.broker.BrokerServiceAware;
import java.net.URI;
import org.apache.activemq.broker.BrokerService;

public class TransportFactorySupport
{
    public static TransportServer bind(final BrokerService brokerService, final URI location) throws IOException {
        final TransportFactory tf = TransportFactory.findTransportFactory(location);
        if (brokerService != null && tf instanceof BrokerServiceAware) {
            ((BrokerServiceAware)tf).setBrokerService(brokerService);
        }
        try {
            if (brokerService != null) {
                SslContext.setCurrentSslContext(brokerService.getSslContext());
            }
            return tf.doBind(location);
        }
        finally {
            SslContext.setCurrentSslContext(null);
        }
    }
}
