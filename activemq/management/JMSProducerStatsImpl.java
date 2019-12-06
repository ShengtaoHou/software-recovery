// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.management;

import org.apache.activemq.util.IndentPrinter;
import javax.jms.Destination;

public class JMSProducerStatsImpl extends JMSEndpointStatsImpl
{
    private String destination;
    
    public JMSProducerStatsImpl(final JMSSessionStatsImpl sessionStats, final Destination destination) {
        super(sessionStats);
        if (destination != null) {
            this.destination = destination.toString();
        }
    }
    
    public JMSProducerStatsImpl(final CountStatisticImpl messageCount, final CountStatisticImpl pendingMessageCount, final CountStatisticImpl expiredMessageCount, final TimeStatisticImpl messageWaitTime, final TimeStatisticImpl messageRateTime, final String destination) {
        super(messageCount, pendingMessageCount, expiredMessageCount, messageWaitTime, messageRateTime);
        this.destination = destination;
    }
    
    public String getDestination() {
        return this.destination;
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("producer ");
        buffer.append(this.destination);
        buffer.append(" { ");
        buffer.append(super.toString());
        buffer.append(" }");
        return buffer.toString();
    }
    
    @Override
    public void dump(final IndentPrinter out) {
        out.printIndent();
        out.print("producer ");
        out.print(this.destination);
        out.println(" {");
        out.incrementIndent();
        super.dump(out);
        out.decrementIndent();
        out.printIndent();
        out.println("}");
    }
}
