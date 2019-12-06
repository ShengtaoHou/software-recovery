// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.jndi;

import javax.naming.Binding;
import javax.naming.NameClassPair;
import org.slf4j.LoggerFactory;
import javax.naming.OperationNotSupportedException;
import javax.naming.NotContextException;
import javax.naming.NamingEnumeration;
import javax.naming.NameNotFoundException;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.spi.NamingManager;
import javax.naming.Reference;
import javax.naming.LinkRef;
import javax.naming.NamingException;
import java.util.Iterator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Hashtable;
import javax.naming.NameParser;
import org.slf4j.Logger;
import java.io.Serializable;
import javax.naming.Context;

public class ReadOnlyContext implements Context, Serializable
{
    private static final Logger LOG;
    public static final String SEPARATOR = "/";
    protected static final NameParser NAME_PARSER;
    private static final long serialVersionUID = -5754338187296859149L;
    protected final Hashtable<String, Object> environment;
    protected final Map<String, Object> bindings;
    protected final Map<String, Object> treeBindings;
    private boolean frozen;
    private String nameInNamespace;
    
    public ReadOnlyContext() {
        this.nameInNamespace = "";
        this.environment = new Hashtable<String, Object>();
        this.bindings = new HashMap<String, Object>();
        this.treeBindings = new HashMap<String, Object>();
    }
    
    public ReadOnlyContext(final Hashtable env) {
        this.nameInNamespace = "";
        if (env == null) {
            this.environment = new Hashtable<String, Object>();
        }
        else {
            this.environment = new Hashtable<String, Object>(env);
        }
        this.bindings = (Map<String, Object>)Collections.EMPTY_MAP;
        this.treeBindings = (Map<String, Object>)Collections.EMPTY_MAP;
    }
    
    public ReadOnlyContext(final Hashtable environment, final Map<String, Object> bindings) {
        this.nameInNamespace = "";
        if (environment == null) {
            this.environment = new Hashtable<String, Object>();
        }
        else {
            this.environment = new Hashtable<String, Object>(environment);
        }
        this.bindings = new HashMap<String, Object>();
        this.treeBindings = new HashMap<String, Object>();
        if (bindings != null) {
            for (final Map.Entry<String, Object> binding : bindings.entrySet()) {
                try {
                    this.internalBind(binding.getKey(), binding.getValue());
                }
                catch (Throwable e) {
                    ReadOnlyContext.LOG.error("Failed to bind " + binding.getKey() + "=" + binding.getValue(), e);
                }
            }
        }
        this.frozen = true;
    }
    
    public ReadOnlyContext(final Hashtable environment, final Map bindings, final String nameInNamespace) {
        this(environment, bindings);
        this.nameInNamespace = nameInNamespace;
    }
    
    protected ReadOnlyContext(final ReadOnlyContext clone, final Hashtable env) {
        this.nameInNamespace = "";
        this.bindings = clone.bindings;
        this.treeBindings = clone.treeBindings;
        this.environment = new Hashtable<String, Object>(env);
    }
    
    protected ReadOnlyContext(final ReadOnlyContext clone, final Hashtable<String, Object> env, final String nameInNamespace) {
        this(clone, env);
        this.nameInNamespace = nameInNamespace;
    }
    
    public void freeze() {
        this.frozen = true;
    }
    
    boolean isFrozen() {
        return this.frozen;
    }
    
