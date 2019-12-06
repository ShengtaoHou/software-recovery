// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.jms.pool;

import javax.jms.Connection;

public class JcaPooledConnectionFactory extends XaPooledConnectionFactory
{
    private String name;
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    @Override
    protected ConnectionPool createConnectionPool(final Connection connection) {
        return new JcaConnectionPool(connection, this.getTransactionManager(), this.getName());
    }
}
