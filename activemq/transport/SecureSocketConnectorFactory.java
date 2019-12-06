// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

import org.eclipse.jetty.util.ssl.SslContextFactory;
import javax.net.ssl.SSLContext;
import org.eclipse.jetty.server.ssl.SslConnector;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.apache.activemq.transport.https.Krb5AndCertsSslSocketConnector;
import java.util.Map;
import org.apache.activemq.util.IntrospectionSupport;
import org.eclipse.jetty.server.Connector;
import org.apache.activemq.broker.SslContext;

public class SecureSocketConnectorFactory extends SocketConnectorFactory
{
    private String keyPassword;
    private String keyStorePassword;
    private String keyStore;
    private String trustStorePassword;
    private String trustStore;
    private boolean needClientAuth;
    private boolean wantClientAuth;
    private String keyStoreType;
    private String secureRandomCertficateAlgorithm;
    private String trustCertificateAlgorithm;
    private String keyCertificateAlgorithm;
    private String protocol;
    private String auth;
    private SslContext context;
    
    public SecureSocketConnectorFactory(final SslContext context) {
        this.keyPassword = System.getProperty("javax.net.ssl.keyPassword");
        this.keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
        this.keyStore = System.getProperty("javax.net.ssl.keyStore");
        this.trustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
        this.trustStore = System.getProperty("javax.net.ssl.trustStore");
        this.context = context;
    }
    
    @Override
    public Connector createConnector() throws Exception {
        IntrospectionSupport.setProperties(this, this.getTransportOptions());
        SslConnector sslConnector;
        if (Krb5AndCertsSslSocketConnector.isKrb(this.auth)) {
            sslConnector = (SslConnector)new Krb5AndCertsSslSocketConnector();
            ((Krb5AndCertsSslSocketConnector)sslConnector).setMode(this.auth);
        }
        else {
            sslConnector = (SslConnector)new SslSelectChannelConnector();
        }
        final SSLContext sslContext = (this.context == null) ? null : this.context.getSSLContext();
        final SslContextFactory factory = sslConnector.getSslContextFactory();
        if (this.context != null) {
            factory.setSslContext(sslContext);
        }
        else {
            if (this.keyStore != null) {
                factory.setKeyStorePath(this.keyStore);
            }
            if (this.keyStorePassword != null) {
                factory.setKeyStorePassword(this.keyStorePassword);
            }
            if (this.keyPassword == null && this.keyStorePassword != null) {
                factory.setKeyStorePassword(this.keyStorePassword);
            }
            if (this.keyStoreType != null) {
                factory.setKeyStoreType(this.keyStoreType);
            }
            if (this.secureRandomCertficateAlgorithm != null) {
                factory.setSecureRandomAlgorithm(this.secureRandomCertficateAlgorithm);
            }
            if (this.keyCertificateAlgorithm != null) {
                factory.setSslKeyManagerFactoryAlgorithm(this.keyCertificateAlgorithm);
            }
            if (this.trustCertificateAlgorithm != null) {
                factory.setTrustManagerFactoryAlgorithm(this.trustCertificateAlgorithm);
            }
            if (this.protocol != null) {
                factory.setProtocol(this.protocol);
            }
            if (this.trustStore != null) {
                factory.setTrustStore(this.trustStore);
            }
            if (this.trustStorePassword != null) {
                factory.setTrustStorePassword(this.trustStorePassword);
            }
        }
        factory.setNeedClientAuth(this.needClientAuth);
        factory.setWantClientAuth(this.wantClientAuth);
        return (Connector)sslConnector;
    }
    
    public String getKeyStore() {
        return this.keyStore;
    }
    
    public void setKeyStore(final String keyStore) {
        this.keyStore = keyStore;
    }
    
    public String getKeyPassword() {
        return this.keyPassword;
    }
    
    public void setKeyPassword(final String keyPassword) {
        this.keyPassword = keyPassword;
    }
    
    public String getKeyStoreType() {
        return this.keyStoreType;
    }
    
    public void setKeyStoreType(final String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }
    
    public String getKeyStorePassword() {
        return this.keyStorePassword;
    }
    
    public void setKeyStorePassword(final String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }
    
    public String getProtocol() {
        return this.protocol;
    }
    
    public void setProtocol(final String protocol) {
        this.protocol = protocol;
    }
    
    public String getSecureRandomCertficateAlgorithm() {
        return this.secureRandomCertficateAlgorithm;
    }
    
    public void setSecureRandomCertficateAlgorithm(final String secureRandomCertficateAlgorithm) {
        this.secureRandomCertficateAlgorithm = secureRandomCertficateAlgorithm;
    }
    
    public String getKeyCertificateAlgorithm() {
        return this.keyCertificateAlgorithm;
    }
    
    public void setKeyCertificateAlgorithm(final String keyCertificateAlgorithm) {
        this.keyCertificateAlgorithm = keyCertificateAlgorithm;
    }
    
    public String getTrustCertificateAlgorithm() {
        return this.trustCertificateAlgorithm;
    }
    
    public void setTrustCertificateAlgorithm(final String trustCertificateAlgorithm) {
        this.trustCertificateAlgorithm = trustCertificateAlgorithm;
    }
    
    public String getAuth() {
        return this.auth;
    }
    
    public void setAuth(final String auth) {
        this.auth = auth;
    }
    
    public boolean isWantClientAuth() {
        return this.wantClientAuth;
    }
    
    public void setWantClientAuth(final boolean wantClientAuth) {
        this.wantClientAuth = wantClientAuth;
    }
    
    public boolean isNeedClientAuth() {
        return this.needClientAuth;
    }
    
    public void setNeedClientAuth(final boolean needClientAuth) {
        this.needClientAuth = needClientAuth;
    }
    
    public String getTrustStore() {
        return this.trustStore;
    }
    
    public void setTrustStore(final String trustStore) {
        this.trustStore = trustStore;
    }
    
    public String getTrustStorePassword() {
        return this.trustStorePassword;
    }
    
    public void setTrustStorePassword(final String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }
}
