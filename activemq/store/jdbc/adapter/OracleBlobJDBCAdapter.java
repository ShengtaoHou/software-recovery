// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.jdbc.adapter;

import java.sql.SQLException;
import java.sql.Blob;
import java.sql.ResultSet;
import org.apache.activemq.store.jdbc.Statements;

public class OracleBlobJDBCAdapter extends BlobJDBCAdapter
{
    @Override
    public void setStatements(final Statements statements) {
        statements.setLongDataType("NUMBER");
        statements.setSequenceDataType("NUMBER");
        super.setStatements(statements);
    }
    
    @Override
    protected byte[] getBinaryData(final ResultSet rs, final int index) throws SQLException {
        final Blob aBlob = rs.getBlob(index);
        if (aBlob == null) {
            return null;
        }
        return aBlob.getBytes(1L, (int)aBlob.length());
    }
}
