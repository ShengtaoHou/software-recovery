// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

import java.net.URI;
import java.io.IOException;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.util.FactoryFinder;

public final class BrokerFactory
{
    private static final FactoryFinder BROKER_FACTORY_HANDLER_FINDER;
    private static final ThreadLocal<Boolean> START_DEFAULT;
    
    private BrokerFactory() {
    }
    
    public static BrokerFactoryHandler createBrokerFactoryHandler(final String type) throws IOException {
        try {
            return (BrokerFactoryHandler)BrokerFactory.BROKER_FACTORY_HANDLER_FINDER.newInstance(type);
        }
        catch (Throwable e) {
            throw IOExceptionSupport.create("Could not load " + type + " factory:" + e, e);
        }
    }
    
    public static BrokerService createBroker(final URI brokerURI) throws Exception {
        return createBroker(brokerURI, false);
    }
    
    public static BrokerService createBroker(final URI brokerURI, final boolean startBroker) throws Exception {
        if (brokerURI.getScheme() == null) {
            throw new IllegalArgumentException("Invalid broker URI, no scheme specified: " + brokerURI);
        }
        final BrokerFactoryHandler handler = createBrokerFactoryHandler(brokerURI.getScheme());
        final BrokerService broker = handler.createBroker(brokerURI);
        if (startBroker) {
            broker.start();
        }
        return broker;
    }
    
    public static BrokerService createBroker(final String brokerURI) throws Exception {
        return createBroker(new URI(brokerURI));
    }
    
    public static BrokerService createBroker(final String brokerURI, final boolean startBroker) throws Exception {
        return createBroker(new URI(brokerURI), startBroker);
    }
    
    public static void setStartDefault(final boolean startDefault) {
        BrokerFactory.START_DEFAULT.set(startDefault);
    }
    
    public static void resetStartDefault() {
        BrokerFactory.START_DEFAULT.remove();
    }
    
    public static boolean getStartDefault() {
        final Boolean value = BrokerFactory.START_DEFAULT.get();
        return value == null || value;
    }
    
    static {
        BROKER_FACTORY_HANDLER_FINDER = new FactoryFinder("META-INF/services/org/apache/activemq/broker/");
        START_DEFAULT = new ThreadLocal<Boolean>();
    }
}
