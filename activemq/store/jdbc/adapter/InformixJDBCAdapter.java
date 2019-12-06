// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.jdbc.adapter;

import org.apache.activemq.store.jdbc.Statements;

public class InformixJDBCAdapter extends BlobJDBCAdapter
{
    @Override
    public void setStatements(final Statements statements) {
        statements.setContainerNameDataType("VARCHAR(150)");
        statements.setStringIdDataType("VARCHAR(150)");
        statements.setLongDataType("INT8");
        statements.setSequenceDataType("INT8");
        statements.setBinaryDataType("BYTE");
        super.setStatements(statements);
    }
}
