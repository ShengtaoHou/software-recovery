// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

public interface ScheduledMessage
{
    public static final String AMQ_SCHEDULED_DELAY = "AMQ_SCHEDULED_DELAY";
    public static final String AMQ_SCHEDULED_PERIOD = "AMQ_SCHEDULED_PERIOD";
    public static final String AMQ_SCHEDULED_REPEAT = "AMQ_SCHEDULED_REPEAT";
    public static final String AMQ_SCHEDULED_CRON = "AMQ_SCHEDULED_CRON";
    public static final String AMQ_SCHEDULED_ID = "scheduledJobId";
    public static final String AMQ_SCHEDULER_MANAGEMENT_DESTINATION = "ActiveMQ.Scheduler.Management";
    public static final String AMQ_SCHEDULER_ACTION = "AMQ_SCHEDULER_ACTION";
    public static final String AMQ_SCHEDULER_ACTION_BROWSE = "BROWSE";
    public static final String AMQ_SCHEDULER_ACTION_REMOVE = "REMOVE";
    public static final String AMQ_SCHEDULER_ACTION_REMOVEALL = "REMOVEALL";
    public static final String AMQ_SCHEDULER_ACTION_START_TIME = "ACTION_START_TIME";
    public static final String AMQ_SCHEDULER_ACTION_END_TIME = "ACTION_END_TIME";
}
