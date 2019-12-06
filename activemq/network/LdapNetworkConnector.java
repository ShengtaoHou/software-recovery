// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network;

import org.slf4j.LoggerFactory;
import javax.naming.event.NamingExceptionEvent;
import javax.naming.event.NamingEvent;
import javax.naming.directory.Attributes;
import java.util.Iterator;
import javax.naming.NamingEnumeration;
import javax.naming.event.NamingListener;
import javax.naming.event.EventDirContext;
import javax.naming.directory.SearchResult;
import javax.naming.CommunicationException;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;
import org.apache.activemq.util.URISupport;
import java.util.concurrent.ConcurrentHashMap;
import javax.naming.directory.DirContext;
import java.util.Map;
import javax.naming.directory.SearchControls;
import java.net.URI;
import org.slf4j.Logger;
import javax.naming.event.ObjectChangeListener;
import javax.naming.event.NamespaceChangeListener;

public class LdapNetworkConnector extends NetworkConnector implements NamespaceChangeListener, ObjectChangeListener
{
    private static final Logger LOG;
    private static final String REQUIRED_OBJECT_CLASS_FILTER = "(&(objectClass=ipHost)(objectClass=ipService))";
    private URI[] availableURIs;
    private int availableURIsIndex;
    private String base;
    private boolean failover;
    private long curReconnectDelay;
    private long maxReconnectDelay;
    private String user;
    private String password;
    private boolean anonymousAuthentication;
    private SearchControls searchControls;
    private String searchFilter;
    private boolean searchEventListener;
    private Map<URI, NetworkConnector> connectorMap;
    private Map<URI, Integer> referenceMap;
    private Map<String, URI> uuidMap;
    private DirContext context;
    private URI ldapURI;
    
    public LdapNetworkConnector() {
        this.availableURIs = null;
        this.availableURIsIndex = 0;
        this.base = null;
        this.failover = false;
        this.curReconnectDelay = 1000L;
        this.maxReconnectDelay = 30000L;
        this.user = null;
        this.password = null;
        this.anonymousAuthentication = false;
        this.searchControls = new SearchControls();
        this.searchFilter = "(&(objectClass=ipHost)(objectClass=ipService))";
        this.searchEventListener = false;
        this.connectorMap = new ConcurrentHashMap<URI, NetworkConnector>();
        this.referenceMap = new ConcurrentHashMap<URI, Integer>();
        this.uuidMap = new ConcurrentHashMap<String, URI>();
        this.context = null;
        this.ldapURI = null;
    }
    
    public URI getUri() {
        return this.availableURIs[++this.availableURIsIndex % this.availableURIs.length];
    }
    
    public void setUri(final URI uri) throws Exception {
        final URISupport.CompositeData data = URISupport.parseComposite(uri);
        if (data.getScheme().equals("failover")) {
            this.availableURIs = data.getComponents();
            this.failover = true;
        }
        else {
            this.availableURIs = new URI[] { uri };
        }
    }
    
    public void setBase(final String base) {
        this.base = base;
    }
    
    public void setUser(final String user) {
        this.user = user;
    }
    
    @Override
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public void setAnonymousAuthentication(final boolean anonymousAuthentication) {
        this.anonymousAuthentication = anonymousAuthentication;
    }
    
    public void setSearchScope(final String searchScope) throws Exception {
        int scope;
        if (searchScope.equals("OBJECT_SCOPE")) {
            scope = 0;
        }
        else if (searchScope.equals("ONELEVEL_SCOPE")) {
            scope = 1;
        }
        else {
            if (!searchScope.equals("SUBTREE_SCOPE")) {
                throw new Exception("ERR: unknown LDAP search scope specified: " + searchScope);
            }
            scope = 2;
        }
        this.searchControls.setSearchScope(scope);
    }
    
    public void setSearchFilter(final String searchFilter) {
        this.searchFilter = "(&(&(objectClass=ipHost)(objectClass=ipService))(" + searchFilter + "))";
    }
    
    public void setSearchEventListener(final boolean searchEventListener) {
        this.searchEventListener = searchEventListener;
    }
    
