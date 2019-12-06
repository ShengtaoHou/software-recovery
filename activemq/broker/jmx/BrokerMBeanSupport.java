// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import java.util.Map;
import java.util.Hashtable;
import org.apache.activemq.broker.region.policy.AbortSlowConsumerStrategy;
import org.apache.activemq.transaction.XATransaction;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.ActiveMQDestination;
import javax.management.MalformedObjectNameException;
import org.apache.activemq.util.JMXSupport;
import javax.management.ObjectName;

public class BrokerMBeanSupport
{
    public static ObjectName createBrokerObjectName(final String jmxDomainName, final String brokerName) throws MalformedObjectNameException {
        String objectNameStr = jmxDomainName + ":type=Broker,brokerName=";
        objectNameStr += JMXSupport.encodeObjectNamePart(brokerName);
        return new ObjectName(objectNameStr);
    }
    
    public static ObjectName createDestinationName(final ObjectName brokerObjectName, final ActiveMQDestination destination) throws MalformedObjectNameException {
        return createDestinationName(brokerObjectName.toString(), destination);
    }
    
    public static ObjectName createDestinationName(final String brokerObjectName, final ActiveMQDestination destination) throws MalformedObjectNameException {
        String objectNameStr = brokerObjectName;
        objectNameStr += createDestinationProperties(destination);
        return new ObjectName(objectNameStr);
    }
    
    public static ObjectName createDestinationName(final String brokerObjectName, final String type, final String name) throws MalformedObjectNameException {
        String objectNameStr = brokerObjectName;
        objectNameStr += createDestinationProperties(type, name);
        return new ObjectName(objectNameStr);
    }
    
    private static String createDestinationProperties(final ActiveMQDestination destination) {
        String result = "";
        if (destination != null) {
            result = createDestinationProperties(destination.getDestinationTypeAsString(), destination.getPhysicalName());
        }
        return result;
    }
    
    private static String createDestinationProperties(final String type, final String name) {
        return ",destinationType=" + JMXSupport.encodeObjectNamePart(type) + ",destinationName=" + JMXSupport.encodeObjectNamePart(name);
    }
    
    public static ObjectName createSubscriptionName(final ObjectName brokerObjectName, final String connectionClientId, final ConsumerInfo info) throws MalformedObjectNameException {
        return createSubscriptionName(brokerObjectName.toString(), connectionClientId, info);
    }
    
    public static ObjectName createSubscriptionName(final String brokerObjectName, final String connectionClientId, final ConsumerInfo info) throws MalformedObjectNameException {
        String objectNameStr = brokerObjectName;
        objectNameStr = objectNameStr + createDestinationProperties(info.getDestination()) + ",endpoint=Consumer";
        objectNameStr = objectNameStr + ",clientId=" + JMXSupport.encodeObjectNamePart(connectionClientId);
        objectNameStr += ",consumerId=";
        if (info.isDurable()) {
            objectNameStr = objectNameStr + "Durable(" + JMXSupport.encodeObjectNamePart(connectionClientId + ":" + info.getSubscriptionName()) + ")";
        }
        else {
            objectNameStr += JMXSupport.encodeObjectNamePart(info.getConsumerId().toString());
        }
        return new ObjectName(objectNameStr);
    }
    
    public static ObjectName createProducerName(final ObjectName brokerObjectName, final String connectionClientId, final ProducerInfo info) throws MalformedObjectNameException {
        return createProducerName(brokerObjectName.toString(), connectionClientId, info);
    }
    
    public static ObjectName createProducerName(final String brokerObjectName, final String connectionClientId, final ProducerInfo producerInfo) throws MalformedObjectNameException {
        String objectNameStr = brokerObjectName;
        if (producerInfo.getDestination() == null) {
            objectNameStr += ",endpoint=dynamicProducer";
        }
        else {
            objectNameStr = objectNameStr + createDestinationProperties(producerInfo.getDestination()) + ",endpoint=Producer";
        }
        objectNameStr = objectNameStr + ",clientId=" + JMXSupport.encodeObjectNamePart(connectionClientId);
        objectNameStr = objectNameStr + ",producerId=" + JMXSupport.encodeObjectNamePart(producerInfo.getProducerId().toString());
        return new ObjectName(objectNameStr);
    }
    
    public static ObjectName createXATransactionName(final ObjectName brokerObjectName, final XATransaction transaction) throws MalformedObjectNameException {
        return createXATransactionName(brokerObjectName.toString(), transaction);
    }
    
