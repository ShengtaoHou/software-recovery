// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated.groups;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.LoggerFactory;
import java.util.Collections;
import org.linkedin.util.io.PathUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Id;
import java.util.ArrayList;
import org.apache.zookeeper.data.ACL;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.linkedin.util.clock.SystemClock;
import org.linkedin.zookeeper.client.ZooKeeperFactory;
import java.util.Iterator;
import java.util.IdentityHashMap;
import org.linkedin.zookeeper.client.ChrootedZKClient;
import org.linkedin.zookeeper.client.IZKClient;
import org.linkedin.util.concurrent.ConcurrentUtils;
import java.util.concurrent.TimeoutException;
import org.apache.zookeeper.WatchedEvent;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.io.UnsupportedEncodingException;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.framework.InvalidSyntaxException;
import org.linkedin.util.clock.Timespan;
import org.linkedin.zookeeper.client.IZooKeeper;
import org.linkedin.zookeeper.client.IZooKeeperFactory;
import org.linkedin.zookeeper.client.LifecycleListener;
import java.util.List;
import org.linkedin.util.clock.Clock;
import java.util.Map;
import org.slf4j.Logger;
import org.apache.zookeeper.Watcher;
import org.linkedin.zookeeper.client.AbstractZKClient;

public class ZKClient extends AbstractZKClient implements Watcher
{
    private static final Logger LOG;
    private Map<String, String> acls;
    private String password;
    private static final String CHARSET = "UTF-8";
    private final Clock _clock;
    private final List<LifecycleListener> _listeners;
    protected final Object _lock;
    protected volatile State _state;
    private final StateChangeDispatcher _stateChangeDispatcher;
    protected IZooKeeperFactory _factory;
    protected IZooKeeper _zk;
    protected Timespan _reconnectTimeout;
    protected Timespan sessionTimeout;
    private ExpiredSessionRecovery _expiredSessionRecovery;
    
    public void start() throws Exception {
        synchronized (this._lock) {
            this._stateChangeDispatcher.setDaemon(true);
            this._stateChangeDispatcher.start();
            this.doStart();
        }
    }
    
