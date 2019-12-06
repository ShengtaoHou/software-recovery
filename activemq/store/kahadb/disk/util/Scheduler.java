// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.util;

import java.util.TimerTask;
import java.util.HashMap;
import java.util.Timer;

public final class Scheduler
{
    public static final Timer CLOCK_DAEMON;
    private static final HashMap<Runnable, TimerTask> TIMER_TASKS;
    
    private Scheduler() {
    }
    
    public static synchronized void executePeriodically(final Runnable task, final long period) {
        final TimerTask timerTask = new SchedulerTimerTask(task);
        Scheduler.CLOCK_DAEMON.schedule(timerTask, period, period);
        Scheduler.TIMER_TASKS.put(task, timerTask);
    }
    
    public static synchronized void cancel(final Runnable task) {
        final TimerTask ticket = Scheduler.TIMER_TASKS.remove(task);
        if (ticket != null) {
            ticket.cancel();
            Scheduler.CLOCK_DAEMON.purge();
        }
    }
    
    public static void executeAfterDelay(final Runnable task, final long redeliveryDelay) {
        final TimerTask timerTask = new SchedulerTimerTask(task);
        Scheduler.CLOCK_DAEMON.schedule(timerTask, redeliveryDelay);
    }
    
    public static void shutdown() {
        Scheduler.CLOCK_DAEMON.cancel();
    }
    
    static {
        CLOCK_DAEMON = new Timer("KahaDB Scheduler", true);
        TIMER_TASKS = new HashMap<Runnable, TimerTask>();
    }
}
