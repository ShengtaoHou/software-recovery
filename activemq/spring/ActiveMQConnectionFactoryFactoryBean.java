// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.spring;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.FactoryBean;

public class ActiveMQConnectionFactoryFactoryBean implements FactoryBean
{
    private List<String> tcpHostAndPorts;
    private Long maxInactivityDuration;
    private String tcpProperties;
    private Long maxReconnectDelay;
    private String failoverProperties;
    
    public ActiveMQConnectionFactoryFactoryBean() {
        this.tcpHostAndPorts = new ArrayList<String>();
    }
    
    public Object getObject() throws Exception {
        final ActiveMQConnectionFactory answer = new ActiveMQConnectionFactory();
        final String brokerURL = this.getBrokerURL();
        answer.setBrokerURL(brokerURL);
        return answer;
    }
    
    public String getBrokerURL() {
        final StringBuffer buffer = new StringBuffer("failover:(");
        int counter = 0;
        for (final String tcpHostAndPort : this.tcpHostAndPorts) {
            if (counter++ > 0) {
                buffer.append(",");
            }
            buffer.append(this.createTcpHostAndPortUrl(tcpHostAndPort));
        }
        buffer.append(")");
        final List<String> parameters = new ArrayList<String>();
        if (this.maxReconnectDelay != null) {
            parameters.add("maxReconnectDelay=" + this.maxReconnectDelay);
        }
        if (this.notEmpty(this.failoverProperties)) {
            parameters.add(this.failoverProperties);
        }
        buffer.append(this.asQueryString(parameters));
        return buffer.toString();
    }
    
    public Class getObjectType() {
        return ActiveMQConnectionFactory.class;
    }
    
    public boolean isSingleton() {
        return true;
    }
    
    public List<String> getTcpHostAndPorts() {
        return this.tcpHostAndPorts;
    }
    
    public void setTcpHostAndPorts(final List<String> tcpHostAndPorts) {
        this.tcpHostAndPorts = tcpHostAndPorts;
    }
    
    public void setTcpHostAndPort(final String tcpHostAndPort) {
        (this.tcpHostAndPorts = new ArrayList<String>()).add(tcpHostAndPort);
    }
    
    public Long getMaxInactivityDuration() {
        return this.maxInactivityDuration;
    }
    
    public void setMaxInactivityDuration(final Long maxInactivityDuration) {
        this.maxInactivityDuration = maxInactivityDuration;
    }
    
    public String getTcpProperties() {
        return this.tcpProperties;
    }
    
    public void setTcpProperties(final String tcpProperties) {
        this.tcpProperties = tcpProperties;
    }
    
    public Long getMaxReconnectDelay() {
        return this.maxReconnectDelay;
    }
    
    public void setMaxReconnectDelay(final Long maxReconnectDelay) {
        this.maxReconnectDelay = maxReconnectDelay;
    }
    
    public String getFailoverProperties() {
        return this.failoverProperties;
    }
    
    public void setFailoverProperties(final String failoverProperties) {
        this.failoverProperties = failoverProperties;
    }
    
    protected String asQueryString(final List<String> parameters) {
        final int size = parameters.size();
        if (size < 1) {
            return "";
        }
        final StringBuffer buffer = new StringBuffer("?");
        buffer.append(parameters.get(0));
        for (int i = 1; i < size; ++i) {
            buffer.append("&");
            buffer.append(parameters.get(i));
        }
        return buffer.toString();
    }
    
    protected String createTcpHostAndPortUrl(final String tcpHostAndPort) {
        final List<String> parameters = new ArrayList<String>();
        if (this.maxInactivityDuration != null) {
            parameters.add("wireFormat.maxInactivityDuration=" + this.maxInactivityDuration);
        }
        if (this.notEmpty(this.tcpProperties)) {
            parameters.add(this.tcpProperties);
        }
        return tcpHostAndPort + this.asQueryString(parameters);
    }
    
    protected boolean notEmpty(final String text) {
        return text != null && text.length() > 0;
    }
}
