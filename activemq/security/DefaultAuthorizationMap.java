// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.security;

import java.util.HashSet;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.security.Principal;
import java.util.Iterator;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.Collection;
import java.util.Set;
import org.apache.activemq.filter.DestinationMapEntry;
import java.util.List;
import org.apache.activemq.filter.DestinationMap;

public class DefaultAuthorizationMap extends DestinationMap implements AuthorizationMap
{
    public static final String DEFAULT_GROUP_CLASS = "org.apache.activemq.jaas.GroupPrincipal";
    private AuthorizationEntry defaultEntry;
    private TempDestinationAuthorizationEntry tempDestinationAuthorizationEntry;
    protected String groupClass;
    static final String WILDCARD = "*";
    
    public DefaultAuthorizationMap() {
        this.groupClass = "org.apache.activemq.jaas.GroupPrincipal";
    }
    
    public DefaultAuthorizationMap(final List<DestinationMapEntry> authorizationEntries) {
        this.groupClass = "org.apache.activemq.jaas.GroupPrincipal";
        this.setAuthorizationEntries(authorizationEntries);
    }
    
    public void setTempDestinationAuthorizationEntry(final TempDestinationAuthorizationEntry tempDestinationAuthorizationEntry) {
        this.tempDestinationAuthorizationEntry = tempDestinationAuthorizationEntry;
    }
    
    public TempDestinationAuthorizationEntry getTempDestinationAuthorizationEntry() {
        return this.tempDestinationAuthorizationEntry;
    }
    
    @Override
    public Set<Object> getTempDestinationAdminACLs() {
        if (this.tempDestinationAuthorizationEntry != null) {
            final Set<Object> answer = new WildcardAwareSet<Object>();
            answer.addAll(this.tempDestinationAuthorizationEntry.getAdminACLs());
            return answer;
        }
        return null;
    }
    
    @Override
    public Set<Object> getTempDestinationReadACLs() {
        if (this.tempDestinationAuthorizationEntry != null) {
            final Set<Object> answer = new WildcardAwareSet<Object>();
            answer.addAll(this.tempDestinationAuthorizationEntry.getReadACLs());
            return answer;
        }
        return null;
    }
    
    @Override
    public Set<Object> getTempDestinationWriteACLs() {
        if (this.tempDestinationAuthorizationEntry != null) {
            final Set<Object> answer = new WildcardAwareSet<Object>();
            answer.addAll(this.tempDestinationAuthorizationEntry.getWriteACLs());
            return answer;
        }
        return null;
    }
    
    @Override
    public Set<Object> getAdminACLs(final ActiveMQDestination destination) {
        final Set<AuthorizationEntry> entries = this.getAllEntries(destination);
        final Set<Object> answer = new WildcardAwareSet<Object>();
        for (final AuthorizationEntry entry : entries) {
            answer.addAll(entry.getAdminACLs());
        }
        return answer;
    }
    
    @Override
    public Set<Object> getReadACLs(final ActiveMQDestination destination) {
        final Set<AuthorizationEntry> entries = this.getAllEntries(destination);
        final Set<Object> answer = new WildcardAwareSet<Object>();
        for (final AuthorizationEntry entry : entries) {
            answer.addAll(entry.getReadACLs());
        }
        return answer;
    }
    
    @Override
    public Set<Object> getWriteACLs(final ActiveMQDestination destination) {
        final Set<AuthorizationEntry> entries = this.getAllEntries(destination);
        final Set<Object> answer = new WildcardAwareSet<Object>();
        for (final AuthorizationEntry entry : entries) {
            answer.addAll(entry.getWriteACLs());
        }
        return answer;
    }
    
    public AuthorizationEntry getEntryFor(final ActiveMQDestination destination) {
        AuthorizationEntry answer = (AuthorizationEntry)this.chooseValue(destination);
        if (answer == null) {
            answer = this.getDefaultEntry();
        }
        return answer;
    }
    
