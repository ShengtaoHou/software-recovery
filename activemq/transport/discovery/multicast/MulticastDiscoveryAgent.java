// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.discovery.multicast;

import org.slf4j.LoggerFactory;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.command.DiscoveryEvent;
import java.util.Iterator;
import java.net.SocketTimeoutException;
import java.net.DatagramPacket;
import org.apache.activemq.util.ThreadPoolUtils;
import java.net.NetworkInterface;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.net.MulticastSocket;
import org.apache.activemq.transport.discovery.DiscoveryListener;
import java.net.SocketAddress;
import java.net.InetAddress;
import java.net.URI;
import java.util.Map;
import org.slf4j.Logger;
import org.apache.activemq.transport.discovery.DiscoveryAgent;

public class MulticastDiscoveryAgent implements DiscoveryAgent, Runnable
{
    public static final String DEFAULT_DISCOVERY_URI_STRING = "multicast://239.255.2.3:6155";
    public static final String DEFAULT_HOST_STR = "default";
    public static final String DEFAULT_HOST_IP;
    public static final int DEFAULT_PORT = 6155;
    private static final Logger LOG;
    private static final String TYPE_SUFFIX = "ActiveMQ-4.";
    private static final String ALIVE = "alive.";
    private static final String DEAD = "dead.";
    private static final String DELIMITER = "%";
    private static final int BUFF_SIZE = 8192;
    private static final int DEFAULT_IDLE_TIME = 500;
    private static final int HEARTBEAT_MISS_BEFORE_DEATH = 10;
    private long initialReconnectDelay;
    private long maxReconnectDelay;
    private long backOffMultiplier;
    private boolean useExponentialBackOff;
    private int maxReconnectAttempts;
    private int timeToLive;
    private boolean loopBackMode;
    private Map<String, RemoteBrokerData> brokersByService;
    private String group;
    private URI discoveryURI;
    private InetAddress inetAddress;
    private SocketAddress sockAddress;
    private DiscoveryListener discoveryListener;
    private String selfService;
    private MulticastSocket mcast;
    private Thread runner;
    private long keepAliveInterval;
    private String mcInterface;
    private String mcNetworkInterface;
    private String mcJoinNetworkInterface;
    private long lastAdvertizeTime;
    private AtomicBoolean started;
    private boolean reportAdvertizeFailed;
    private ExecutorService executor;
    
    public MulticastDiscoveryAgent() {
        this.initialReconnectDelay = 5000L;
        this.maxReconnectDelay = 30000L;
        this.backOffMultiplier = 2L;
        this.timeToLive = 1;
        this.brokersByService = new ConcurrentHashMap<String, RemoteBrokerData>();
        this.group = "default";
        this.keepAliveInterval = 500L;
        this.started = new AtomicBoolean(false);
        this.reportAdvertizeFailed = true;
        this.executor = null;
    }
    
    @Override
    public void setDiscoveryListener(final DiscoveryListener listener) {
        this.discoveryListener = listener;
    }
    
    @Override
    public void registerService(final String name) throws IOException {
        this.selfService = name;
        if (this.started.get()) {
            this.doAdvertizeSelf();
        }
    }
    
    public boolean isLoopBackMode() {
        return this.loopBackMode;
    }
    
    public void setLoopBackMode(final boolean loopBackMode) {
        this.loopBackMode = loopBackMode;
    }
    
    public int getTimeToLive() {
        return this.timeToLive;
    }
    
    public void setTimeToLive(final int timeToLive) {
        this.timeToLive = timeToLive;
    }
    
    public URI getDiscoveryURI() {
        return this.discoveryURI;
    }
    
    public void setDiscoveryURI(final URI discoveryURI) {
        this.discoveryURI = discoveryURI;
    }
    
    public long getKeepAliveInterval() {
        return this.keepAliveInterval;
    }
    
    public void setKeepAliveInterval(final long keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
    }
    
    public void setInterface(final String mcInterface) {
        this.mcInterface = mcInterface;
    }
    
    public void setNetworkInterface(final String mcNetworkInterface) {
        this.mcNetworkInterface = mcNetworkInterface;
    }
    
    public void setJoinNetworkInterface(final String mcJoinNetwrokInterface) {
        this.mcJoinNetworkInterface = mcJoinNetwrokInterface;
    }
    
