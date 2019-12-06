// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.thread;

import java.util.concurrent.Executor;

public class DeterministicTaskRunner implements TaskRunner
{
    private final Executor executor;
    private final Task task;
    private final Runnable runable;
    private boolean shutdown;
    
    public DeterministicTaskRunner(final Executor executor, final Task task) {
        this.executor = executor;
        this.task = task;
        this.runable = new Runnable() {
            @Override
            public void run() {
                Thread.currentThread();
                DeterministicTaskRunner.this.runTask();
            }
        };
    }
    
    @Override
    public void wakeup() throws InterruptedException {
        synchronized (this.runable) {
            if (this.shutdown) {
                return;
            }
            this.executor.execute(this.runable);
        }
    }
    
    @Override
    public void shutdown(final long timeout) throws InterruptedException {
        synchronized (this.runable) {
            this.shutdown = true;
        }
    }
    
    @Override
    public void shutdown() throws InterruptedException {
        this.shutdown(0L);
    }
    
    final void runTask() {
        synchronized (this.runable) {
            if (this.shutdown) {
                this.runable.notifyAll();
                return;
            }
        }
        this.task.iterate();
    }
}
