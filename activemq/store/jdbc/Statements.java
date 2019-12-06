// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.jdbc;

public class Statements
{
    protected String messageTableName;
    protected String durableSubAcksTableName;
    protected String lockTableName;
    protected String binaryDataType;
    protected String containerNameDataType;
    protected String msgIdDataType;
    protected String sequenceDataType;
    protected String longDataType;
    protected String stringIdDataType;
    protected boolean useExternalMessageReferences;
    private String tablePrefix;
    private String addMessageStatement;
    private String updateMessageStatement;
    private String removeMessageStatement;
    private String findMessageSequenceIdStatement;
    private String findMessageStatement;
    private String findMessageByIdStatement;
    private String findAllMessagesStatement;
    private String findLastSequenceIdInMsgsStatement;
    private String findLastSequenceIdInAcksStatement;
    private String createDurableSubStatement;
    private String findDurableSubStatement;
    private String findAllDurableSubsStatement;
    private String updateLastPriorityAckRowOfDurableSubStatement;
    private String deleteSubscriptionStatement;
    private String findAllDurableSubMessagesStatement;
    private String findDurableSubMessagesStatement;
    private String findDurableSubMessagesByPriorityStatement;
    private String findAllDestinationsStatement;
    private String removeAllMessagesStatement;
    private String removeAllSubscriptionsStatement;
    private String[] createSchemaStatements;
    private String[] createLockSchemaStatements;
    private String[] dropSchemaStatements;
    private String lockCreateStatement;
    private String lockUpdateStatement;
    private String nextDurableSubscriberMessageStatement;
    private String durableSubscriberMessageCountStatement;
    private String lastAckedDurableSubscriberMessageStatement;
    private String destinationMessageCountStatement;
    private String findNextMessagesStatement;
    private String findNextMessagesByPriorityStatement;
    private boolean useLockCreateWhereClause;
    private String findAllMessageIdsStatement;
    private String lastProducerSequenceIdStatement;
    private String selectDurablePriorityAckStatement;
    private String insertDurablePriorityAckStatement;
    private String updateDurableLastAckStatement;
    private String deleteOldMessagesStatementWithPriority;
    private String durableSubscriberMessageCountStatementWithPriority;
    private String dropAckPKAlterStatementEnd;
    private String updateXidFlagStatement;
    private String findOpsPendingOutcomeStatement;
    private String clearXidFlagStatement;
    private String updateDurableLastAckInTxStatement;
    private String findAcksPendingOutcomeStatement;
    private String clearDurableLastAckInTxStatement;
    private String updateDurableLastAckWithPriorityStatement;
    private String updateDurableLastAckWithPriorityInTxStatement;
    private String findXidByIdStatement;
    private String leaseObtainStatement;
    private String currentDateTimeStatement;
    private String leaseUpdateStatement;
    private String leaseOwnerStatement;
    
    public Statements() {
        this.messageTableName = "ACTIVEMQ_MSGS";
        this.durableSubAcksTableName = "ACTIVEMQ_ACKS";
        this.lockTableName = "ACTIVEMQ_LOCK";
        this.binaryDataType = "BLOB";
        this.containerNameDataType = "VARCHAR(250)";
        this.msgIdDataType = "VARCHAR(250)";
        this.sequenceDataType = "BIGINT";
        this.longDataType = "BIGINT";
        this.stringIdDataType = "VARCHAR(250)";
        this.tablePrefix = "";
    }
    
