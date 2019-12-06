// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.security;

import org.slf4j.LoggerFactory;
import org.apache.activemq.jaas.LDAPLoginModule;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;
import javax.naming.directory.Attribute;
import javax.naming.ldap.Rdn;
import java.util.Iterator;
import javax.naming.directory.Attributes;
import javax.naming.NamingEnumeration;
import javax.naming.ldap.LdapName;
import javax.naming.directory.SearchResult;
import org.apache.activemq.advisory.AdvisorySupport;
import org.apache.activemq.filter.DestinationMap;
import org.apache.activemq.command.ActiveMQDestination;
import javax.naming.directory.SearchControls;
import javax.naming.NamingException;
import java.util.HashSet;
import org.apache.activemq.jaas.GroupPrincipal;
import java.util.Set;
import java.util.Map;
import java.text.MessageFormat;
import javax.naming.directory.DirContext;
import org.slf4j.Logger;

public class LDAPAuthorizationMap implements AuthorizationMap
{
    public static final String INITIAL_CONTEXT_FACTORY = "initialContextFactory";
    public static final String CONNECTION_URL = "connectionURL";
    public static final String CONNECTION_USERNAME = "connectionUsername";
    public static final String CONNECTION_PASSWORD = "connectionPassword";
    public static final String CONNECTION_PROTOCOL = "connectionProtocol";
    public static final String AUTHENTICATION = "authentication";
    public static final String TOPIC_SEARCH_MATCHING = "topicSearchMatching";
    public static final String TOPIC_SEARCH_SUBTREE = "topicSearchSubtree";
    public static final String QUEUE_SEARCH_MATCHING = "queueSearchMatching";
    public static final String QUEUE_SEARCH_SUBTREE = "queueSearchSubtree";
    public static final String ADMIN_BASE = "adminBase";
    public static final String ADMIN_ATTRIBUTE = "adminAttribute";
    public static final String READ_BASE = "readBase";
    public static final String READ_ATTRIBUTE = "readAttribute";
    public static final String WRITE_BASE = "writeBAse";
    public static final String WRITE_ATTRIBUTE = "writeAttribute";
    private static final Logger LOG;
    private String initialContextFactory;
    private String connectionURL;
    private String connectionUsername;
    private String connectionPassword;
    private String connectionProtocol;
    private String authentication;
    private DirContext context;
    private MessageFormat topicSearchMatchingFormat;
    private MessageFormat queueSearchMatchingFormat;
    private String advisorySearchBase;
    private String tempSearchBase;
    private boolean topicSearchSubtreeBool;
    private boolean queueSearchSubtreeBool;
    private boolean useAdvisorySearchBase;
    private String adminBase;
    private String adminAttribute;
    private String readBase;
    private String readAttribute;
    private String writeBase;
    private String writeAttribute;
    
    public LDAPAuthorizationMap() {
        this.advisorySearchBase = "uid=ActiveMQ.Advisory,ou=topics,ou=destinations,o=ActiveMQ,dc=example,dc=com";
        this.tempSearchBase = "uid=ActiveMQ.Temp,ou=topics,ou=destinations,o=ActiveMQ,dc=example,dc=com";
        this.topicSearchSubtreeBool = true;
        this.queueSearchSubtreeBool = true;
        this.useAdvisorySearchBase = true;
        this.initialContextFactory = "com.sun.jndi.ldap.LdapCtxFactory";
        this.connectionURL = "ldap://localhost:10389";
        this.connectionUsername = "uid=admin,ou=system";
        this.connectionPassword = "secret";
        this.connectionProtocol = "s";
        this.authentication = "simple";
        this.topicSearchMatchingFormat = new MessageFormat("uid={0},ou=topics,ou=destinations,o=ActiveMQ,dc=example,dc=com");
        this.queueSearchMatchingFormat = new MessageFormat("uid={0},ou=queues,ou=destinations,o=ActiveMQ,dc=example,dc=com");
        this.adminBase = "(cn=admin)";
        this.adminAttribute = "uniqueMember";
        this.readBase = "(cn=read)";
        this.readAttribute = "uniqueMember";
        this.writeBase = "(cn=write)";
        this.writeAttribute = "uniqueMember";
    }
    
