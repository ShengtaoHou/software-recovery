// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.nio;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.LinkedList;
import java.util.concurrent.Executor;

public final class SelectorManager
{
    public static final SelectorManager SINGLETON;
    private Executor selectorExecutor;
    private Executor channelExecutor;
    private final LinkedList<SelectorWorker> freeWorkers;
    private int maxChannelsPerWorker;
    
    public SelectorManager() {
        this.selectorExecutor = this.createDefaultExecutor();
        this.channelExecutor = this.selectorExecutor;
        this.freeWorkers = new LinkedList<SelectorWorker>();
        this.maxChannelsPerWorker = 1024;
    }
    
    protected ExecutorService createDefaultExecutor() {
        final ThreadPoolExecutor rc = new ThreadPoolExecutor(0, Integer.MAX_VALUE, getDefaultKeepAliveTime(), TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadFactory() {
            private long i = 0L;
            
            @Override
            public Thread newThread(final Runnable runnable) {
                ++this.i;
                final Thread t = new Thread(runnable, "ActiveMQ NIO Worker " + this.i);
                return t;
            }
        });
        return rc;
    }
    
    private static int getDefaultKeepAliveTime() {
        return Integer.getInteger("org.apache.activemq.transport.nio.SelectorManager.keepAliveTime", 30);
    }
    
    public static SelectorManager getInstance() {
        return SelectorManager.SINGLETON;
    }
    
    public synchronized SelectorSelection register(final SocketChannel socketChannel, final Listener listener) throws IOException {
        SelectorSelection selection = null;
        while (selection == null) {
            if (this.freeWorkers.size() > 0) {
                final SelectorWorker worker = this.freeWorkers.getFirst();
                if (worker.isReleased()) {
                    this.freeWorkers.remove(worker);
                }
                else {
                    worker.retain();
                    selection = new SelectorSelection(worker, socketChannel, listener);
                }
            }
            else {
                final SelectorWorker worker = new SelectorWorker(this);
                this.freeWorkers.addFirst(worker);
                selection = new SelectorSelection(worker, socketChannel, listener);
            }
        }
        return selection;
    }
    
    synchronized void onWorkerFullEvent(final SelectorWorker worker) {
        this.freeWorkers.remove(worker);
    }
    
    public synchronized void onWorkerEmptyEvent(final SelectorWorker worker) {
        this.freeWorkers.remove(worker);
    }
    
    public synchronized void onWorkerNotFullEvent(final SelectorWorker worker) {
        this.freeWorkers.addFirst(worker);
    }
    
    public Executor getChannelExecutor() {
        return this.channelExecutor;
    }
    
    public void setChannelExecutor(final Executor channelExecutor) {
        this.channelExecutor = channelExecutor;
    }
    
    public int getMaxChannelsPerWorker() {
        return this.maxChannelsPerWorker;
    }
    
    public void setMaxChannelsPerWorker(final int maxChannelsPerWorker) {
        this.maxChannelsPerWorker = maxChannelsPerWorker;
    }
    
    public Executor getSelectorExecutor() {
        return this.selectorExecutor;
    }
    
    public void setSelectorExecutor(final Executor selectorExecutor) {
        this.selectorExecutor = selectorExecutor;
    }
    
    static {
        SINGLETON = new SelectorManager();
    }
    
    public interface Listener
    {
        void onSelect(final SelectorSelection p0);
        
        void onError(final SelectorSelection p0, final Throwable p1);
    }
}
