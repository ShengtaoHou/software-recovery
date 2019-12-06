// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.spring;

import java.net.MalformedURLException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.ArrayList;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.net.ssl.TrustManager;
import javax.net.ssl.KeyManager;
import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.KeyManagerFactory;
import org.apache.activemq.broker.SslContext;

public class SpringSslContext extends SslContext
{
    private String keyStoreType;
    private String trustStoreType;
    private String secureRandomAlgorithm;
    private String keyStoreAlgorithm;
    private String trustStoreAlgorithm;
    private String keyStore;
    private String trustStore;
    private String keyStoreKeyPassword;
    private String keyStorePassword;
    private String trustStorePassword;
    
    public SpringSslContext() {
        this.keyStoreType = "jks";
        this.trustStoreType = "jks";
        this.secureRandomAlgorithm = "SHA1PRNG";
        this.keyStoreAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
        this.trustStoreAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
    }
    
    @PostConstruct
    private void postConstruct() {
        try {
            this.afterPropertiesSet();
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void afterPropertiesSet() throws Exception {
        this.keyManagers.addAll(this.createKeyManagers());
        this.trustManagers.addAll(this.createTrustManagers());
        if (this.secureRandom == null) {
            this.secureRandom = this.createSecureRandom();
        }
    }
    
    private SecureRandom createSecureRandom() throws NoSuchAlgorithmException {
        return SecureRandom.getInstance(this.secureRandomAlgorithm);
    }
    
    private Collection<TrustManager> createTrustManagers() throws Exception {
        final KeyStore ks = this.createTrustManagerKeyStore();
        if (ks == null) {
            return new ArrayList<TrustManager>(0);
        }
        final TrustManagerFactory tmf = TrustManagerFactory.getInstance(this.trustStoreAlgorithm);
        tmf.init(ks);
        return Arrays.asList(tmf.getTrustManagers());
    }
    
    private Collection<KeyManager> createKeyManagers() throws Exception {
        final KeyStore ks = this.createKeyManagerKeyStore();
        if (ks == null) {
            return new ArrayList<KeyManager>(0);
        }
        final KeyManagerFactory tmf = KeyManagerFactory.getInstance(this.keyStoreAlgorithm);
        tmf.init(ks, (char[])((this.keyStoreKeyPassword == null) ? ((this.keyStorePassword == null) ? null : this.keyStorePassword.toCharArray()) : this.keyStoreKeyPassword.toCharArray()));
        return Arrays.asList(tmf.getKeyManagers());
    }
    
    private KeyStore createTrustManagerKeyStore() throws Exception {
        if (this.trustStore == null) {
            return null;
        }
        final KeyStore ks = KeyStore.getInstance(this.trustStoreType);
        final InputStream is = Utils.resourceFromString(this.trustStore).getInputStream();
        try {
            ks.load(is, (char[])((this.trustStorePassword == null) ? null : this.trustStorePassword.toCharArray()));
        }
        finally {
            is.close();
        }
        return ks;
    }
    
    private KeyStore createKeyManagerKeyStore() throws Exception {
        if (this.keyStore == null) {
            return null;
        }
        final KeyStore ks = KeyStore.getInstance(this.keyStoreType);
        final InputStream is = Utils.resourceFromString(this.keyStore).getInputStream();
        try {
            ks.load(is, (char[])((this.keyStorePassword == null) ? null : this.keyStorePassword.toCharArray()));
        }
        finally {
            is.close();
        }
        return ks;
    }
    
    public String getTrustStoreType() {
        return this.trustStoreType;
    }
    
    public String getKeyStoreType() {
        return this.keyStoreType;
    }
    
    public String getKeyStore() {
        return this.keyStore;
    }
    
    public void setKeyStore(final String keyStore) throws MalformedURLException {
        this.keyStore = keyStore;
    }
    
    public String getTrustStore() {
        return this.trustStore;
    }
    
    public void setTrustStore(final String trustStore) throws MalformedURLException {
        this.trustStore = trustStore;
    }
    
    public String getKeyStoreAlgorithm() {
        return this.keyStoreAlgorithm;
    }
    
    public void setKeyStoreAlgorithm(final String keyAlgorithm) {
        this.keyStoreAlgorithm = keyAlgorithm;
    }
    
    public String getTrustStoreAlgorithm() {
        return this.trustStoreAlgorithm;
    }
    
    public void setTrustStoreAlgorithm(final String trustAlgorithm) {
        this.trustStoreAlgorithm = trustAlgorithm;
    }
    
    public String getKeyStoreKeyPassword() {
        return this.keyStoreKeyPassword;
    }
    
    public void setKeyStoreKeyPassword(final String keyPassword) {
        this.keyStoreKeyPassword = keyPassword;
    }
    
    public String getKeyStorePassword() {
        return this.keyStorePassword;
    }
    
    public void setKeyStorePassword(final String keyPassword) {
        this.keyStorePassword = keyPassword;
    }
    
    public String getTrustStorePassword() {
        return this.trustStorePassword;
    }
    
    public void setTrustStorePassword(final String trustPassword) {
        this.trustStorePassword = trustPassword;
    }
    
    public void setKeyStoreType(final String keyType) {
        this.keyStoreType = keyType;
    }
    
    public void setTrustStoreType(final String trustType) {
        this.trustStoreType = trustType;
    }
    
    public String getSecureRandomAlgorithm() {
        return this.secureRandomAlgorithm;
    }
    
    public void setSecureRandomAlgorithm(final String secureRandomAlgorithm) {
        this.secureRandomAlgorithm = secureRandomAlgorithm;
    }
}
