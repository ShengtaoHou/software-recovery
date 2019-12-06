// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.jndi;

import java.util.Properties;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import javax.jms.Topic;
import javax.jms.Queue;
import java.util.List;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.net.URISyntaxException;
import java.util.Iterator;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.command.ActiveMQQueue;
import javax.naming.NamingException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.naming.Context;
import java.util.Hashtable;
import javax.naming.spi.InitialContextFactory;

public class ActiveMQInitialContextFactory implements InitialContextFactory
{
    private static final String[] DEFAULT_CONNECTION_FACTORY_NAMES;
    private String connectionPrefix;
    private String queuePrefix;
    private String topicPrefix;
    
    public ActiveMQInitialContextFactory() {
        this.connectionPrefix = "connection.";
        this.queuePrefix = "queue.";
        this.topicPrefix = "topic.";
    }
    
    @Override
    public Context getInitialContext(final Hashtable environment) throws NamingException {
        final Map<String, Object> data = new ConcurrentHashMap<String, Object>();
        final String[] names = this.getConnectionFactoryNames(environment);
        for (int i = 0; i < names.length; ++i) {
            ActiveMQConnectionFactory factory = null;
            final String name = names[i];
            try {
                factory = this.createConnectionFactory(name, environment);
            }
            catch (Exception e) {
                throw new NamingException("Invalid broker URL");
            }
            data.put(name, factory);
        }
        this.createQueues(data, environment);
        this.createTopics(data, environment);
        data.put("dynamicQueues", new LazyCreateContext() {
            private static final long serialVersionUID = 6503881346214855588L;
            
            @Override
            protected Object createEntry(final String name) {
                return new ActiveMQQueue(name);
            }
        });
        data.put("dynamicTopics", new LazyCreateContext() {
            private static final long serialVersionUID = 2019166796234979615L;
            
            @Override
            protected Object createEntry(final String name) {
                return new ActiveMQTopic(name);
            }
        });
        return this.createContext(environment, data);
    }
    
    public String getTopicPrefix() {
        return this.topicPrefix;
    }
    
    public void setTopicPrefix(final String topicPrefix) {
        this.topicPrefix = topicPrefix;
    }
    
    public String getQueuePrefix() {
        return this.queuePrefix;
    }
    
    public void setQueuePrefix(final String queuePrefix) {
        this.queuePrefix = queuePrefix;
    }
    
    protected ReadOnlyContext createContext(final Hashtable environment, final Map<String, Object> data) {
        return new ReadOnlyContext(environment, data);
    }
    
    protected ActiveMQConnectionFactory createConnectionFactory(final String name, final Hashtable environment) throws URISyntaxException {
        final Hashtable temp = new Hashtable(environment);
        if (ActiveMQInitialContextFactory.DEFAULT_CONNECTION_FACTORY_NAMES[1].equals(name)) {
            temp.put("xa", String.valueOf(true));
        }
        final String prefix = this.connectionPrefix + name + ".";
        for (final Map.Entry entry : environment.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(prefix)) {
                temp.remove(key);
                key = key.substring(prefix.length());
                temp.put(key, entry.getValue());
            }
        }
        return this.createConnectionFactory(temp);
    }
    
    protected String[] getConnectionFactoryNames(final Map environment) {
        final String factoryNames = environment.get("connectionFactoryNames");
        if (factoryNames != null) {
            final List<String> list = new ArrayList<String>();
            final StringTokenizer enumeration = new StringTokenizer(factoryNames, ",");
            while (enumeration.hasMoreTokens()) {
                list.add(enumeration.nextToken().trim());
            }
            final int size = list.size();
            if (size > 0) {
                final String[] answer = new String[size];
                list.toArray(answer);
                return answer;
            }
        }
        return ActiveMQInitialContextFactory.DEFAULT_CONNECTION_FACTORY_NAMES;
    }
    
    protected void createQueues(final Map<String, Object> data, final Hashtable environment) {
        for (final Map.Entry entry : environment.entrySet()) {
            final String key = entry.getKey().toString();
            if (key.startsWith(this.queuePrefix)) {
                final String jndiName = key.substring(this.queuePrefix.length());
                data.put(jndiName, this.createQueue(entry.getValue().toString()));
            }
        }
    }
    
    protected void createTopics(final Map<String, Object> data, final Hashtable environment) {
        for (final Map.Entry entry : environment.entrySet()) {
            final String key = entry.getKey().toString();
            if (key.startsWith(this.topicPrefix)) {
                final String jndiName = key.substring(this.topicPrefix.length());
                data.put(jndiName, this.createTopic(entry.getValue().toString()));
            }
        }
    }
    
    protected Queue createQueue(final String name) {
        return new ActiveMQQueue(name);
    }
    
    protected Topic createTopic(final String name) {
        return new ActiveMQTopic(name);
    }
    
    protected ActiveMQConnectionFactory createConnectionFactory(final Hashtable environment) throws URISyntaxException {
        final ActiveMQConnectionFactory answer = this.needsXA(environment) ? new ActiveMQXAConnectionFactory() : new ActiveMQConnectionFactory();
        final Properties properties = new Properties();
        properties.putAll(environment);
        answer.setProperties(properties);
        return answer;
    }
    
    private boolean needsXA(final Hashtable environment) {
        final boolean isXA = Boolean.parseBoolean(environment.get("xa"));
        environment.remove("xa");
        return isXA;
    }
    
    public String getConnectionPrefix() {
        return this.connectionPrefix;
    }
    
    public void setConnectionPrefix(final String connectionPrefix) {
        this.connectionPrefix = connectionPrefix;
    }
    
    static {
        DEFAULT_CONNECTION_FACTORY_NAMES = new String[] { "ConnectionFactory", "XAConnectionFactory", "QueueConnectionFactory", "TopicConnectionFactory" };
    }
}
