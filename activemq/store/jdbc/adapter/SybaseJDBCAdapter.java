// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.jdbc.adapter;

import org.apache.activemq.store.jdbc.Statements;

public class SybaseJDBCAdapter extends ImageBasedJDBCAdaptor
{
    @Override
    public void setStatements(final Statements statements) {
        statements.setLockCreateStatement("LOCK TABLE " + statements.getFullLockTableName() + " IN EXCLUSIVE MODE");
        statements.setLongDataType("DECIMAL");
        statements.setSequenceDataType("DECIMAL");
        super.setStatements(statements);
    }
}
