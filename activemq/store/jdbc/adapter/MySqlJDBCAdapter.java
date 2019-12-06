// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.jdbc.adapter;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.activemq.store.jdbc.Statements;

public class MySqlJDBCAdapter extends DefaultJDBCAdapter
{
    public static final String INNODB = "INNODB";
    public static final String NDBCLUSTER = "NDBCLUSTER";
    public static final String BDB = "BDB";
    public static final String MYISAM = "MYISAM";
    public static final String ISAM = "ISAM";
    public static final String MERGE = "MERGE";
    public static final String HEAP = "HEAP";
    String engineType;
    String typeStatement;
    
    public MySqlJDBCAdapter() {
        this.engineType = "INNODB";
        this.typeStatement = "ENGINE";
    }
    
    @Override
    public void setStatements(final Statements statements) {
        final String type = this.engineType.toUpperCase();
        if (!type.equals("INNODB") && !type.equals("NDBCLUSTER")) {
            statements.setLockCreateStatement("LOCK TABLE " + statements.getFullLockTableName() + " WRITE");
        }
        statements.setBinaryDataType("LONGBLOB");
        String typeClause = this.typeStatement + "=" + type;
        if (type.equals("NDBCLUSTER")) {
            typeClause = this.typeStatement + "=" + "INNODB";
        }
        String[] s = statements.getCreateSchemaStatements();
        for (int i = 0; i < s.length; ++i) {
            if (s[i].startsWith("CREATE TABLE")) {
                s[i] = s[i] + " " + typeClause;
            }
        }
        if (type.equals("NDBCLUSTER")) {
            final ArrayList<String> l = new ArrayList<String>(Arrays.asList(s));
            l.add("ALTER TABLE " + statements.getFullMessageTableName() + " ENGINE=" + "NDBCLUSTER");
            l.add("ALTER TABLE " + statements.getFullAckTableName() + " ENGINE=" + "NDBCLUSTER");
            l.add("ALTER TABLE " + statements.getFullLockTableName() + " ENGINE=" + "NDBCLUSTER");
            l.add("FLUSH TABLES");
            s = l.toArray(new String[l.size()]);
            statements.setCreateSchemaStatements(s);
        }
        super.setStatements(statements);
    }
    
    public String getEngineType() {
        return this.engineType;
    }
    
    public void setEngineType(final String engineType) {
        this.engineType = engineType;
    }
    
    public String getTypeStatement() {
        return this.typeStatement;
    }
    
    public void setTypeStatement(final String typeStatement) {
        this.typeStatement = typeStatement;
    }
}
