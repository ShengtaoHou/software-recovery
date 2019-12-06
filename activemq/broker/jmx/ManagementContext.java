// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import org.slf4j.LoggerFactory;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.management.Attribute;
import java.rmi.registry.LocateRegistry;
import java.lang.reflect.Method;
import javax.management.JMException;
import javax.management.InstanceNotFoundException;
import java.util.Set;
import javax.management.QueryExp;
import javax.management.ObjectInstance;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import java.util.List;
import java.util.Iterator;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.server.UnicastRemoteObject;
import javax.management.MBeanServerFactory;
import java.io.IOException;
import org.slf4j.MDC;
import java.util.concurrent.ConcurrentHashMap;
import java.rmi.registry.Registry;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Map;
import javax.management.MBeanServer;
import org.slf4j.Logger;
import org.apache.activemq.Service;

public class ManagementContext implements Service
{
    public static final String DEFAULT_DOMAIN = "org.apache.activemq";
    private static final Logger LOG;
    private MBeanServer beanServer;
    private String jmxDomainName;
    private boolean useMBeanServer;
    private boolean createMBeanServer;
    private boolean locallyCreateMBeanServer;
    private boolean createConnector;
    private boolean findTigerMbeanServer;
    private String connectorHost;
    private int connectorPort;
    private Map<String, ?> environment;
    private int rmiServerPort;
    private String connectorPath;
    private final AtomicBoolean started;
    private final AtomicBoolean connectorStarting;
    private JMXConnectorServer connectorServer;
    private ObjectName namingServiceObjectName;
    private Registry registry;
    private final Map<ObjectName, ObjectName> registeredMBeanNames;
    private boolean allowRemoteAddressInMBeanNames;
    private String brokerName;
    
    public ManagementContext() {
        this(null);
    }
    
    public ManagementContext(final MBeanServer server) {
        this.jmxDomainName = "org.apache.activemq";
        this.useMBeanServer = true;
        this.createMBeanServer = true;
        this.createConnector = true;
        this.findTigerMbeanServer = true;
        this.connectorHost = "localhost";
        this.connectorPort = 1099;
        this.connectorPath = "/jmxrmi";
        this.started = new AtomicBoolean(false);
        this.connectorStarting = new AtomicBoolean(false);
        this.registeredMBeanNames = new ConcurrentHashMap<ObjectName, ObjectName>();
        this.allowRemoteAddressInMBeanNames = true;
        this.beanServer = server;
    }
    
    @Override
    public void start() throws IOException {
        if (this.started.compareAndSet(false, true)) {
            if (this.connectorHost == null) {
                this.connectorHost = "localhost";
            }
            this.getMBeanServer();
            if (this.connectorServer != null) {
                try {
                    if (this.getMBeanServer().isRegistered(this.namingServiceObjectName)) {
                        ManagementContext.LOG.debug("Invoking start on mbean: {}", this.namingServiceObjectName);
                        this.getMBeanServer().invoke(this.namingServiceObjectName, "start", null, null);
                    }
                }
                catch (Throwable ignore) {
                    ManagementContext.LOG.debug("Error invoking start on MBean {}. This exception is ignored.", this.namingServiceObjectName, ignore);
                }
                final Thread t = new Thread("JMX connector") {
                    @Override
                    public void run() {
                        if (ManagementContext.this.brokerName != null) {
                            MDC.put("activemq.broker", ManagementContext.this.brokerName);
                        }
                        try {
                            final JMXConnectorServer server = ManagementContext.this.connectorServer;
                            if (ManagementContext.this.started.get() && server != null) {
                                ManagementContext.LOG.debug("Starting JMXConnectorServer...");
                                ManagementContext.this.connectorStarting.set(true);
                                try {
                                    MDC.remove("activemq.broker");
                                    server.start();
                                }
                                finally {
                                    if (ManagementContext.this.brokerName != null) {
                                        MDC.put("activemq.broker", ManagementContext.this.brokerName);
                                    }
                                    ManagementContext.this.connectorStarting.set(false);
                                }
                                ManagementContext.LOG.info("JMX consoles can connect to {}", server.getAddress());
                            }
                        }
                        catch (IOException e) {
                            ManagementContext.LOG.warn("Failed to start JMX connector {}. Will restart management to re-create JMX connector, trying to remedy this issue.", e.getMessage());
                            ManagementContext.LOG.debug("Reason for failed JMX connector start", e);
                        }
                        finally {
                            MDC.remove("activemq.broker");
                        }
                    }
                };
                t.setDaemon(true);
                t.start();
            }
        }
    }
    
