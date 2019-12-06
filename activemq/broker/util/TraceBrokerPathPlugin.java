// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.util;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.command.MessageDispatch;
import org.slf4j.Logger;
import org.apache.activemq.broker.BrokerPluginSupport;

public class TraceBrokerPathPlugin extends BrokerPluginSupport
{
    private String stampProperty;
    private static final Logger LOG;
    
    public TraceBrokerPathPlugin() {
        this.stampProperty = "BrokerPath";
    }
    
    public String getStampProperty() {
        return this.stampProperty;
    }
    
    public void setStampProperty(final String stampProperty) {
        if (stampProperty != null && !stampProperty.isEmpty()) {
            this.stampProperty = stampProperty;
        }
    }
    
    @Override
    public void preProcessDispatch(final MessageDispatch messageDispatch) {
        try {
            if (messageDispatch != null && messageDispatch.getMessage() != null) {
                String brokerStamp = (String)messageDispatch.getMessage().getProperty(this.getStampProperty());
                if (brokerStamp == null) {
                    brokerStamp = this.getBrokerName();
                }
                else {
                    brokerStamp = brokerStamp + "," + this.getBrokerName();
                }
                messageDispatch.getMessage().setProperty(this.getStampProperty(), brokerStamp);
                messageDispatch.getMessage().setMarshalledProperties(null);
            }
        }
        catch (IOException ioe) {
            TraceBrokerPathPlugin.LOG.warn("Setting broker property failed", ioe);
        }
        super.preProcessDispatch(messageDispatch);
    }
    
    static {
        LOG = LoggerFactory.getLogger(TraceBrokerPathPlugin.class);
    }
}
