// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.jdbc.adapter;

import org.apache.activemq.store.jdbc.Statements;

public class ImageBasedJDBCAdaptor extends DefaultJDBCAdapter
{
    @Override
    public void setStatements(final Statements statements) {
        statements.setBinaryDataType("IMAGE");
        super.setStatements(statements);
    }
}
