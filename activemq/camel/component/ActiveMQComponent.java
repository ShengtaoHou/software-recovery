// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.camel.component;

import org.slf4j.LoggerFactory;
import java.util.Set;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.ArrayList;
import java.util.List;
import org.apache.camel.ComponentConfiguration;
import java.util.Iterator;
import javax.jms.Connection;
import java.net.URISyntaxException;
import org.apache.camel.util.ObjectHelper;
import org.apache.camel.util.URISupport;
import org.apache.camel.util.IntrospectionSupport;
import java.util.Map;
import org.apache.camel.component.jms.JmsConfiguration;
import org.apache.camel.CamelContext;
import org.apache.activemq.advisory.DestinationSource;
import org.apache.activemq.EnhancedConnection;
import org.slf4j.Logger;
import org.apache.activemq.Service;
import org.springframework.jms.connection.SingleConnectionFactory;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.camel.spi.EndpointCompleter;
import org.apache.camel.component.jms.JmsComponent;

public class ActiveMQComponent extends JmsComponent implements EndpointCompleter
{
    private final CopyOnWriteArrayList<SingleConnectionFactory> singleConnectionFactoryList;
    private final CopyOnWriteArrayList<Service> pooledConnectionFactoryServiceList;
    private static final transient Logger LOG;
    private boolean exposeAllQueues;
    private CamelEndpointLoader endpointLoader;
    private EnhancedConnection connection;
    DestinationSource source;
    boolean sourceInitialized;
    
    public static ActiveMQComponent activeMQComponent() {
        return new ActiveMQComponent();
    }
    
    public static ActiveMQComponent activeMQComponent(final String brokerURL) {
        final ActiveMQComponent answer = new ActiveMQComponent();
        if (answer.getConfiguration() instanceof ActiveMQConfiguration) {
            ((ActiveMQConfiguration)answer.getConfiguration()).setBrokerURL(brokerURL);
        }
        return answer;
    }
    
    public ActiveMQComponent() {
        this.singleConnectionFactoryList = new CopyOnWriteArrayList<SingleConnectionFactory>();
        this.pooledConnectionFactoryServiceList = new CopyOnWriteArrayList<Service>();
        this.sourceInitialized = false;
    }
    
    public ActiveMQComponent(final CamelContext context) {
        super(context);
        this.singleConnectionFactoryList = new CopyOnWriteArrayList<SingleConnectionFactory>();
        this.pooledConnectionFactoryServiceList = new CopyOnWriteArrayList<Service>();
        this.sourceInitialized = false;
    }
    
    public ActiveMQComponent(final ActiveMQConfiguration configuration) {
        super((JmsConfiguration)configuration);
        this.singleConnectionFactoryList = new CopyOnWriteArrayList<SingleConnectionFactory>();
        this.pooledConnectionFactoryServiceList = new CopyOnWriteArrayList<Service>();
        this.sourceInitialized = false;
    }
    
    public void setBrokerURL(final String brokerURL) {
        if (this.getConfiguration() instanceof ActiveMQConfiguration) {
            ((ActiveMQConfiguration)this.getConfiguration()).setBrokerURL(brokerURL);
        }
    }
    
    public void setUserName(final String userName) {
        if (this.getConfiguration() instanceof ActiveMQConfiguration) {
            ((ActiveMQConfiguration)this.getConfiguration()).setUserName(userName);
        }
    }
    
    public void setPassword(final String password) {
        if (this.getConfiguration() instanceof ActiveMQConfiguration) {
            ((ActiveMQConfiguration)this.getConfiguration()).setPassword(password);
        }
    }
    
    public boolean isExposeAllQueues() {
        return this.exposeAllQueues;
    }
    
    public void setExposeAllQueues(final boolean exposeAllQueues) {
        this.exposeAllQueues = exposeAllQueues;
    }
    
    public void setUsePooledConnection(final boolean usePooledConnection) {
        if (this.getConfiguration() instanceof ActiveMQConfiguration) {
            ((ActiveMQConfiguration)this.getConfiguration()).setUsePooledConnection(usePooledConnection);
        }
    }
    
