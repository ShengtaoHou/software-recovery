// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.jdbc.adapter;

import org.apache.activemq.store.jdbc.Statements;

public class AxionJDBCAdapter extends StreamJDBCAdapter
{
    @Override
    public void setStatements(final Statements statements) {
        final String[] createStatements = { "CREATE TABLE " + statements.getFullMessageTableName() + "(ID " + statements.getSequenceDataType() + " NOT NULL, CONTAINER " + statements.getContainerNameDataType() + ", MSGID_PROD " + statements.getMsgIdDataType() + ", MSGID_SEQ " + statements.getSequenceDataType() + ", EXPIRATION " + statements.getLongDataType() + ", MSG " + (statements.isUseExternalMessageReferences() ? statements.getStringIdDataType() : statements.getBinaryDataType()) + ", PRIMARY KEY ( ID ) )", "CREATE INDEX " + statements.getFullMessageTableName() + "_MIDX ON " + statements.getFullMessageTableName() + " (MSGID_PROD,MSGID_SEQ)", "CREATE INDEX " + statements.getFullMessageTableName() + "_CIDX ON " + statements.getFullMessageTableName() + " (CONTAINER)", "CREATE INDEX " + statements.getFullMessageTableName() + "_EIDX ON " + statements.getFullMessageTableName() + " (EXPIRATION)", "CREATE TABLE " + statements.getFullAckTableName() + "(CONTAINER " + statements.getContainerNameDataType() + " NOT NULL, SUB_DEST " + statements.getContainerNameDataType() + ", CLIENT_ID " + statements.getStringIdDataType() + " NOT NULL, SUB_NAME " + statements.getStringIdDataType() + " NOT NULL, SELECTOR " + statements.getStringIdDataType() + ", LAST_ACKED_ID " + statements.getSequenceDataType() + ", PRIMARY KEY ( CONTAINER, CLIENT_ID, SUB_NAME))" };
        statements.setCreateSchemaStatements(createStatements);
        statements.setLongDataType("LONG");
        statements.setSequenceDataType("LONG");
        super.setStatements(statements);
    }
}
