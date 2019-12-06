// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.jdbc;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import org.apache.activemq.util.ServiceStopper;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.util.IOExceptionSupport;
import java.sql.Statement;
import org.slf4j.Logger;

public class LeaseDatabaseLocker extends AbstractJDBCLocker
{
    private static final Logger LOG;
    protected int maxAllowableDiffFromDBTime;
    protected long diffFromCurrentTime;
    protected String leaseHolderId;
    protected boolean handleStartException;
    
    public LeaseDatabaseLocker() {
        this.maxAllowableDiffFromDBTime = 0;
        this.diffFromCurrentTime = Long.MAX_VALUE;
    }
    
    public void doStart() throws Exception {
        if (this.lockAcquireSleepInterval < this.lockable.getLockKeepAlivePeriod()) {
            LeaseDatabaseLocker.LOG.warn("LockableService keep alive period: " + this.lockable.getLockKeepAlivePeriod() + ", which renews the lease, is less than lockAcquireSleepInterval: " + this.lockAcquireSleepInterval + ", the lease duration. These values will allow the lease to expire.");
        }
        LeaseDatabaseLocker.LOG.info(this.getLeaseHolderId() + " attempting to acquire exclusive lease to become the master");
        final String sql = this.getStatements().getLeaseObtainStatement();
        LeaseDatabaseLocker.LOG.debug(this.getLeaseHolderId() + " locking Query is " + sql);
        long now = 0L;
        while (!this.isStopping()) {
            Connection connection = null;
            PreparedStatement statement = null;
            try {
                connection = this.getConnection();
                this.initTimeDiff(connection);
                statement = connection.prepareStatement(sql);
                this.setQueryTimeout(statement);
                now = System.currentTimeMillis() + this.diffFromCurrentTime;
                statement.setString(1, this.getLeaseHolderId());
                statement.setLong(2, now + this.lockAcquireSleepInterval);
                statement.setLong(3, now);
                final int result = statement.executeUpdate();
                if (result == 1 && this.keepAlive()) {
                    break;
                }
                this.reportLeasOwnerShipAndDuration(connection);
            }
            catch (Exception e) {
                LeaseDatabaseLocker.LOG.debug(this.getLeaseHolderId() + " lease acquire failure: " + e, e);
                if (this.isStopping()) {
                    throw new Exception("Cannot start broker as being asked to shut down. Interrupted attempt to acquire lock: " + e, e);
                }
                if (this.handleStartException) {
                    this.lockable.getBrokerService().handleIOException(IOExceptionSupport.create(e));
                }
            }
            finally {
                this.close(statement);
                this.close(connection);
            }
            LeaseDatabaseLocker.LOG.info(this.getLeaseHolderId() + " failed to acquire lease.  Sleeping for " + this.lockAcquireSleepInterval + " milli(s) before trying again...");
            TimeUnit.MILLISECONDS.sleep(this.lockAcquireSleepInterval);
        }
        if (this.isStopping()) {
            throw new RuntimeException(this.getLeaseHolderId() + " failing lease acquire due to stop");
        }
        LeaseDatabaseLocker.LOG.info(this.getLeaseHolderId() + ", becoming master with lease expiry " + new Date(now) + " on dataSource: " + this.dataSource);
    }
    