    @Override
    public void stop() throws Exception {
        if (this.started.compareAndSet(true, false)) {
            final MBeanServer mbeanServer = this.getMBeanServer();
            if (mbeanServer != null) {
                for (final Map.Entry<ObjectName, ObjectName> entry : this.registeredMBeanNames.entrySet()) {
                    final ObjectName actualName = entry.getValue();
                    if (actualName != null && this.beanServer.isRegistered(actualName)) {
                        ManagementContext.LOG.debug("Unregistering MBean {}", actualName);
                        mbeanServer.unregisterMBean(actualName);
                    }
                }
            }
            this.registeredMBeanNames.clear();
            final JMXConnectorServer server = this.connectorServer;
            this.connectorServer = null;
            if (server != null) {
                try {
                    if (!this.connectorStarting.get()) {
                        ManagementContext.LOG.debug("Stopping jmx connector");
                        server.stop();
                    }
                }
                catch (IOException e) {
                    ManagementContext.LOG.warn("Failed to stop jmx connector: {}", e.getMessage());
                }
                try {
                    if (this.namingServiceObjectName != null && this.getMBeanServer().isRegistered(this.namingServiceObjectName)) {
                        ManagementContext.LOG.debug("Stopping MBean {}", this.namingServiceObjectName);
                        this.getMBeanServer().invoke(this.namingServiceObjectName, "stop", null, null);
                        ManagementContext.LOG.debug("Unregistering MBean {}", this.namingServiceObjectName);
                        this.getMBeanServer().unregisterMBean(this.namingServiceObjectName);
                    }
                }
                catch (Throwable ignore) {
                    ManagementContext.LOG.warn("Error stopping and unregsitering MBean {} due to {}", this.namingServiceObjectName, ignore.getMessage());
                }
                this.namingServiceObjectName = null;
            }
            if (this.locallyCreateMBeanServer && this.beanServer != null) {
                final List<MBeanServer> list = MBeanServerFactory.findMBeanServer(null);
                if (list != null && !list.isEmpty() && list.contains(this.beanServer)) {
                    ManagementContext.LOG.debug("Releasing MBeanServer {}", this.beanServer);
                    MBeanServerFactory.releaseMBeanServer(this.beanServer);
                }
            }
            this.beanServer = null;
        }
        if (this.registry != null) {
            try {
                UnicastRemoteObject.unexportObject(this.registry, true);
                ManagementContext.LOG.debug("Unexported JMX RMI Registry");
            }
            catch (NoSuchObjectException e2) {
                ManagementContext.LOG.debug("Error occurred while unexporting JMX RMI registry. This exception will be ignored.");
            }
            this.registry = null;
        }
    }
    
    public String getBrokerName() {
        return this.brokerName;
    }
    
    public void setBrokerName(final String brokerName) {
        this.brokerName = brokerName;
    }
    
    public String getJmxDomainName() {
        return this.jmxDomainName;
    }
    
    public void setJmxDomainName(final String jmxDomainName) {
        this.jmxDomainName = jmxDomainName;
    }
    
    protected MBeanServer getMBeanServer() {
        if (this.beanServer == null) {
            this.beanServer = this.findMBeanServer();
        }
        return this.beanServer;
    }
    
    public void setMBeanServer(final MBeanServer beanServer) {
        this.beanServer = beanServer;
    }
    
    public boolean isUseMBeanServer() {
        return this.useMBeanServer;
    }
    
    public void setUseMBeanServer(final boolean useMBeanServer) {
        this.useMBeanServer = useMBeanServer;
    }
    
    public boolean isCreateMBeanServer() {
        return this.createMBeanServer;
    }
    
    public void setCreateMBeanServer(final boolean enableJMX) {
        this.createMBeanServer = enableJMX;
    }
    
    public boolean isFindTigerMbeanServer() {
        return this.findTigerMbeanServer;
    }
    
    public boolean isConnectorStarted() {
        return this.connectorStarting.get() || (this.connectorServer != null && this.connectorServer.isActive());
    }
    
    public void setFindTigerMbeanServer(final boolean findTigerMbeanServer) {
        this.findTigerMbeanServer = findTigerMbeanServer;
    }
    
