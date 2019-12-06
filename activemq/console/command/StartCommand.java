// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.command;

import org.apache.activemq.broker.BrokerService;
import java.util.concurrent.CountDownLatch;
import org.apache.activemq.broker.BrokerFactory;
import java.net.URI;
import java.util.List;

public class StartCommand extends AbstractCommand
{
    public static final String DEFAULT_CONFIG_URI = "xbean:activemq.xml";
    protected String[] helpFile;
    
    public StartCommand() {
        this.helpFile = new String[] { "Task Usage: Main start [start-options] [uri]", "Description: Creates and starts a broker using a configuration file, or a broker URI.", "", "Start Options:", "    -D<name>=<value>      Define a system property.", "    --version             Display the version information.", "    -h,-?,--help          Display the start broker help information.", "", "URI:", "", "    XBean based broker configuration:", "", "        Example: Main xbean:file:activemq.xml", "            Loads the xbean configuration file from the current working directory", "        Example: Main xbean:activemq.xml", "            Loads the xbean configuration file from the classpath", "", "    URI Parameter based broker configuration:", "", "        Example: Main broker:(tcp://localhost:61616, tcp://localhost:5000)?useJmx=true", "            Configures the broker with 2 transport connectors and jmx enabled", "        Example: Main broker:(tcp://localhost:61616, network:tcp://localhost:5000)?persistent=false", "            Configures the broker with 1 transport connector, and 1 network connector and persistence disabled", "" };
    }
    
    @Override
    public String getName() {
        return "start";
    }
    
    @Override
    public String getOneLineDescription() {
        return "Creates and starts a broker using a configuration file, or a broker URI.";
    }
    
    @Override
    protected void runTask(final List<String> brokerURIs) throws Exception {
        while (true) {
            BrokerService broker;
            try {
                URI configURI;
                if (brokerURIs.isEmpty()) {
                    configURI = new URI("xbean:activemq.xml");
                }
                else {
                    configURI = new URI(brokerURIs.get(0));
                }
                System.out.println("Loading message broker from: " + configURI);
                broker = BrokerFactory.createBroker(configURI);
                broker.start();
            }
            catch (Exception e) {
                this.context.printException(new RuntimeException("Failed to execute start task. Reason: " + e, e));
                throw e;
            }
            if (!broker.waitUntilStarted()) {
                throw new Exception(broker.getStartException());
            }
            final CountDownLatch shutdownLatch = new CountDownLatch(1);
            final Thread jvmShutdownHook = new Thread() {
                @Override
                public void run() {
                    try {
                        broker.stop();
                    }
                    catch (Exception ex) {}
                }
            };
            Runtime.getRuntime().addShutdownHook(jvmShutdownHook);
            broker.addShutdownHook(new Runnable() {
                @Override
                public void run() {
                    shutdownLatch.countDown();
                }
            });
            shutdownLatch.await();
            try {
                Runtime.getRuntime().removeShutdownHook(jvmShutdownHook);
            }
            catch (Throwable t) {}
            if (!broker.isRestartRequested()) {
                return;
            }
            System.out.println("Restarting broker");
        }
    }
    
    @Override
    protected void printHelp() {
        this.context.printHelp(this.helpFile);
    }
}