    public static ObjectName createXATransactionName(final String brokerObjectName, final XATransaction transaction) throws MalformedObjectNameException {
        String objectNameStr = brokerObjectName;
        objectNameStr += ",transactionType=RecoveredXaTransaction";
        objectNameStr = objectNameStr + ",Xid=" + JMXSupport.encodeObjectNamePart(transaction.getTransactionId().toString());
        return new ObjectName(objectNameStr);
    }
    
    public static ObjectName createPersistenceAdapterName(final String brokerObjectName, final String name) throws MalformedObjectNameException {
        String objectNameStr = brokerObjectName;
        objectNameStr += ",Service=PersistenceAdapter";
        objectNameStr = objectNameStr + ",InstanceName=" + JMXSupport.encodeObjectNamePart(name);
        return new ObjectName(objectNameStr);
    }
    
    public static ObjectName createAbortSlowConsumerStrategyName(final ObjectName brokerObjectName, final AbortSlowConsumerStrategy strategy) throws MalformedObjectNameException {
        return createAbortSlowConsumerStrategyName(brokerObjectName.toString(), strategy);
    }
    
    public static ObjectName createAbortSlowConsumerStrategyName(final String brokerObjectName, final AbortSlowConsumerStrategy strategy) throws MalformedObjectNameException {
        String objectNameStr = brokerObjectName;
        objectNameStr = objectNameStr + ",Service=SlowConsumerStrategy,InstanceName=" + JMXSupport.encodeObjectNamePart(strategy.getName());
        final ObjectName objectName = new ObjectName(objectNameStr);
        return objectName;
    }
    
    public static ObjectName createConnectorName(final ObjectName brokerObjectName, final String type, final String name) throws MalformedObjectNameException {
        return createConnectorName(brokerObjectName.toString(), type, name);
    }
    
    public static ObjectName createConnectorName(final String brokerObjectName, final String type, final String name) throws MalformedObjectNameException {
        String objectNameStr = brokerObjectName;
        objectNameStr = objectNameStr + ",connector=" + type + ",connectorName=" + JMXSupport.encodeObjectNamePart(name);
        final ObjectName objectName = new ObjectName(objectNameStr);
        return objectName;
    }
    
    public static ObjectName createNetworkConnectorName(final ObjectName brokerObjectName, final String type, final String name) throws MalformedObjectNameException {
        return createNetworkConnectorName(brokerObjectName.toString(), type, name);
    }
    
    public static ObjectName createNetworkConnectorName(final String brokerObjectName, final String type, final String name) throws MalformedObjectNameException {
        String objectNameStr = brokerObjectName;
        objectNameStr = objectNameStr + ",connector=" + type + ",networkConnectorName=" + JMXSupport.encodeObjectNamePart(name);
        final ObjectName objectName = new ObjectName(objectNameStr);
        return objectName;
    }
    
    public static ObjectName createConnectionViewByType(final ObjectName connectorName, final String type, final String name) throws MalformedObjectNameException {
        String objectNameStr = connectorName.toString();
        objectNameStr = objectNameStr + ",connectionViewType=" + JMXSupport.encodeObjectNamePart(type);
        objectNameStr = objectNameStr + ",connectionName=" + JMXSupport.encodeObjectNamePart(name);
        return new ObjectName(objectNameStr);
    }
    
    public static ObjectName createNetworkBridgeObjectName(final ObjectName connectorName, final String remoteAddress) throws MalformedObjectNameException {
        final Hashtable<String, String> map = new Hashtable<String, String>(connectorName.getKeyPropertyList());
        map.put("networkBridge", JMXSupport.encodeObjectNamePart(remoteAddress));
        return new ObjectName(connectorName.getDomain(), map);
    }
    
    public static ObjectName createNetworkOutBoundDestinationObjectName(final ObjectName networkName, final ActiveMQDestination destination) throws MalformedObjectNameException {
        String str = networkName.toString();
        str = str + ",direction=outbound" + createDestinationProperties(destination);
        return new ObjectName(str);
    }
    
    public static ObjectName createNetworkInBoundDestinationObjectName(final ObjectName networkName, final ActiveMQDestination destination) throws MalformedObjectNameException {
        String str = networkName.toString();
        str = str + ",direction=inbound" + createDestinationProperties(destination);
        return new ObjectName(str);
    }
    
    public static ObjectName createProxyConnectorName(final ObjectName brokerObjectName, final String type, final String name) throws MalformedObjectNameException {
        return createProxyConnectorName(brokerObjectName.toString(), type, name);
    }
    
    public static ObjectName createProxyConnectorName(final String brokerObjectName, final String type, final String name) throws MalformedObjectNameException {
        String objectNameStr = brokerObjectName;
        objectNameStr = objectNameStr + ",connector=" + type + ",proxyConnectorName=" + JMXSupport.encodeObjectNamePart(name);
        final ObjectName objectName = new ObjectName(objectNameStr);
        return objectName;
    }
    