    public ObjectName createCustomComponentMBeanName(final String type, final String name) {
        ObjectName result = null;
        final String tmp = this.jmxDomainName + ":type=" + sanitizeString(type) + ",name=" + sanitizeString(name);
        try {
            result = new ObjectName(tmp);
        }
        catch (MalformedObjectNameException e) {
            ManagementContext.LOG.error("Couldn't create ObjectName from: {}, {}", type, name);
        }
        return result;
    }
    
    private static String sanitizeString(final String in) {
        String result = null;
        if (in != null) {
            result = in.replace(':', '_');
            result = result.replace('/', '_');
            result = result.replace('\\', '_');
        }
        return result;
    }
    
    public static ObjectName getSystemObjectName(final String domainName, final String containerName, final Class<?> theClass) throws MalformedObjectNameException, NullPointerException {
        final String tmp = domainName + ":type=" + theClass.getName() + ",name=" + getRelativeName(containerName, theClass);
        return new ObjectName(tmp);
    }
    
    private static String getRelativeName(final String containerName, final Class<?> theClass) {
        String name = theClass.getName();
        final int index = name.lastIndexOf(".");
        if (index >= 0 && index + 1 < name.length()) {
            name = name.substring(index + 1);
        }
        return containerName + "." + name;
    }
    
    public Object newProxyInstance(final ObjectName objectName, final Class<?> interfaceClass, final boolean notificationBroadcaster) {
        return MBeanServerInvocationHandler.newProxyInstance(this.getMBeanServer(), objectName, interfaceClass, notificationBroadcaster);
    }
    
    public Object getAttribute(final ObjectName name, final String attribute) throws Exception {
        return this.getMBeanServer().getAttribute(name, attribute);
    }
    
    public ObjectInstance registerMBean(final Object bean, final ObjectName name) throws Exception {
        final ObjectInstance result = this.getMBeanServer().registerMBean(bean, name);
        this.registeredMBeanNames.put(name, result.getObjectName());
        return result;
    }
    
    public Set<ObjectName> queryNames(final ObjectName name, final QueryExp query) throws Exception {
        if (name != null) {
            final ObjectName actualName = this.registeredMBeanNames.get(name);
            if (actualName != null) {
                return this.getMBeanServer().queryNames(actualName, query);
            }
        }
        return this.getMBeanServer().queryNames(name, query);
    }
    
    public ObjectInstance getObjectInstance(final ObjectName name) throws InstanceNotFoundException {
        return this.getMBeanServer().getObjectInstance(name);
    }
    
    public void unregisterMBean(final ObjectName name) throws JMException {
        final ObjectName actualName = this.registeredMBeanNames.get(name);
        if (this.beanServer != null && actualName != null && this.beanServer.isRegistered(actualName) && this.registeredMBeanNames.remove(name) != null) {
            ManagementContext.LOG.debug("Unregistering MBean {}", actualName);
            this.beanServer.unregisterMBean(actualName);
        }
    }
    
    protected synchronized MBeanServer findMBeanServer() {
        MBeanServer result = null;
        try {
            if (this.useMBeanServer) {
                if (this.findTigerMbeanServer) {
                    result = this.findTigerMBeanServer();
                }
                if (result == null) {
                    final List<MBeanServer> list = MBeanServerFactory.findMBeanServer(null);
                    if (list != null && list.size() > 0) {
                        result = list.get(0);
                    }
                }
            }
            if (result == null && this.createMBeanServer) {
                result = this.createMBeanServer();
            }
        }
        catch (NoClassDefFoundError e) {
            ManagementContext.LOG.error("Could not load MBeanServer", e);
        }
        catch (Throwable e2) {
            ManagementContext.LOG.error("Failed to initialize MBeanServer", e2);
        }
        return result;
    }
    
    public MBeanServer findTigerMBeanServer() {
        final String name = "java.lang.management.ManagementFactory";
        final Class<?> type = loadClass(name, ManagementContext.class.getClassLoader());
        if (type != null) {
            try {
                final Method method = type.getMethod("getPlatformMBeanServer", (Class<?>[])new Class[0]);
                if (method != null) {
                    final Object answer = method.invoke(null, new Object[0]);
                    if (answer instanceof MBeanServer) {
                        if (this.createConnector) {
                            this.createConnector((MBeanServer)answer);
                        }
                        return (MBeanServer)answer;
                    }
                    ManagementContext.LOG.warn("Could not cast: {} into an MBeanServer. There must be some classloader strangeness in town", answer);
                }
                else {
                    ManagementContext.LOG.warn("Method getPlatformMBeanServer() does not appear visible on type: {}", type.getName());
                }
            }
            catch (Exception e) {
                ManagementContext.LOG.warn("Failed to call getPlatformMBeanServer() due to: ", e);
            }
        }
        else {
            ManagementContext.LOG.trace("Class not found: {} so probably running on Java 1.4", name);
        }
        return null;
    }
    
