// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.scheduler;

import org.slf4j.LoggerFactory;
import org.apache.activemq.usage.JobSchedulerUsage;
import org.apache.activemq.state.ProducerState;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.command.MessageId;
import java.io.IOException;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.command.TransactionId;
import java.util.Iterator;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.transaction.Synchronization;
import org.apache.activemq.util.TypeConversionSupport;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.security.SecurityContext;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.usage.SystemUsage;
import org.apache.activemq.command.ProducerId;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.wireformat.WireFormat;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.util.LongSequenceGenerator;
import org.apache.activemq.util.IdGenerator;
import org.slf4j.Logger;
import org.apache.activemq.broker.BrokerFilter;

public class SchedulerBroker extends BrokerFilter implements JobListener
{
    private static final Logger LOG;
    private static final IdGenerator ID_GENERATOR;
    private final LongSequenceGenerator messageIdGenerator;
    private final AtomicBoolean started;
    private final WireFormat wireFormat;
    private final ConnectionContext context;
    private final ProducerId producerId;
    private final SystemUsage systemUsage;
    private final JobSchedulerStore store;
    private JobScheduler scheduler;
    
    public SchedulerBroker(final BrokerService brokerService, final Broker next, final JobSchedulerStore store) throws Exception {
        super(next);
        this.messageIdGenerator = new LongSequenceGenerator();
        this.started = new AtomicBoolean();
        this.wireFormat = new OpenWireFormat();
        this.context = new ConnectionContext();
        this.producerId = new ProducerId();
        this.store = store;
        this.producerId.setConnectionId(SchedulerBroker.ID_GENERATOR.generateId());
        this.context.setSecurityContext(SecurityContext.BROKER_SECURITY_CONTEXT);
        this.context.setBroker(next);
        this.systemUsage = brokerService.getSystemUsage();
        this.wireFormat.setVersion(brokerService.getStoreOpenWireVersion());
    }
    
    public synchronized JobScheduler getJobScheduler() throws Exception {
        return new JobSchedulerFacade(this);
    }
    
    @Override
    public void start() throws Exception {
        this.started.set(true);
        this.getInternalScheduler();
        super.start();
    }
    
    @Override
    public void stop() throws Exception {
        if (this.started.compareAndSet(true, false)) {
            if (this.store != null) {
                this.store.stop();
            }
            if (this.scheduler != null) {
                this.scheduler.removeListener(this);
                this.scheduler = null;
            }
        }
        super.stop();
    }
    
    @Override
    public void send(final ProducerBrokerExchange producerExchange, final Message messageSend) throws Exception {
        final ConnectionContext context = producerExchange.getConnectionContext();
        final String jobId = (String)messageSend.getProperty("scheduledJobId");
        final Object cronValue = messageSend.getProperty("AMQ_SCHEDULED_CRON");
        final Object periodValue = messageSend.getProperty("AMQ_SCHEDULED_PERIOD");
        final Object delayValue = messageSend.getProperty("AMQ_SCHEDULED_DELAY");
        final String physicalName = messageSend.getDestination().getPhysicalName();
        final boolean schedularManage = physicalName.regionMatches(true, 0, "ActiveMQ.Scheduler.Management", 0, "ActiveMQ.Scheduler.Management".length());
        if (schedularManage) {
            final JobScheduler scheduler = this.getInternalScheduler();
            final ActiveMQDestination replyTo = messageSend.getReplyTo();
            final String action = (String)messageSend.getProperty("AMQ_SCHEDULER_ACTION");
            if (action != null) {
                final Object startTime = messageSend.getProperty("ACTION_START_TIME");
                final Object endTime = messageSend.getProperty("ACTION_END_TIME");
                if (replyTo != null && action.equals("BROWSE")) {
                    if (startTime != null && endTime != null) {
                        final long start = (long)TypeConversionSupport.convert(startTime, Long.class);
                        final long finish = (long)TypeConversionSupport.convert(endTime, Long.class);
                        for (final Job job : scheduler.getAllJobs(start, finish)) {
                            this.sendScheduledJob(producerExchange.getConnectionContext(), job, replyTo);
                        }
                    }
                    else {
                        for (final Job job2 : scheduler.getAllJobs()) {
                            this.sendScheduledJob(producerExchange.getConnectionContext(), job2, replyTo);
                        }
                    }
                }
                if (jobId != null && action.equals("REMOVE")) {
                    scheduler.remove(jobId);
                }
                else if (action.equals("REMOVEALL")) {
                    if (startTime != null && endTime != null) {
                        final long start = (long)TypeConversionSupport.convert(startTime, Long.class);
                        final long finish = (long)TypeConversionSupport.convert(endTime, Long.class);
                        scheduler.removeAllJobs(start, finish);
                    }
                    else {
                        scheduler.removeAllJobs();
                    }
                }
            }
        }
        else if ((cronValue != null || periodValue != null || delayValue != null) && jobId == null) {
            if (context.isInTransaction()) {
                context.getTransaction().addSynchronization(new Synchronization() {
                    @Override
                    public void afterCommit() throws Exception {
                        SchedulerBroker.this.doSchedule(messageSend, cronValue, periodValue, delayValue);
                    }
                });
            }
            else {
                this.doSchedule(messageSend, cronValue, periodValue, delayValue);
            }
        }
        else {
            super.send(producerExchange, messageSend);
        }
    }
    