    public void setUseSingleConnection(final boolean useSingleConnection) {
        if (this.getConfiguration() instanceof ActiveMQConfiguration) {
            ((ActiveMQConfiguration)this.getConfiguration()).setUseSingleConnection(useSingleConnection);
        }
    }
    
    protected void addPooledConnectionFactoryService(final Service pooledConnectionFactoryService) {
        this.pooledConnectionFactoryServiceList.add(pooledConnectionFactoryService);
    }
    
    protected void addSingleConnectionFactory(final SingleConnectionFactory singleConnectionFactory) {
        this.singleConnectionFactoryList.add(singleConnectionFactory);
    }
    
    protected String convertPathToActualDestination(final String path, final Map<String, Object> parameters) {
        final Map options = IntrospectionSupport.extractProperties((Map)parameters, "destination.");
        String query;
        try {
            query = URISupport.createQueryString(options);
        }
        catch (URISyntaxException e) {
            throw ObjectHelper.wrapRuntimeCamelException((Throwable)e);
        }
        if (ObjectHelper.isNotEmpty((Object)query)) {
            return path + "?" + query;
        }
        return path;
    }
    
    protected void doStart() throws Exception {
        super.doStart();
        if (this.isExposeAllQueues()) {
            this.createDestinationSource();
            (this.endpointLoader = new CamelEndpointLoader(this.getCamelContext(), this.source)).afterPropertiesSet();
        }
    }
    
    protected void createDestinationSource() {
        try {
            if (this.source == null) {
                if (this.connection == null) {
                    final Connection value = this.getConfiguration().getConnectionFactory().createConnection();
                    if (!(value instanceof EnhancedConnection)) {
                        throw new IllegalArgumentException("Created JMS Connection is not an EnhancedConnection: " + value);
                    }
                    (this.connection = (EnhancedConnection)value).start();
                }
                this.source = this.connection.getDestinationSource();
            }
        }
        catch (Throwable t) {
            ActiveMQComponent.LOG.info("Can't get destination source, endpoint completer will not work", t);
        }
    }
    
    protected void doStop() throws Exception {
        if (this.source != null) {
            this.source.stop();
            this.source = null;
        }
        if (this.connection != null) {
            this.connection.close();
            this.connection = null;
        }
        for (final Service s : this.pooledConnectionFactoryServiceList) {
            s.stop();
        }
        this.pooledConnectionFactoryServiceList.clear();
        for (final SingleConnectionFactory s2 : this.singleConnectionFactoryList) {
            s2.destroy();
        }
        this.singleConnectionFactoryList.clear();
        super.doStop();
    }
    
    public void setConfiguration(final JmsConfiguration configuration) {
        if (configuration instanceof ActiveMQConfiguration) {
            ((ActiveMQConfiguration)configuration).setActiveMQComponent(this);
        }
        super.setConfiguration(configuration);
    }
    
    protected JmsConfiguration createConfiguration() {
        final ActiveMQConfiguration answer = new ActiveMQConfiguration();
        answer.setActiveMQComponent(this);
        return answer;
    }
    
    public List<String> completeEndpointPath(final ComponentConfiguration componentConfiguration, final String completionText) {
        if (!this.sourceInitialized) {
            this.createDestinationSource();
            this.sourceInitialized = true;
        }
        final ArrayList<String> answer = new ArrayList<String>();
        if (this.source != null) {
            Set candidates = this.source.getQueues();
            String destinationName = completionText;
            if (completionText.startsWith("topic:")) {
                candidates = this.source.getTopics();
                destinationName = completionText.substring(6);
            }
            else if (completionText.startsWith("queue:")) {
                destinationName = completionText.substring(6);
            }
            for (final ActiveMQDestination destination : candidates) {
                if (destination.getPhysicalName().startsWith(destinationName)) {
                    answer.add(destination.getPhysicalName());
                }
            }
        }
        return answer;
    }
    
    static {
        LOG = LoggerFactory.getLogger(ActiveMQComponent.class);
    }
}
