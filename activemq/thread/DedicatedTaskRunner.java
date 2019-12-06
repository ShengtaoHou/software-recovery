// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.thread;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

class DedicatedTaskRunner implements TaskRunner
{
    private static final Logger LOG;
    private final Task task;
    private final Thread thread;
    private final Object mutex;
    private boolean threadTerminated;
    private boolean pending;
    private boolean shutdown;
    
    public DedicatedTaskRunner(final Task task, final String name, final int priority, final boolean daemon) {
        this.mutex = new Object();
        this.task = task;
        (this.thread = new Thread(name) {
            @Override
            public void run() {
                try {
                    DedicatedTaskRunner.this.runTask();
                }
                finally {
                    DedicatedTaskRunner.LOG.trace("Run task done: {}", task);
                }
            }
        }).setDaemon(daemon);
        this.thread.setName(name);
        this.thread.setPriority(priority);
        this.thread.start();
    }
    
    @Override
    public void wakeup() throws InterruptedException {
        synchronized (this.mutex) {
            if (this.shutdown) {
                return;
            }
            this.pending = true;
            this.mutex.notifyAll();
        }
    }
    
    @Override
    public void shutdown(final long timeout) throws InterruptedException {
        DedicatedTaskRunner.LOG.trace("Shutdown timeout: {} task: {}", (Object)timeout, this.task);
        synchronized (this.mutex) {
            this.shutdown = true;
            this.pending = true;
            this.mutex.notifyAll();
            if (Thread.currentThread() != this.thread && !this.threadTerminated) {
                this.mutex.wait(timeout);
            }
        }
    }
    
    @Override
    public void shutdown() throws InterruptedException {
        this.shutdown(0L);
    }
    
    final void runTask() {
        try {
            while (true) {
                synchronized (this.mutex) {
                    this.pending = false;
                    if (this.shutdown) {
                        return;
                    }
                }
                DedicatedTaskRunner.LOG.trace("Running task {}", this.task);
                if (!this.task.iterate()) {
                    synchronized (this.mutex) {
                        if (this.shutdown) {
                            return;
                        }
                        while (!this.pending) {
                            this.mutex.wait();
                        }
                    }
                }
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            synchronized (this.mutex) {
                this.threadTerminated = true;
                this.mutex.notifyAll();
            }
        }
        finally {
            synchronized (this.mutex) {
                this.threadTerminated = true;
                this.mutex.notifyAll();
            }
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(DedicatedTaskRunner.class);
    }
}