    @Override
    public void start() throws Exception {
        LdapNetworkConnector.LOG.info("connecting...");
        final Hashtable<String, String> env = new Hashtable<String, String>();
        env.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
        this.ldapURI = this.getUri();
        LdapNetworkConnector.LOG.debug("    URI [{}]", this.ldapURI);
        env.put("java.naming.provider.url", this.ldapURI.toString());
        if (this.anonymousAuthentication) {
            LdapNetworkConnector.LOG.debug("    login credentials [anonymous]");
            env.put("java.naming.security.authentication", "none");
        }
        else {
            LdapNetworkConnector.LOG.debug("    login credentials [{}:******]", this.user);
            env.put("java.naming.security.principal", this.user);
            env.put("java.naming.security.credentials", this.password);
        }
        boolean isConnected = false;
        while (!isConnected) {
            try {
                this.context = new InitialDirContext(env);
                isConnected = true;
            }
            catch (CommunicationException err) {
                if (!this.failover) {
                    throw err;
                }
                this.ldapURI = this.getUri();
                LdapNetworkConnector.LOG.error("connection error [{}], failover connection to [{}]", env.get("java.naming.provider.url"), this.ldapURI.toString());
                env.put("java.naming.provider.url", this.ldapURI.toString());
                Thread.sleep(this.curReconnectDelay);
                this.curReconnectDelay = Math.min(this.curReconnectDelay * 2L, this.maxReconnectDelay);
            }
        }
        LdapNetworkConnector.LOG.info("searching for network connectors...");
        LdapNetworkConnector.LOG.debug("    base   [{}]", this.base);
        LdapNetworkConnector.LOG.debug("    filter [{}]", this.searchFilter);
        LdapNetworkConnector.LOG.debug("    scope  [{}]", (Object)this.searchControls.getSearchScope());
        final NamingEnumeration<SearchResult> results = this.context.search(this.base, this.searchFilter, this.searchControls);
        while (results.hasMore()) {
            this.addConnector(results.next());
        }
        if (this.searchEventListener) {
            LdapNetworkConnector.LOG.info("registering persistent search listener...");
            final EventDirContext eventContext = (EventDirContext)this.context.lookup("");
            eventContext.addNamingListener(this.base, this.searchFilter, this.searchControls, this);
        }
        else {
            this.context.close();
        }
    }
    
    @Override
    public void stop() throws Exception {
        LdapNetworkConnector.LOG.info("stopping context...");
        for (final NetworkConnector connector : this.connectorMap.values()) {
            connector.stop();
        }
        this.connectorMap.clear();
        this.referenceMap.clear();
        this.uuidMap.clear();
        this.context.close();
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + this.getName() + "[" + this.ldapURI.toString() + "]";
    }
    
    protected synchronized void addConnector(final SearchResult result) throws Exception {
        final String uuid = this.toUUID(result);
        if (this.uuidMap.containsKey(uuid)) {
            LdapNetworkConnector.LOG.warn("connector already regsitered for UUID [{}]", uuid);
            return;
        }
        final URI connectorURI = this.toURI(result);
        if (this.connectorMap.containsKey(connectorURI)) {
            final int referenceCount = this.referenceMap.get(connectorURI) + 1;
            LdapNetworkConnector.LOG.warn("connector reference added for URI [{}], UUID [{}], total reference(s) [{}]", connectorURI, uuid, referenceCount);
            this.referenceMap.put(connectorURI, referenceCount);
            this.uuidMap.put(uuid, connectorURI);
            return;
        }
        final NetworkConnector connector = this.getBrokerService().addNetworkConnector(connectorURI);
        connector.setDynamicOnly(this.isDynamicOnly());
        connector.setDecreaseNetworkConsumerPriority(this.isDecreaseNetworkConsumerPriority());
        connector.setNetworkTTL(this.getNetworkTTL());
        connector.setConsumerTTL(this.getConsumerTTL());
        connector.setMessageTTL(this.getMessageTTL());
        connector.setConduitSubscriptions(this.isConduitSubscriptions());
        connector.setExcludedDestinations(this.getExcludedDestinations());
        connector.setDynamicallyIncludedDestinations(this.getDynamicallyIncludedDestinations());
        connector.setDuplex(this.isDuplex());
        connector.setLocalUri(this.getBrokerService().getVmConnectorURI());
        connector.setBrokerName(this.getBrokerService().getBrokerName());
        connector.setDurableDestinations(this.getBrokerService().getBroker().getDurableDestinations());
        this.connectorMap.put(connectorURI, connector);
        this.referenceMap.put(connectorURI, 1);
        this.uuidMap.put(uuid, connectorURI);
        connector.start();
        LdapNetworkConnector.LOG.info("connector added with URI [{}]", connectorURI);
    }
    