    private void reportLeasOwnerShipAndDuration(final Connection connection) throws SQLException {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(this.getStatements().getLeaseOwnerStatement());
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                LeaseDatabaseLocker.LOG.info(this.getLeaseHolderId() + " Lease held by " + resultSet.getString(1) + " till " + new Date(resultSet.getLong(2)));
            }
        }
        finally {
            this.close(statement);
        }
    }
    
    protected long initTimeDiff(final Connection connection) throws SQLException {
        if (Long.MAX_VALUE == this.diffFromCurrentTime) {
            if (this.maxAllowableDiffFromDBTime > 0) {
                this.diffFromCurrentTime = this.determineTimeDifference(connection);
            }
            else {
                this.diffFromCurrentTime = 0L;
            }
        }
        return this.diffFromCurrentTime;
    }
    
    protected long determineTimeDifference(final Connection connection) throws SQLException {
        final PreparedStatement statement = connection.prepareStatement(this.getStatements().getCurrentDateTime());
        final ResultSet resultSet = statement.executeQuery();
        long result = 0L;
        if (resultSet.next()) {
            final Timestamp timestamp = resultSet.getTimestamp(1);
            final long diff = System.currentTimeMillis() - timestamp.getTime();
            if (Math.abs(diff) > this.maxAllowableDiffFromDBTime) {
                result = -diff;
            }
            LeaseDatabaseLocker.LOG.info(this.getLeaseHolderId() + " diff adjust from db: " + result + ", db time: " + timestamp);
        }
        return result;
    }
    
    public void doStop(final ServiceStopper stopper) throws Exception {
        if (this.lockable.getBrokerService() != null && this.lockable.getBrokerService().isRestartRequested()) {
            return;
        }
        this.releaseLease();
    }
    
    private void releaseLease() {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = this.getConnection();
            statement = connection.prepareStatement(this.getStatements().getLeaseUpdateStatement());
            statement.setString(1, null);
            statement.setLong(2, 0L);
            statement.setString(3, this.getLeaseHolderId());
            if (statement.executeUpdate() == 1) {
                LeaseDatabaseLocker.LOG.info(this.getLeaseHolderId() + ", released lease");
            }
        }
        catch (Exception e) {
            LeaseDatabaseLocker.LOG.error(this.getLeaseHolderId() + " failed to release lease: " + e, e);
        }
        finally {
            this.close(statement);
            this.close(connection);
        }
    }
    
    @Override
    public boolean keepAlive() throws IOException {
        boolean result = false;
        final String sql = this.getStatements().getLeaseUpdateStatement();
        LeaseDatabaseLocker.LOG.debug(this.getLeaseHolderId() + ", lease keepAlive Query is " + sql);
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = this.getConnection();
            this.initTimeDiff(connection);
            statement = connection.prepareStatement(sql);
            this.setQueryTimeout(statement);
            final long now = System.currentTimeMillis() + this.diffFromCurrentTime;
            statement.setString(1, this.getLeaseHolderId());
            statement.setLong(2, now + this.lockAcquireSleepInterval);
            statement.setString(3, this.getLeaseHolderId());
            result = (statement.executeUpdate() == 1);
            if (!result) {
                this.reportLeasOwnerShipAndDuration(connection);
            }
        }
        catch (Exception e) {
            LeaseDatabaseLocker.LOG.warn(this.getLeaseHolderId() + ", failed to update lease: " + e, e);
            final IOException ioe = IOExceptionSupport.create(e);
            this.lockable.getBrokerService().handleIOException(ioe);
            throw ioe;
        }
        finally {
            this.close(statement);
            this.close(connection);
        }
        return result;
    }
    
    public String getLeaseHolderId() {
        if (this.leaseHolderId == null && this.lockable.getBrokerService() != null) {
            this.leaseHolderId = this.lockable.getBrokerService().getBrokerName();
        }
        return this.leaseHolderId;
    }
    
    public void setLeaseHolderId(final String leaseHolderId) {
        this.leaseHolderId = leaseHolderId;
    }
    
    public int getMaxAllowableDiffFromDBTime() {
        return this.maxAllowableDiffFromDBTime;
    }
    
    public void setMaxAllowableDiffFromDBTime(final int maxAllowableDiffFromDBTime) {
        this.maxAllowableDiffFromDBTime = maxAllowableDiffFromDBTime;
    }
    
    public boolean isHandleStartException() {
        return this.handleStartException;
    }
    
    public void setHandleStartException(final boolean handleStartException) {
        this.handleStartException = handleStartException;
    }
    
    @Override
    public String toString() {
        return "LeaseDatabaseLocker owner:" + this.leaseHolderId + ",duration:" + this.lockAcquireSleepInterval + ",renew:" + this.lockAcquireSleepInterval;
    }
    
    static {
        LOG = LoggerFactory.getLogger(LeaseDatabaseLocker.class);
    }
}