    private void doSchedule(final Message messageSend, final Object cronValue, final Object periodValue, final Object delayValue) throws Exception {
        long delay = 0L;
        long period = 0L;
        int repeat = 0;
        String cronEntry = "";
        final Message msg = messageSend.copy();
        msg.setTransactionId(null);
        final ByteSequence packet = this.wireFormat.marshal(msg);
        if (cronValue != null) {
            cronEntry = cronValue.toString();
        }
        if (periodValue != null) {
            period = (long)TypeConversionSupport.convert(periodValue, Long.class);
        }
        if (delayValue != null) {
            delay = (long)TypeConversionSupport.convert(delayValue, Long.class);
        }
        final Object repeatValue = msg.getProperty("AMQ_SCHEDULED_REPEAT");
        if (repeatValue != null) {
            repeat = (int)TypeConversionSupport.convert(repeatValue, Integer.class);
        }
        this.getInternalScheduler().schedule(msg.getMessageId().toString(), new ByteSequence(packet.data, packet.offset, packet.length), cronEntry, delay, period, repeat);
    }
    
    @Override
    public void scheduledJob(final String id, final ByteSequence job) {
        final ByteSequence packet = new ByteSequence(job.getData(), job.getOffset(), job.getLength());
        try {
            final Message messageSend = (Message)this.wireFormat.unmarshal(packet);
            messageSend.setOriginalTransactionId(null);
            final Object repeatValue = messageSend.getProperty("AMQ_SCHEDULED_REPEAT");
            final Object cronValue = messageSend.getProperty("AMQ_SCHEDULED_CRON");
            final String cronStr = (cronValue != null) ? cronValue.toString() : null;
            int repeat = 0;
            if (repeatValue != null) {
                repeat = (int)TypeConversionSupport.convert(repeatValue, Integer.class);
            }
            if (this.systemUsage.getJobSchedulerUsage() != null) {
                final JobSchedulerUsage usage = this.systemUsage.getJobSchedulerUsage();
                if (usage.isFull()) {
                    final String logMessage = "Job Scheduler Store is Full (" + usage.getPercentUsage() + "% of " + usage.getLimit() + "). Stopping producer (" + messageSend.getProducerId() + ") to prevent flooding of the job scheduler store. See http://activemq.apache.org/producer-flow-control.html for more info";
                    long nextWarn;
                    final long start = nextWarn = System.currentTimeMillis();
                    while (!usage.waitForSpace(1000L)) {
                        if (this.context.getStopping().get()) {
                            throw new IOException("Connection closed, send aborted.");
                        }
                        final long now = System.currentTimeMillis();
                        if (now < nextWarn) {
                            continue;
                        }
                        SchedulerBroker.LOG.info("" + usage + ": " + logMessage + " (blocking for: " + (now - start) / 1000L + "s)");
                        nextWarn = now + 30000L;
                    }
                }
            }
            if (repeat != 0 || (cronStr != null && cronStr.length() > 0)) {
                messageSend.setMessageId(new MessageId(this.producerId, this.messageIdGenerator.getNextSequenceId()));
            }
            messageSend.setProperty("scheduledJobId", id);
            messageSend.removeProperty("AMQ_SCHEDULED_PERIOD");
            messageSend.removeProperty("AMQ_SCHEDULED_DELAY");
            messageSend.removeProperty("AMQ_SCHEDULED_REPEAT");
            messageSend.removeProperty("AMQ_SCHEDULED_CRON");
            if (messageSend.getTimestamp() > 0L && messageSend.getExpiration() > 0L) {
                final long oldExpiration = messageSend.getExpiration();
                final long newTimeStamp = System.currentTimeMillis();
                long timeToLive = 0L;
                final long oldTimestamp = messageSend.getTimestamp();
                if (oldExpiration > 0L) {
                    timeToLive = oldExpiration - oldTimestamp;
                }
                final long expiration = timeToLive + newTimeStamp;
                if (expiration > oldExpiration) {
                    if (timeToLive > 0L && expiration > 0L) {
                        messageSend.setExpiration(expiration);
                    }
                    messageSend.setTimestamp(newTimeStamp);
                    SchedulerBroker.LOG.debug("Set message {} timestamp from {} to {}", messageSend.getMessageId(), oldTimestamp, newTimeStamp);
                }
            }
            final ProducerBrokerExchange producerExchange = new ProducerBrokerExchange();
            producerExchange.setConnectionContext(this.context);
            producerExchange.setMutable(true);
            producerExchange.setProducerState(new ProducerState(new ProducerInfo()));
            super.send(producerExchange, messageSend);
        }
        catch (Exception e) {
            SchedulerBroker.LOG.error("Failed to send scheduled message {}", id, e);
        }
    }
    