    public static ObjectName createJmsConnectorName(final ObjectName brokerObjectName, final String type, final String name) throws MalformedObjectNameException {
        return createJmsConnectorName(brokerObjectName.toString(), type, name);
    }
    
    public static ObjectName createJmsConnectorName(final String brokerObjectName, final String type, final String name) throws MalformedObjectNameException {
        String objectNameStr = brokerObjectName;
        objectNameStr = objectNameStr + ",connector=" + type + ",JmsConnectors=" + JMXSupport.encodeObjectNamePart(name);
        final ObjectName objectName = new ObjectName(objectNameStr);
        return objectName;
    }
    
    public static ObjectName createJobSchedulerServiceName(final ObjectName brokerObjectName) throws MalformedObjectNameException {
        return createJobSchedulerServiceName(brokerObjectName.toString());
    }
    
    public static ObjectName createJobSchedulerServiceName(final String brokerObjectName) throws MalformedObjectNameException {
        String objectNameStr = brokerObjectName;
        objectNameStr += ",service=JobScheduler,name=JMS";
        final ObjectName objectName = new ObjectName(objectNameStr);
        return objectName;
    }
    
    public static ObjectName createHealthServiceName(final ObjectName brokerObjectName) throws MalformedObjectNameException {
        return createHealthServiceName(brokerObjectName.toString());
    }
    
    public static ObjectName createHealthServiceName(final String brokerObjectName) throws MalformedObjectNameException {
        String objectNameStr = brokerObjectName;
        objectNameStr += ",service=Health";
        final ObjectName objectName = new ObjectName(objectNameStr);
        return objectName;
    }
    
    public static ObjectName createConnectionQuery(final String jmxDomainName, final String brokerName, final String name) throws MalformedObjectNameException {
        final ObjectName brokerMBeanName = createBrokerObjectName(jmxDomainName, brokerName);
        return createConnectionQuery(brokerMBeanName.toString(), name);
    }
    
    public static ObjectName createConnectionQuery(final String brokerMBeanName, final String name) throws MalformedObjectNameException {
        return new ObjectName(brokerMBeanName + ",connector=*,connectorName=*,connectionViewType=*,connectionName=" + JMXSupport.encodeObjectNamePart(name));
    }
    
    public static ObjectName createQueueQuery(final String brokerMBeanName) throws MalformedObjectNameException {
        return createConnectionQuery(brokerMBeanName, "*");
    }
    
    public static ObjectName createQueueQuery(final String brokerMBeanName, final String destinationName) throws MalformedObjectNameException {
        return new ObjectName(brokerMBeanName + ",destinationType=Queue,destinationName=" + JMXSupport.encodeObjectNamePart(destinationName));
    }
    
    public static ObjectName createTopicQuery(final String brokerMBeanName) throws MalformedObjectNameException {
        return createConnectionQuery(brokerMBeanName, "*");
    }
    
    public static ObjectName createTopicQuery(final String brokerMBeanName, final String destinationName) throws MalformedObjectNameException {
        return new ObjectName(brokerMBeanName + ",destinationType=Topic,destinationName=" + JMXSupport.encodeObjectNamePart(destinationName));
    }
    
    public static ObjectName createConsumerQueury(final String jmxDomainName, final String clientId) throws MalformedObjectNameException {
        return createConsumerQueury(jmxDomainName, null, clientId);
    }
    
    public static ObjectName createConsumerQueury(final String jmxDomainName, final String brokerName, final String clientId) throws MalformedObjectNameException {
        return new ObjectName(jmxDomainName + ":type=Broker,brokerName=" + ((brokerName != null) ? brokerName : "*") + ",destinationType=*,destinationName=*,endpoint=Consumer,clientId=" + JMXSupport.encodeObjectNamePart(clientId) + ",consumerId=*");
    }
    
    public static ObjectName createProducerQueury(final String jmxDomainName, final String clientId) throws MalformedObjectNameException {
        return createProducerQueury(jmxDomainName, null, clientId);
    }
    
    public static ObjectName createProducerQueury(final String jmxDomainName, final String brokerName, final String clientId) throws MalformedObjectNameException {
        return new ObjectName(jmxDomainName + ":type=Broker,brokerName=" + ((brokerName != null) ? brokerName : "*") + ",destinationType=*,destinationName=*,endpoint=Producer,clientId=" + JMXSupport.encodeObjectNamePart(clientId) + ",producerId=*");
    }
}
