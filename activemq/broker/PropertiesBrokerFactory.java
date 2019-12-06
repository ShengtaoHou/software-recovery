// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.io.IOException;
import java.net.URL;
import java.io.FileInputStream;
import java.io.File;
import java.util.Properties;
import java.util.Map;
import org.apache.activemq.util.IntrospectionSupport;
import java.net.URI;

public class PropertiesBrokerFactory implements BrokerFactoryHandler
{
    @Override
    public BrokerService createBroker(final URI brokerURI) throws Exception {
        final Map properties = this.loadProperties(brokerURI);
        final BrokerService brokerService = this.createBrokerService(brokerURI, properties);
        IntrospectionSupport.setProperties(brokerService, properties);
        return brokerService;
    }
    
    protected Map loadProperties(final URI brokerURI) throws IOException {
        final String remaining = brokerURI.getSchemeSpecificPart();
        final Properties properties = new Properties();
        final File file = new File(remaining);
        InputStream inputStream = null;
        if (file.exists()) {
            inputStream = new FileInputStream(file);
        }
        else {
            URL url = null;
            try {
                url = new URL(remaining);
            }
            catch (MalformedURLException e) {
                inputStream = this.findResourceOnClassPath(remaining);
                if (inputStream == null) {
                    throw new IOException("File does not exist: " + remaining + ", could not be found on the classpath and is not a valid URL: " + e);
                }
            }
            if (inputStream == null && url != null) {
                inputStream = url.openStream();
            }
        }
        if (inputStream != null) {
            properties.load(inputStream);
            inputStream.close();
        }
        try {
            final Properties systemProperties = System.getProperties();
            properties.putAll(systemProperties);
        }
        catch (Exception ex) {}
        return properties;
    }
    
    protected InputStream findResourceOnClassPath(final String remaining) {
        InputStream answer = Thread.currentThread().getContextClassLoader().getResourceAsStream(remaining);
        if (answer == null) {
            answer = this.getClass().getClassLoader().getResourceAsStream(remaining);
        }
        return answer;
    }
    
    protected BrokerService createBrokerService(final URI brokerURI, final Map properties) {
        return new BrokerService();
    }
}