    protected synchronized JobScheduler getInternalScheduler() throws Exception {
        if (this.started.get()) {
            if (this.scheduler == null && this.store != null) {
                (this.scheduler = this.store.getJobScheduler("JMS")).addListener(this);
                this.scheduler.startDispatching();
            }
            return this.scheduler;
        }
        return null;
    }
    
    protected void sendScheduledJob(final ConnectionContext context, final Job job, final ActiveMQDestination replyTo) throws Exception {
        final ByteSequence packet = new ByteSequence(job.getPayload());
        try {
            final Message msg = (Message)this.wireFormat.unmarshal(packet);
            msg.setOriginalTransactionId(null);
            msg.setPersistent(false);
            msg.setType("Advisory");
            msg.setMessageId(new MessageId(this.producerId, this.messageIdGenerator.getNextSequenceId()));
            msg.setDestination(replyTo);
            msg.setResponseRequired(false);
            msg.setProducerId(this.producerId);
            msg.setProperty("scheduledJobId", job.getJobId());
            final boolean originalFlowControl = context.isProducerFlowControl();
            final ProducerBrokerExchange producerExchange = new ProducerBrokerExchange();
            producerExchange.setConnectionContext(context);
            producerExchange.setMutable(true);
            producerExchange.setProducerState(new ProducerState(new ProducerInfo()));
            try {
                context.setProducerFlowControl(false);
                this.next.send(producerExchange, msg);
            }
            finally {
                context.setProducerFlowControl(originalFlowControl);
            }
        }
        catch (Exception e) {
            SchedulerBroker.LOG.error("Failed to send scheduled message {}", job.getJobId(), e);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(SchedulerBroker.class);
        ID_GENERATOR = new IdGenerator();
    }
}
