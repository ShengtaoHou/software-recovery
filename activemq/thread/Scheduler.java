// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.thread;

import org.apache.activemq.util.ServiceStopper;
import java.util.TimerTask;
import java.util.HashMap;
import java.util.Timer;
import org.apache.activemq.util.ServiceSupport;

public final class Scheduler extends ServiceSupport
{
    private final String name;
    private Timer timer;
    private final HashMap<Runnable, TimerTask> timerTasks;
    
    public Scheduler(final String name) {
        this.timerTasks = new HashMap<Runnable, TimerTask>();
        this.name = name;
    }
    
    public void executePeriodically(final Runnable task, final long period) {
        final TimerTask timerTask = new SchedulerTimerTask(task);
        this.timer.schedule(timerTask, period, period);
        this.timerTasks.put(task, timerTask);
    }
    
    @Deprecated
    public synchronized void schedualPeriodically(final Runnable task, final long period) {
        final TimerTask timerTask = new SchedulerTimerTask(task);
        this.timer.schedule(timerTask, period, period);
        this.timerTasks.put(task, timerTask);
    }
    
    public synchronized void cancel(final Runnable task) {
        final TimerTask ticket = this.timerTasks.remove(task);
        if (ticket != null) {
            ticket.cancel();
            this.timer.purge();
        }
    }
    
    public synchronized void executeAfterDelay(final Runnable task, final long redeliveryDelay) {
        final TimerTask timerTask = new SchedulerTimerTask(task);
        this.timer.schedule(timerTask, redeliveryDelay);
    }
    
    public void shutdown() {
        this.timer.cancel();
    }
    
    @Override
    protected synchronized void doStart() throws Exception {
        this.timer = new Timer(this.name, true);
    }
    
    @Override
    protected synchronized void doStop(final ServiceStopper stopper) throws Exception {
        if (this.timer != null) {
            this.timer.cancel();
        }
    }
    
    public String getName() {
        return this.name;
    }
}
