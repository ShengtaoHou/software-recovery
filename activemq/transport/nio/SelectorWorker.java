// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.nio;

import java.util.Iterator;
import java.util.Set;
import java.nio.channels.SelectionKey;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.nio.channels.Selector;
import java.util.concurrent.atomic.AtomicInteger;

public class SelectorWorker implements Runnable
{
    private static final AtomicInteger NEXT_ID;
    final SelectorManager manager;
    final Selector selector;
    final int id;
    private final int maxChannelsPerWorker;
    final AtomicInteger retainCounter;
    private final ConcurrentLinkedQueue<Runnable> ioTasks;
    
    public SelectorWorker(final SelectorManager manager) throws IOException {
        this.id = SelectorWorker.NEXT_ID.getAndIncrement();
        this.retainCounter = new AtomicInteger(1);
        this.ioTasks = new ConcurrentLinkedQueue<Runnable>();
        this.manager = manager;
        this.selector = Selector.open();
        this.maxChannelsPerWorker = manager.getMaxChannelsPerWorker();
        manager.getSelectorExecutor().execute(this);
    }
    
    void retain() {
        if (this.retainCounter.incrementAndGet() == this.maxChannelsPerWorker) {
            this.manager.onWorkerFullEvent(this);
        }
    }
    
    void release() {
        final int use = this.retainCounter.decrementAndGet();
        if (use == 0) {
            this.manager.onWorkerEmptyEvent(this);
        }
        else if (use == this.maxChannelsPerWorker - 1) {
            this.manager.onWorkerNotFullEvent(this);
        }
    }
    
    boolean isReleased() {
        return this.retainCounter.get() == 0;
    }
    
    public void addIoTask(final Runnable work) {
        this.ioTasks.add(work);
        this.selector.wakeup();
    }
    
    private void processIoTasks() {
        Runnable task;
        while ((task = this.ioTasks.poll()) != null) {
            try {
                task.run();
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void run() {
        final String origName = Thread.currentThread().getName();
        try {
            Thread.currentThread().setName("Selector Worker: " + this.id);
            while (!this.isReleased()) {
                this.processIoTasks();
                final int count = this.selector.select(10L);
                if (count == 0) {
                    continue;
                }
                final Set keys = this.selector.selectedKeys();
                final Iterator i = keys.iterator();
                while (i.hasNext()) {
                    final SelectionKey key = i.next();
                    i.remove();
                    final SelectorSelection s = (SelectorSelection)key.attachment();
                    try {
                        if (key.isValid()) {
                            key.interestOps(0);
                        }
                        this.manager.getChannelExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    s.onSelect();
                                    s.enable();
                                }
                                catch (Throwable e) {
                                    s.onError(e);
                                }
                            }
                        });
                    }
                    catch (Throwable e) {
                        s.onError(e);
                    }
                }
            }
        }
        catch (Throwable e2) {
            e2.printStackTrace();
            final Set keys = this.selector.keys();
            for (final SelectionKey key : keys) {
                final SelectorSelection s = (SelectorSelection)key.attachment();
                s.onError(e2);
            }
        }
        finally {
            try {
                this.manager.onWorkerEmptyEvent(this);
                this.selector.close();
            }
            catch (IOException ignore) {
                ignore.printStackTrace();
            }
            Thread.currentThread().setName(origName);
        }
    }
    
    static {
        NEXT_ID = new AtomicInteger();
    }
}
