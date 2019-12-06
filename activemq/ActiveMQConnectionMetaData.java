// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Vector;
import java.util.Enumeration;
import javax.jms.ConnectionMetaData;

public final class ActiveMQConnectionMetaData implements ConnectionMetaData
{
    public static final String PROVIDER_VERSION;
    public static final int PROVIDER_MAJOR_VERSION;
    public static final int PROVIDER_MINOR_VERSION;
    public static final ActiveMQConnectionMetaData INSTANCE;
    
    private ActiveMQConnectionMetaData() {
    }
    
    @Override
    public String getJMSVersion() {
        return "1.1";
    }
    
    @Override
    public int getJMSMajorVersion() {
        return 1;
    }
    
    @Override
    public int getJMSMinorVersion() {
        return 1;
    }
    
    @Override
    public String getJMSProviderName() {
        return "ActiveMQ";
    }
    
    @Override
    public String getProviderVersion() {
        return ActiveMQConnectionMetaData.PROVIDER_VERSION;
    }
    
    @Override
    public int getProviderMajorVersion() {
        return ActiveMQConnectionMetaData.PROVIDER_MAJOR_VERSION;
    }
    
    @Override
    public int getProviderMinorVersion() {
        return ActiveMQConnectionMetaData.PROVIDER_MINOR_VERSION;
    }
    
    @Override
    public Enumeration<String> getJMSXPropertyNames() {
        final Vector<String> jmxProperties = new Vector<String>();
        jmxProperties.add("JMSXUserID");
        jmxProperties.add("JMSXGroupID");
        jmxProperties.add("JMSXGroupSeq");
        jmxProperties.add("JMSXDeliveryCount");
        jmxProperties.add("JMSXProducerTXID");
        return jmxProperties.elements();
    }
    
    static {
        INSTANCE = new ActiveMQConnectionMetaData();
        String version = null;
        int major = 0;
        int minor = 0;
        try {
            final Package p = Package.getPackage("org.apache.activemq");
            if (p != null) {
                version = p.getImplementationVersion();
                final Pattern pattern = Pattern.compile("(\\d+)\\.(\\d+).*");
                final Matcher m = pattern.matcher(version);
                if (m.matches()) {
                    major = Integer.parseInt(m.group(1));
                    minor = Integer.parseInt(m.group(2));
                }
            }
        }
        catch (Throwable t) {}
        PROVIDER_VERSION = version;
        PROVIDER_MAJOR_VERSION = major;
        PROVIDER_MINOR_VERSION = minor;
    }
}
