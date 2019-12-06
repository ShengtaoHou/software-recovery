// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.thread;

import java.util.TimerTask;

public class SchedulerTimerTask extends TimerTask
{
    private final Runnable task;
    
    public SchedulerTimerTask(final Runnable task) {
        this.task = task;
    }
    
    @Override
    public void run() {
        this.task.run();
    }
}
