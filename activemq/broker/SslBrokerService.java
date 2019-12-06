// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

import java.security.KeyManagementException;
import java.io.IOException;
import org.apache.activemq.transport.TransportFactorySupport;
import org.apache.activemq.transport.tcp.SslTransportFactory;
import org.apache.activemq.transport.TransportServer;
import java.net.URI;
import java.security.SecureRandom;
import javax.net.ssl.TrustManager;
import javax.net.ssl.KeyManager;

public class SslBrokerService extends BrokerService
{
    public TransportConnector addSslConnector(final String bindAddress, final KeyManager[] km, final TrustManager[] tm, final SecureRandom random) throws Exception {
        return this.addSslConnector(new URI(bindAddress), km, tm, random);
    }
    
    public TransportConnector addSslConnector(final URI bindAddress, final KeyManager[] km, final TrustManager[] tm, final SecureRandom random) throws Exception {
        return this.addConnector(this.createSslTransportServer(bindAddress, km, tm, random));
    }
    
    protected TransportServer createSslTransportServer(final URI brokerURI, final KeyManager[] km, final TrustManager[] tm, final SecureRandom random) throws IOException, KeyManagementException {
        if (brokerURI.getScheme().equals("ssl")) {
            final SslTransportFactory transportFactory = new SslTransportFactory();
            final SslContext ctx = new SslContext(km, tm, random);
            SslContext.setCurrentSslContext(ctx);
            try {
                return transportFactory.doBind(brokerURI);
            }
            finally {
                SslContext.setCurrentSslContext(null);
            }
        }
        return TransportFactorySupport.bind(this, brokerURI);
    }
}
