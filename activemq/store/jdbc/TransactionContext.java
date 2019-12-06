// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.jdbc;

import org.slf4j.LoggerFactory;
import java.sql.SQLException;
import org.apache.activemq.util.IOExceptionSupport;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import javax.sql.DataSource;
import org.slf4j.Logger;

public class TransactionContext
{
    private static final Logger LOG;
    private final DataSource dataSource;
    private final JDBCPersistenceAdapter persistenceAdapter;
    private Connection connection;
    private boolean inTx;
    private PreparedStatement addMessageStatement;
    private PreparedStatement removedMessageStatement;
    private PreparedStatement updateLastAckStatement;
    private int transactionIsolation;
    
    public TransactionContext(final JDBCPersistenceAdapter persistenceAdapter) throws IOException {
        this.transactionIsolation = 1;
        this.persistenceAdapter = persistenceAdapter;
        this.dataSource = persistenceAdapter.getDataSource();
    }
    
    public Connection getConnection() throws IOException {
        if (this.connection == null) {
            try {
                this.connection = this.dataSource.getConnection();
                if (this.persistenceAdapter.isChangeAutoCommitAllowed()) {
                    final boolean autoCommit = !this.inTx;
                    if (this.connection.getAutoCommit() != autoCommit) {
                        TransactionContext.LOG.trace("Setting auto commit to {} on connection {}", (Object)autoCommit, this.connection);
                        this.connection.setAutoCommit(autoCommit);
                    }
                }
            }
            catch (SQLException e) {
                JDBCPersistenceAdapter.log("Could not get JDBC connection: ", e);
                this.inTx = false;
                this.close();
                final IOException ioe = IOExceptionSupport.create(e);
                this.persistenceAdapter.getBrokerService().handleIOException(ioe);
                throw ioe;
            }
            try {
                this.connection.setTransactionIsolation(this.transactionIsolation);
            }
            catch (Throwable e2) {
                TransactionContext.LOG.trace("Cannot set transaction isolation to " + this.transactionIsolation + " due " + e2.getMessage() + ". This exception is ignored.", e2);
            }
        }
        return this.connection;
    }
    
    public void executeBatch() throws SQLException {
        try {
            this.executeBatch(this.addMessageStatement, "Failed add a message");
        }
        finally {
            this.addMessageStatement = null;
            try {
                this.executeBatch(this.removedMessageStatement, "Failed to remove a message");
            }
            finally {
                this.removedMessageStatement = null;
                try {
                    this.executeBatch(this.updateLastAckStatement, "Failed to ack a message");
                }
                finally {
                    this.updateLastAckStatement = null;
                }
            }
        }
    }
    
    private void executeBatch(final PreparedStatement p, final String message) throws SQLException {
        if (p == null) {
            return;
        }
        try {
            final int[] rc = p.executeBatch();
            for (int i = 0; i < rc.length; ++i) {
                final int code = rc[i];
                if (code < 0 && code != -2) {
                    throw new SQLException(message + ". Response code: " + code);
                }
            }
        }
        finally {
            try {
                p.close();
            }
            catch (Throwable t) {}
        }
    }
    
    public void close() throws IOException {
        if (!this.inTx) {
            try {
                try {
                    this.executeBatch();
                }
                finally {
                    if (this.connection != null && !this.connection.getAutoCommit()) {
                        this.connection.commit();
                    }
                }
            }
            catch (SQLException e) {
                JDBCPersistenceAdapter.log("Error while closing connection: ", e);
                final IOException ioe = IOExceptionSupport.create(e);
                this.persistenceAdapter.getBrokerService().handleIOException(ioe);
                throw ioe;
            }
            finally {
                try {
                    if (this.connection != null) {
                        this.connection.close();
                    }
                }
                catch (Throwable e2) {
                    TransactionContext.LOG.trace("Closing connection failed due: " + e2.getMessage() + ". This exception is ignored.", e2);
                    this.connection = null;
                }
                finally {
                    this.connection = null;
                }
            }
        }
    }
    
    public void begin() throws IOException {
        if (this.inTx) {
            throw new IOException("Already started.");
        }
        this.inTx = true;
        this.connection = this.getConnection();
    }
    
    public void commit() throws IOException {
        if (!this.inTx) {
            throw new IOException("Not started.");
        }
        try {
            this.executeBatch();
            if (!this.connection.getAutoCommit()) {
                this.connection.commit();
            }
        }
        catch (SQLException e) {
            JDBCPersistenceAdapter.log("Commit failed: ", e);
            try {
                this.doRollback();
            }
            catch (Exception ex) {}
            final IOException ioe = IOExceptionSupport.create(e);
            this.persistenceAdapter.getBrokerService().handleIOException(ioe);
            throw ioe;
        }
        finally {
            this.inTx = false;
            this.close();
        }
    }
    
    public void rollback() throws IOException {
        if (!this.inTx) {
            throw new IOException("Not started.");
        }
        try {
            this.doRollback();
        }
        catch (SQLException e) {
            JDBCPersistenceAdapter.log("Rollback failed: ", e);
            throw IOExceptionSupport.create(e);
        }
        finally {
            this.inTx = false;
            this.close();
        }
    }
    
    private void doRollback() throws SQLException {
        if (this.addMessageStatement != null) {
            this.addMessageStatement.close();
            this.addMessageStatement = null;
        }
        if (this.removedMessageStatement != null) {
            this.removedMessageStatement.close();
            this.removedMessageStatement = null;
        }
        if (this.updateLastAckStatement != null) {
            this.updateLastAckStatement.close();
            this.updateLastAckStatement = null;
        }
        this.connection.rollback();
    }
    
    public PreparedStatement getAddMessageStatement() {
        return this.addMessageStatement;
    }
    
    public void setAddMessageStatement(final PreparedStatement addMessageStatement) {
        this.addMessageStatement = addMessageStatement;
    }
    
    public PreparedStatement getUpdateLastAckStatement() {
        return this.updateLastAckStatement;
    }
    
    public void setUpdateLastAckStatement(final PreparedStatement ackMessageStatement) {
        this.updateLastAckStatement = ackMessageStatement;
    }
    
    public PreparedStatement getRemovedMessageStatement() {
        return this.removedMessageStatement;
    }
    
    public void setRemovedMessageStatement(final PreparedStatement removedMessageStatement) {
        this.removedMessageStatement = removedMessageStatement;
    }
    
    public void setTransactionIsolation(final int transactionIsolation) {
        this.transactionIsolation = transactionIsolation;
    }
    
    static {
        LOG = LoggerFactory.getLogger(TransactionContext.class);
    }
}
