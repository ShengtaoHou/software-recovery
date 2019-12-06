// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.management;

import org.apache.activemq.util.IndentPrinter;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.activemq.ActiveMQConnection;
import java.util.List;

public class JMSStatsImpl extends StatsImpl
{
    private List<ActiveMQConnection> connections;
    
    public JMSStatsImpl() {
        this.connections = new CopyOnWriteArrayList<ActiveMQConnection>();
    }
    
    public JMSConnectionStatsImpl[] getConnections() {
        final Object[] connectionArray = this.connections.toArray();
        final int size = connectionArray.length;
        final JMSConnectionStatsImpl[] answer = new JMSConnectionStatsImpl[size];
        for (int i = 0; i < size; ++i) {
            final ActiveMQConnection connection = (ActiveMQConnection)connectionArray[i];
            answer[i] = connection.getConnectionStats();
        }
        return answer;
    }
    
    public void addConnection(final ActiveMQConnection connection) {
        this.connections.add(connection);
    }
    
    public void removeConnection(final ActiveMQConnection connection) {
        this.connections.remove(connection);
    }
    
    public void dump(final IndentPrinter out) {
        out.printIndent();
        out.println("factory {");
        out.incrementIndent();
        final JMSConnectionStatsImpl[] array = this.getConnections();
        for (int i = 0; i < array.length; ++i) {
            final JMSConnectionStatsImpl connectionStat = array[i];
            connectionStat.dump(out);
        }
        out.decrementIndent();
        out.printIndent();
        out.println("}");
        out.flush();
    }
    
    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        final JMSConnectionStatsImpl[] stats = this.getConnections();
        for (int size = stats.length, i = 0; i < size; ++i) {
            stats[i].setEnabled(enabled);
        }
    }
}
