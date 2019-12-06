// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network;

import java.net.URI;
import org.apache.activemq.transport.TransportFactory;
import java.util.Map;
import java.util.HashMap;
import org.apache.activemq.util.URISupport;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.transport.Transport;

public final class NetworkBridgeFactory
{
    private NetworkBridgeFactory() {
    }
    
    public static DemandForwardingBridge createBridge(final NetworkBridgeConfiguration config, final Transport localTransport, final Transport remoteTransport) {
        return createBridge(config, localTransport, remoteTransport, null);
    }
    
    public static DemandForwardingBridge createBridge(final NetworkBridgeConfiguration configuration, final Transport localTransport, final Transport remoteTransport, final NetworkBridgeListener listener) {
        DemandForwardingBridge result = null;
        if (configuration.isConduitSubscriptions()) {
            result = new DurableConduitBridge(configuration, localTransport, remoteTransport);
        }
        else {
            result = new DemandForwardingBridge(configuration, localTransport, remoteTransport);
        }
        if (listener != null) {
            result.setNetworkBridgeListener(listener);
        }
        return result;
    }
    
    public static Transport createLocalTransport(final Broker broker) throws Exception {
        URI uri = broker.getVmConnectorURI();
        final HashMap<String, String> map = new HashMap<String, String>(URISupport.parseParameters(uri));
        map.put("network", "true");
        map.put("async", "true");
        uri = URISupport.createURIWithQuery(uri, URISupport.createQueryString(map));
        return TransportFactory.connect(uri);
    }
}
