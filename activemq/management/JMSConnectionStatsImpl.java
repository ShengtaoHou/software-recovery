// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.management;

import org.apache.activemq.util.IndentPrinter;
import org.apache.activemq.ActiveMQSession;
import java.util.List;

public class JMSConnectionStatsImpl extends StatsImpl
{
    private List sessions;
    private boolean transactional;
    
    public JMSConnectionStatsImpl(final List sessions, final boolean transactional) {
        this.sessions = sessions;
        this.transactional = transactional;
    }
    
    public JMSSessionStatsImpl[] getSessions() {
        final Object[] sessionArray = this.sessions.toArray();
        final int size = sessionArray.length;
        final JMSSessionStatsImpl[] answer = new JMSSessionStatsImpl[size];
        for (int i = 0; i < size; ++i) {
            final ActiveMQSession session = (ActiveMQSession)sessionArray[i];
            answer[i] = session.getSessionStats();
        }
        return answer;
    }
    
    @Override
    public void reset() {
        super.reset();
        final JMSSessionStatsImpl[] stats = this.getSessions();
        for (int size = stats.length, i = 0; i < size; ++i) {
            stats[i].reset();
        }
    }
    
    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        final JMSSessionStatsImpl[] stats = this.getSessions();
        for (int size = stats.length, i = 0; i < size; ++i) {
            stats[i].setEnabled(enabled);
        }
    }
    
    public boolean isTransactional() {
        return this.transactional;
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer("connection{ ");
        final JMSSessionStatsImpl[] array = this.getSessions();
        for (int i = 0; i < array.length; ++i) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append(Integer.toString(i));
            buffer.append(" = ");
            buffer.append(array[i]);
        }
        buffer.append(" }");
        return buffer.toString();
    }
    
    public void dump(final IndentPrinter out) {
        out.printIndent();
        out.println("connection {");
        out.incrementIndent();
        final JMSSessionStatsImpl[] array = this.getSessions();
        for (int i = 0; i < array.length; ++i) {
            final JMSSessionStatsImpl sessionStat = array[i];
            out.printIndent();
            out.println("session {");
            out.incrementIndent();
            sessionStat.dump(out);
            out.decrementIndent();
            out.printIndent();
            out.println("}");
        }
        out.decrementIndent();
        out.printIndent();
        out.println("}");
        out.flush();
    }
}
