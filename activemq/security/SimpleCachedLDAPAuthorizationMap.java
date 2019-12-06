// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.security;

import javax.naming.event.ObjectChangeListener;
import javax.naming.event.NamespaceChangeListener;
import org.slf4j.LoggerFactory;
import javax.naming.event.NamingExceptionEvent;
import javax.naming.Name;
import javax.naming.Binding;
import javax.naming.InvalidNameException;
import javax.naming.event.NamingEvent;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.command.ActiveMQQueue;
import javax.naming.ldap.Rdn;
import javax.naming.directory.Attributes;
import javax.naming.directory.Attribute;
import java.util.Set;
import org.apache.activemq.jaas.UserPrincipal;
import java.util.HashSet;
import javax.naming.ldap.LdapName;
import javax.naming.directory.SearchResult;
import javax.naming.NamingEnumeration;
import java.util.List;
import java.util.Collection;
import org.apache.activemq.filter.DestinationMapEntry;
import java.util.ArrayList;
import javax.naming.event.NamingListener;
import javax.naming.directory.SearchControls;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;
import javax.naming.event.EventDirContext;
import javax.naming.directory.DirContext;
import org.slf4j.Logger;

public class SimpleCachedLDAPAuthorizationMap implements AuthorizationMap
{
    private static final Logger LOG;
    private final String initialContextFactory = "com.sun.jndi.ldap.LdapCtxFactory";
    private String connectionURL;
    private String connectionUsername;
    private String connectionPassword;
    private String connectionProtocol;
    private String authentication;
    private int queuePrefixLength;
    private int topicPrefixLength;
    private int tempPrefixLength;
    private String queueSearchBase;
    private String topicSearchBase;
    private String tempSearchBase;
    private String permissionGroupMemberAttribute;
    private String adminPermissionGroupSearchFilter;
    private String readPermissionGroupSearchFilter;
    private String writePermissionGroupSearchFilter;
    private boolean legacyGroupMapping;
    private String groupObjectClass;
    private String userObjectClass;
    private String groupNameAttribute;
    private String userNameAttribute;
    private int refreshInterval;
    private boolean refreshDisabled;
    protected String groupClass;
    private long lastUpdated;
    private static String ANY_DESCENDANT;
    protected DirContext context;
    private EventDirContext eventContext;
    private final AtomicReference<DefaultAuthorizationMap> map;
    private final ThreadPoolExecutor updaterService;
    protected Map<ActiveMQDestination, AuthorizationEntry> entries;
    