    protected Map<String, Object> internalBind(final String name, final Object value) throws NamingException {
        assert name != null && name.length() > 0;
        assert !this.frozen;
        final Map<String, Object> newBindings = new HashMap<String, Object>();
        final int pos = name.indexOf(47);
        if (pos == -1) {
            if (this.treeBindings.put(name, value) != null) {
                throw new NamingException("Something already bound at " + name);
            }
            this.bindings.put(name, value);
            newBindings.put(name, value);
        }
        else {
            final String segment = name.substring(0, pos);
            assert segment != null;
            assert !segment.equals("");
            Object o = this.treeBindings.get(segment);
            if (o == null) {
                o = this.newContext();
                this.treeBindings.put(segment, o);
                this.bindings.put(segment, o);
                newBindings.put(segment, o);
            }
            else if (!(o instanceof ReadOnlyContext)) {
                throw new NamingException("Something already bound where a subcontext should go");
            }
            final ReadOnlyContext readOnlyContext = (ReadOnlyContext)o;
            final String remainder = name.substring(pos + 1);
            final Map<String, Object> subBindings = readOnlyContext.internalBind(remainder, value);
            for (final Map.Entry entry : subBindings.entrySet()) {
                final String subName = segment + "/" + entry.getKey();
                final Object bound = entry.getValue();
                this.treeBindings.put(subName, bound);
                newBindings.put(subName, bound);
            }
        }
        return newBindings;
    }
    
    protected ReadOnlyContext newContext() {
        return new ReadOnlyContext();
    }
    
    @Override
    public Object addToEnvironment(final String propName, final Object propVal) throws NamingException {
        return this.environment.put(propName, propVal);
    }
    
    @Override
    public Hashtable<String, Object> getEnvironment() throws NamingException {
        return (Hashtable<String, Object>)this.environment.clone();
    }
    
    @Override
    public Object removeFromEnvironment(final String propName) throws NamingException {
        return this.environment.remove(propName);
    }
    
    @Override
    public Object lookup(final String name) throws NamingException {
        if (name.length() == 0) {
            return this;
        }
        Object result = this.treeBindings.get(name);
        if (result == null) {
            result = this.bindings.get(name);
        }
        if (result != null) {
            if (result instanceof LinkRef) {
                final LinkRef ref = (LinkRef)result;
                result = this.lookup(ref.getLinkName());
            }
            if (result instanceof Reference) {
                try {
                    result = NamingManager.getObjectInstance(result, null, null, this.environment);
                }
                catch (NamingException e) {
                    throw e;
                }
                catch (Exception e2) {
                    throw (NamingException)new NamingException("could not look up : " + name).initCause(e2);
                }
            }
            if (result instanceof ReadOnlyContext) {
                String prefix = this.getNameInNamespace();
                if (prefix.length() > 0) {
                    prefix += "/";
                }
                result = new ReadOnlyContext((ReadOnlyContext)result, this.environment, prefix + name);
            }
            return result;
        }
        final int pos = name.indexOf(58);
        if (pos > 0) {
            final String scheme = name.substring(0, pos);
            final Context ctx = NamingManager.getURLContext(scheme, this.environment);
            if (ctx == null) {
                throw new NamingException("scheme " + scheme + " not recognized");
            }
            return ctx.lookup(name);
        }
        else {
            final CompositeName path = new CompositeName(name);
            if (path.size() == 0) {
                return this;
            }
            final String first = path.get(0);
            Object obj = this.bindings.get(first);
            if (obj == null) {
                throw new NameNotFoundException(name);
            }
            if (obj instanceof Context && path.size() > 1) {
                final Context subContext = (Context)obj;
                obj = subContext.lookup(path.getSuffix(1));
            }
            return obj;
        }
    }
    
    @Override
    public Object lookup(final Name name) throws NamingException {
        return this.lookup(name.toString());
    }
    
    @Override
    public Object lookupLink(final String name) throws NamingException {
        return this.lookup(name);
    }
    
    @Override
    public Name composeName(final Name name, final Name prefix) throws NamingException {
        final Name result = (Name)prefix.clone();
        result.addAll(name);
        return result;
    }
    
    @Override
    public String composeName(final String name, final String prefix) throws NamingException {
        final CompositeName result = new CompositeName(prefix);
        result.addAll(new CompositeName(name));
        return result.toString();
    }
    
    @Override
    public NamingEnumeration list(final String name) throws NamingException {
        final Object o = this.lookup(name);
        if (o == this) {
            return new ListEnumeration();
        }
        if (o instanceof Context) {
            return ((Context)o).list("");
        }
        throw new NotContextException();
    }
    