    public String[] getCreateSchemaStatements() {
        if (this.createSchemaStatements == null) {
            this.createSchemaStatements = new String[] { "CREATE TABLE " + this.getFullMessageTableName() + "(ID " + this.sequenceDataType + " NOT NULL, CONTAINER " + this.containerNameDataType + ", MSGID_PROD " + this.msgIdDataType + ", MSGID_SEQ " + this.sequenceDataType + ", EXPIRATION " + this.longDataType + ", MSG " + (this.useExternalMessageReferences ? this.stringIdDataType : this.binaryDataType) + ", PRIMARY KEY ( ID ) )", "CREATE INDEX " + this.getFullMessageTableName() + "_MIDX ON " + this.getFullMessageTableName() + " (MSGID_PROD,MSGID_SEQ)", "CREATE INDEX " + this.getFullMessageTableName() + "_CIDX ON " + this.getFullMessageTableName() + " (CONTAINER)", "CREATE INDEX " + this.getFullMessageTableName() + "_EIDX ON " + this.getFullMessageTableName() + " (EXPIRATION)", "CREATE TABLE " + this.getFullAckTableName() + "(CONTAINER " + this.containerNameDataType + " NOT NULL, SUB_DEST " + this.stringIdDataType + ", CLIENT_ID " + this.stringIdDataType + " NOT NULL, SUB_NAME " + this.stringIdDataType + " NOT NULL, SELECTOR " + this.stringIdDataType + ", LAST_ACKED_ID " + this.sequenceDataType + ", PRIMARY KEY ( CONTAINER, CLIENT_ID, SUB_NAME))", "ALTER TABLE " + this.getFullMessageTableName() + " ADD PRIORITY " + this.sequenceDataType, "CREATE INDEX " + this.getFullMessageTableName() + "_PIDX ON " + this.getFullMessageTableName() + " (PRIORITY)", "ALTER TABLE " + this.getFullMessageTableName() + " ADD XID " + this.stringIdDataType, "ALTER TABLE " + this.getFullAckTableName() + " ADD PRIORITY " + this.sequenceDataType + " DEFAULT 5 NOT NULL", "ALTER TABLE " + this.getFullAckTableName() + " ADD XID " + this.stringIdDataType, "ALTER TABLE " + this.getFullAckTableName() + " " + this.getDropAckPKAlterStatementEnd(), "ALTER TABLE " + this.getFullAckTableName() + " ADD PRIMARY KEY (CONTAINER, CLIENT_ID, SUB_NAME, PRIORITY)", "CREATE INDEX " + this.getFullMessageTableName() + "_XIDX ON " + this.getFullMessageTableName() + " (XID)", "CREATE INDEX " + this.getFullAckTableName() + "_XIDX ON " + this.getFullAckTableName() + " (XID)" };
        }
        this.getCreateLockSchemaStatements();
        final String[] allCreateStatements = new String[this.createSchemaStatements.length + this.createLockSchemaStatements.length];
        System.arraycopy(this.createSchemaStatements, 0, allCreateStatements, 0, this.createSchemaStatements.length);
        System.arraycopy(this.createLockSchemaStatements, 0, allCreateStatements, this.createSchemaStatements.length, this.createLockSchemaStatements.length);
        return allCreateStatements;
    }
    
    public String[] getCreateLockSchemaStatements() {
        if (this.createLockSchemaStatements == null) {
            this.createLockSchemaStatements = new String[] { "CREATE TABLE " + this.getFullLockTableName() + "( ID " + this.longDataType + " NOT NULL, TIME " + this.longDataType + ", BROKER_NAME " + this.stringIdDataType + ", PRIMARY KEY (ID) )", "INSERT INTO " + this.getFullLockTableName() + "(ID) VALUES (1)" };
        }
        return this.createLockSchemaStatements;
    }
    
    public String getDropAckPKAlterStatementEnd() {
        if (this.dropAckPKAlterStatementEnd == null) {
            this.dropAckPKAlterStatementEnd = "DROP PRIMARY KEY";
        }
        return this.dropAckPKAlterStatementEnd;
    }
    
    public void setDropAckPKAlterStatementEnd(final String dropAckPKAlterStatementEnd) {
        this.dropAckPKAlterStatementEnd = dropAckPKAlterStatementEnd;
    }
    
    public String[] getDropSchemaStatements() {
        if (this.dropSchemaStatements == null) {
            this.dropSchemaStatements = new String[] { "DROP TABLE " + this.getFullAckTableName() + "", "DROP TABLE " + this.getFullMessageTableName() + "", "DROP TABLE " + this.getFullLockTableName() + "" };
        }
        return this.dropSchemaStatements;
    }
    