    @Override
    public void start() throws Exception {
        if (this.started.compareAndSet(false, true)) {
            if (this.group == null || this.group.length() == 0) {
                throw new IOException("You must specify a group to discover");
            }
            String type = this.getType();
            if (!type.endsWith(".")) {
                MulticastDiscoveryAgent.LOG.warn("The type '" + type + "' should end with '.' to be a valid Discovery type");
                type += ".";
            }
            if (this.discoveryURI == null) {
                this.discoveryURI = new URI("multicast://239.255.2.3:6155");
            }
            if (MulticastDiscoveryAgent.LOG.isTraceEnabled()) {
                MulticastDiscoveryAgent.LOG.trace("start - discoveryURI = " + this.discoveryURI);
            }
            String myHost = this.discoveryURI.getHost();
            int myPort = this.discoveryURI.getPort();
            if ("default".equals(myHost)) {
                myHost = MulticastDiscoveryAgent.DEFAULT_HOST_IP;
            }
            if (myPort < 0) {
                myPort = 6155;
            }
            if (MulticastDiscoveryAgent.LOG.isTraceEnabled()) {
                MulticastDiscoveryAgent.LOG.trace("start - myHost = " + myHost);
                MulticastDiscoveryAgent.LOG.trace("start - myPort = " + myPort);
                MulticastDiscoveryAgent.LOG.trace("start - group  = " + this.group);
                MulticastDiscoveryAgent.LOG.trace("start - interface  = " + this.mcInterface);
                MulticastDiscoveryAgent.LOG.trace("start - network interface  = " + this.mcNetworkInterface);
                MulticastDiscoveryAgent.LOG.trace("start - join network interface  = " + this.mcJoinNetworkInterface);
            }
            this.inetAddress = InetAddress.getByName(myHost);
            this.sockAddress = new InetSocketAddress(this.inetAddress, myPort);
            (this.mcast = new MulticastSocket(myPort)).setLoopbackMode(this.loopBackMode);
            this.mcast.setTimeToLive(this.getTimeToLive());
            if (this.mcJoinNetworkInterface != null) {
                this.mcast.joinGroup(this.sockAddress, NetworkInterface.getByName(this.mcJoinNetworkInterface));
            }
            else {
                this.mcast.joinGroup(this.inetAddress);
            }
            this.mcast.setSoTimeout((int)this.keepAliveInterval);
            if (this.mcInterface != null) {
                this.mcast.setInterface(InetAddress.getByName(this.mcInterface));
            }
            if (this.mcNetworkInterface != null) {
                this.mcast.setNetworkInterface(NetworkInterface.getByName(this.mcNetworkInterface));
            }
            (this.runner = new Thread(this)).setName(this.toString() + ":" + this.runner.getName());
            this.runner.setDaemon(true);
            this.runner.start();
            this.doAdvertizeSelf();
        }
    }
    
    @Override
    public void stop() throws Exception {
        if (this.started.compareAndSet(true, false)) {
            this.doAdvertizeSelf();
            if (this.mcast != null) {
                this.mcast.close();
            }
            if (this.runner != null) {
                this.runner.interrupt();
            }
            if (this.executor != null) {
                ThreadPoolUtils.shutdownNow(this.executor);
                this.executor = null;
            }
        }
    }
    
    public String getType() {
        return this.group + "." + "ActiveMQ-4.";
    }
    
    @Override
    public void run() {
        final byte[] buf = new byte[8192];
        final DatagramPacket packet = new DatagramPacket(buf, 0, buf.length);
        while (this.started.get()) {
            this.doTimeKeepingServices();
            try {
                this.mcast.receive(packet);
                if (packet.getLength() <= 0) {
                    continue;
                }
                final String str = new String(packet.getData(), packet.getOffset(), packet.getLength());
                this.processData(str);
            }
            catch (SocketTimeoutException ex) {}
            catch (IOException e) {
                if (!this.started.get()) {
                    continue;
                }
                MulticastDiscoveryAgent.LOG.error("failed to process packet: " + e);
            }
        }
    }
    
    private void processData(final String str) {
        if (this.discoveryListener != null && str.startsWith(this.getType())) {
            final String payload = str.substring(this.getType().length());
            if (payload.startsWith("alive.")) {
                final String brokerName = this.getBrokerName(payload.substring("alive.".length()));
                final String service = payload.substring("alive.".length() + brokerName.length() + 2);
                this.processAlive(brokerName, service);
            }
            else {
                final String brokerName = this.getBrokerName(payload.substring("dead.".length()));
                final String service = payload.substring("dead.".length() + brokerName.length() + 2);
                this.processDead(service);
            }
        }
    }
    
