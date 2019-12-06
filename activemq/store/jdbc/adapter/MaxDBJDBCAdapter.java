// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.jdbc.adapter;

import org.apache.activemq.store.jdbc.Statements;

public class MaxDBJDBCAdapter extends DefaultJDBCAdapter
{
    @Override
    public void setStatements(final Statements statements) {
        statements.setBinaryDataType("LONG BYTE");
        statements.setStringIdDataType("VARCHAR(250) ASCII");
        statements.setContainerNameDataType("VARCHAR(250) ASCII");
        statements.setLongDataType("INTEGER");
        statements.setSequenceDataType("INTEGER");
        super.setStatements(statements);
    }
}
