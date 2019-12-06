// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.jdbc.adapter;

import org.apache.activemq.store.jdbc.Statements;

public class OracleJDBCAdapter extends DefaultJDBCAdapter
{
    @Override
    public void setStatements(final Statements statements) {
        statements.setLongDataType("NUMBER");
        statements.setSequenceDataType("NUMBER");
        super.setStatements(statements);
    }
}
