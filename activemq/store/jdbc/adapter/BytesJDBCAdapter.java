// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.jdbc.adapter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class BytesJDBCAdapter extends DefaultJDBCAdapter
{
    @Override
    protected byte[] getBinaryData(final ResultSet rs, final int index) throws SQLException {
        return rs.getBytes(index);
    }
    
    @Override
    protected void setBinaryData(final PreparedStatement s, final int index, final byte[] data) throws SQLException {
        s.setBytes(index, data);
    }
}