    public LDAPAuthorizationMap(final Map<String, String> options) {
        this.advisorySearchBase = "uid=ActiveMQ.Advisory,ou=topics,ou=destinations,o=ActiveMQ,dc=example,dc=com";
        this.tempSearchBase = "uid=ActiveMQ.Temp,ou=topics,ou=destinations,o=ActiveMQ,dc=example,dc=com";
        this.topicSearchSubtreeBool = true;
        this.queueSearchSubtreeBool = true;
        this.useAdvisorySearchBase = true;
        this.initialContextFactory = options.get("initialContextFactory");
        this.connectionURL = options.get("connectionURL");
        this.connectionUsername = options.get("connectionUsername");
        this.connectionPassword = options.get("connectionPassword");
        this.connectionProtocol = options.get("connectionProtocol");
        this.authentication = options.get("authentication");
        this.adminBase = options.get("adminBase");
        this.adminAttribute = options.get("adminAttribute");
        this.readBase = options.get("readBase");
        this.readAttribute = options.get("readAttribute");
        this.writeBase = options.get("writeBAse");
        this.writeAttribute = options.get("writeAttribute");
        final String topicSearchMatching = options.get("topicSearchMatching");
        final String topicSearchSubtree = options.get("topicSearchSubtree");
        final String queueSearchMatching = options.get("queueSearchMatching");
        final String queueSearchSubtree = options.get("queueSearchSubtree");
        this.topicSearchMatchingFormat = new MessageFormat(topicSearchMatching);
        this.queueSearchMatchingFormat = new MessageFormat(queueSearchMatching);
        this.topicSearchSubtreeBool = Boolean.valueOf(topicSearchSubtree);
        this.queueSearchSubtreeBool = Boolean.valueOf(queueSearchSubtree);
    }
    
    @Override
    public Set<GroupPrincipal> getTempDestinationAdminACLs() {
        try {
            this.context = this.open();
        }
        catch (NamingException e) {
            LDAPAuthorizationMap.LOG.error(e.toString());
            return new HashSet<GroupPrincipal>();
        }
        final SearchControls constraints = new SearchControls();
        constraints.setReturningAttributes(new String[] { this.adminAttribute });
        return this.getACLs(this.tempSearchBase, constraints, this.adminBase, this.adminAttribute);
    }
    
    @Override
    public Set<GroupPrincipal> getTempDestinationReadACLs() {
        try {
            this.context = this.open();
        }
        catch (NamingException e) {
            LDAPAuthorizationMap.LOG.error(e.toString());
            return new HashSet<GroupPrincipal>();
        }
        final SearchControls constraints = new SearchControls();
        constraints.setReturningAttributes(new String[] { this.readAttribute });
        return this.getACLs(this.tempSearchBase, constraints, this.readBase, this.readAttribute);
    }
    
    @Override
    public Set<GroupPrincipal> getTempDestinationWriteACLs() {
        try {
            this.context = this.open();
        }
        catch (NamingException e) {
            LDAPAuthorizationMap.LOG.error(e.toString());
            return new HashSet<GroupPrincipal>();
        }
        final SearchControls constraints = new SearchControls();
        constraints.setReturningAttributes(new String[] { this.writeAttribute });
        return this.getACLs(this.tempSearchBase, constraints, this.writeBase, this.writeAttribute);
    }
    
    @Override
    public Set<GroupPrincipal> getAdminACLs(final ActiveMQDestination destination) {
        if (destination.isComposite()) {
            return this.getCompositeACLs(destination, this.adminBase, this.adminAttribute);
        }
        return this.getACLs(destination, this.adminBase, this.adminAttribute);
    }
    
    @Override
    public Set<GroupPrincipal> getReadACLs(final ActiveMQDestination destination) {
        if (destination.isComposite()) {
            return this.getCompositeACLs(destination, this.readBase, this.readAttribute);
        }
        return this.getACLs(destination, this.readBase, this.readAttribute);
    }
    
    @Override
    public Set<GroupPrincipal> getWriteACLs(final ActiveMQDestination destination) {
        if (destination.isComposite()) {
            return this.getCompositeACLs(destination, this.writeBase, this.writeAttribute);
        }
        return this.getACLs(destination, this.writeBase, this.writeAttribute);
    }
    
