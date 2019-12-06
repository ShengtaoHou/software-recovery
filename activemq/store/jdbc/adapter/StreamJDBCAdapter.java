// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.jdbc.adapter;

import org.apache.activemq.util.ByteArrayInputStream;
import java.sql.PreparedStatement;
import java.io.InputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.io.ByteArrayOutputStream;
import java.sql.ResultSet;

public class StreamJDBCAdapter extends DefaultJDBCAdapter
{
    @Override
    protected byte[] getBinaryData(final ResultSet rs, final int index) throws SQLException {
        try {
            final InputStream is = rs.getBinaryStream(index);
            final ByteArrayOutputStream os = new ByteArrayOutputStream(4096);
            int ch;
            while ((ch = is.read()) >= 0) {
                os.write(ch);
            }
            is.close();
            os.close();
            return os.toByteArray();
        }
        catch (IOException e) {
            throw (SQLException)new SQLException("Error reading binary parameter: " + index).initCause(e);
        }
    }
    
    @Override
    protected void setBinaryData(final PreparedStatement s, final int index, final byte[] data) throws SQLException {
        s.setBinaryStream(index, new ByteArrayInputStream(data), data.length);
    }
}
