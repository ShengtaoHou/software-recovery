// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.jdbc.adapter;

import org.apache.activemq.store.jdbc.Statements;

public class PostgresqlJDBCAdapter extends BytesJDBCAdapter
{
    public String acksPkName;
    
    public PostgresqlJDBCAdapter() {
        this.acksPkName = "activemq_acks_pkey";
    }
    
    @Override
    public void setStatements(final Statements statements) {
        statements.setBinaryDataType("BYTEA");
        statements.setDropAckPKAlterStatementEnd("DROP CONSTRAINT \"" + this.getAcksPkName() + "\"");
        super.setStatements(statements);
    }
    
    private String getAcksPkName() {
        return this.acksPkName;
    }
    
    public void setAcksPkName(final String acksPkName) {
        this.acksPkName = acksPkName;
    }
}
