// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

public class DiscoveryEvent implements DataStructure
{
    public static final byte DATA_STRUCTURE_TYPE = 40;
    protected String serviceName;
    protected String brokerName;
    
    public DiscoveryEvent() {
    }
    
    public DiscoveryEvent(final String serviceName) {
        this.serviceName = serviceName;
    }
    
    protected DiscoveryEvent(final DiscoveryEvent copy) {
        this.serviceName = copy.serviceName;
        this.brokerName = copy.brokerName;
    }
    
    @Override
    public byte getDataStructureType() {
        return 40;
    }
    
    public String getServiceName() {
        return this.serviceName;
    }
    
    public void setServiceName(final String serviceName) {
        this.serviceName = serviceName;
    }
    
    public String getBrokerName() {
        return this.brokerName;
    }
    
    public void setBrokerName(final String name) {
        this.brokerName = name;
    }
    
    @Override
    public boolean isMarshallAware() {
        return false;
    }
}
