// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.mqtt;

import org.apache.activemq.util.LRUCache;
import org.slf4j.LoggerFactory;
import org.apache.activemq.Service;
import org.apache.activemq.broker.BrokerService;
import org.fusesource.mqtt.codec.PUBLISH;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.util.ServiceStopper;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.apache.activemq.util.ServiceSupport;

public class MQTTPacketIdGenerator extends ServiceSupport
{
    private static final Logger LOG;
    private static final Object LOCK;
    Map<String, PacketIdMaps> clientIdMap;
    private final NonZeroSequenceGenerator messageIdGenerator;
    
    private MQTTPacketIdGenerator() {
        this.clientIdMap = new ConcurrentHashMap<String, PacketIdMaps>();
        this.messageIdGenerator = new NonZeroSequenceGenerator();
    }
    
    @Override
    protected void doStop(final ServiceStopper stopper) throws Exception {
        synchronized (this) {
            this.clientIdMap = new ConcurrentHashMap<String, PacketIdMaps>();
        }
    }
    
    @Override
    protected void doStart() throws Exception {
    }
    
    public void startClientSession(final String clientId) {
        if (!this.clientIdMap.containsKey(clientId)) {
            this.clientIdMap.put(clientId, new PacketIdMaps());
        }
    }
    
    public boolean stopClientSession(final String clientId) {
        return this.clientIdMap.remove(clientId) != null;
    }
    
    public short setPacketId(final String clientId, final MQTTSubscription subscription, final ActiveMQMessage message, final PUBLISH publish) {
        final PacketIdMaps idMaps = this.clientIdMap.get(clientId);
        if (idMaps == null) {
            final short id = this.messageIdGenerator.getNextSequenceId();
            publish.messageId(id);
            return id;
        }
        return idMaps.setPacketId(subscription, message, publish);
    }
    
    public void ackPacketId(final String clientId, final short packetId) {
        final PacketIdMaps idMaps = this.clientIdMap.get(clientId);
        if (idMaps != null) {
            idMaps.ackPacketId(packetId);
        }
    }
    
    public short getNextSequenceId(final String clientId) {
        final PacketIdMaps idMaps = this.clientIdMap.get(clientId);
        return (idMaps != null) ? idMaps.getNextSequenceId() : this.messageIdGenerator.getNextSequenceId();
    }
    
    public static MQTTPacketIdGenerator getMQTTPacketIdGenerator(final BrokerService broker) {
        MQTTPacketIdGenerator result = null;
        if (broker != null) {
            synchronized (MQTTPacketIdGenerator.LOCK) {
                final Service[] services = broker.getServices();
                if (services != null) {
                    for (final Service service : services) {
                        if (service instanceof MQTTPacketIdGenerator) {
                            return (MQTTPacketIdGenerator)service;
                        }
                    }
                }
                result = new MQTTPacketIdGenerator();
                broker.addService(result);
                if (broker.isStarted()) {
                    try {
                        result.start();
                    }
                    catch (Exception e) {
                        MQTTPacketIdGenerator.LOG.warn("Couldn't start MQTTPacketIdGenerator");
                    }
                }
            }
        }
        return result;
    }
    
    static {
        LOG = LoggerFactory.getLogger(MQTTPacketIdGenerator.class);
        LOCK = new Object();
    }
    
    private class PacketIdMaps
    {
        private final NonZeroSequenceGenerator messageIdGenerator;
        final Map<String, Short> activemqToPacketIds;
        final Map<Short, String> packetIdsToActivemq;
        
        private PacketIdMaps() {
            this.messageIdGenerator = new NonZeroSequenceGenerator();
            this.activemqToPacketIds = new LRUCache<String, Short>(5000);
            this.packetIdsToActivemq = new LRUCache<Short, String>(5000);
        }
        
        short setPacketId(final MQTTSubscription subscription, final ActiveMQMessage message, final PUBLISH publish) {
            final StringBuilder subscriptionKey = new StringBuilder();
            subscriptionKey.append(subscription.getConsumerInfo().getDestination().getPhysicalName()).append(':').append(message.getJMSMessageID());
            final String keyStr = subscriptionKey.toString();
            Short packetId;
            synchronized (this.activemqToPacketIds) {
                packetId = this.activemqToPacketIds.get(keyStr);
                if (packetId == null) {
                    packetId = this.getNextSequenceId();
                    this.activemqToPacketIds.put(keyStr, packetId);
                    this.packetIdsToActivemq.put(packetId, keyStr);
                }
                else {
                    publish.dup(true);
                }
            }
            publish.messageId((short)packetId);
            return packetId;
        }
        
        void ackPacketId(final short packetId) {
            synchronized (this.activemqToPacketIds) {
                final String subscriptionKey = this.packetIdsToActivemq.remove(packetId);
                if (subscriptionKey != null) {
                    this.activemqToPacketIds.remove(subscriptionKey);
                }
            }
        }
        
        short getNextSequenceId() {
            return this.messageIdGenerator.getNextSequenceId();
        }
    }
    
    private class NonZeroSequenceGenerator
    {
        private short lastSequenceId;
        
        public synchronized short getNextSequenceId() {
            final short lastSequenceId = (short)(this.lastSequenceId + 1);
            this.lastSequenceId = lastSequenceId;
            final short val = lastSequenceId;
            return (short)((val != 0) ? val : (++this.lastSequenceId));
        }
    }
}
