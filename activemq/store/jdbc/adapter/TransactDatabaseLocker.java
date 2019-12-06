// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.jdbc.adapter;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.activemq.store.jdbc.DefaultDatabaseLocker;

public class TransactDatabaseLocker extends DefaultDatabaseLocker
{
    private static final Logger LOG;
    
    @Override
    public void doStart() throws Exception {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: ldc             "Attempting to acquire the exclusive lock to become the Master broker"
        //     5: invokeinterface org/slf4j/Logger.info:(Ljava/lang/String;)V
        //    10: aconst_null    
        //    11: astore_1        /* statement */
        //    12: aload_0         /* this */
        //    13: aload_0         /* this */
        //    14: getfield        org/apache/activemq/store/jdbc/adapter/TransactDatabaseLocker.dataSource:Ljavax/sql/DataSource;
        //    17: invokeinterface javax/sql/DataSource.getConnection:()Ljava/sql/Connection;
        //    22: putfield        org/apache/activemq/store/jdbc/adapter/TransactDatabaseLocker.connection:Ljava/sql/Connection;
        //    25: aload_0         /* this */
        //    26: getfield        org/apache/activemq/store/jdbc/adapter/TransactDatabaseLocker.connection:Ljava/sql/Connection;
        //    29: iconst_0       
        //    30: invokeinterface java/sql/Connection.setAutoCommit:(Z)V
        //    35: aload_0         /* this */
        //    36: invokevirtual   org/apache/activemq/store/jdbc/adapter/TransactDatabaseLocker.getStatements:()Lorg/apache/activemq/store/jdbc/Statements;
        //    39: invokevirtual   org/apache/activemq/store/jdbc/Statements.getLockCreateStatement:()Ljava/lang/String;
        //    42: astore_2        /* sql */
        //    43: aload_0         /* this */
        //    44: getfield        org/apache/activemq/store/jdbc/adapter/TransactDatabaseLocker.connection:Ljava/sql/Connection;
        //    47: aload_2         /* sql */
        //    48: invokeinterface java/sql/Connection.prepareStatement:(Ljava/lang/String;)Ljava/sql/PreparedStatement;
        //    53: astore_1        /* statement */
        //    54: aload_1         /* statement */
        //    55: invokeinterface java/sql/PreparedStatement.getMetaData:()Ljava/sql/ResultSetMetaData;
        //    60: ifnull          80
        //    63: aload_1         /* statement */
        //    64: invokeinterface java/sql/PreparedStatement.executeQuery:()Ljava/sql/ResultSet;
        //    69: astore_3        /* rs */
        //    70: aload_3         /* rs */
        //    71: invokeinterface java/sql/ResultSet.next:()Z
        //    76: pop            
        //    77: goto            87
        //    80: aload_1         /* statement */
        //    81: invokeinterface java/sql/PreparedStatement.execute:()Z
        //    86: pop            
        //    87: aconst_null    
        //    88: aload_1         /* statement */
        //    89: if_acmpeq       439
        //    92: aload_1         /* statement */
        //    93: invokeinterface java/sql/PreparedStatement.close:()V
        //    98: goto            130
        //   101: astore_3        /* e1 */
        //   102: getstatic       org/apache/activemq/store/jdbc/adapter/TransactDatabaseLocker.LOG:Lorg/slf4j/Logger;
        //   105: new             Ljava/lang/StringBuilder;
        //   108: dup            
        //   109: invokespecial   java/lang/StringBuilder.<init>:()V
        //   112: ldc             "Caught while closing statement: "
        //   114: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   117: aload_3         /* e1 */
        //   118: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   121: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   124: aload_3         /* e1 */
        //   125: invokeinterface org/slf4j/Logger.warn:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   130: aconst_null    
        //   131: astore_1        /* statement */
        //   132: goto            439
        //   135: astore_2        /* sql */
        //   136: aload_0         /* this */
        //   137: invokevirtual   org/apache/activemq/store/jdbc/adapter/TransactDatabaseLocker.isStopping:()Z
        //   140: ifeq            171
        //   143: new             Ljava/lang/Exception;
        //   146: dup            
        //   147: new             Ljava/lang/StringBuilder;
        //   150: dup            
        //   151: invokespecial   java/lang/StringBuilder.<init>:()V
        //   154: ldc             "Cannot start broker as being asked to shut down. Interrupted attempt to acquire lock: "
        //   156: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   159: aload_2         /* e */
        //   160: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   163: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   166: aload_2         /* e */
        //   167: invokespecial   java/lang/Exception.<init>:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   170: athrow         
        //   171: aload_0         /* this */
        //   172: getfield        org/apache/activemq/store/jdbc/adapter/TransactDatabaseLocker.exceptionHandler:Lorg/apache/activemq/util/Handler;
        //   175: ifnull          250
        //   178: aload_0         /* this */
        //   179: getfield        org/apache/activemq/store/jdbc/adapter/TransactDatabaseLocker.exceptionHandler:Lorg/apache/activemq/util/Handler;
        //   182: aload_2         /* e */
        //   183: invokeinterface org/apache/activemq/util/Handler.handle:(Ljava/lang/Object;)V
        //   188: goto            278
        //   191: astore_3        /* handlerException */
        //   192: getstatic       org/apache/activemq/store/jdbc/adapter/TransactDatabaseLocker.LOG:Lorg/slf4j/Logger;
        //   195: new             Ljava/lang/StringBuilder;
        //   198: dup            
        //   199: invokespecial   java/lang/StringBuilder.<init>:()V
        //   202: ldc             "The exception handler "
        //   204: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   207: aload_0         /* this */
        //   208: getfield        org/apache/activemq/store/jdbc/adapter/TransactDatabaseLocker.exceptionHandler:Lorg/apache/activemq/util/Handler;
        //   211: invokevirtual   java/lang/Object.getClass:()Ljava/lang/Class;
        //   214: invokevirtual   java/lang/Class.getCanonicalName:()Ljava/lang/String;
        //   217: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   220: ldc             " threw this exception: "
        //   222: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   225: aload_3         /* handlerException */
        //   226: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   229: ldc             " while trying to handle this excpetion: "
        //   231: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   234: aload_2         /* e */
        //   235: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   238: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   241: aload_3         /* handlerException */
        //   242: invokeinterface org/slf4j/Logger.error:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   247: goto            278
        //   250: getstatic       org/apache/activemq/store/jdbc/adapter/TransactDatabaseLocker.LOG:Lorg/slf4j/Logger;
        //   253: new             Ljava/lang/StringBuilder;
        //   256: dup            
        //   257: invokespecial   java/lang/StringBuilder.<init>:()V
        //   260: ldc             "Failed to acquire lock: "
        //   262: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   265: aload_2         /* e */
        //   266: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   269: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   272: aload_2         /* e */
        //   273: invokeinterface org/slf4j/Logger.error:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   278: aconst_null    
        //   279: aload_1         /* statement */
        //   280: if_acmpeq       379
        //   283: aload_1         /* statement */
        //   284: invokeinterface java/sql/PreparedStatement.close:()V
        //   289: goto            321
        //   292: astore_2        /* e1 */
        //   293: getstatic       org/apache/activemq/store/jdbc/adapter/TransactDatabaseLocker.LOG:Lorg/slf4j/Logger;
        //   296: new             Ljava/lang/StringBuilder;
        //   299: dup            
        //   300: invokespecial   java/lang/StringBuilder.<init>:()V
        //   303: ldc             "Caught while closing statement: "
        //   305: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   308: aload_2         /* e1 */
        //   309: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   312: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   315: aload_2         /* e1 */
        //   316: invokeinterface org/slf4j/Logger.warn:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   321: aconst_null    
        //   322: astore_1        /* statement */
        //   323: goto            379
        //   326: astore          4
        //   328: aconst_null    
        //   329: aload_1         /* statement */
        //   330: if_acmpeq       376
        //   333: aload_1         /* statement */
        //   334: invokeinterface java/sql/PreparedStatement.close:()V
        //   339: goto            374
        //   342: astore          e1
        //   344: getstatic       org/apache/activemq/store/jdbc/adapter/TransactDatabaseLocker.LOG:Lorg/slf4j/Logger;
        //   347: new             Ljava/lang/StringBuilder;
        //   350: dup            
        //   351: invokespecial   java/lang/StringBuilder.<init>:()V
        //   354: ldc             "Caught while closing statement: "
        //   356: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   359: aload           e1
        //   361: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   364: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   367: aload           e1
        //   369: invokeinterface org/slf4j/Logger.warn:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   374: aconst_null    
        //   375: astore_1        /* statement */
        //   376: aload           4
        //   378: athrow         
        //   379: getstatic       org/apache/activemq/store/jdbc/adapter/TransactDatabaseLocker.LOG:Lorg/slf4j/Logger;
        //   382: new             Ljava/lang/StringBuilder;
        //   385: dup            
        //   386: invokespecial   java/lang/StringBuilder.<init>:()V
        //   389: ldc             "Sleeping for "
        //   391: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   394: aload_0         /* this */
        //   395: getfield        org/apache/activemq/store/jdbc/adapter/TransactDatabaseLocker.lockAcquireSleepInterval:J
        //   398: invokevirtual   java/lang/StringBuilder.append:(J)Ljava/lang/StringBuilder;
        //   401: ldc             " milli(s) before trying again to get the lock..."
        //   403: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   406: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   409: invokeinterface org/slf4j/Logger.debug:(Ljava/lang/String;)V
        //   414: aload_0         /* this */
        //   415: getfield        org/apache/activemq/store/jdbc/adapter/TransactDatabaseLocker.lockAcquireSleepInterval:J
        //   418: invokestatic    java/lang/Thread.sleep:(J)V
        //   421: goto            12
        //   424: astore_2        /* ie */
        //   425: getstatic       org/apache/activemq/store/jdbc/adapter/TransactDatabaseLocker.LOG:Lorg/slf4j/Logger;
        //   428: ldc             "Master lock retry sleep interrupted"
        //   430: aload_2         /* ie */
        //   431: invokeinterface org/slf4j/Logger.warn:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   436: goto            12
        //   439: getstatic       org/apache/activemq/store/jdbc/adapter/TransactDatabaseLocker.LOG:Lorg/slf4j/Logger;
        //   442: new             Ljava/lang/StringBuilder;
        //   445: dup            
        //   446: invokespecial   java/lang/StringBuilder.<init>:()V
        //   449: ldc             "Becoming the master on dataSource: "
        //   451: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   454: aload_0         /* this */
        //   455: getfield        org/apache/activemq/store/jdbc/adapter/TransactDatabaseLocker.dataSource:Ljavax/sql/DataSource;
        //   458: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   461: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   464: invokeinterface org/slf4j/Logger.info:(Ljava/lang/String;)V
        //   469: return         
        //    Exceptions:
        //  throws java.lang.Exception
        //    StackMapTable: 00 13 FC 00 0C 07 00 4C FC 00 43 07 00 4D 06 4D 07 00 4E 1C FF 00 04 00 02 07 00 4F 07 00 4C 00 01 07 00 50 FC 00 23 07 00 50 53 07 00 51 3A FA 00 1B 4D 07 00 4E 1C 44 07 00 51 FF 00 0F 00 05 07 00 4F 07 00 4C 00 00 07 00 51 00 01 07 00 4E 1F 01 F8 00 02 6C 07 00 52 0E
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                            
        //  -----  -----  -----  -----  --------------------------------
        //  92     98     101    130    Ljava/sql/SQLException;
        //  12     87     135    439    Ljava/lang/Exception;
        //  178    188    191    250    Ljava/lang/Throwable;
        //  283    289    292    321    Ljava/sql/SQLException;
        //  12     87     326    379    Any
        //  135    278    326    379    Any
        //  333    339    342    374    Ljava/sql/SQLException;
        //  326    328    326    379    Any
        //  414    421    424    439    Ljava/lang/InterruptedException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index 196 out of bounds for length 196
        //     at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:64)
        //     at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:70)
        //     at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:248)
        //     at java.base/java.util.Objects.checkIndex(Objects.java:372)
        //     at java.base/java.util.ArrayList.get(ArrayList.java:458)
        //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3321)
        //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3569)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:113)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:211)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    static {
        LOG = LoggerFactory.getLogger(TransactDatabaseLocker.class);
    }
}
