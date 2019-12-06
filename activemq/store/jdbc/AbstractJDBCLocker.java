// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.jdbc;

import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.IOException;
import org.apache.activemq.store.PersistenceAdapter;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.apache.activemq.broker.AbstractLocker;

public abstract class AbstractJDBCLocker extends AbstractLocker
{
    private static final Logger LOG;
    protected DataSource dataSource;
    private Statements statements;
    protected JDBCPersistenceAdapter jdbcAdapter;
    protected boolean createTablesOnStartup;
    protected int queryTimeout;
    
    public AbstractJDBCLocker() {
        this.queryTimeout = -1;
    }
    
    @Override
    public void configure(final PersistenceAdapter adapter) throws IOException {
        if (adapter instanceof JDBCPersistenceAdapter) {
            this.jdbcAdapter = (JDBCPersistenceAdapter)adapter;
            this.dataSource = ((JDBCPersistenceAdapter)adapter).getLockDataSource();
        }
    }
    
    protected Statements getStatements() {
        if (this.statements == null && this.jdbcAdapter != null) {
            this.statements = this.jdbcAdapter.getStatements();
        }
        return this.statements;
    }
    
    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public void setStatements(final Statements statements) {
        this.statements = statements;
    }
    
    protected void setQueryTimeout(final Statement statement) throws SQLException {
        if (this.queryTimeout > 0) {
            statement.setQueryTimeout(this.queryTimeout);
        }
    }
    
    public int getQueryTimeout() {
        return this.queryTimeout;
    }
    
    public void setQueryTimeout(final int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }
    
    public void setCreateTablesOnStartup(final boolean createTablesOnStartup) {
        this.createTablesOnStartup = createTablesOnStartup;
    }
    
    protected Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }
    
    protected void close(final Connection connection) {
        if (null != connection) {
            try {
                connection.close();
            }
            catch (SQLException e1) {
                AbstractJDBCLocker.LOG.debug("exception while closing connection: " + e1, e1);
            }
        }
    }
    
    protected void close(final Statement statement) {
        if (null != statement) {
            try {
                statement.close();
            }
            catch (SQLException e1) {
                AbstractJDBCLocker.LOG.debug("exception while closing statement: " + e1, e1);
            }
        }
    }
    
    public void preStart() {
        if (this.createTablesOnStartup) {
            final String[] createStatements = this.getStatements().getCreateLockSchemaStatements();
            Connection connection = null;
            Statement statement = null;
            try {
                connection = this.getConnection();
                statement = connection.createStatement();
                this.setQueryTimeout(statement);
                for (int i = 0; i < createStatements.length; ++i) {
                    AbstractJDBCLocker.LOG.debug("Executing SQL: " + createStatements[i]);
                    try {
                        statement.execute(createStatements[i]);
                    }
                    catch (SQLException e) {
                        AbstractJDBCLocker.LOG.info("Could not create lock tables; they could already exist. Failure was: " + createStatements[i] + " Message: " + e.getMessage() + " SQLState: " + e.getSQLState() + " Vendor code: " + e.getErrorCode());
                    }
                }
            }
            catch (SQLException e2) {
                AbstractJDBCLocker.LOG.warn("Could not create lock tables; Failure Message: " + e2.getMessage() + " SQLState: " + e2.getSQLState() + " Vendor code: " + e2.getErrorCode(), e2);
            }
            finally {
                this.close(statement);
                this.close(connection);
            }
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(AbstractJDBCLocker.class);
    }
}
