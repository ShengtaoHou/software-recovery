// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.jdbc.adapter;

import java.sql.SQLException;
import java.sql.Blob;
import java.sql.ResultSet;
import org.apache.activemq.store.jdbc.Statements;

public class DB2JDBCAdapter extends DefaultJDBCAdapter
{
    @Override
    public void setStatements(final Statements statements) {
        final String lockCreateStatement = "LOCK TABLE " + statements.getFullLockTableName() + " IN EXCLUSIVE MODE";
        statements.setLockCreateStatement(lockCreateStatement);
        super.setStatements(statements);
    }
    
    @Override
    protected byte[] getBinaryData(final ResultSet rs, final int index) throws SQLException {
        final Blob aBlob = rs.getBlob(index);
        return aBlob.getBytes(1L, (int)aBlob.length());
    }
}