    public void setACLs(final Map<String, String> acls) {
        this.acls = acls;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    protected void doStart() throws InvalidSyntaxException, ConfigurationException, UnsupportedEncodingException {
        this.connect();
    }
    
    public void close() {
        if (this._stateChangeDispatcher != null) {
            this._stateChangeDispatcher.end();
            try {
                this._stateChangeDispatcher.join(1000L);
            }
            catch (Exception e) {
                ZKClient.LOG.debug("ignored exception", e);
            }
        }
        synchronized (this._lock) {
            if (this._zk != null) {
                try {
                    this.changeState(State.NONE);
                    this._zk.close();
                    final Thread th = this.getSendThread();
                    if (th != null) {
                        th.join(1000L);
                    }
                    this._zk = null;
                }
                catch (Exception e2) {
                    ZKClient.LOG.debug("ignored exception", e2);
                }
            }
        }
    }
    
    protected Thread getSendThread() {
        try {
            return (Thread)this.getField(this._zk, "_zk", "cnxn", "sendThread");
        }
        catch (Throwable e) {
            return null;
        }
    }
    
    protected Object getField(Object obj, final String... names) throws Exception {
        for (final String name : names) {
            obj = this.getField(obj, name);
        }
        return obj;
    }
    
    protected Object getField(final Object obj, final String name) throws Exception {
        final Class clazz = obj.getClass();
        while (clazz != null) {
            for (final Field f : clazz.getDeclaredFields()) {
                if (f.getName().equals(name)) {
                    f.setAccessible(true);
                    return f.get(obj);
                }
            }
        }
        throw new NoSuchFieldError(name);
    }
    
    protected void changeState(final State newState) {
        synchronized (this._lock) {
            final State oldState = this._state;
            if (oldState != newState) {
                this._stateChangeDispatcher.addEvent(oldState, newState);
                this._state = newState;
                this._lock.notifyAll();
            }
        }
    }
    
    public void testGenerateConnectionLoss() throws Exception {
        this.waitForConnected();
        final Object clientCnxnSocket = this.getField(this._zk, "_zk", "cnxn", "sendThread", "clientCnxnSocket");
        this.callMethod(clientCnxnSocket, "testableCloseSocket", new Object[0]);
    }
    
    protected Object callMethod(final Object obj, final String name, final Object... args) throws Exception {
        final Class clazz = obj.getClass();
        while (clazz != null) {
            for (final Method m : clazz.getDeclaredMethods()) {
                if (m.getName().equals(name)) {
                    m.setAccessible(true);
                    return m.invoke(obj, args);
                }
            }
        }
        throw new NoSuchMethodError(name);
    }
    
    protected void tryConnect() {
        synchronized (this._lock) {
            try {
                this.connect();
            }
            catch (Throwable e) {
                ZKClient.LOG.warn("Error while restarting:", e);
                if (this._expiredSessionRecovery == null) {
                    (this._expiredSessionRecovery = new ExpiredSessionRecovery()).setDaemon(true);
                    this._expiredSessionRecovery.start();
                }
            }
        }
    }
    
    public void connect() throws UnsupportedEncodingException {
        synchronized (this._lock) {
            this.changeState(State.CONNECTING);
            this._zk = this._factory.createZooKeeper((Watcher)this);
            if (this.password != null) {
                this._zk.addAuthInfo("digest", ("fabric:" + this.password).getBytes("UTF-8"));
            }
        }
    }
    
    public void process(final WatchedEvent event) {
        if (event.getState() != null) {
            ZKClient.LOG.debug("event: {}", event.getState());
            synchronized (this._lock) {
                switch (event.getState()) {
                    case SyncConnected: {
                        this.changeState(State.CONNECTED);
                        break;
                    }
                    case Disconnected: {
                        if (this._state != State.NONE) {
                            this.changeState(State.RECONNECTING);
                            break;
                        }
                        break;
                    }
                    case Expired: {
                        this._zk = null;
                        ZKClient.LOG.warn("Expiration detected: trying to restart...");
                        this.tryConnect();
                        break;
                    }
                    default: {
                        ZKClient.LOG.warn("unprocessed event state: {}", event.getState());
                        break;
                    }
                }
            }
        }
    }
    
    protected IZooKeeper getZk() {
        final State state = this._state;
        if (state == State.NONE) {
            throw new IllegalStateException("ZooKeeper client has not been configured yet. You need to either create an ensemble or join one.");
        }
        if (state != State.CONNECTED) {
            try {
                this.waitForConnected();
            }
            catch (Exception e) {
                throw new IllegalStateException("Error waiting for ZooKeeper connection", e);
            }
        }
        final IZooKeeper zk = this._zk;
        if (zk == null) {
            throw new IllegalStateException("No ZooKeeper connection available");
        }
        return zk;
    }
    
    public void waitForConnected(final Timespan timeout) throws InterruptedException, TimeoutException {
        this.waitForState(State.CONNECTED, timeout);
    }
    
    public void waitForConnected() throws InterruptedException, TimeoutException {
        this.waitForConnected(null);
    }
    
    public void waitForState(final State state, final Timespan timeout) throws TimeoutException, InterruptedException {
        final long endTime = ((timeout == null) ? this.sessionTimeout : timeout).futureTimeMillis(this._clock);
        if (this._state != state) {
            synchronized (this._lock) {
                while (this._state != state) {
                    ConcurrentUtils.awaitUntil(this._clock, this._lock, endTime);
                }
            }
        }
    }
    
    public void registerListener(final LifecycleListener listener) {
        if (listener == null) {
            throw new IllegalStateException("listener is null");
        }
        if (!this._listeners.contains(listener)) {
            this._listeners.add(listener);
        }
        if (this._state == State.CONNECTED) {
            listener.onConnected();
        }
    }
    
    public void removeListener(final LifecycleListener listener) {
        if (listener == null) {
            throw new IllegalStateException("listener is null");
        }
        this._listeners.remove(listener);
    }
    
    public IZKClient chroot(final String path) {
        return (IZKClient)new ChrootedZKClient((IZKClient)this, this.adjustPath(path));
    }
    
    public boolean isConnected() {
        return this._state == State.CONNECTED;
    }
    
    public boolean isConfigured() {
        return this._state != State.NONE;
    }
    
    public String getConnectString() {
        return this._factory.getConnectString();
    }
    
    protected Map<Object, Boolean> callListeners(final Map<Object, Boolean> history, final Boolean connectedEvent) {
        final Map<Object, Boolean> newHistory = new IdentityHashMap<Object, Boolean>();
        for (final LifecycleListener listener : this._listeners) {
            final Boolean previousEvent = history.get(listener);
            Label_0106: {
                if (previousEvent != null) {
                    if (previousEvent == connectedEvent) {
                        break Label_0106;
                    }
                }
                try {
                    if (connectedEvent) {
                        listener.onConnected();
                    }
                    else {
                        listener.onDisconnected();
                    }
                }
                catch (Throwable e) {
                    ZKClient.LOG.warn("Exception while executing listener (ignored)", e);
                }
            }
            newHistory.put(listener, connectedEvent);
        }
        return newHistory;
    }
    
    public ZKClient(final String connectString, final Timespan sessionTimeout, final Watcher watcher) {
        this((IZooKeeperFactory)new ZooKeeperFactory(connectString, sessionTimeout, watcher));
    }
    
    public ZKClient(final IZooKeeperFactory factory) {
        this(factory, null);
    }
    
    public ZKClient(final IZooKeeperFactory factory, final String chroot) {
        super(chroot);
        this._clock = (Clock)SystemClock.instance();
        this._listeners = new CopyOnWriteArrayList<LifecycleListener>();
        this._lock = new Object();
        this._state = State.NONE;
        this._stateChangeDispatcher = new StateChangeDispatcher();
        this._reconnectTimeout = Timespan.parse("20s");
        this.sessionTimeout = new Timespan(30L, Timespan.TimeUnit.SECOND);
        this._expiredSessionRecovery = null;
        this._factory = factory;
        final Map<String, String> acls = new HashMap<String, String>();
        acls.put("/", "world:anyone:acdrw");
        this.setACLs(acls);
    }
    
    private static int getPermFromString(final String permString) {
        int perm = 0;
        for (int i = 0; i < permString.length(); ++i) {
            switch (permString.charAt(i)) {
                case 'r': {
                    perm |= 0x1;
                    break;
                }
                case 'w': {
                    perm |= 0x2;
                    break;
                }
                case 'c': {
                    perm |= 0x4;
                    break;
                }
                case 'd': {
                    perm |= 0x8;
                    break;
                }
                case 'a': {
                    perm |= 0x10;
                    break;
                }
                default: {
                    System.err.println("Unknown perm type: " + permString.charAt(i));
                    break;
                }
            }
        }
        return perm;
    }
    
    private static List<ACL> parseACLs(final String aclString) {
        final String[] acls = aclString.split(",");
        final List<ACL> acl = new ArrayList<ACL>();
        for (final String a : acls) {
            final int firstColon = a.indexOf(58);
            final int lastColon = a.lastIndexOf(58);
            if (firstColon == -1 || lastColon == -1 || firstColon == lastColon) {
                System.err.println(a + " does not have the form scheme:id:perm");
            }
            else {
                final ACL newAcl = new ACL();
                newAcl.setId(new Id(a.substring(0, firstColon), a.substring(firstColon + 1, lastColon)));
                newAcl.setPerms(getPermFromString(a.substring(lastColon + 1)));
                acl.add(newAcl);
            }
        }
        return acl;
    }
    
    public Stat createOrSetByteWithParents(final String path, final byte[] data, final List<ACL> acl, final CreateMode createMode) throws InterruptedException, KeeperException {
        if (this.exists(path) != null) {
            return this.setByteData(path, data);
        }
        try {
            this.createBytesNodeWithParents(path, data, (List)acl, createMode);
            return null;
        }
        catch (KeeperException.NodeExistsException e) {
            return this.setByteData(path, data);
        }
    }
    
    public String create(final String path, final CreateMode createMode) throws InterruptedException, KeeperException {
        return this.create(path, (byte[])null, createMode);
    }
    
    public String create(final String path, final String data, final CreateMode createMode) throws InterruptedException, KeeperException {
        return this.create(path, this.toByteData(data), createMode);
    }
    
    public String create(final String path, final byte[] data, final CreateMode createMode) throws InterruptedException, KeeperException {
        return this.getZk().create(this.adjustPath(path), data, (List)this.getNodeACLs(path), createMode);
    }
    
    public String createWithParents(final String path, final CreateMode createMode) throws InterruptedException, KeeperException {
        return this.createWithParents(path, (byte[])null, createMode);
    }
    
    public String createWithParents(final String path, final String data, final CreateMode createMode) throws InterruptedException, KeeperException {
        return this.createWithParents(path, this.toByteData(data), createMode);
    }
    
    public String createWithParents(final String path, final byte[] data, final CreateMode createMode) throws InterruptedException, KeeperException {
        this.createParents(path);
        return this.create(path, data, createMode);
    }
    
    public Stat createOrSetWithParents(final String path, final String data, final CreateMode createMode) throws InterruptedException, KeeperException {
        return this.createOrSetWithParents(path, this.toByteData(data), createMode);
    }
    
    public Stat createOrSetWithParents(final String path, final byte[] data, final CreateMode createMode) throws InterruptedException, KeeperException {
        if (this.exists(path) != null) {
            return this.setByteData(path, data);
        }
        try {
            this.createWithParents(path, data, createMode);
            return null;
        }
        catch (KeeperException.NodeExistsException e) {
            return this.setByteData(path, data);
        }
    }
    
    public void fixACLs(final String path, final boolean recursive) throws InterruptedException, KeeperException {
        if (this.exists(path) != null) {
            this.doFixACLs(path, recursive);
        }
    }
    
    private void doFixACLs(final String path, final boolean recursive) throws KeeperException, InterruptedException {
        this.setACL(path, (List)this.getNodeACLs(path), -1);
        if (recursive) {
            for (final String child : this.getChildren(path)) {
                this.doFixACLs(path.equals("/") ? ("/" + child) : (path + "/" + child), recursive);
            }
        }
    }
    
    private List<ACL> getNodeACLs(final String path) {
        final String acl = this.doGetNodeACLs(this.adjustPath(path));
        if (acl == null) {
            throw new IllegalStateException("Could not find matching ACLs for " + path);
        }
        return parseACLs(acl);
    }
    
    protected String doGetNodeACLs(final String path) {
        String longestPath = "";
        for (final String acl : this.acls.keySet()) {
            if (acl.length() > longestPath.length() && path.startsWith(acl)) {
                longestPath = acl;
            }
        }
        return this.acls.get(longestPath);
    }
    
    private void createParents(String path) throws InterruptedException, KeeperException {
        path = PathUtils.getParentPath(this.adjustPath(path));
        path = PathUtils.removeTrailingSlash(path);
        final List<String> paths = new ArrayList<String>();
        while (!path.equals("") && this.getZk().exists(path, false) == null) {
            paths.add(path);
            path = PathUtils.getParentPath(path);
            path = PathUtils.removeTrailingSlash(path);
        }
        Collections.reverse(paths);
        for (final String p : paths) {
            try {
                this.getZk().create(p, (byte[])null, (List)this.getNodeACLs(p), CreateMode.PERSISTENT);
            }
            catch (KeeperException.NodeExistsException e) {
                if (!ZKClient.LOG.isDebugEnabled()) {
                    continue;
                }
                ZKClient.LOG.debug("parent already exists " + p);
            }
        }
    }
    
    private byte[] toByteData(final String data) {
        if (data == null) {
            return null;
        }
        try {
            return data.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(ZKClient.class.getName());
    }
    
    public enum State
    {
        NONE, 
        CONNECTING, 
        CONNECTED, 
        RECONNECTING;
    }
    
    private class StateChangeDispatcher extends Thread
    {
        private final AtomicBoolean _running;
        private final BlockingQueue<Boolean> _events;
        
        private StateChangeDispatcher() {
            super("ZooKeeper state change dispatcher thread");
            this._running = new AtomicBoolean(true);
            this._events = new LinkedBlockingQueue<Boolean>();
        }
        
        @Override
        public void run() {
            Map<Object, Boolean> history = new IdentityHashMap<Object, Boolean>();
            ZKClient.LOG.info("Starting StateChangeDispatcher");
            while (this._running.get()) {
                Boolean isConnectedEvent;
                try {
                    isConnectedEvent = this._events.take();
                }
                catch (InterruptedException e) {
                    continue;
                }
                if (this._running.get()) {
                    if (isConnectedEvent == null) {
                        continue;
                    }
                    final Map<Object, Boolean> newHistory = history = ZKClient.this.callListeners(history, isConnectedEvent);
                }
            }
            ZKClient.LOG.info("StateChangeDispatcher terminated.");
        }
        
        public void end() {
            this._running.set(false);
            this._events.add(false);
        }
        
        public void addEvent(final ZKClient.State oldState, final ZKClient.State newState) {
            ZKClient.LOG.debug("addEvent: {} => {}", oldState, newState);
            if (newState == ZKClient.State.CONNECTED) {
                this._events.add(true);
            }
            else if (oldState == ZKClient.State.CONNECTED) {
                this._events.add(false);
            }
        }
    }
    
    private class ExpiredSessionRecovery extends Thread
    {
        private ExpiredSessionRecovery() {
            super("ZooKeeper expired session recovery thread");
        }
        
        @Override
        public void run() {
            ZKClient.LOG.info("Entering recovery mode");
            synchronized (ZKClient.this._lock) {
                try {
                    int count = 0;
                    while (ZKClient.this._state == ZKClient.State.NONE) {
                        try {
                            ++count;
                            ZKClient.LOG.warn("Recovery mode: trying to reconnect to zookeeper [" + count + "]");
                            ZKClient.this.connect();
                        }
                        catch (Throwable e) {
                            ZKClient.LOG.warn("Recovery mode: reconnect attempt failed [" + count + "]... waiting for " + ZKClient.this._reconnectTimeout, e);
                            try {
                                ZKClient.this._lock.wait(ZKClient.this._reconnectTimeout.getDurationInMilliseconds());
                            }
                            catch (InterruptedException e2) {
                                throw new RuntimeException("Recovery mode: wait interrupted... bailing out", e2);
                            }
                        }
                    }
                }
                finally {
                    ZKClient.this._expiredSessionRecovery = null;
                    ZKClient.LOG.info("Exiting recovery mode.");
                }
            }
        }
    }
}