    private void doTimeKeepingServices() {
        if (this.started.get()) {
            final long currentTime = System.currentTimeMillis();
            if (currentTime < this.lastAdvertizeTime || currentTime - this.keepAliveInterval > this.lastAdvertizeTime) {
                this.doAdvertizeSelf();
                this.lastAdvertizeTime = currentTime;
            }
            this.doExpireOldServices();
        }
    }
    
    private void doAdvertizeSelf() {
        if (this.selfService != null) {
            String payload = this.getType();
            payload += (this.started.get() ? "alive." : "dead.");
            payload += "%localhost%";
            payload += this.selfService;
            try {
                final byte[] data = payload.getBytes();
                final DatagramPacket packet = new DatagramPacket(data, 0, data.length, this.sockAddress);
                this.mcast.send(packet);
            }
            catch (IOException e) {
                if (this.reportAdvertizeFailed) {
                    this.reportAdvertizeFailed = false;
                    MulticastDiscoveryAgent.LOG.error("Failed to advertise our service: " + payload, e);
                    if ("Operation not permitted".equals(e.getMessage())) {
                        MulticastDiscoveryAgent.LOG.error("The 'Operation not permitted' error has been know to be caused by improper firewall/network setup.  Please make sure that the OS is properly configured to allow multicast traffic over: " + this.mcast.getLocalAddress());
                    }
                }
            }
        }
    }
    
    private void processAlive(final String brokerName, final String service) {
        if (this.selfService == null || !service.equals(this.selfService)) {
            RemoteBrokerData data = this.brokersByService.get(service);
            if (data == null) {
                data = new RemoteBrokerData(brokerName, service);
                this.brokersByService.put(service, data);
                this.fireServiceAddEvent(data);
                this.doAdvertizeSelf();
            }
            else {
                data.updateHeartBeat();
                if (data.doRecovery()) {
                    this.fireServiceAddEvent(data);
                }
            }
        }
    }
    
    private void processDead(final String service) {
        if (!service.equals(this.selfService)) {
            final RemoteBrokerData data = this.brokersByService.remove(service);
            if (data != null && !data.isFailed()) {
                this.fireServiceRemovedEvent(data);
            }
        }
    }
    
    private void doExpireOldServices() {
        final long expireTime = System.currentTimeMillis() - this.keepAliveInterval * 10L;
        for (final RemoteBrokerData data : this.brokersByService.values()) {
            if (data.getLastHeartBeat() < expireTime) {
                this.processDead(data.getServiceName());
            }
        }
    }
    
    private String getBrokerName(final String str) {
        String result = null;
        final int start = str.indexOf("%");
        if (start >= 0) {
            final int end = str.indexOf("%", start + 1);
            result = str.substring(start + 1, end);
        }
        return result;
    }
    
    @Override
    public void serviceFailed(final DiscoveryEvent event) throws IOException {
        final RemoteBrokerData data = this.brokersByService.get(event.getServiceName());
        if (data != null && data.markFailed()) {
            this.fireServiceRemovedEvent(data);
        }
    }
    