    public SimpleCachedLDAPAuthorizationMap() {
        this.connectionURL = "ldap://localhost:1024";
        this.connectionUsername = "uid=admin,ou=system";
        this.connectionPassword = "secret";
        this.connectionProtocol = "s";
        this.authentication = "simple";
        this.queuePrefixLength = 4;
        this.topicPrefixLength = 4;
        this.tempPrefixLength = 4;
        this.queueSearchBase = "ou=Queue,ou=Destination,ou=ActiveMQ,ou=system";
        this.topicSearchBase = "ou=Topic,ou=Destination,ou=ActiveMQ,ou=system";
        this.tempSearchBase = "ou=Temp,ou=Destination,ou=ActiveMQ,ou=system";
        this.permissionGroupMemberAttribute = "member";
        this.adminPermissionGroupSearchFilter = "(cn=Admin)";
        this.readPermissionGroupSearchFilter = "(cn=Read)";
        this.writePermissionGroupSearchFilter = "(cn=Write)";
        this.legacyGroupMapping = true;
        this.groupObjectClass = "groupOfNames";
        this.userObjectClass = "person";
        this.groupNameAttribute = "cn";
        this.userNameAttribute = "uid";
        this.refreshInterval = -1;
        this.refreshDisabled = false;
        this.groupClass = "org.apache.activemq.jaas.GroupPrincipal";
        this.map = new AtomicReference<DefaultAuthorizationMap>(new DefaultAuthorizationMap());
        this.entries = new ConcurrentHashMap<ActiveMQDestination, AuthorizationEntry>();
        (this.updaterService = new ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(2), new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable r) {
                return new Thread(r, "SimpleCachedLDAPAuthorizationMap update thread");
            }
        })).setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
    }
    
    protected DirContext createContext() throws NamingException {
        final Hashtable<String, String> env = new Hashtable<String, String>();
        env.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
        if (this.connectionUsername != null || !"".equals(this.connectionUsername)) {
            env.put("java.naming.security.principal", this.connectionUsername);
        }
        if (this.connectionPassword != null || !"".equals(this.connectionPassword)) {
            env.put("java.naming.security.credentials", this.connectionPassword);
        }
        env.put("java.naming.security.protocol", this.connectionProtocol);
        env.put("java.naming.provider.url", this.connectionURL);
        env.put("java.naming.security.authentication", this.authentication);
        return new InitialDirContext(env);
    }
    
    protected boolean isContextAlive() {
        boolean alive = false;
        if (this.context != null) {
            try {
                this.context.getAttributes("");
                alive = true;
            }
            catch (Exception ex) {}
        }
        return alive;
    }
    
    protected DirContext open() throws NamingException {
        if (this.isContextAlive()) {
            return this.context;
        }
        try {
            this.context = this.createContext();
            if (this.refreshInterval == -1 && !this.refreshDisabled) {
                this.eventContext = (EventDirContext)this.context.lookup("");
                final SearchControls constraints = new SearchControls();
                constraints.setSearchScope(2);
                for (final PermissionType permissionType : PermissionType.values()) {
                    this.eventContext.addNamingListener(this.queueSearchBase, this.getFilterForPermissionType(permissionType), constraints, new CachedLDAPAuthorizationMapNamespaceChangeListener(DestinationType.QUEUE, permissionType));
                }
                this.eventContext.addNamingListener(this.queueSearchBase, "cn=*", new SearchControls(), new CachedLDAPAuthorizationMapNamespaceChangeListener(DestinationType.QUEUE, null));
                for (final PermissionType permissionType : PermissionType.values()) {
                    this.eventContext.addNamingListener(this.topicSearchBase, this.getFilterForPermissionType(permissionType), constraints, new CachedLDAPAuthorizationMapNamespaceChangeListener(DestinationType.TOPIC, permissionType));
                }
                this.eventContext.addNamingListener(this.topicSearchBase, "cn=*", new SearchControls(), new CachedLDAPAuthorizationMapNamespaceChangeListener(DestinationType.TOPIC, null));
                for (final PermissionType permissionType : PermissionType.values()) {
                    this.eventContext.addNamingListener(this.tempSearchBase, this.getFilterForPermissionType(permissionType), constraints, new CachedLDAPAuthorizationMapNamespaceChangeListener(DestinationType.TEMP, permissionType));
                }
            }
        }
        catch (NamingException e) {
            this.context = null;
            throw e;
        }
        return this.context;
    }
    
    protected void query() throws Exception {
        final DirContext currentContext = this.open();
        final SearchControls constraints = new SearchControls();
        constraints.setSearchScope(2);
        final DefaultAuthorizationMap newMap = new DefaultAuthorizationMap();
        for (final PermissionType permissionType : PermissionType.values()) {
            try {
                this.processQueryResults(newMap, currentContext.search(this.queueSearchBase, this.getFilterForPermissionType(permissionType), constraints), DestinationType.QUEUE, permissionType);
            }
            catch (Exception e) {
                SimpleCachedLDAPAuthorizationMap.LOG.error("Policy not applied!.  Error processing policy under '{}' with filter '{}'", (Object)new Object[] { this.queueSearchBase, this.getFilterForPermissionType(permissionType) }, e);
            }
        }
        for (final PermissionType permissionType : PermissionType.values()) {
            try {
                this.processQueryResults(newMap, currentContext.search(this.topicSearchBase, this.getFilterForPermissionType(permissionType), constraints), DestinationType.TOPIC, permissionType);
            }
            catch (Exception e) {
                SimpleCachedLDAPAuthorizationMap.LOG.error("Policy not applied!.  Error processing policy under '{}' with filter '{}'", (Object)new Object[] { this.topicSearchBase, this.getFilterForPermissionType(permissionType) }, e);
            }
        }
        for (final PermissionType permissionType : PermissionType.values()) {
            try {
                this.processQueryResults(newMap, currentContext.search(this.tempSearchBase, this.getFilterForPermissionType(permissionType), constraints), DestinationType.TEMP, permissionType);
            }
            catch (Exception e) {
                SimpleCachedLDAPAuthorizationMap.LOG.error("Policy not applied!.  Error processing policy under '{}' with filter '{}'", (Object)new Object[] { this.tempSearchBase, this.getFilterForPermissionType(permissionType) }, e);
            }
        }
        newMap.setAuthorizationEntries(new ArrayList<DestinationMapEntry>(this.entries.values()));
        newMap.setGroupClass(this.groupClass);
        this.map.set(newMap);
        this.updated();
    }
    
    protected void processQueryResults(final DefaultAuthorizationMap map, final NamingEnumeration<SearchResult> results, final DestinationType destinationType, final PermissionType permissionType) throws Exception {
        while (results.hasMore()) {
            final SearchResult result = results.next();
            AuthorizationEntry entry = null;
            try {
                entry = this.getEntry(map, new LdapName(result.getNameInNamespace()), destinationType);
            }
            catch (Exception e) {
                SimpleCachedLDAPAuthorizationMap.LOG.error("Policy not applied!  Error parsing authorization policy entry under {}", result.getNameInNamespace(), e);
                continue;
            }
            this.applyACL(entry, result, permissionType);
        }
    }
    
    protected void updated() {
        this.lastUpdated = System.currentTimeMillis();
    }
    
    protected AuthorizationEntry getEntry(final DefaultAuthorizationMap map, final LdapName dn, final DestinationType destinationType) {
        AuthorizationEntry entry = null;
        switch (destinationType) {
            case TEMP: {
                if (dn.size() != this.getPrefixLengthForDestinationType(destinationType) + 1) {
                    throw new IllegalArgumentException("Malformed policy structure for a temporary destination policy entry.  The permission group entries should be immediately below the temporary policy base DN.");
                }
                entry = map.getTempDestinationAuthorizationEntry();
                if (entry == null) {
                    entry = new TempDestinationAuthorizationEntry();
                    map.setTempDestinationAuthorizationEntry((TempDestinationAuthorizationEntry)entry);
                    break;
                }
                break;
            }
            case QUEUE:
            case TOPIC: {
                if (dn.size() != this.getPrefixLengthForDestinationType(destinationType) + 2) {
                    throw new IllegalArgumentException("Malformed policy structure for a queue or topic destination policy entry.  The destination pattern and permission group entries should be nested below the queue or topic policy base DN.");
                }
                final ActiveMQDestination dest = this.formatDestination(dn, destinationType);
                if (dest == null) {
                    break;
                }
                entry = this.entries.get(dest);
                if (entry == null) {
                    entry = new AuthorizationEntry();
                    entry.setDestination(dest);
                    this.entries.put(dest, entry);
                    break;
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown destination type " + destinationType);
            }
        }
        return entry;
    }
    
    protected void applyACL(final AuthorizationEntry entry, final SearchResult result, final PermissionType permissionType) throws NamingException {
        final Attribute memberAttribute = result.getAttributes().get(this.permissionGroupMemberAttribute);
        final NamingEnumeration<?> memberAttributeEnum = memberAttribute.getAll();
        final HashSet<Object> members = new HashSet<Object>();
        while (memberAttributeEnum.hasMoreElements()) {
            final String memberDn = memberAttributeEnum.nextElement();
            boolean group = false;
            boolean user = false;
            String principalName = null;
            if (!this.legacyGroupMapping) {
                Attributes memberAttributes;
                try {
                    memberAttributes = this.context.getAttributes(memberDn, new String[] { "objectClass", this.groupNameAttribute, this.userNameAttribute });
                }
                catch (NamingException e) {
                    SimpleCachedLDAPAuthorizationMap.LOG.error("Policy not applied! Unknown member {} in policy entry {}", (Object)new Object[] { memberDn, result.getNameInNamespace() }, e);
                    continue;
                }
                final Attribute memberEntryObjectClassAttribute = memberAttributes.get("objectClass");
                final NamingEnumeration<?> memberEntryObjectClassAttributeEnum = memberEntryObjectClassAttribute.getAll();
                while (memberEntryObjectClassAttributeEnum.hasMoreElements()) {
                    final String objectClass = memberEntryObjectClassAttributeEnum.nextElement();
                    if (objectClass.equalsIgnoreCase(this.groupObjectClass)) {
                        group = true;
                        final Attribute name = memberAttributes.get(this.groupNameAttribute);
                        if (name == null) {
                            SimpleCachedLDAPAuthorizationMap.LOG.error("Policy not applied! Group {} does not have name attribute {} under entry {}", memberDn, this.groupNameAttribute, result.getNameInNamespace());
                            break;
                        }
                        principalName = (String)name.get();
                    }
                    if (objectClass.equalsIgnoreCase(this.userObjectClass)) {
                        user = true;
                        final Attribute name = memberAttributes.get(this.userNameAttribute);
                        if (name == null) {
                            SimpleCachedLDAPAuthorizationMap.LOG.error("Policy not applied! User {} does not have name attribute {} under entry {}", memberDn, this.userNameAttribute, result.getNameInNamespace());
                            break;
                        }
                        principalName = (String)name.get();
                    }
                }
            }
            else {
                group = true;
                principalName = memberDn.replaceAll("(cn|CN)=", "");
            }
            if ((!group && !user) || (group && user)) {
                SimpleCachedLDAPAuthorizationMap.LOG.error("Policy not applied! Can't determine type of member {} under entry {}", memberDn, result.getNameInNamespace());
            }
            else {
                if (principalName == null) {
                    continue;
                }
                final DefaultAuthorizationMap map = this.map.get();
                if (group && !user) {
                    try {
                        members.add(DefaultAuthorizationMap.createGroupPrincipal(principalName, map.getGroupClass()));
                        continue;
                    }
                    catch (Exception e2) {
                        final NamingException ne = new NamingException("Can't create a group " + principalName + " of class " + map.getGroupClass());
                        ne.initCause(e2);
                        throw ne;
                    }
                }
                if (group || !user) {
                    continue;
                }
                members.add(new UserPrincipal(principalName));
            }
        }
        try {
            this.applyAcl(entry, permissionType, members);
        }
        catch (Exception e3) {
            SimpleCachedLDAPAuthorizationMap.LOG.error("Policy not applied! Error adding principals to ACL under {}", result.getNameInNamespace(), e3);
        }
    }
    
    protected void applyAcl(final AuthorizationEntry entry, final PermissionType permissionType, final Set<Object> acls) {
        switch (permissionType) {
            case READ: {
                entry.setReadACLs(acls);
                break;
            }
            case WRITE: {
                entry.setWriteACLs(acls);
                break;
            }
            case ADMIN: {
                entry.setAdminACLs(acls);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown permission " + permissionType + ".");
            }
        }
    }
    
    protected ActiveMQDestination formatDestination(final LdapName dn, final DestinationType destinationType) {
        ActiveMQDestination destination = null;
        switch (destinationType) {
            case QUEUE:
            case TOPIC: {
                if (dn.size() == this.getPrefixLengthForDestinationType(destinationType) + 2) {
                    destination = this.formatDestination(dn.getRdn(dn.size() - 2), destinationType);
                }
                else {
                    if (dn.size() != this.getPrefixLengthForDestinationType(destinationType) + 1) {
                        throw new IllegalArgumentException("Malformed DN for representing a permission or destination entry.");
                    }
                    destination = this.formatDestination(dn.getRdn(dn.size() - 1), destinationType);
                }
                return destination;
            }
            default: {
                throw new IllegalArgumentException("Cannot format destination for destination type " + destinationType);
            }
        }
    }
    
    protected ActiveMQDestination formatDestination(final Rdn destinationName, final DestinationType destinationType) {
        ActiveMQDestination dest = null;
        switch (destinationType) {
            case QUEUE: {
                dest = new ActiveMQQueue(this.formatDestinationName(destinationName));
                break;
            }
            case TOPIC: {
                dest = new ActiveMQTopic(this.formatDestinationName(destinationName));
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown destination type: " + destinationType);
            }
        }
        return dest;
    }
    
    protected String formatDestinationName(final Rdn destinationName) {
        return destinationName.getValue().toString().replaceAll(SimpleCachedLDAPAuthorizationMap.ANY_DESCENDANT, ">");
    }
    
    protected <T> Set<T> transcribeSet(final Set<T> source) {
        if (source != null) {
            return new HashSet<T>((Collection<? extends T>)source);
        }
        return null;
    }
    
    protected String getFilterForPermissionType(final PermissionType permissionType) {
        String filter = null;
        switch (permissionType) {
            case ADMIN: {
                filter = this.adminPermissionGroupSearchFilter;
                break;
            }
            case READ: {
                filter = this.readPermissionGroupSearchFilter;
                break;
            }
            case WRITE: {
                filter = this.writePermissionGroupSearchFilter;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown permission type " + permissionType);
            }
        }
        return filter;
    }
    
    protected int getPrefixLengthForDestinationType(final DestinationType destinationType) {
        int filter = 0;
        switch (destinationType) {
            case QUEUE: {
                filter = this.queuePrefixLength;
                break;
            }
            case TOPIC: {
                filter = this.topicPrefixLength;
                break;
            }
            case TEMP: {
                filter = this.tempPrefixLength;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown permission type " + destinationType);
            }
        }
        return filter;
    }
    
    protected void checkForUpdates() {
        if (this.context != null && this.refreshDisabled) {
            return;
        }
        if (this.context == null || (!this.refreshDisabled && this.refreshInterval != -1 && System.currentTimeMillis() >= this.lastUpdated + this.refreshInterval)) {
            this.updaterService.execute(new Runnable() {
                @Override
                public void run() {
                    if (SimpleCachedLDAPAuthorizationMap.this.context == null || (!SimpleCachedLDAPAuthorizationMap.this.refreshDisabled && SimpleCachedLDAPAuthorizationMap.this.refreshInterval != -1 && System.currentTimeMillis() >= SimpleCachedLDAPAuthorizationMap.this.lastUpdated + SimpleCachedLDAPAuthorizationMap.this.refreshInterval)) {
                        if (!SimpleCachedLDAPAuthorizationMap.this.isContextAlive()) {
                            try {
                                SimpleCachedLDAPAuthorizationMap.this.context = SimpleCachedLDAPAuthorizationMap.this.createContext();
                            }
                            catch (NamingException ne) {
                                return;
                            }
                        }
                        SimpleCachedLDAPAuthorizationMap.this.entries.clear();
                        SimpleCachedLDAPAuthorizationMap.LOG.debug("Updating authorization map!");
                        try {
                            SimpleCachedLDAPAuthorizationMap.this.query();
                        }
                        catch (Exception e) {
                            SimpleCachedLDAPAuthorizationMap.LOG.error("Error updating authorization map.  Partial policy may be applied until the next successful update.", e);
                        }
                    }
                }
            });
        }
    }
    
    @Override
    public Set<Object> getTempDestinationAdminACLs() {
        this.checkForUpdates();
        final DefaultAuthorizationMap map = this.map.get();
        return this.transcribeSet(map.getTempDestinationAdminACLs());
    }
    
    @Override
    public Set<Object> getTempDestinationReadACLs() {
        this.checkForUpdates();
        final DefaultAuthorizationMap map = this.map.get();
        return this.transcribeSet(map.getTempDestinationReadACLs());
    }
    
    @Override
    public Set<Object> getTempDestinationWriteACLs() {
        this.checkForUpdates();
        final DefaultAuthorizationMap map = this.map.get();
        return this.transcribeSet(map.getTempDestinationWriteACLs());
    }
    
    @Override
    public Set<Object> getAdminACLs(final ActiveMQDestination destination) {
        this.checkForUpdates();
        final DefaultAuthorizationMap map = this.map.get();
        return map.getAdminACLs(destination);
    }
    
    @Override
    public Set<Object> getReadACLs(final ActiveMQDestination destination) {
        this.checkForUpdates();
        final DefaultAuthorizationMap map = this.map.get();
        return map.getReadACLs(destination);
    }
    
    @Override
    public Set<Object> getWriteACLs(final ActiveMQDestination destination) {
        this.checkForUpdates();
        final DefaultAuthorizationMap map = this.map.get();
        return map.getWriteACLs(destination);
    }
    
    public void objectAdded(final NamingEvent namingEvent, final DestinationType destinationType, final PermissionType permissionType) {
        SimpleCachedLDAPAuthorizationMap.LOG.debug("Adding object: {}", namingEvent.getNewBinding());
        final SearchResult result = (SearchResult)namingEvent.getNewBinding();
        try {
            final DefaultAuthorizationMap map = this.map.get();
            final LdapName name = new LdapName(result.getName());
            final AuthorizationEntry entry = this.getEntry(map, name, destinationType);
            this.applyACL(entry, result, permissionType);
            if (!(entry instanceof TempDestinationAuthorizationEntry)) {
                map.put(entry.getDestination(), entry);
            }
        }
        catch (InvalidNameException e) {
            SimpleCachedLDAPAuthorizationMap.LOG.error("Policy not applied!  Error parsing DN for addition of {}", result.getName(), e);
        }
        catch (Exception e2) {
            SimpleCachedLDAPAuthorizationMap.LOG.error("Policy not applied!  Error processing object addition for addition of {}", result.getName(), e2);
        }
    }
    
    public void objectRemoved(final NamingEvent namingEvent, final DestinationType destinationType, final PermissionType permissionType) {
        SimpleCachedLDAPAuthorizationMap.LOG.debug("Removing object: {}", namingEvent.getOldBinding());
        final Binding result = namingEvent.getOldBinding();
        try {
            final DefaultAuthorizationMap map = this.map.get();
            final LdapName name = new LdapName(result.getName());
            final AuthorizationEntry entry = this.getEntry(map, name, destinationType);
            this.applyAcl(entry, permissionType, new HashSet<Object>());
        }
        catch (InvalidNameException e) {
            SimpleCachedLDAPAuthorizationMap.LOG.error("Policy not applied!  Error parsing DN for object removal for removal of {}", result.getName(), e);
        }
        catch (Exception e2) {
            SimpleCachedLDAPAuthorizationMap.LOG.error("Policy not applied!  Error processing object removal for removal of {}", result.getName(), e2);
        }
    }
    
    public void objectRenamed(final NamingEvent namingEvent, final DestinationType destinationType, final PermissionType permissionType) {
        final Binding oldBinding = namingEvent.getOldBinding();
        final Binding newBinding = namingEvent.getNewBinding();
        SimpleCachedLDAPAuthorizationMap.LOG.debug("Renaming object: {} to {}", oldBinding, newBinding);
        try {
            final LdapName oldName = new LdapName(oldBinding.getName());
            final ActiveMQDestination oldDest = this.formatDestination(oldName, destinationType);
            final LdapName newName = new LdapName(newBinding.getName());
            final ActiveMQDestination newDest = this.formatDestination(newName, destinationType);
            if (permissionType != null) {
                this.objectRemoved(namingEvent, destinationType, permissionType);
                final SearchControls controls = new SearchControls();
                controls.setSearchScope(0);
                boolean matchedToType = false;
                for (final PermissionType newPermissionType : PermissionType.values()) {
                    final NamingEnumeration<SearchResult> results = this.context.search(newName, this.getFilterForPermissionType(newPermissionType), controls);
                    if (results.hasMore()) {
                        this.objectAdded(namingEvent, destinationType, newPermissionType);
                        matchedToType = true;
                        break;
                    }
                }
                if (!matchedToType) {
                    SimpleCachedLDAPAuthorizationMap.LOG.error("Policy not applied!  Error processing object rename for rename of {} to {}. Could not determine permission type of new object.", oldBinding.getName(), newBinding.getName());
                }
            }
            else if (oldDest != null && newDest != null) {
                final AuthorizationEntry entry = this.entries.remove(oldDest);
                if (entry != null) {
                    entry.setDestination(newDest);
                    final DefaultAuthorizationMap map = this.map.get();
                    map.put(newDest, entry);
                    map.remove(oldDest, entry);
                    this.entries.put(newDest, entry);
                }
                else {
                    SimpleCachedLDAPAuthorizationMap.LOG.warn("No authorization entry for {}", oldDest);
                }
            }
        }
        catch (InvalidNameException e) {
            SimpleCachedLDAPAuthorizationMap.LOG.error("Policy not applied!  Error parsing DN for object rename for rename of {} to {}", (Object)new Object[] { oldBinding.getName(), newBinding.getName() }, e);
        }
        catch (Exception e2) {
            SimpleCachedLDAPAuthorizationMap.LOG.error("Policy not applied!  Error processing object rename for rename of {} to {}", (Object)new Object[] { oldBinding.getName(), newBinding.getName() }, e2);
        }
    }
    
    public void objectChanged(final NamingEvent namingEvent, final DestinationType destinationType, final PermissionType permissionType) {
        SimpleCachedLDAPAuthorizationMap.LOG.debug("Changing object {} to {}", namingEvent.getOldBinding(), namingEvent.getNewBinding());
        this.objectRemoved(namingEvent, destinationType, permissionType);
        this.objectAdded(namingEvent, destinationType, permissionType);
    }
    
    public void namingExceptionThrown(final NamingExceptionEvent namingExceptionEvent) {
        this.context = null;
        SimpleCachedLDAPAuthorizationMap.LOG.error("Caught unexpected exception.", namingExceptionEvent.getException());
    }
    
    public void afterPropertiesSet() throws Exception {
        this.query();
    }
    
    public void destroy() throws Exception {
        if (this.eventContext != null) {
            this.eventContext.close();
            this.eventContext = null;
        }
        if (this.context != null) {
            this.context.close();
            this.context = null;
        }
    }
    
    public String getConnectionURL() {
        return this.connectionURL;
    }
    
    public void setConnectionURL(final String connectionURL) {
        this.connectionURL = connectionURL;
    }
    
    public String getConnectionUsername() {
        return this.connectionUsername;
    }
    
    public void setConnectionUsername(final String connectionUsername) {
        this.connectionUsername = connectionUsername;
    }
    
    public String getConnectionPassword() {
        return this.connectionPassword;
    }
    
    public void setConnectionPassword(final String connectionPassword) {
        this.connectionPassword = connectionPassword;
    }
    
    public String getConnectionProtocol() {
        return this.connectionProtocol;
    }
    
    public void setConnectionProtocol(final String connectionProtocol) {
        this.connectionProtocol = connectionProtocol;
    }
    
    public String getAuthentication() {
        return this.authentication;
    }
    
    public void setAuthentication(final String authentication) {
        this.authentication = authentication;
    }
    
    public String getQueueSearchBase() {
        return this.queueSearchBase;
    }
    
    public void setQueueSearchBase(final String queueSearchBase) {
        try {
            final LdapName baseName = new LdapName(queueSearchBase);
            this.queuePrefixLength = baseName.size();
            this.queueSearchBase = queueSearchBase;
        }
        catch (InvalidNameException e) {
            throw new IllegalArgumentException("Invalid base DN value " + queueSearchBase, e);
        }
    }
    
    public String getTopicSearchBase() {
        return this.topicSearchBase;
    }
    
    public void setTopicSearchBase(final String topicSearchBase) {
        try {
            final LdapName baseName = new LdapName(topicSearchBase);
            this.topicPrefixLength = baseName.size();
            this.topicSearchBase = topicSearchBase;
        }
        catch (InvalidNameException e) {
            throw new IllegalArgumentException("Invalid base DN value " + topicSearchBase, e);
        }
    }
    
    public String getTempSearchBase() {
        return this.tempSearchBase;
    }
    
    public void setTempSearchBase(final String tempSearchBase) {
        try {
            final LdapName baseName = new LdapName(tempSearchBase);
            this.tempPrefixLength = baseName.size();
            this.tempSearchBase = tempSearchBase;
        }
        catch (InvalidNameException e) {
            throw new IllegalArgumentException("Invalid base DN value " + tempSearchBase, e);
        }
    }
    
    public String getPermissionGroupMemberAttribute() {
        return this.permissionGroupMemberAttribute;
    }
    
    public void setPermissionGroupMemberAttribute(final String permissionGroupMemberAttribute) {
        this.permissionGroupMemberAttribute = permissionGroupMemberAttribute;
    }
    
    public String getAdminPermissionGroupSearchFilter() {
        return this.adminPermissionGroupSearchFilter;
    }
    
    public void setAdminPermissionGroupSearchFilter(final String adminPermissionGroupSearchFilter) {
        this.adminPermissionGroupSearchFilter = adminPermissionGroupSearchFilter;
    }
    
    public String getReadPermissionGroupSearchFilter() {
        return this.readPermissionGroupSearchFilter;
    }
    
    public void setReadPermissionGroupSearchFilter(final String readPermissionGroupSearchFilter) {
        this.readPermissionGroupSearchFilter = readPermissionGroupSearchFilter;
    }
    
    public String getWritePermissionGroupSearchFilter() {
        return this.writePermissionGroupSearchFilter;
    }
    
    public void setWritePermissionGroupSearchFilter(final String writePermissionGroupSearchFilter) {
        this.writePermissionGroupSearchFilter = writePermissionGroupSearchFilter;
    }
    
    public boolean isLegacyGroupMapping() {
        return this.legacyGroupMapping;
    }
    
    public void setLegacyGroupMapping(final boolean legacyGroupMapping) {
        this.legacyGroupMapping = legacyGroupMapping;
    }
    
    public String getGroupObjectClass() {
        return this.groupObjectClass;
    }
    
    public void setGroupObjectClass(final String groupObjectClass) {
        this.groupObjectClass = groupObjectClass;
    }
    
    public String getUserObjectClass() {
        return this.userObjectClass;
    }
    
    public void setUserObjectClass(final String userObjectClass) {
        this.userObjectClass = userObjectClass;
    }
    
    public String getGroupNameAttribute() {
        return this.groupNameAttribute;
    }
    
    public void setGroupNameAttribute(final String groupNameAttribute) {
        this.groupNameAttribute = groupNameAttribute;
    }
    
    public String getUserNameAttribute() {
        return this.userNameAttribute;
    }
    
    public void setUserNameAttribute(final String userNameAttribute) {
        this.userNameAttribute = userNameAttribute;
    }
    
    public boolean isRefreshDisabled() {
        return this.refreshDisabled;
    }
    
    public void setRefreshDisabled(final boolean refreshDisabled) {
        this.refreshDisabled = refreshDisabled;
    }
    
    public int getRefreshInterval() {
        return this.refreshInterval;
    }
    
    public void setRefreshInterval(final int refreshInterval) {
        this.refreshInterval = refreshInterval;
    }
    
    public String getGroupClass() {
        return this.groupClass;
    }
    
    public void setGroupClass(final String groupClass) {
        this.groupClass = groupClass;
        this.map.get().setGroupClass(groupClass);
    }
    
    static {
        LOG = LoggerFactory.getLogger(SimpleCachedLDAPAuthorizationMap.class);
        SimpleCachedLDAPAuthorizationMap.ANY_DESCENDANT = "\\$";
    }
    
    protected enum DestinationType
    {
        QUEUE, 
        TOPIC, 
        TEMP;
    }
    
    protected enum PermissionType
    {
        READ, 
        WRITE, 
        ADMIN;
    }
    
    protected class CachedLDAPAuthorizationMapNamespaceChangeListener implements NamespaceChangeListener, ObjectChangeListener
    {
        private final DestinationType destinationType;
        private final PermissionType permissionType;
        
        public CachedLDAPAuthorizationMapNamespaceChangeListener(final DestinationType destinationType, final PermissionType permissionType) {
            this.destinationType = destinationType;
            this.permissionType = permissionType;
        }
        
        @Override
        public void namingExceptionThrown(final NamingExceptionEvent evt) {
            SimpleCachedLDAPAuthorizationMap.this.namingExceptionThrown(evt);
        }
        
        @Override
        public void objectAdded(final NamingEvent evt) {
            if (this.permissionType != null) {
                SimpleCachedLDAPAuthorizationMap.this.objectAdded(evt, this.destinationType, this.permissionType);
            }
        }
        
        @Override
        public void objectRemoved(final NamingEvent evt) {
            if (this.permissionType != null) {
                SimpleCachedLDAPAuthorizationMap.this.objectRemoved(evt, this.destinationType, this.permissionType);
            }
        }
        
        @Override
        public void objectRenamed(final NamingEvent evt) {
            SimpleCachedLDAPAuthorizationMap.this.objectRenamed(evt, this.destinationType, this.permissionType);
        }
        
        @Override
        public void objectChanged(final NamingEvent evt) {
            if (this.permissionType != null) {
                SimpleCachedLDAPAuthorizationMap.this.objectChanged(evt, this.destinationType, this.permissionType);
            }
        }
    }
}
