// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import java.util.ArrayList;
import javax.net.ssl.SSLContext;
import java.security.SecureRandom;
import javax.net.ssl.TrustManager;
import javax.net.ssl.KeyManager;
import java.util.List;

public class SslContext
{
    protected String protocol;
    protected String provider;
    protected List<KeyManager> keyManagers;
    protected List<TrustManager> trustManagers;
    protected SecureRandom secureRandom;
    private SSLContext sslContext;
    private static final ThreadLocal<SslContext> current;
    
    public SslContext() {
        this.protocol = "TLS";
        this.provider = null;
        this.keyManagers = new ArrayList<KeyManager>();
        this.trustManagers = new ArrayList<TrustManager>();
    }
    
    public SslContext(final KeyManager[] km, final TrustManager[] tm, final SecureRandom random) {
        this.protocol = "TLS";
        this.provider = null;
        this.keyManagers = new ArrayList<KeyManager>();
        this.trustManagers = new ArrayList<TrustManager>();
        if (km != null) {
            this.setKeyManagers(Arrays.asList(km));
        }
        if (tm != null) {
            this.setTrustManagers(Arrays.asList(tm));
        }
        this.setSecureRandom(random);
    }
    
    public static void setCurrentSslContext(final SslContext bs) {
        SslContext.current.set(bs);
    }
    
    public static SslContext getCurrentSslContext() {
        return SslContext.current.get();
    }
    
    public KeyManager[] getKeyManagersAsArray() {
        final KeyManager[] rc = new KeyManager[this.keyManagers.size()];
        return this.keyManagers.toArray(rc);
    }
    
    public TrustManager[] getTrustManagersAsArray() {
        final TrustManager[] rc = new TrustManager[this.trustManagers.size()];
        return this.trustManagers.toArray(rc);
    }
    
    public void addKeyManager(final KeyManager km) {
        this.keyManagers.add(km);
    }
    
    public boolean removeKeyManager(final KeyManager km) {
        return this.keyManagers.remove(km);
    }
    
    public void addTrustManager(final TrustManager tm) {
        this.trustManagers.add(tm);
    }
    
    public boolean removeTrustManager(final TrustManager tm) {
        return this.trustManagers.remove(tm);
    }
    
    public List<KeyManager> getKeyManagers() {
        return this.keyManagers;
    }
    
    public void setKeyManagers(final List<KeyManager> keyManagers) {
        this.keyManagers = keyManagers;
    }
    
    public List<TrustManager> getTrustManagers() {
        return this.trustManagers;
    }
    
    public void setTrustManagers(final List<TrustManager> trustManagers) {
        this.trustManagers = trustManagers;
    }
    
    public SecureRandom getSecureRandom() {
        return this.secureRandom;
    }
    
    public void setSecureRandom(final SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }
    
    public String getProtocol() {
        return this.protocol;
    }
    
    public void setProtocol(final String protocol) {
        this.protocol = protocol;
    }
    
    public String getProvider() {
        return this.provider;
    }
    
    public void setProvider(final String provider) {
        this.provider = provider;
    }
    
    public SSLContext getSSLContext() throws NoSuchProviderException, NoSuchAlgorithmException, KeyManagementException {
        if (this.sslContext == null) {
            if (this.provider == null) {
                this.sslContext = SSLContext.getInstance(this.protocol);
            }
            else {
                this.sslContext = SSLContext.getInstance(this.protocol, this.provider);
            }
            this.sslContext.init(this.getKeyManagersAsArray(), this.getTrustManagersAsArray(), this.getSecureRandom());
        }
        return this.sslContext;
    }
    
    public void setSSLContext(final SSLContext sslContext) {
        this.sslContext = sslContext;
    }
    
    static {
        current = new ThreadLocal<SslContext>();
    }
}