    private void fireServiceRemovedEvent(final RemoteBrokerData data) {
        if (this.discoveryListener != null && this.started.get()) {
            this.getExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    final DiscoveryListener discoveryListener = MulticastDiscoveryAgent.this.discoveryListener;
                    if (discoveryListener != null) {
                        discoveryListener.onServiceRemove(data);
                    }
                }
            });
        }
    }
    
    private void fireServiceAddEvent(final RemoteBrokerData data) {
        if (this.discoveryListener != null && this.started.get()) {
            this.getExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    final DiscoveryListener discoveryListener = MulticastDiscoveryAgent.this.discoveryListener;
                    if (discoveryListener != null) {
                        discoveryListener.onServiceAdd(data);
                    }
                }
            });
        }
    }
    
    private ExecutorService getExecutor() {
        if (this.executor == null) {
            final String threadName = "Notifier-" + this.toString();
            this.executor = new ThreadPoolExecutor(1, 1, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
                @Override
                public Thread newThread(final Runnable runable) {
                    final Thread t = new Thread(runable, threadName);
                    t.setDaemon(true);
                    return t;
                }
            });
        }
        return this.executor;
    }
    
    public long getBackOffMultiplier() {
        return this.backOffMultiplier;
    }
    
    public void setBackOffMultiplier(final long backOffMultiplier) {
        this.backOffMultiplier = backOffMultiplier;
    }
    
    public long getInitialReconnectDelay() {
        return this.initialReconnectDelay;
    }
    
    public void setInitialReconnectDelay(final long initialReconnectDelay) {
        this.initialReconnectDelay = initialReconnectDelay;
    }
    
    public int getMaxReconnectAttempts() {
        return this.maxReconnectAttempts;
    }
    
    public void setMaxReconnectAttempts(final int maxReconnectAttempts) {
        this.maxReconnectAttempts = maxReconnectAttempts;
    }
    
    public long getMaxReconnectDelay() {
        return this.maxReconnectDelay;
    }
    
    public void setMaxReconnectDelay(final long maxReconnectDelay) {
        this.maxReconnectDelay = maxReconnectDelay;
    }
    
    public boolean isUseExponentialBackOff() {
        return this.useExponentialBackOff;
    }
    
    public void setUseExponentialBackOff(final boolean useExponentialBackOff) {
        this.useExponentialBackOff = useExponentialBackOff;
    }
    
    public void setGroup(final String group) {
        this.group = group;
    }
    
    @Override
    public String toString() {
        return "MulticastDiscoveryAgent-" + ((this.selfService != null) ? ("advertise:" + this.selfService) : ("listener:" + this.discoveryListener));
    }
    
    static {
        DEFAULT_HOST_IP = System.getProperty("activemq.partition.discovery", "239.255.2.3");
        LOG = LoggerFactory.getLogger(MulticastDiscoveryAgent.class);
    }
    
    class RemoteBrokerData extends DiscoveryEvent
    {
        long lastHeartBeat;
        long recoveryTime;
        int failureCount;
        boolean failed;
        
        public RemoteBrokerData(final String brokerName, final String service) {
            super(service);
            this.setBrokerName(brokerName);
            this.lastHeartBeat = System.currentTimeMillis();
        }
        
        public synchronized void updateHeartBeat() {
            this.lastHeartBeat = System.currentTimeMillis();
            if (!this.failed && this.failureCount > 0 && this.lastHeartBeat - this.recoveryTime > 60000L) {
                if (MulticastDiscoveryAgent.LOG.isDebugEnabled()) {
                    MulticastDiscoveryAgent.LOG.debug("I now think that the " + this.serviceName + " service has recovered.");
                }
                this.failureCount = 0;
                this.recoveryTime = 0L;
            }
        }
        
        public synchronized long getLastHeartBeat() {
            return this.lastHeartBeat;
        }
        
        public synchronized boolean markFailed() {
            if (!this.failed) {
                this.failed = true;
                ++this.failureCount;
                long reconnectDelay;
                if (!MulticastDiscoveryAgent.this.useExponentialBackOff) {
                    reconnectDelay = MulticastDiscoveryAgent.this.initialReconnectDelay;
                }
                else {
                    reconnectDelay = (long)Math.pow((double)MulticastDiscoveryAgent.this.backOffMultiplier, this.failureCount);
                    if (reconnectDelay > MulticastDiscoveryAgent.this.maxReconnectDelay) {
                        reconnectDelay = MulticastDiscoveryAgent.this.maxReconnectDelay;
                    }
                }
                if (MulticastDiscoveryAgent.LOG.isDebugEnabled()) {
                    MulticastDiscoveryAgent.LOG.debug("Remote failure of " + this.serviceName + " while still receiving multicast advertisements.  Advertising events will be suppressed for " + reconnectDelay + " ms, the current failure count is: " + this.failureCount);
                }
                this.recoveryTime = System.currentTimeMillis() + reconnectDelay;
                return true;
            }
            return false;
        }
        
        public synchronized boolean doRecovery() {
            if (!this.failed) {
                return false;
            }
            if (MulticastDiscoveryAgent.this.maxReconnectAttempts > 0 && this.failureCount > MulticastDiscoveryAgent.this.maxReconnectAttempts) {
                if (MulticastDiscoveryAgent.LOG.isDebugEnabled()) {
                    MulticastDiscoveryAgent.LOG.debug("Max reconnect attempts of the " + this.serviceName + " service has been reached.");
                }
                return false;
            }
            if (System.currentTimeMillis() < this.recoveryTime) {
                return false;
            }
            if (MulticastDiscoveryAgent.LOG.isDebugEnabled()) {
                MulticastDiscoveryAgent.LOG.debug("Resuming event advertisement of the " + this.serviceName + " service.");
            }
            this.failed = false;
            return true;
        }
        
        public boolean isFailed() {
            return this.failed;
        }
    }
}