    public String getAdminAttribute() {
        return this.adminAttribute;
    }
    
    public void setAdminAttribute(final String adminAttribute) {
        this.adminAttribute = adminAttribute;
    }
    
    public String getAdminBase() {
        return this.adminBase;
    }
    
    public void setAdminBase(final String adminBase) {
        this.adminBase = adminBase;
    }
    
    public String getAuthentication() {
        return this.authentication;
    }
    
    public void setAuthentication(final String authentication) {
        this.authentication = authentication;
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
    
    public DirContext getContext() {
        return this.context;
    }
    
    public void setContext(final DirContext context) {
        this.context = context;
    }
    
    public String getInitialContextFactory() {
        return this.initialContextFactory;
    }
    
    public void setInitialContextFactory(final String initialContextFactory) {
        this.initialContextFactory = initialContextFactory;
    }
    
    public MessageFormat getQueueSearchMatchingFormat() {
        return this.queueSearchMatchingFormat;
    }
    
    public void setQueueSearchMatchingFormat(final MessageFormat queueSearchMatchingFormat) {
        this.queueSearchMatchingFormat = queueSearchMatchingFormat;
    }
    
    public boolean isQueueSearchSubtreeBool() {
        return this.queueSearchSubtreeBool;
    }
    
    public void setQueueSearchSubtreeBool(final boolean queueSearchSubtreeBool) {
        this.queueSearchSubtreeBool = queueSearchSubtreeBool;
    }
    
    public String getReadAttribute() {
        return this.readAttribute;
    }
    
    public void setReadAttribute(final String readAttribute) {
        this.readAttribute = readAttribute;
    }
    
    public String getReadBase() {
        return this.readBase;
    }
    
    public void setReadBase(final String readBase) {
        this.readBase = readBase;
    }
    
    public MessageFormat getTopicSearchMatchingFormat() {
        return this.topicSearchMatchingFormat;
    }
    
    public void setTopicSearchMatchingFormat(final MessageFormat topicSearchMatchingFormat) {
        this.topicSearchMatchingFormat = topicSearchMatchingFormat;
    }
    
    public boolean isTopicSearchSubtreeBool() {
        return this.topicSearchSubtreeBool;
    }
    
    public void setTopicSearchSubtreeBool(final boolean topicSearchSubtreeBool) {
        this.topicSearchSubtreeBool = topicSearchSubtreeBool;
    }
    
    public String getWriteAttribute() {
        return this.writeAttribute;
    }
    
    public void setWriteAttribute(final String writeAttribute) {
        this.writeAttribute = writeAttribute;
    }
    
    public String getWriteBase() {
        return this.writeBase;
    }
    
    public void setWriteBase(final String writeBase) {
        this.writeBase = writeBase;
    }
    
    public boolean isUseAdvisorySearchBase() {
        return this.useAdvisorySearchBase;
    }
    
    public void setUseAdvisorySearchBase(final boolean useAdvisorySearchBase) {
        this.useAdvisorySearchBase = useAdvisorySearchBase;
    }
    
    public String getAdvisorySearchBase() {
        return this.advisorySearchBase;
    }
    
    public void setAdvisorySearchBase(final String advisorySearchBase) {
        this.advisorySearchBase = advisorySearchBase;
    }
    
    public String getTempSearchBase() {
        return this.tempSearchBase;
    }
    
    public void setTempSearchBase(final String tempSearchBase) {
        this.tempSearchBase = tempSearchBase;
    }
    
    protected Set<GroupPrincipal> getCompositeACLs(final ActiveMQDestination destination, final String roleBase, final String roleAttribute) {
        final ActiveMQDestination[] dests = destination.getCompositeDestinations();
        Set<GroupPrincipal> acls = null;
        for (final ActiveMQDestination dest : dests) {
            acls = (Set<GroupPrincipal>)DestinationMap.union(acls, this.getACLs(dest, roleBase, roleAttribute));
            if (acls == null) {
                break;
            }
            if (acls.isEmpty()) {
                break;
            }
        }
        return acls;
    }
    
    protected Set<GroupPrincipal> getACLs(final ActiveMQDestination destination, final String roleBase, final String roleAttribute) {
        try {
            this.context = this.open();
        }
        catch (NamingException e) {
            LDAPAuthorizationMap.LOG.error(e.toString());
            return new HashSet<GroupPrincipal>();
        }
        String destinationBase = "";
        final SearchControls constraints = new SearchControls();
        if (AdvisorySupport.isAdvisoryTopic(destination) && this.useAdvisorySearchBase) {
            destinationBase = this.advisorySearchBase;
        }
        else {
            if ((destination.getDestinationType() & 0x1) == 0x1) {
                destinationBase = this.queueSearchMatchingFormat.format(new String[] { destination.getPhysicalName() });
                if (this.queueSearchSubtreeBool) {
                    constraints.setSearchScope(2);
                }
                else {
                    constraints.setSearchScope(1);
                }
            }
            if ((destination.getDestinationType() & 0x2) == 0x2) {
                destinationBase = this.topicSearchMatchingFormat.format(new String[] { destination.getPhysicalName() });
                if (this.topicSearchSubtreeBool) {
                    constraints.setSearchScope(2);
                }
                else {
                    constraints.setSearchScope(1);
                }
            }
        }
        constraints.setReturningAttributes(new String[] { roleAttribute });
        return this.getACLs(destinationBase, constraints, roleBase, roleAttribute);
    }
    
    protected Set<GroupPrincipal> getACLs(final String destinationBase, final SearchControls constraints, final String roleBase, final String roleAttribute) {
        try {
            final Set<GroupPrincipal> roles = new HashSet<GroupPrincipal>();
            Set<String> acls = new HashSet<String>();
            final NamingEnumeration<?> results = this.context.search(destinationBase, roleBase, constraints);
            while (results.hasMore()) {
                final SearchResult result = (SearchResult)results.next();
                final Attributes attrs = result.getAttributes();
                if (attrs == null) {
                    continue;
                }
                acls = this.addAttributeValues(roleAttribute, attrs, acls);
            }
            for (final String roleName : acls) {
                final LdapName ldapname = new LdapName(roleName);
                final Rdn rdn = ldapname.getRdn(ldapname.size() - 1);
                LDAPAuthorizationMap.LOG.debug("Found role: [" + rdn.getValue().toString() + "]");
                roles.add(new GroupPrincipal(rdn.getValue().toString()));
            }
            return roles;
        }
        catch (NamingException e) {
            LDAPAuthorizationMap.LOG.error(e.toString());
            return new HashSet<GroupPrincipal>();
        }
    }
    
    protected Set<String> addAttributeValues(final String attrId, final Attributes attrs, Set<String> values) throws NamingException {
        if (attrId == null || attrs == null) {
            return values;
        }
        if (values == null) {
            values = new HashSet<String>();
        }
        final Attribute attr = attrs.get(attrId);
        if (attr == null) {
            return values;
        }
        final NamingEnumeration<?> e = attr.getAll();
        while (e.hasMore()) {
            final String value = (String)e.next();
            values.add(value);
        }
        return values;
    }
    
    protected DirContext open() throws NamingException {
        if (this.context != null) {
            return this.context;
        }
        try {
            final Hashtable<String, String> env = new Hashtable<String, String>();
            env.put("java.naming.factory.initial", this.initialContextFactory);
            if (this.connectionUsername != null || !"".equals(this.connectionUsername)) {
                env.put("java.naming.security.principal", this.connectionUsername);
            }
            if (this.connectionPassword != null || !"".equals(this.connectionPassword)) {
                env.put("java.naming.security.credentials", this.connectionPassword);
            }
            env.put("java.naming.security.protocol", this.connectionProtocol);
            env.put("java.naming.provider.url", this.connectionURL);
            env.put("java.naming.security.authentication", this.authentication);
            this.context = new InitialDirContext(env);
        }
        catch (NamingException e) {
            LDAPAuthorizationMap.LOG.error(e.toString());
            throw e;
        }
        return this.context;
    }
    
    static {
        LOG = LoggerFactory.getLogger(LDAPLoginModule.class);
    }
}
