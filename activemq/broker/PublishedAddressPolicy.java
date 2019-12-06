// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

import java.util.Locale;
import java.net.UnknownHostException;
import org.apache.activemq.util.InetAddressUtil;
import java.net.InetAddress;
import java.net.URI;
import java.util.HashMap;

public class PublishedAddressPolicy
{
    private String clusterClientUriQuery;
    private PublishedHostStrategy publishedHostStrategy;
    private HashMap<Integer, Integer> portMapping;
    
    public PublishedAddressPolicy() {
        this.publishedHostStrategy = PublishedHostStrategy.DEFAULT;
        this.portMapping = new HashMap<Integer, Integer>();
    }
    
    public URI getPublishableConnectURI(final TransportConnector connector) throws Exception {
        final URI connectorURI = connector.getConnectUri();
        if (connectorURI == null) {
            return null;
        }
        final String scheme = connectorURI.getScheme();
        final String userInfo = this.getPublishedUserInfoValue(connectorURI.getUserInfo());
        final String host = this.getPublishedHostValue(connectorURI.getHost());
        int port = connectorURI.getPort();
        if (this.portMapping.containsKey(port)) {
            port = this.portMapping.get(port);
        }
        final String path = this.getPublishedPathValue(connectorURI.getPath());
        final String fragment = this.getPublishedFragmentValue(connectorURI.getFragment());
        final URI publishedURI = new URI(scheme, userInfo, host, port, path, this.getClusterClientUriQuery(), fragment);
        return publishedURI;
    }
    
    public String getPublishableConnectString(final TransportConnector connector) throws Exception {
        return this.getPublishableConnectURI(connector).toString();
    }
    
    protected String getPublishedHostValue(final String uriHostEntry) throws UnknownHostException {
        String result = uriHostEntry;
        if (this.publishedHostStrategy.equals(PublishedHostStrategy.IPADDRESS)) {
            final InetAddress address = InetAddress.getByName(uriHostEntry);
            result = address.getHostAddress();
        }
        else if (this.publishedHostStrategy.equals(PublishedHostStrategy.HOSTNAME)) {
            final InetAddress address = InetAddress.getByName(uriHostEntry);
            if (address.isAnyLocalAddress()) {
                result = InetAddressUtil.getLocalHostName();
            }
            else {
                result = address.getHostName();
            }
        }
        else if (this.publishedHostStrategy.equals(PublishedHostStrategy.FQDN)) {
            final InetAddress address = InetAddress.getByName(uriHostEntry);
            if (address.isAnyLocalAddress()) {
                result = InetAddressUtil.getLocalHostName();
            }
            else {
                result = address.getCanonicalHostName();
            }
        }
        return result;
    }
    
    protected String getPublishedPathValue(final String uriPathEntry) {
        return uriPathEntry;
    }
    
    protected String getPublishedFragmentValue(final String uriFragmentEntry) {
        return uriFragmentEntry;
    }
    
    protected String getPublishedUserInfoValue(final String uriUserInfoEntry) {
        return uriUserInfoEntry;
    }
    
    public String getClusterClientUriQuery() {
        return this.clusterClientUriQuery;
    }
    
    public void setClusterClientUriQuery(final String clusterClientUriQuery) {
        this.clusterClientUriQuery = clusterClientUriQuery;
    }
    
    public PublishedHostStrategy getPublishedHostStrategy() {
        return this.publishedHostStrategy;
    }
    
    public void setPublishedHostStrategy(final PublishedHostStrategy strategy) {
        this.publishedHostStrategy = strategy;
    }
    
    public void setPublishedHostStrategy(final String strategy) {
        this.publishedHostStrategy = PublishedHostStrategy.getValue(strategy);
    }
    
    public void setPortMapping(final HashMap<Integer, Integer> portMapping) {
        this.portMapping = portMapping;
    }
    
    public enum PublishedHostStrategy
    {
        DEFAULT, 
        IPADDRESS, 
        HOSTNAME, 
        FQDN;
        
        public static PublishedHostStrategy getValue(final String value) {
            return valueOf(value.toUpperCase(Locale.ENGLISH));
        }
    }
}
