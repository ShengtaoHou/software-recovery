// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter;

import org.apache.activemq.broker.region.MessageReference;
import java.io.IOException;
import org.apache.activemq.command.Message;

public class NonCachedMessageEvaluationContext extends MessageEvaluationContext
{
    @Override
    public Message getMessage() throws IOException {
        return (this.messageReference != null) ? this.messageReference.getMessage() : null;
    }
    
    @Override
    public void setMessageReference(final MessageReference messageReference) {
        this.messageReference = messageReference;
    }
    
    @Override
    protected void clearMessageCache() {
    }
}
