// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import java.io.IOException;
import org.apache.activemq.util.MarshallingSupport;
import java.util.Properties;
import org.apache.activemq.state.CommandVisitor;

public class BrokerInfo extends BaseCommand
{
    private static final String PASSIVE_SLAVE_KEY = "passiveSlave";
    public static final byte DATA_STRUCTURE_TYPE = 2;
    BrokerId brokerId;
    String brokerURL;
    boolean slaveBroker;
    boolean masterBroker;
    boolean faultTolerantConfiguration;
    boolean networkConnection;
    boolean duplexConnection;
    BrokerInfo[] peerBrokerInfos;
    String brokerName;
    long connectionId;
    String brokerUploadUrl;
    String networkProperties;
    transient int refCount;
    
    public BrokerInfo() {
        this.refCount = 0;
    }
    
    public BrokerInfo copy() {
        final BrokerInfo copy = new BrokerInfo();
        this.copy(copy);
        return copy;
    }
    
    private void copy(final BrokerInfo copy) {
        super.copy(copy);
        copy.brokerId = this.brokerId;
        copy.brokerURL = this.brokerURL;
        copy.slaveBroker = this.slaveBroker;
        copy.masterBroker = this.masterBroker;
        copy.faultTolerantConfiguration = this.faultTolerantConfiguration;
        copy.networkConnection = this.networkConnection;
        copy.duplexConnection = this.duplexConnection;
        copy.peerBrokerInfos = this.peerBrokerInfos;
        copy.brokerName = this.brokerName;
        copy.connectionId = this.connectionId;
        copy.brokerUploadUrl = this.brokerUploadUrl;
        copy.networkProperties = this.networkProperties;
    }
    
    @Override
    public boolean isBrokerInfo() {
        return true;
    }
    
    @Override
    public byte getDataStructureType() {
        return 2;
    }
    
    public BrokerId getBrokerId() {
        return this.brokerId;
    }
    
    public void setBrokerId(final BrokerId brokerId) {
        this.brokerId = brokerId;
    }
    
    public String getBrokerURL() {
        return this.brokerURL;
    }
    
    public void setBrokerURL(final String brokerURL) {
        this.brokerURL = brokerURL;
    }
    
    public BrokerInfo[] getPeerBrokerInfos() {
        return this.peerBrokerInfos;
    }
    
    public void setPeerBrokerInfos(final BrokerInfo[] peerBrokerInfos) {
        this.peerBrokerInfos = peerBrokerInfos;
    }
    
    public String getBrokerName() {
        return this.brokerName;
    }
    
    public void setBrokerName(final String brokerName) {
        this.brokerName = brokerName;
    }
    
    @Override
    public Response visit(final CommandVisitor visitor) throws Exception {
        return visitor.processBrokerInfo(this);
    }
    
    public boolean isSlaveBroker() {
        return this.slaveBroker;
    }
    
    public void setSlaveBroker(final boolean slaveBroker) {
        this.slaveBroker = slaveBroker;
    }
    
    public boolean isMasterBroker() {
        return this.masterBroker;
    }
    
    public void setMasterBroker(final boolean masterBroker) {
        this.masterBroker = masterBroker;
    }
    
    public boolean isFaultTolerantConfiguration() {
        return this.faultTolerantConfiguration;
    }
    
    public void setFaultTolerantConfiguration(final boolean faultTolerantConfiguration) {
        this.faultTolerantConfiguration = faultTolerantConfiguration;
    }
    
    public boolean isDuplexConnection() {
        return this.duplexConnection;
    }
    
    public void setDuplexConnection(final boolean duplexConnection) {
        this.duplexConnection = duplexConnection;
    }
    
    public boolean isNetworkConnection() {
        return this.networkConnection;
    }
    
    public void setNetworkConnection(final boolean networkConnection) {
        this.networkConnection = networkConnection;
    }
    
    public long getConnectionId() {
        return this.connectionId;
    }
    
    public void setConnectionId(final long connectionId) {
        this.connectionId = connectionId;
    }
    
    public String getBrokerUploadUrl() {
        return this.brokerUploadUrl;
    }
    
    public void setBrokerUploadUrl(final String brokerUploadUrl) {
        this.brokerUploadUrl = brokerUploadUrl;
    }
    
    public String getNetworkProperties() {
        return this.networkProperties;
    }
    
    public void setNetworkProperties(final String networkProperties) {
        this.networkProperties = networkProperties;
    }
    
    public boolean isPassiveSlave() {
        boolean result = false;
        final Properties props = this.getProperties();
        if (props != null) {
            result = Boolean.parseBoolean(props.getProperty("passiveSlave", "false"));
        }
        return result;
    }
    
    public void setPassiveSlave(final boolean value) {
        final Properties props = new Properties();
        props.put("passiveSlave", Boolean.toString(value));
        try {
            this.networkProperties = MarshallingSupport.propertiesToString(props);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Properties getProperties() {
        Properties result = null;
        try {
            result = MarshallingSupport.stringToProperties(this.getNetworkProperties());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public int getRefCount() {
        return this.refCount;
    }
    
    public void incrementRefCount() {
        ++this.refCount;
    }
    
    public int decrementRefCount() {
        return --this.refCount;
    }
}
