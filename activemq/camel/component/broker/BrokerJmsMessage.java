// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.camel.component.broker;

import org.apache.camel.impl.DefaultMessage;
import org.apache.camel.util.ObjectHelper;
import org.apache.camel.component.jms.JmsMessageHelper;
import org.apache.camel.component.jms.JmsBinding;
import javax.jms.Message;
import org.apache.camel.component.jms.JmsMessage;

public class BrokerJmsMessage extends JmsMessage
{
    public BrokerJmsMessage(final Message jmsMessage, final JmsBinding binding) {
        super(jmsMessage, binding);
    }
    
    public String toString() {
        if (this.getJmsMessage() != null) {
            return "BrokerJmsMessage[JMSMessageID: " + JmsMessageHelper.getJMSMessageID(this.getJmsMessage());
        }
        return "BrokerJmsMessage@" + ObjectHelper.getIdentityHashCode((Object)this);
    }
    
    public void copyFrom(final org.apache.camel.Message that) {
        super.copyFrom(that);
        if (that instanceof JmsMessage && this.getJmsMessage() == null) {
            this.setJmsMessage(((JmsMessage)that).getJmsMessage());
        }
    }
    
    public BrokerJmsMessage newInstance() {
        return new BrokerJmsMessage(null, this.getBinding());
    }
}