    protected synchronized void removeConnector(final SearchResult result) throws Exception {
        final String uuid = this.toUUID(result);
        if (!this.uuidMap.containsKey(uuid)) {
            LdapNetworkConnector.LOG.warn("connector not registered for UUID [{}]", uuid);
            return;
        }
        final URI connectorURI = this.uuidMap.get(uuid);
        if (!this.connectorMap.containsKey(connectorURI)) {
            LdapNetworkConnector.LOG.warn("connector not registered for URI [{}]", connectorURI);
            return;
        }
        final int referenceCount = this.referenceMap.get(connectorURI) - 1;
        this.referenceMap.put(connectorURI, referenceCount);
        this.uuidMap.remove(uuid);
        LdapNetworkConnector.LOG.debug("connector referenced removed for URI [{}], UUID[{}], remaining reference(s) [{}]", connectorURI, uuid, referenceCount);
        if (referenceCount > 0) {
            return;
        }
        final NetworkConnector connector = this.connectorMap.remove(connectorURI);
        connector.stop();
        LdapNetworkConnector.LOG.info("connector removed with URI [{}]", connectorURI);
    }
    
    protected URI toURI(final SearchResult result) throws Exception {
        final Attributes attributes = result.getAttributes();
        final String address = (String)attributes.get("iphostnumber").get();
        final String port = (String)attributes.get("ipserviceport").get();
        final String protocol = (String)attributes.get("ipserviceprotocol").get();
        final URI connectorURI = new URI("static:(" + protocol + "://" + address + ":" + port + ")");
        LdapNetworkConnector.LOG.debug("retrieved URI from SearchResult [{}]", connectorURI);
        return connectorURI;
    }
    
    protected String toUUID(final SearchResult result) {
        final String uuid = result.getNameInNamespace();
        LdapNetworkConnector.LOG.debug("retrieved UUID from SearchResult [{}]", uuid);
        return uuid;
    }
    
    @Override
    public void objectAdded(final NamingEvent event) {
        LdapNetworkConnector.LOG.debug("entry added");
        try {
            this.addConnector((SearchResult)event.getNewBinding());
        }
        catch (Exception err) {
            LdapNetworkConnector.LOG.error("ERR: caught unexpected exception", err);
        }
    }
    
    @Override
    public void objectRemoved(final NamingEvent event) {
        LdapNetworkConnector.LOG.debug("entry removed");
        try {
            this.removeConnector((SearchResult)event.getOldBinding());
        }
        catch (Exception err) {
            LdapNetworkConnector.LOG.error("ERR: caught unexpected exception", err);
        }
    }
    
    @Override
    public void objectRenamed(final NamingEvent event) {
        LdapNetworkConnector.LOG.debug("entry renamed");
        final String uuidOld = event.getOldBinding().getName();
        final String uuidNew = event.getNewBinding().getName();
        final URI connectorURI = this.uuidMap.remove(uuidOld);
        this.uuidMap.put(uuidNew, connectorURI);
        LdapNetworkConnector.LOG.debug("connector reference renamed for URI [{}], Old UUID [{}], New UUID [{}]", connectorURI, uuidOld, uuidNew);
    }
    
    @Override
    public void objectChanged(final NamingEvent event) {
        LdapNetworkConnector.LOG.debug("entry changed");
        try {
            final SearchResult result = (SearchResult)event.getNewBinding();
            this.removeConnector(result);
            this.addConnector(result);
        }
        catch (Exception err) {
            LdapNetworkConnector.LOG.error("ERR: caught unexpected exception", err);
        }
    }
    
    @Override
    public void namingExceptionThrown(final NamingExceptionEvent event) {
        LdapNetworkConnector.LOG.error("ERR: caught unexpected exception", event.getException());
    }
    
    static {
        LOG = LoggerFactory.getLogger(LdapNetworkConnector.class);
    }
}