    public String getAddMessageStatement() {
        if (this.addMessageStatement == null) {
            this.addMessageStatement = "INSERT INTO " + this.getFullMessageTableName() + "(ID, MSGID_PROD, MSGID_SEQ, CONTAINER, EXPIRATION, PRIORITY, MSG, XID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        }
        return this.addMessageStatement;
    }
    
    public String getUpdateMessageStatement() {
        if (this.updateMessageStatement == null) {
            this.updateMessageStatement = "UPDATE " + this.getFullMessageTableName() + " SET MSG=? WHERE MSGID_PROD=? AND MSGID_SEQ=? AND CONTAINER=?";
        }
        return this.updateMessageStatement;
    }
    
    public String getRemoveMessageStatement() {
        if (this.removeMessageStatement == null) {
            this.removeMessageStatement = "DELETE FROM " + this.getFullMessageTableName() + " WHERE ID=?";
        }
        return this.removeMessageStatement;
    }
    
    public String getFindMessageSequenceIdStatement() {
        if (this.findMessageSequenceIdStatement == null) {
            this.findMessageSequenceIdStatement = "SELECT ID, PRIORITY FROM " + this.getFullMessageTableName() + " WHERE MSGID_PROD=? AND MSGID_SEQ=? AND CONTAINER=?";
        }
        return this.findMessageSequenceIdStatement;
    }
    
    public String getFindMessageStatement() {
        if (this.findMessageStatement == null) {
            this.findMessageStatement = "SELECT MSG FROM " + this.getFullMessageTableName() + " WHERE MSGID_PROD=? AND MSGID_SEQ=?";
        }
        return this.findMessageStatement;
    }
    
    public String getFindMessageByIdStatement() {
        if (this.findMessageByIdStatement == null) {
            this.findMessageByIdStatement = "SELECT MSG FROM " + this.getFullMessageTableName() + " WHERE ID=?";
        }
        return this.findMessageByIdStatement;
    }
    
    public String getFindXidByIdStatement() {
        if (this.findXidByIdStatement == null) {
            this.findXidByIdStatement = "SELECT XID FROM " + this.getFullMessageTableName() + " WHERE ID=?";
        }
        return this.findXidByIdStatement;
    }
    
    public String getFindAllMessagesStatement() {
        if (this.findAllMessagesStatement == null) {
            this.findAllMessagesStatement = "SELECT ID, MSG FROM " + this.getFullMessageTableName() + " WHERE CONTAINER=? ORDER BY ID";
        }
        return this.findAllMessagesStatement;
    }
    
    public String getFindAllMessageIdsStatement() {
        if (this.findAllMessageIdsStatement == null) {
            this.findAllMessageIdsStatement = "SELECT ID, MSGID_PROD, MSGID_SEQ FROM " + this.getFullMessageTableName() + " ORDER BY ID DESC";
        }
        return this.findAllMessageIdsStatement;
    }
    
    public String getFindLastSequenceIdInMsgsStatement() {
        if (this.findLastSequenceIdInMsgsStatement == null) {
            this.findLastSequenceIdInMsgsStatement = "SELECT MAX(ID) FROM " + this.getFullMessageTableName();
        }
        return this.findLastSequenceIdInMsgsStatement;
    }
    
    public String getLastProducerSequenceIdStatement() {
        if (this.lastProducerSequenceIdStatement == null) {
            this.lastProducerSequenceIdStatement = "SELECT MAX(MSGID_SEQ) FROM " + this.getFullMessageTableName() + " WHERE MSGID_PROD=?";
        }
        return this.lastProducerSequenceIdStatement;
    }
    
    public String getFindLastSequenceIdInAcksStatement() {
        if (this.findLastSequenceIdInAcksStatement == null) {
            this.findLastSequenceIdInAcksStatement = "SELECT MAX(LAST_ACKED_ID) FROM " + this.getFullAckTableName();
        }
        return this.findLastSequenceIdInAcksStatement;
    }
    
    public String getCreateDurableSubStatement() {
        if (this.createDurableSubStatement == null) {
            this.createDurableSubStatement = "INSERT INTO " + this.getFullAckTableName() + "(CONTAINER, CLIENT_ID, SUB_NAME, SELECTOR, LAST_ACKED_ID, SUB_DEST, PRIORITY) VALUES (?, ?, ?, ?, ?, ?, ?)";
        }
        return this.createDurableSubStatement;
    }
    
    public String getFindDurableSubStatement() {
        if (this.findDurableSubStatement == null) {
            this.findDurableSubStatement = "SELECT SELECTOR, SUB_DEST FROM " + this.getFullAckTableName() + " WHERE CONTAINER=? AND CLIENT_ID=? AND SUB_NAME=?";
        }
        return this.findDurableSubStatement;
    }
    
    public String getFindAllDurableSubsStatement() {
        if (this.findAllDurableSubsStatement == null) {
            this.findAllDurableSubsStatement = "SELECT SELECTOR, SUB_NAME, CLIENT_ID, SUB_DEST FROM " + this.getFullAckTableName() + " WHERE CONTAINER=? AND PRIORITY=0";
        }
        return this.findAllDurableSubsStatement;
    }
    
    public String getUpdateLastPriorityAckRowOfDurableSubStatement() {
        if (this.updateLastPriorityAckRowOfDurableSubStatement == null) {
            this.updateLastPriorityAckRowOfDurableSubStatement = "UPDATE " + this.getFullAckTableName() + " SET LAST_ACKED_ID=? WHERE CONTAINER=? AND CLIENT_ID=? AND SUB_NAME=? AND PRIORITY=?";
        }
        return this.updateLastPriorityAckRowOfDurableSubStatement;
    }
    
    public String getDeleteSubscriptionStatement() {
        if (this.deleteSubscriptionStatement == null) {
            this.deleteSubscriptionStatement = "DELETE FROM " + this.getFullAckTableName() + " WHERE CONTAINER=? AND CLIENT_ID=? AND SUB_NAME=?";
        }
        return this.deleteSubscriptionStatement;
    }
    
    public String getFindAllDurableSubMessagesStatement() {
        if (this.findAllDurableSubMessagesStatement == null) {
            this.findAllDurableSubMessagesStatement = "SELECT M.ID, M.MSG FROM " + this.getFullMessageTableName() + " M, " + this.getFullAckTableName() + " D  WHERE D.CONTAINER=? AND D.CLIENT_ID=? AND D.SUB_NAME=? AND M.CONTAINER=D.CONTAINER AND M.ID > D.LAST_ACKED_ID ORDER BY M.PRIORITY DESC, M.ID";
        }
        return this.findAllDurableSubMessagesStatement;
    }
    
    public String getFindDurableSubMessagesStatement() {
        if (this.findDurableSubMessagesStatement == null) {
            this.findDurableSubMessagesStatement = "SELECT M.ID, M.MSG FROM " + this.getFullMessageTableName() + " M, " + this.getFullAckTableName() + " D  WHERE D.CONTAINER=? AND D.CLIENT_ID=? AND D.SUB_NAME=? AND M.XID IS NULL AND M.CONTAINER=D.CONTAINER AND M.ID > D.LAST_ACKED_ID AND M.ID > ? ORDER BY M.ID";
        }
        return this.findDurableSubMessagesStatement;
    }
    
    public String getFindDurableSubMessagesByPriorityStatement() {
        if (this.findDurableSubMessagesByPriorityStatement == null) {
            this.findDurableSubMessagesByPriorityStatement = "SELECT M.ID, M.MSG FROM " + this.getFullMessageTableName() + " M, " + this.getFullAckTableName() + " D WHERE D.CONTAINER=? AND D.CLIENT_ID=? AND D.SUB_NAME=? AND M.XID IS NULL AND M.CONTAINER=D.CONTAINER AND M.PRIORITY=D.PRIORITY AND M.ID > D.LAST_ACKED_ID AND M.ID > ? AND M.PRIORITY = ? ORDER BY M.ID";
        }
        return this.findDurableSubMessagesByPriorityStatement;
    }
    
    public String findAllDurableSubMessagesStatement() {
        if (this.findAllDurableSubMessagesStatement == null) {
            this.findAllDurableSubMessagesStatement = "SELECT M.ID, M.MSG FROM " + this.getFullMessageTableName() + " M, " + this.getFullAckTableName() + " D  WHERE D.CONTAINER=? AND D.CLIENT_ID=? AND D.SUB_NAME=? AND M.CONTAINER=D.CONTAINER AND M.ID > D.LAST_ACKED_ID ORDER BY M.ID";
        }
        return this.findAllDurableSubMessagesStatement;
    }
    
    public String getNextDurableSubscriberMessageStatement() {
        if (this.nextDurableSubscriberMessageStatement == null) {
            this.nextDurableSubscriberMessageStatement = "SELECT M.ID, M.MSG FROM " + this.getFullMessageTableName() + " M, " + this.getFullAckTableName() + " D  WHERE D.CONTAINER=? AND D.CLIENT_ID=? AND D.SUB_NAME=? AND M.CONTAINER=D.CONTAINER AND M.ID > ? ORDER BY M.ID ";
        }
        return this.nextDurableSubscriberMessageStatement;
    }
    
    public String getDurableSubscriberMessageCountStatement() {
        if (this.durableSubscriberMessageCountStatement == null) {
            this.durableSubscriberMessageCountStatement = "SELECT COUNT(*) FROM " + this.getFullMessageTableName() + " M, " + this.getFullAckTableName() + " D  WHERE D.CONTAINER=? AND D.CLIENT_ID=? AND D.SUB_NAME=? AND M.CONTAINER=D.CONTAINER      AND M.ID >          ( SELECT LAST_ACKED_ID FROM " + this.getFullAckTableName() + "           WHERE CONTAINER=D.CONTAINER AND CLIENT_ID=D.CLIENT_ID           AND SUB_NAME=D.SUB_NAME )";
        }
        return this.durableSubscriberMessageCountStatement;
    }
    
    public String getDurableSubscriberMessageCountStatementWithPriority() {
        if (this.durableSubscriberMessageCountStatementWithPriority == null) {
            this.durableSubscriberMessageCountStatementWithPriority = "SELECT COUNT(*) FROM " + this.getFullMessageTableName() + " M, " + this.getFullAckTableName() + " D  WHERE D.CONTAINER=? AND D.CLIENT_ID=? AND D.SUB_NAME=? AND M.CONTAINER=D.CONTAINER  AND M.PRIORITY=D.PRIORITY  AND M.ID > D.LAST_ACKED_ID";
        }
        return this.durableSubscriberMessageCountStatementWithPriority;
    }
    
    public String getFindAllDestinationsStatement() {
        if (this.findAllDestinationsStatement == null) {
            this.findAllDestinationsStatement = "SELECT DISTINCT CONTAINER FROM " + this.getFullMessageTableName() + " UNION SELECT DISTINCT CONTAINER FROM " + this.getFullAckTableName();
        }
        return this.findAllDestinationsStatement;
    }
    
    public String getRemoveAllMessagesStatement() {
        if (this.removeAllMessagesStatement == null) {
            this.removeAllMessagesStatement = "DELETE FROM " + this.getFullMessageTableName() + " WHERE CONTAINER=?";
        }
        return this.removeAllMessagesStatement;
    }
    
    public String getRemoveAllSubscriptionsStatement() {
        if (this.removeAllSubscriptionsStatement == null) {
            this.removeAllSubscriptionsStatement = "DELETE FROM " + this.getFullAckTableName() + " WHERE CONTAINER=?";
        }
        return this.removeAllSubscriptionsStatement;
    }
    
    public String getDeleteOldMessagesStatementWithPriority() {
        if (this.deleteOldMessagesStatementWithPriority == null) {
            this.deleteOldMessagesStatementWithPriority = "DELETE FROM " + this.getFullMessageTableName() + " WHERE (PRIORITY=? AND ID <=      ( SELECT min(" + this.getFullAckTableName() + ".LAST_ACKED_ID)       FROM " + this.getFullAckTableName() + " WHERE " + this.getFullAckTableName() + ".CONTAINER=" + this.getFullMessageTableName() + ".CONTAINER        AND " + this.getFullAckTableName() + ".PRIORITY=?)   )";
        }
        return this.deleteOldMessagesStatementWithPriority;
    }
    
    public String getLockCreateStatement() {
        if (this.lockCreateStatement == null) {
            this.lockCreateStatement = "SELECT * FROM " + this.getFullLockTableName();
            if (this.useLockCreateWhereClause) {
                this.lockCreateStatement += " WHERE ID = 1";
            }
            this.lockCreateStatement += " FOR UPDATE";
        }
        return this.lockCreateStatement;
    }
    
    public String getLeaseObtainStatement() {
        if (this.leaseObtainStatement == null) {
            this.leaseObtainStatement = "UPDATE " + this.getFullLockTableName() + " SET BROKER_NAME=?, TIME=? WHERE (TIME IS NULL OR TIME < ?) AND ID = 1";
        }
        return this.leaseObtainStatement;
    }
    
    public String getCurrentDateTime() {
        if (this.currentDateTimeStatement == null) {
            this.currentDateTimeStatement = "SELECT CURRENT_TIMESTAMP FROM " + this.getFullLockTableName();
        }
        return this.currentDateTimeStatement;
    }
    
    public String getLeaseUpdateStatement() {
        if (this.leaseUpdateStatement == null) {
            this.leaseUpdateStatement = "UPDATE " + this.getFullLockTableName() + " SET BROKER_NAME=?, TIME=? WHERE BROKER_NAME=? AND ID = 1";
        }
        return this.leaseUpdateStatement;
    }
    
    public String getLeaseOwnerStatement() {
        if (this.leaseOwnerStatement == null) {
            this.leaseOwnerStatement = "SELECT BROKER_NAME, TIME FROM " + this.getFullLockTableName() + " WHERE ID = 1";
        }
        return this.leaseOwnerStatement;
    }
    
    public String getLockUpdateStatement() {
        if (this.lockUpdateStatement == null) {
            this.lockUpdateStatement = "UPDATE " + this.getFullLockTableName() + " SET TIME = ? WHERE ID = 1";
        }
        return this.lockUpdateStatement;
    }
    
    public String getDestinationMessageCountStatement() {
        if (this.destinationMessageCountStatement == null) {
            this.destinationMessageCountStatement = "SELECT COUNT(*) FROM " + this.getFullMessageTableName() + " WHERE CONTAINER=? AND XID IS NULL";
        }
        return this.destinationMessageCountStatement;
    }
    
    public String getFindNextMessagesStatement() {
        if (this.findNextMessagesStatement == null) {
            this.findNextMessagesStatement = "SELECT ID, MSG FROM " + this.getFullMessageTableName() + " WHERE CONTAINER=? AND ID > ? AND XID IS NULL ORDER BY ID";
        }
        return this.findNextMessagesStatement;
    }
    
    public String getFindNextMessagesByPriorityStatement() {
        if (this.findNextMessagesByPriorityStatement == null) {
            this.findNextMessagesByPriorityStatement = "SELECT ID, MSG FROM " + this.getFullMessageTableName() + " WHERE CONTAINER=? AND XID IS NULL AND ((ID > ? AND PRIORITY = ?) OR PRIORITY < ?) ORDER BY PRIORITY DESC, ID";
        }
        return this.findNextMessagesByPriorityStatement;
    }
    
    public String getLastAckedDurableSubscriberMessageStatement() {
        if (this.lastAckedDurableSubscriberMessageStatement == null) {
            this.lastAckedDurableSubscriberMessageStatement = "SELECT MAX(LAST_ACKED_ID) FROM " + this.getFullAckTableName() + " WHERE CONTAINER=? AND CLIENT_ID=? AND SUB_NAME=?";
        }
        return this.lastAckedDurableSubscriberMessageStatement;
    }
    
    public String getSelectDurablePriorityAckStatement() {
        if (this.selectDurablePriorityAckStatement == null) {
            this.selectDurablePriorityAckStatement = "SELECT LAST_ACKED_ID FROM " + this.getFullAckTableName() + " WHERE CONTAINER=? AND CLIENT_ID=? AND SUB_NAME=? AND PRIORITY = ?";
        }
        return this.selectDurablePriorityAckStatement;
    }
    
    public String getInsertDurablePriorityAckStatement() {
        if (this.insertDurablePriorityAckStatement == null) {
            this.insertDurablePriorityAckStatement = "INSERT INTO " + this.getFullAckTableName() + "(CONTAINER, CLIENT_ID, SUB_NAME, PRIORITY) VALUES (?, ?, ?, ?)";
        }
        return this.insertDurablePriorityAckStatement;
    }
    
    public String getUpdateDurableLastAckStatement() {
        if (this.updateDurableLastAckStatement == null) {
            this.updateDurableLastAckStatement = "UPDATE " + this.getFullAckTableName() + " SET LAST_ACKED_ID=?, XID = NULL WHERE CONTAINER=? AND CLIENT_ID=? AND SUB_NAME=?";
        }
        return this.updateDurableLastAckStatement;
    }
    
    public String getUpdateDurableLastAckInTxStatement() {
        if (this.updateDurableLastAckInTxStatement == null) {
            this.updateDurableLastAckInTxStatement = "UPDATE " + this.getFullAckTableName() + " SET XID=? WHERE CONTAINER=? AND CLIENT_ID=? AND SUB_NAME=?";
        }
        return this.updateDurableLastAckInTxStatement;
    }
    
    public String getUpdateDurableLastAckWithPriorityStatement() {
        if (this.updateDurableLastAckWithPriorityStatement == null) {
            this.updateDurableLastAckWithPriorityStatement = "UPDATE " + this.getFullAckTableName() + " SET LAST_ACKED_ID=?, XID = NULL WHERE CONTAINER=? AND CLIENT_ID=? AND SUB_NAME=? AND PRIORITY=?";
        }
        return this.updateDurableLastAckWithPriorityStatement;
    }
    
    public String getUpdateDurableLastAckWithPriorityInTxStatement() {
        if (this.updateDurableLastAckWithPriorityInTxStatement == null) {
            this.updateDurableLastAckWithPriorityInTxStatement = "UPDATE " + this.getFullAckTableName() + " SET XID=? WHERE CONTAINER=? AND CLIENT_ID=? AND SUB_NAME=? AND PRIORITY=?";
        }
        return this.updateDurableLastAckWithPriorityInTxStatement;
    }
    
    public String getClearDurableLastAckInTxStatement() {
        if (this.clearDurableLastAckInTxStatement == null) {
            this.clearDurableLastAckInTxStatement = "UPDATE " + this.getFullAckTableName() + " SET XID = NULL WHERE CONTAINER=? AND CLIENT_ID=? AND SUB_NAME=? AND PRIORITY=?";
        }
        return this.clearDurableLastAckInTxStatement;
    }
    
    public String getFindOpsPendingOutcomeStatement() {
        if (this.findOpsPendingOutcomeStatement == null) {
            this.findOpsPendingOutcomeStatement = "SELECT ID, XID, MSG FROM " + this.getFullMessageTableName() + " WHERE XID IS NOT NULL ORDER BY ID";
        }
        return this.findOpsPendingOutcomeStatement;
    }
    
    public String getFindAcksPendingOutcomeStatement() {
        if (this.findAcksPendingOutcomeStatement == null) {
            this.findAcksPendingOutcomeStatement = "SELECT XID, CONTAINER, CLIENT_ID, SUB_NAME FROM " + this.getFullAckTableName() + " WHERE XID IS NOT NULL";
        }
        return this.findAcksPendingOutcomeStatement;
    }
    
    public String getUpdateXidFlagStatement() {
        if (this.updateXidFlagStatement == null) {
            this.updateXidFlagStatement = "UPDATE " + this.getFullMessageTableName() + " SET XID = ? WHERE ID = ?";
        }
        return this.updateXidFlagStatement;
    }
    
    public String getClearXidFlagStatement() {
        if (this.clearXidFlagStatement == null) {
            this.clearXidFlagStatement = "UPDATE " + this.getFullMessageTableName() + " SET XID = NULL WHERE ID = ?";
        }
        return this.clearXidFlagStatement;
    }
    
    public String getFullMessageTableName() {
        return this.getTablePrefix() + this.getMessageTableName();
    }
    
    public String getFullAckTableName() {
        return this.getTablePrefix() + this.getDurableSubAcksTableName();
    }
    
    public String getFullLockTableName() {
        return this.getTablePrefix() + this.getLockTableName();
    }
    
    public String getContainerNameDataType() {
        return this.containerNameDataType;
    }
    
    public void setContainerNameDataType(final String containerNameDataType) {
        this.containerNameDataType = containerNameDataType;
    }
    
    public String getBinaryDataType() {
        return this.binaryDataType;
    }
    
    public void setBinaryDataType(final String messageDataType) {
        this.binaryDataType = messageDataType;
    }
    
    public String getMessageTableName() {
        return this.messageTableName;
    }
    
    public void setMessageTableName(final String messageTableName) {
        this.messageTableName = messageTableName;
    }
    
    public String getMsgIdDataType() {
        return this.msgIdDataType;
    }
    
    public void setMsgIdDataType(final String msgIdDataType) {
        this.msgIdDataType = msgIdDataType;
    }
    
    public String getSequenceDataType() {
        return this.sequenceDataType;
    }
    
    public void setSequenceDataType(final String sequenceDataType) {
        this.sequenceDataType = sequenceDataType;
    }
    
    public String getTablePrefix() {
        return this.tablePrefix;
    }
    
    public void setTablePrefix(final String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }
    
    public String getDurableSubAcksTableName() {
        return this.durableSubAcksTableName;
    }
    
    public void setDurableSubAcksTableName(final String durableSubAcksTableName) {
        this.durableSubAcksTableName = durableSubAcksTableName;
    }
    
    public String getLockTableName() {
        return this.lockTableName;
    }
    
    public void setLockTableName(final String lockTableName) {
        this.lockTableName = lockTableName;
    }
    
    public String getLongDataType() {
        return this.longDataType;
    }
    
    public void setLongDataType(final String longDataType) {
        this.longDataType = longDataType;
    }
    
    public String getStringIdDataType() {
        return this.stringIdDataType;
    }
    
    public void setStringIdDataType(final String stringIdDataType) {
        this.stringIdDataType = stringIdDataType;
    }
    
    public void setUseExternalMessageReferences(final boolean useExternalMessageReferences) {
        this.useExternalMessageReferences = useExternalMessageReferences;
    }
    
    public boolean isUseExternalMessageReferences() {
        return this.useExternalMessageReferences;
    }
    
    public void setAddMessageStatement(final String addMessageStatment) {
        this.addMessageStatement = addMessageStatment;
    }
    
    public void setCreateDurableSubStatement(final String createDurableSubStatment) {
        this.createDurableSubStatement = createDurableSubStatment;
    }
    
    public void setCreateSchemaStatements(final String[] createSchemaStatments) {
        this.createSchemaStatements = createSchemaStatments;
    }
    
    public void setCreateLockSchemaStatements(final String[] createLockSchemaStatments) {
        this.createLockSchemaStatements = createLockSchemaStatments;
    }
    
    public void setDeleteOldMessagesStatementWithPriority(final String deleteOldMessagesStatementWithPriority) {
        this.deleteOldMessagesStatementWithPriority = deleteOldMessagesStatementWithPriority;
    }
    
    public void setDeleteSubscriptionStatement(final String deleteSubscriptionStatment) {
        this.deleteSubscriptionStatement = deleteSubscriptionStatment;
    }
    
    public void setDropSchemaStatements(final String[] dropSchemaStatments) {
        this.dropSchemaStatements = dropSchemaStatments;
    }
    
    public void setFindAllDestinationsStatement(final String findAllDestinationsStatment) {
        this.findAllDestinationsStatement = findAllDestinationsStatment;
    }
    
    public void setFindAllDurableSubMessagesStatement(final String findAllDurableSubMessagesStatment) {
        this.findAllDurableSubMessagesStatement = findAllDurableSubMessagesStatment;
    }
    
    public void setFindAllDurableSubsStatement(final String findAllDurableSubsStatment) {
        this.findAllDurableSubsStatement = findAllDurableSubsStatment;
    }
    
    public void setFindAllMessagesStatement(final String findAllMessagesStatment) {
        this.findAllMessagesStatement = findAllMessagesStatment;
    }
    
    public void setFindDurableSubStatement(final String findDurableSubStatment) {
        this.findDurableSubStatement = findDurableSubStatment;
    }
    
    public void setFindLastSequenceIdInAcksStatement(final String findLastSequenceIdInAcks) {
        this.findLastSequenceIdInAcksStatement = findLastSequenceIdInAcks;
    }
    
    public void setFindLastSequenceIdInMsgsStatement(final String findLastSequenceIdInMsgs) {
        this.findLastSequenceIdInMsgsStatement = findLastSequenceIdInMsgs;
    }
    
    public void setFindMessageSequenceIdStatement(final String findMessageSequenceIdStatment) {
        this.findMessageSequenceIdStatement = findMessageSequenceIdStatment;
    }
    
    public void setFindMessageStatement(final String findMessageStatment) {
        this.findMessageStatement = findMessageStatment;
    }
    
    public void setFindMessageByIdStatement(final String findMessageByIdStatement) {
        this.findMessageByIdStatement = findMessageByIdStatement;
    }
    
    public void setRemoveAllMessagesStatement(final String removeAllMessagesStatment) {
        this.removeAllMessagesStatement = removeAllMessagesStatment;
    }
    
    public void setRemoveAllSubscriptionsStatement(final String removeAllSubscriptionsStatment) {
        this.removeAllSubscriptionsStatement = removeAllSubscriptionsStatment;
    }
    
    public void setRemoveMessageStatment(final String removeMessageStatement) {
        this.removeMessageStatement = removeMessageStatement;
    }
    
    public void setUpdateLastPriorityAckRowOfDurableSubStatement(final String updateLastPriorityAckRowOfDurableSubStatement) {
        this.updateLastPriorityAckRowOfDurableSubStatement = updateLastPriorityAckRowOfDurableSubStatement;
    }
    
    public void setUpdateMessageStatement(final String updateMessageStatment) {
        this.updateMessageStatement = updateMessageStatment;
    }
    
    public boolean isUseLockCreateWhereClause() {
        return this.useLockCreateWhereClause;
    }
    
    public void setUseLockCreateWhereClause(final boolean useLockCreateWhereClause) {
        this.useLockCreateWhereClause = useLockCreateWhereClause;
    }
    
    public void setLockCreateStatement(final String lockCreateStatement) {
        this.lockCreateStatement = lockCreateStatement;
    }
    
    public void setLockUpdateStatement(final String lockUpdateStatement) {
        this.lockUpdateStatement = lockUpdateStatement;
    }
    
    public void setFindDurableSubMessagesStatement(final String findDurableSubMessagesStatement) {
        this.findDurableSubMessagesStatement = findDurableSubMessagesStatement;
    }
    
    public void setNextDurableSubscriberMessageStatement(final String nextDurableSubscriberMessageStatement) {
        this.nextDurableSubscriberMessageStatement = nextDurableSubscriberMessageStatement;
    }
    
    public void setDurableSubscriberMessageCountStatement(final String durableSubscriberMessageCountStatement) {
        this.durableSubscriberMessageCountStatement = durableSubscriberMessageCountStatement;
    }
    
    public void setDurableSubscriberMessageCountStatementWithPriority(final String durableSubscriberMessageCountStatementWithPriority) {
        this.durableSubscriberMessageCountStatementWithPriority = durableSubscriberMessageCountStatementWithPriority;
    }
    
    public void setFindNextMessagesStatement(final String findNextMessagesStatement) {
        this.findNextMessagesStatement = findNextMessagesStatement;
    }
    
    public void setDestinationMessageCountStatement(final String destinationMessageCountStatement) {
        this.destinationMessageCountStatement = destinationMessageCountStatement;
    }
    
    public void setLastAckedDurableSubscriberMessageStatement(final String lastAckedDurableSubscriberMessageStatement) {
        this.lastAckedDurableSubscriberMessageStatement = lastAckedDurableSubscriberMessageStatement;
    }
    
    public void setLastProducerSequenceIdStatement(final String lastProducerSequenceIdStatement) {
        this.lastProducerSequenceIdStatement = lastProducerSequenceIdStatement;
    }
    
    public void setSelectDurablePriorityAckStatement(final String selectDurablePriorityAckStatement) {
        this.selectDurablePriorityAckStatement = selectDurablePriorityAckStatement;
    }
    
    public void setInsertDurablePriorityAckStatement(final String insertDurablePriorityAckStatement) {
        this.insertDurablePriorityAckStatement = insertDurablePriorityAckStatement;
    }
    
    public void setUpdateDurableLastAckStatement(final String updateDurableLastAckStatement) {
        this.updateDurableLastAckStatement = updateDurableLastAckStatement;
    }
    
    public void setUpdateXidFlagStatement(final String updateXidFlagStatement) {
        this.updateXidFlagStatement = updateXidFlagStatement;
    }
    
    public void setFindOpsPendingOutcomeStatement(final String findOpsPendingOutcomeStatement) {
        this.findOpsPendingOutcomeStatement = findOpsPendingOutcomeStatement;
    }
    
    public void setClearXidFlagStatement(final String clearXidFlagStatement) {
        this.clearXidFlagStatement = clearXidFlagStatement;
    }
    
    public void setUpdateDurableLastAckInTxStatement(final String updateDurableLastAckInTxStatement) {
        this.updateDurableLastAckInTxStatement = updateDurableLastAckInTxStatement;
    }
    
    public void setFindAcksPendingOutcomeStatement(final String findAcksPendingOutcomeStatement) {
        this.findAcksPendingOutcomeStatement = findAcksPendingOutcomeStatement;
    }
    
    public void setClearDurableLastAckInTxStatement(final String clearDurableLastAckInTxStatement) {
        this.clearDurableLastAckInTxStatement = clearDurableLastAckInTxStatement;
    }
    
    public void setUpdateDurableLastAckWithPriorityStatement(final String updateDurableLastAckWithPriorityStatement) {
        this.updateDurableLastAckWithPriorityStatement = updateDurableLastAckWithPriorityStatement;
    }
    
    public void setUpdateDurableLastAckWithPriorityInTxStatement(final String updateDurableLastAckWithPriorityInTxStatement) {
        this.updateDurableLastAckWithPriorityInTxStatement = updateDurableLastAckWithPriorityInTxStatement;
    }
    
    public void setFindXidByIdStatement(final String findXidByIdStatement) {
        this.findXidByIdStatement = findXidByIdStatement;
    }
    
    public void setLeaseObtainStatement(final String leaseObtainStatement) {
        this.leaseObtainStatement = leaseObtainStatement;
    }
    
    public void setCurrentDateTimeStatement(final String currentDateTimeStatement) {
        this.currentDateTimeStatement = currentDateTimeStatement;
    }
    
    public void setLeaseUpdateStatement(final String leaseUpdateStatement) {
        this.leaseUpdateStatement = leaseUpdateStatement;
    }
    
    public void setLeaseOwnerStatement(final String leaseOwnerStatement) {
        this.leaseOwnerStatement = leaseOwnerStatement;
    }
}