    private static Class<?> loadClass(final String name, final ClassLoader loader) {
        try {
            return loader.loadClass(name);
        }
        catch (ClassNotFoundException e) {
            try {
                return Thread.currentThread().getContextClassLoader().loadClass(name);
            }
            catch (ClassNotFoundException e2) {
                return null;
            }
        }
    }
    
    protected MBeanServer createMBeanServer() throws MalformedObjectNameException, IOException {
        final MBeanServer mbeanServer = MBeanServerFactory.createMBeanServer(this.jmxDomainName);
        this.locallyCreateMBeanServer = true;
        if (this.createConnector) {
            this.createConnector(mbeanServer);
        }
        return mbeanServer;
    }
    
    private void createConnector(final MBeanServer mbeanServer) throws MalformedObjectNameException, IOException {
        try {
            if (this.registry == null) {
                ManagementContext.LOG.debug("Creating RMIRegistry on port {}", (Object)this.connectorPort);
                this.registry = LocateRegistry.createRegistry(this.connectorPort);
            }
            this.namingServiceObjectName = ObjectName.getInstance("naming:type=rmiregistry");
            final Class<?> cl = Class.forName("mx4j.tools.naming.NamingService");
            mbeanServer.registerMBean(cl.newInstance(), this.namingServiceObjectName);
            final Attribute attr = new Attribute("Port", this.connectorPort);
            mbeanServer.setAttribute(this.namingServiceObjectName, attr);
        }
        catch (ClassNotFoundException e) {
            ManagementContext.LOG.debug("Probably not using JRE 1.4: {}", e.getLocalizedMessage());
        }
        catch (Throwable e2) {
            ManagementContext.LOG.debug("Failed to create local registry. This exception will be ignored.", e2);
        }
        String rmiServer = "";
        if (this.rmiServerPort != 0) {
            rmiServer = "" + this.getConnectorHost() + ":" + this.rmiServerPort;
        }
        final String serviceURL = "service:jmx:rmi://" + rmiServer + "/jndi/rmi://" + this.getConnectorHost() + ":" + this.connectorPort + this.connectorPath;
        final JMXServiceURL url = new JMXServiceURL(serviceURL);
        this.connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, this.environment, mbeanServer);
        ManagementContext.LOG.debug("Created JMXConnectorServer {}", this.connectorServer);
    }
    
    public String getConnectorPath() {
        return this.connectorPath;
    }
    
    public void setConnectorPath(final String connectorPath) {
        this.connectorPath = connectorPath;
    }
    
    public int getConnectorPort() {
        return this.connectorPort;
    }
    
    public void setConnectorPort(final int connectorPort) {
        this.connectorPort = connectorPort;
    }
    
    public int getRmiServerPort() {
        return this.rmiServerPort;
    }
    
    public void setRmiServerPort(final int rmiServerPort) {
        this.rmiServerPort = rmiServerPort;
    }
    
    public boolean isCreateConnector() {
        return this.createConnector;
    }
    
    public void setCreateConnector(final boolean createConnector) {
        this.createConnector = createConnector;
    }
    
    public String getConnectorHost() {
        return this.connectorHost;
    }
    
    public void setConnectorHost(final String connectorHost) {
        this.connectorHost = connectorHost;
    }
    
    public Map<String, ?> getEnvironment() {
        return this.environment;
    }
    
    public void setEnvironment(final Map<String, ?> environment) {
        this.environment = environment;
    }
    
    public boolean isAllowRemoteAddressInMBeanNames() {
        return this.allowRemoteAddressInMBeanNames;
    }
    
    public void setAllowRemoteAddressInMBeanNames(final boolean allowRemoteAddressInMBeanNames) {
        this.allowRemoteAddressInMBeanNames = allowRemoteAddressInMBeanNames;
    }
    
    static {
        LOG = LoggerFactory.getLogger(ManagementContext.class);
    }
}
