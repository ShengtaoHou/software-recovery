// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.https;

import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import javax.net.ssl.SSLSocketFactory;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.conn.ClientConnectionManager;
import java.net.URI;
import org.apache.activemq.transport.util.TextWireFormat;
import org.apache.activemq.transport.http.HttpClientTransport;

public class HttpsClientTransport extends HttpClientTransport
{
    public HttpsClientTransport(final TextWireFormat wireFormat, final URI remoteUrl) {
        super(wireFormat, remoteUrl);
    }
    
    @Override
    protected ClientConnectionManager createClientConnectionManager() {
        final PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager(this.createSchemeRegistry());
        return (ClientConnectionManager)connectionManager;
    }
    
    private SchemeRegistry createSchemeRegistry() {
        final SchemeRegistry schemeRegistry = new SchemeRegistry();
        try {
            final org.apache.http.conn.ssl.SSLSocketFactory sslSocketFactory = new org.apache.http.conn.ssl.SSLSocketFactory((SSLSocketFactory)SSLSocketFactory.getDefault(), org.apache.http.conn.ssl.SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            schemeRegistry.register(new Scheme("https", this.getRemoteUrl().getPort(), (SchemeSocketFactory)sslSocketFactory));
            return schemeRegistry;
        }
        catch (Exception e) {
            throw new IllegalStateException("Failure trying to create scheme registry", e);
        }
    }
}
