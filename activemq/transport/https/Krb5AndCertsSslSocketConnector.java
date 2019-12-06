// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.https;

import org.slf4j.LoggerFactory;
import java.util.Collections;
import java.security.Principal;
import org.eclipse.jetty.server.ssl.ServletSSL;
import javax.net.ssl.SSLSocket;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.io.EndPoint;
import java.io.IOException;
import javax.net.ssl.SSLServerSocket;
import java.net.ServerSocket;
import java.security.KeyManagementException;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.net.ssl.TrustManager;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import java.util.Random;
import org.slf4j.Logger;
import java.util.List;
import org.eclipse.jetty.server.ssl.SslSocketConnector;

public class Krb5AndCertsSslSocketConnector extends SslSocketConnector
{
    public static final List<String> KRB5_CIPHER_SUITES;
    private static final Logger LOG;
    private static final String REMOTE_PRINCIPAL = "remote_principal";
    private boolean useKrb;
    private boolean useCerts;
    
    public Krb5AndCertsSslSocketConnector() {
        this.useKrb = false;
        this.useCerts = true;
        this.setPasswords();
    }
    
    public static boolean isKrb(final String mode) {
        return mode == MODE.KRB.toString() || mode == MODE.BOTH.toString();
    }
    
    public void setMode(final String mode) {
        this.useKrb = (mode == MODE.KRB.toString() || mode == MODE.BOTH.toString());
        this.useCerts = (mode == MODE.CERTS.toString() || mode == MODE.BOTH.toString());
        this.logIfDebug("useKerb = " + this.useKrb + ", useCerts = " + this.useCerts);
    }
    
    private void setPasswords() {
        if (!this.useCerts) {
            final Random r = new Random();
            System.setProperty("jetty.ssl.password", String.valueOf(r.nextLong()));
            System.setProperty("jetty.ssl.keypassword", String.valueOf(r.nextLong()));
        }
    }
    
    public SslContextFactory getSslContextFactory() {
        final SslContextFactory factory = super.getSslContextFactory();
        if (this.useCerts) {
            return factory;
        }
        try {
            final SSLContext context = (factory.getProvider() == null) ? SSLContext.getInstance(factory.getProtocol()) : SSLContext.getInstance(factory.getProtocol(), factory.getProvider());
            context.init(null, null, null);
            factory.setSslContext(context);
        }
        catch (NoSuchAlgorithmException ex) {}
        catch (NoSuchProviderException ex2) {}
        catch (KeyManagementException ex3) {}
        return factory;
    }
    
    protected ServerSocket newServerSocket(final String host, final int port, final int backlog) throws IOException {
        this.logIfDebug("Creating new KrbServerSocket for: " + host);
        SSLServerSocket ss = null;
        if (this.useCerts) {
            ss = (SSLServerSocket)super.newServerSocket(host, port, backlog);
        }
        else {
            try {
                ss = (SSLServerSocket)super.newServerSocket(host, port, backlog);
            }
            catch (Exception e) {
                Krb5AndCertsSslSocketConnector.LOG.warn("Could not create KRB5 Listener", e);
                throw new IOException("Could not create KRB5 Listener: " + e.toString());
            }
        }
        if (this.useKrb) {
            ss.setNeedClientAuth(true);
            String[] combined;
            if (this.useCerts) {
                final String[] certs = ss.getEnabledCipherSuites();
                combined = new String[certs.length + Krb5AndCertsSslSocketConnector.KRB5_CIPHER_SUITES.size()];
                System.arraycopy(certs, 0, combined, 0, certs.length);
                System.arraycopy(Krb5AndCertsSslSocketConnector.KRB5_CIPHER_SUITES.toArray(new String[0]), 0, combined, certs.length, Krb5AndCertsSslSocketConnector.KRB5_CIPHER_SUITES.size());
            }
            else {
                combined = Krb5AndCertsSslSocketConnector.KRB5_CIPHER_SUITES.toArray(new String[0]);
            }
            ss.setEnabledCipherSuites(combined);
        }
        return ss;
    }
    
    public void customize(final EndPoint endpoint, final Request request) throws IOException {
        if (this.useKrb) {
            final SSLSocket sslSocket = (SSLSocket)endpoint.getTransport();
            final Principal remotePrincipal = sslSocket.getSession().getPeerPrincipal();
            this.logIfDebug("Remote principal = " + remotePrincipal);
            request.setScheme("https");
            request.setAttribute("remote_principal", (Object)remotePrincipal);
            if (!this.useCerts) {
                final String cipherSuite = sslSocket.getSession().getCipherSuite();
                final Integer keySize = ServletSSL.deduceKeyLength(cipherSuite);
                request.setAttribute("javax.servlet.request.cipher_suite", (Object)cipherSuite);
                request.setAttribute("javax.servlet.request.key_size", (Object)keySize);
            }
        }
        if (this.useCerts) {
            super.customize(endpoint, request);
        }
    }
    
    private void logIfDebug(final String s) {
        if (Krb5AndCertsSslSocketConnector.LOG.isDebugEnabled()) {
            Krb5AndCertsSslSocketConnector.LOG.debug(s);
        }
    }
    
    static {
        KRB5_CIPHER_SUITES = Collections.unmodifiableList((List<? extends String>)Collections.singletonList((T)"TLS_KRB5_WITH_3DES_EDE_CBC_SHA"));
        System.setProperty("https.cipherSuites", Krb5AndCertsSslSocketConnector.KRB5_CIPHER_SUITES.get(0));
        LOG = LoggerFactory.getLogger(Krb5AndCertsSslSocketConnector.class);
    }
    
    public enum MODE
    {
        KRB, 
        CERTS, 
        BOTH;
    }
}
