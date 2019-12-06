// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v2;

import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.openwire.DataStreamMarshaller;

public final class MarshallerFactory
{
    private static final DataStreamMarshaller[] MARSHALLER;
    
    private MarshallerFactory() {
    }
    
    private static void add(final DataStreamMarshaller dsm) {
        MarshallerFactory.MARSHALLER[dsm.getDataStructureType()] = dsm;
    }
    
    public static DataStreamMarshaller[] createMarshallerMap(final OpenWireFormat wireFormat) {
        return MarshallerFactory.MARSHALLER;
    }
    
    static {
        MARSHALLER = new DataStreamMarshaller[256];
        add(new ActiveMQBytesMessageMarshaller());
        add(new ActiveMQMapMessageMarshaller());
        add(new ActiveMQMessageMarshaller());
        add(new ActiveMQObjectMessageMarshaller());
        add(new ActiveMQQueueMarshaller());
        add(new ActiveMQStreamMessageMarshaller());
        add(new ActiveMQTempQueueMarshaller());
        add(new ActiveMQTempTopicMarshaller());
        add(new ActiveMQTextMessageMarshaller());
        add(new ActiveMQTopicMarshaller());
        add(new BrokerIdMarshaller());
        add(new BrokerInfoMarshaller());
        add(new ConnectionControlMarshaller());
        add(new ConnectionErrorMarshaller());
        add(new ConnectionIdMarshaller());
        add(new ConnectionInfoMarshaller());
        add(new ConsumerControlMarshaller());
        add(new ConsumerIdMarshaller());
        add(new ConsumerInfoMarshaller());
        add(new ControlCommandMarshaller());
        add(new DataArrayResponseMarshaller());
        add(new DataResponseMarshaller());
        add(new DestinationInfoMarshaller());
        add(new DiscoveryEventMarshaller());
        add(new ExceptionResponseMarshaller());
        add(new FlushCommandMarshaller());
        add(new IntegerResponseMarshaller());
        add(new JournalQueueAckMarshaller());
        add(new JournalTopicAckMarshaller());
        add(new JournalTraceMarshaller());
        add(new JournalTransactionMarshaller());
        add(new KeepAliveInfoMarshaller());
        add(new LastPartialCommandMarshaller());
        add(new LocalTransactionIdMarshaller());
        add(new MessageAckMarshaller());
        add(new MessageDispatchMarshaller());
        add(new MessageDispatchNotificationMarshaller());
        add(new MessageIdMarshaller());
        add(new MessagePullMarshaller());
        add(new NetworkBridgeFilterMarshaller());
        add(new PartialCommandMarshaller());
        add(new ProducerIdMarshaller());
        add(new ProducerInfoMarshaller());
        add(new RemoveInfoMarshaller());
        add(new RemoveSubscriptionInfoMarshaller());
        add(new ReplayCommandMarshaller());
        add(new ResponseMarshaller());
        add(new SessionIdMarshaller());
        add(new SessionInfoMarshaller());
        add(new ShutdownInfoMarshaller());
        add(new SubscriptionInfoMarshaller());
        add(new TransactionInfoMarshaller());
        add(new WireFormatInfoMarshaller());
        add(new XATransactionIdMarshaller());
    }
}
