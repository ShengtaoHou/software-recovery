// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.jdbc.adapter;

import java.io.InputStream;
import org.apache.activemq.util.ByteArrayOutputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.io.IOException;
import org.apache.activemq.command.XATransactionId;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.store.jdbc.TransactionContext;
import org.apache.activemq.store.jdbc.Statements;

public class BlobJDBCAdapter extends DefaultJDBCAdapter
{
    @Override
    public void setStatements(final Statements statements) {
        final String addMessageStatement = "INSERT INTO " + statements.getFullMessageTableName() + "(ID, MSGID_PROD, MSGID_SEQ, CONTAINER, EXPIRATION, PRIORITY, MSG, XID) VALUES (?, ?, ?, ?, ?, ?, empty_blob(), empty_blob())";
        statements.setAddMessageStatement(addMessageStatement);
        final String findMessageByIdStatement = "SELECT MSG FROM " + statements.getFullMessageTableName() + " WHERE ID=? FOR UPDATE";
        statements.setFindMessageByIdStatement(findMessageByIdStatement);
        super.setStatements(statements);
    }
    
    @Override
    public void doAddMessage(final TransactionContext c, final long sequence, final MessageId messageID, final ActiveMQDestination destination, final byte[] data, final long expiration, final byte priority, final XATransactionId xid) throws SQLException, IOException {
        PreparedStatement s = null;
        this.cleanupExclusiveLock.readLock().lock();
        try {
            s = c.getConnection().prepareStatement(this.statements.getAddMessageStatement());
            s.setLong(1, sequence);
            s.setString(2, messageID.getProducerId().toString());
            s.setLong(3, messageID.getProducerSequenceId());
            s.setString(4, destination.getQualifiedName());
            s.setLong(5, expiration);
            s.setLong(6, priority);
            if (s.executeUpdate() != 1) {
                throw new IOException("Failed to add broker message: " + messageID + " in container.");
            }
            s.close();
            this.updateBlob(c.getConnection(), this.statements.getFindMessageByIdStatement(), sequence, data);
            if (xid != null) {
                final byte[] xidVal = xid.getEncodedXidBytes();
                xidVal[0] = 43;
                this.updateBlob(c.getConnection(), this.statements.getFindXidByIdStatement(), sequence, xidVal);
            }
        }
        finally {
            this.cleanupExclusiveLock.readLock().unlock();
            DefaultJDBCAdapter.close(s);
        }
    }
    
    private void updateBlob(final Connection connection, final String findMessageByIdStatement, final long sequence, final byte[] data) throws SQLException, IOException {
        PreparedStatement s = null;
        ResultSet rs = null;
        try {
            s = connection.prepareStatement(this.statements.getFindMessageByIdStatement(), 1003, 1008);
            s.setLong(1, sequence);
            rs = s.executeQuery();
            if (!rs.next()) {
                throw new IOException("Failed select blob for message: " + sequence + " in container.");
            }
            final Blob blob = rs.getBlob(1);
            blob.truncate(0L);
            blob.setBytes(1L, data);
            rs.updateBlob(1, blob);
            rs.updateRow();
        }
        finally {
            DefaultJDBCAdapter.close(rs);
            DefaultJDBCAdapter.close(s);
        }
    }
    
    @Override
    public byte[] doGetMessage(final TransactionContext c, final MessageId id) throws SQLException, IOException {
        PreparedStatement s = null;
        ResultSet rs = null;
        this.cleanupExclusiveLock.readLock().lock();
        try {
            s = c.getConnection().prepareStatement(this.statements.getFindMessageStatement());
            s.setString(1, id.getProducerId().toString());
            s.setLong(2, id.getProducerSequenceId());
            rs = s.executeQuery();
            if (!rs.next()) {
                return null;
            }
            final Blob blob = rs.getBlob(1);
            final InputStream is = blob.getBinaryStream();
            final ByteArrayOutputStream os = new ByteArrayOutputStream((int)blob.length());
            int ch;
            while ((ch = is.read()) >= 0) {
                os.write(ch);
            }
            is.close();
            os.close();
            return os.toByteArray();
        }
        finally {
            this.cleanupExclusiveLock.readLock().unlock();
            DefaultJDBCAdapter.close(rs);
            DefaultJDBCAdapter.close(s);
        }
    }
}