    @Override
    public synchronized Set get(final ActiveMQDestination key) {
        if (key.isComposite()) {
            final ActiveMQDestination[] destinations = key.getCompositeDestinations();
            Set answer = null;
            for (int i = 0; i < destinations.length; ++i) {
                final ActiveMQDestination childDestination = destinations[i];
                answer = DestinationMap.union(answer, this.get(childDestination));
                if (answer == null) {
                    break;
                }
                if (answer.isEmpty()) {
                    break;
                }
            }
            return answer;
        }
        return this.findWildcardMatches(key);
    }
    
    public void setAuthorizationEntries(final List<DestinationMapEntry> entries) {
        super.setEntries(entries);
    }
    
    public AuthorizationEntry getDefaultEntry() {
        return this.defaultEntry;
    }
    
    public void setDefaultEntry(final AuthorizationEntry defaultEntry) {
        this.defaultEntry = defaultEntry;
    }
    
    @Override
    protected Class<? extends DestinationMapEntry> getEntryClass() {
        return AuthorizationEntry.class;
    }
    
    protected Set<AuthorizationEntry> getAllEntries(final ActiveMQDestination destination) {
        final Set<AuthorizationEntry> entries = (Set<AuthorizationEntry>)this.get(destination);
        if (this.defaultEntry != null) {
            entries.add(this.defaultEntry);
        }
        return entries;
    }
    
    public String getGroupClass() {
        return this.groupClass;
    }
    
    public void setGroupClass(final String groupClass) {
        this.groupClass = groupClass;
    }
    
    public static Object createGroupPrincipal(final String name, final String groupClass) throws Exception {
        if ("*".equals(name)) {
            return new Principal() {
                @Override
                public String getName() {
                    return "*";
                }
                
                @Override
                public boolean equals(final Object other) {
                    return true;
                }
                
                @Override
                public int hashCode() {
                    return "*".hashCode();
                }
            };
        }
        final Object[] param = { name };
        final Class<?> cls = Class.forName(groupClass);
        Constructor<?>[] constructors;
        int i;
        Class<?>[] paramTypes;
        for (constructors = cls.getConstructors(), i = 0; i < constructors.length; ++i) {
            paramTypes = constructors[i].getParameterTypes();
            if (paramTypes.length != 0 && paramTypes[0].equals(String.class)) {
                break;
            }
        }
        Object instance;
        if (i < constructors.length) {
            instance = constructors[i].newInstance(param);
        }
        else {
            instance = cls.newInstance();
            Method[] methods;
            Class<?>[] paramTypes2;
            for (methods = cls.getMethods(), i = 0, i = 0; i < methods.length; ++i) {
                paramTypes2 = methods[i].getParameterTypes();
                if (paramTypes2.length != 0 && methods[i].getName().equals("setName") && paramTypes2[0].equals(String.class)) {
                    break;
                }
            }
            if (i >= methods.length) {
                throw new NoSuchMethodException();
            }
            methods[i].invoke(instance, param);
        }
        return instance;
    }
    
    class WildcardAwareSet<T> extends HashSet<T>
    {
        boolean hasWildcard;
        
        WildcardAwareSet() {
            this.hasWildcard = false;
        }
        
        @Override
        public boolean contains(final Object e) {
            return this.hasWildcard || super.contains(e);
        }
        
        @Override
        public boolean addAll(final Collection<? extends T> collection) {
            boolean modified = false;
            for (final T item : collection) {
                if (this.isWildcard(item)) {
                    this.hasWildcard = true;
                }
                if (this.add(item)) {
                    modified = true;
                }
            }
            return modified;
        }
        
        private boolean isWildcard(final T item) {
            try {
                if (item.getClass().getMethod("getName", (Class<?>[])new Class[0]).invoke(item, new Object[0]).equals("*")) {
                    return true;
                }
            }
            catch (Exception ex) {}
            return false;
        }
    }
}
