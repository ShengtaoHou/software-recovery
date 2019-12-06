// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.management;

import org.apache.activemq.util.IndentPrinter;
import javax.jms.Destination;

public class JMSConsumerStatsImpl extends JMSEndpointStatsImpl
{
    private String origin;
    
    public JMSConsumerStatsImpl(final JMSSessionStatsImpl sessionStats, final Destination destination) {
        super(sessionStats);
        if (destination != null) {
            this.origin = destination.toString();
        }
    }
    
    public JMSConsumerStatsImpl(final CountStatisticImpl messageCount, final CountStatisticImpl pendingMessageCount, final CountStatisticImpl expiredMessageCount, final TimeStatisticImpl messageWaitTime, final TimeStatisticImpl messageRateTime, final String origin) {
        super(messageCount, pendingMessageCount, expiredMessageCount, messageWaitTime, messageRateTime);
        this.origin = origin;
    }
    
    public String getOrigin() {
        return this.origin;
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("consumer ");
        buffer.append(this.origin);
        buffer.append(" { ");
        buffer.append(super.toString());
        buffer.append(" }");
        return buffer.toString();
    }
    
    @Override
    public void dump(final IndentPrinter out) {
        out.printIndent();
        out.print("consumer ");
        out.print(this.origin);
        out.println(" {");
        out.incrementIndent();
        super.dump(out);
        out.decrementIndent();
        out.printIndent();
        out.println("}");
    }
}