    @Override
    public NamingEnumeration listBindings(final String name) throws NamingException {
        final Object o = this.lookup(name);
        if (o == this) {
            return new ListBindingEnumeration();
        }
        if (o instanceof Context) {
            return ((Context)o).listBindings("");
        }
        throw new NotContextException();
    }
    
    @Override
    public Object lookupLink(final Name name) throws NamingException {
        return this.lookupLink(name.toString());
    }
    
    @Override
    public NamingEnumeration list(final Name name) throws NamingException {
        return this.list(name.toString());
    }
    
    @Override
    public NamingEnumeration listBindings(final Name name) throws NamingException {
        return this.listBindings(name.toString());
    }
    
    @Override
    public void bind(final Name name, final Object obj) throws NamingException {
        throw new OperationNotSupportedException();
    }
    
    @Override
    public void bind(final String name, final Object obj) throws NamingException {
        throw new OperationNotSupportedException();
    }
    
    @Override
    public void close() throws NamingException {
    }
    
    @Override
    public Context createSubcontext(final Name name) throws NamingException {
        throw new OperationNotSupportedException();
    }
    
    @Override
    public Context createSubcontext(final String name) throws NamingException {
        throw new OperationNotSupportedException();
    }
    
    @Override
    public void destroySubcontext(final Name name) throws NamingException {
        throw new OperationNotSupportedException();
    }
    
    @Override
    public void destroySubcontext(final String name) throws NamingException {
        throw new OperationNotSupportedException();
    }
    
    @Override
    public String getNameInNamespace() throws NamingException {
        return this.nameInNamespace;
    }
    
    @Override
    public NameParser getNameParser(final Name name) throws NamingException {
        return ReadOnlyContext.NAME_PARSER;
    }
    
    @Override
    public NameParser getNameParser(final String name) throws NamingException {
        return ReadOnlyContext.NAME_PARSER;
    }
    
    @Override
    public void rebind(final Name name, final Object obj) throws NamingException {
        throw new OperationNotSupportedException();
    }
    
    @Override
    public void rebind(final String name, final Object obj) throws NamingException {
        throw new OperationNotSupportedException();
    }
    
    @Override
    public void rename(final Name oldName, final Name newName) throws NamingException {
        throw new OperationNotSupportedException();
    }
    
    @Override
    public void rename(final String oldName, final String newName) throws NamingException {
        throw new OperationNotSupportedException();
    }
    
    @Override
    public void unbind(final Name name) throws NamingException {
        throw new OperationNotSupportedException();
    }
    
    @Override
    public void unbind(final String name) throws NamingException {
        throw new OperationNotSupportedException();
    }
    
    static {
        LOG = LoggerFactory.getLogger(ReadOnlyContext.class);
        NAME_PARSER = new NameParserImpl();
    }
    
    private abstract class LocalNamingEnumeration implements NamingEnumeration
    {
        private final Iterator i;
        
        private LocalNamingEnumeration() {
            this.i = ReadOnlyContext.this.bindings.entrySet().iterator();
        }
        
        @Override
        public boolean hasMore() throws NamingException {
            return this.i.hasNext();
        }
        
        @Override
        public boolean hasMoreElements() {
            return this.i.hasNext();
        }
        
        protected Map.Entry getNext() {
            return this.i.next();
        }
        
        @Override
        public void close() throws NamingException {
        }
    }
    
    private class ListEnumeration extends LocalNamingEnumeration
    {
        ListEnumeration() {
        }
        
        @Override
        public Object next() throws NamingException {
            return this.nextElement();
        }
        
        @Override
        public Object nextElement() {
            final Map.Entry entry = this.getNext();
            return new NameClassPair(entry.getKey(), entry.getValue().getClass().getName());
        }
    }
    
    private class ListBindingEnumeration extends LocalNamingEnumeration
    {
        ListBindingEnumeration() {
        }
        
        @Override
        public Object next() throws NamingException {
            return this.nextElement();
        }
        
        @Override
        public Object nextElement() {
            final Map.Entry entry = this.getNext();
            return new Binding(entry.getKey(), entry.getValue());
        }
    }
}
