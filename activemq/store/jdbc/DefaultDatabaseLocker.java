// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.jdbc;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import org.apache.activemq.util.ServiceStopper;
import org.apache.activemq.util.Handler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import org.slf4j.Logger;

public class DefaultDatabaseLocker extends AbstractJDBCLocker
{
    private static final Logger LOG;
    protected volatile PreparedStatement lockCreateStatement;
    protected volatile PreparedStatement lockUpdateStatement;
    protected volatile Connection connection;
    protected Handler<Exception> exceptionHandler;
    
    public void doStart() throws Exception {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: ldc             "Attempting to acquire the exclusive lock to become the Master broker"
        //     5: invokeinterface org/slf4j/Logger.info:(Ljava/lang/String;)V
        //    10: aload_0         /* this */
        //    11: invokevirtual   org/apache/activemq/store/jdbc/DefaultDatabaseLocker.getStatements:()Lorg/apache/activemq/store/jdbc/Statements;
        //    14: invokevirtual   org/apache/activemq/store/jdbc/Statements.getLockCreateStatement:()Ljava/lang/String;
        //    17: astore_1        /* sql */
        //    18: getstatic       org/apache/activemq/store/jdbc/DefaultDatabaseLocker.LOG:Lorg/slf4j/Logger;
        //    21: new             Ljava/lang/StringBuilder;
        //    24: dup            
        //    25: invokespecial   java/lang/StringBuilder.<init>:()V
        //    28: ldc             "Locking Query is "
        //    30: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    33: aload_1         /* sql */
        //    34: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    37: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //    40: invokeinterface org/slf4j/Logger.debug:(Ljava/lang/String;)V
        //    45: aload_0         /* this */
        //    46: aload_0         /* this */
        //    47: getfield        org/apache/activemq/store/jdbc/DefaultDatabaseLocker.dataSource:Ljavax/sql/DataSource;
        //    50: invokeinterface javax/sql/DataSource.getConnection:()Ljava/sql/Connection;
        //    55: putfield        org/apache/activemq/store/jdbc/DefaultDatabaseLocker.connection:Ljava/sql/Connection;
        //    58: aload_0         /* this */
        //    59: getfield        org/apache/activemq/store/jdbc/DefaultDatabaseLocker.connection:Ljava/sql/Connection;
        //    62: iconst_0       
        //    63: invokeinterface java/sql/Connection.setAutoCommit:(Z)V
        //    68: aload_0         /* this */
        //    69: aload_0         /* this */
        //    70: getfield        org/apache/activemq/store/jdbc/DefaultDatabaseLocker.connection:Ljava/sql/Connection;
        //    73: aload_1         /* sql */
        //    74: invokeinterface java/sql/Connection.prepareStatement:(Ljava/lang/String;)Ljava/sql/PreparedStatement;
        //    79: putfield        org/apache/activemq/store/jdbc/DefaultDatabaseLocker.lockCreateStatement:Ljava/sql/PreparedStatement;
        //    82: aload_0         /* this */
        //    83: getfield        org/apache/activemq/store/jdbc/DefaultDatabaseLocker.lockCreateStatement:Ljava/sql/PreparedStatement;
        //    86: invokeinterface java/sql/PreparedStatement.execute:()Z
        //    91: pop            
        //    92: aconst_null    
        //    93: aload_0         /* this */
        //    94: getfield        org/apache/activemq/store/jdbc/DefaultDatabaseLocker.lockCreateStatement:Ljava/sql/PreparedStatement;
        //    97: if_acmpeq       675
        //   100: aload_0         /* this */
        //   101: getfield        org/apache/activemq/store/jdbc/DefaultDatabaseLocker.lockCreateStatement:Ljava/sql/PreparedStatement;
        //   104: invokeinterface java/sql/PreparedStatement.close:()V
        //   109: goto            141
        //   112: astore_2        /* e1 */
        //   113: getstatic       org/apache/activemq/store/jdbc/DefaultDatabaseLocker.LOG:Lorg/slf4j/Logger;
        //   116: new             Ljava/lang/StringBuilder;
        //   119: dup            
        //   120: invokespecial   java/lang/StringBuilder.<init>:()V
        //   123: ldc             "Caught while closing statement: "
        //   125: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   128: aload_2         /* e1 */
        //   129: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   132: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   135: aload_2         /* e1 */
        //   136: invokeinterface org/slf4j/Logger.debug:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   141: aload_0         /* this */
        //   142: aconst_null    
        //   143: putfield        org/apache/activemq/store/jdbc/DefaultDatabaseLocker.lockCreateStatement:Ljava/sql/PreparedStatement;
        //   146: goto            675
        //   149: astore_2        /* e */
        //   150: aload_0         /* this */
        //   151: invokevirtual   org/apache/activemq/store/jdbc/DefaultDatabaseLocker.isStopping:()Z
        //   154: ifeq            185
        //   157: new             Ljava/lang/Exception;
        //   160: dup            
        //   161: new             Ljava/lang/StringBuilder;
        //   164: dup            
        //   165: invokespecial   java/lang/StringBuilder.<init>:()V
        //   168: ldc             "Cannot start broker as being asked to shut down. Interrupted attempt to acquire lock: "
        //   170: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   173: aload_2         /* e */
        //   174: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   177: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   180: aload_2         /* e */
        //   181: invokespecial   java/lang/Exception.<init>:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   184: athrow         
        //   185: aload_0         /* this */
        //   186: getfield        org/apache/activemq/store/jdbc/DefaultDatabaseLocker.exceptionHandler:Lorg/apache/activemq/util/Handler;
        //   189: ifnull          264
        //   192: aload_0         /* this */
        //   193: getfield        org/apache/activemq/store/jdbc/DefaultDatabaseLocker.exceptionHandler:Lorg/apache/activemq/util/Handler;
        //   196: aload_2         /* e */
        //   197: invokeinterface org/apache/activemq/util/Handler.handle:(Ljava/lang/Object;)V
        //   202: goto            292
        //   205: astore_3        /* handlerException */
        //   206: getstatic       org/apache/activemq/store/jdbc/DefaultDatabaseLocker.LOG:Lorg/slf4j/Logger;
        //   209: new             Ljava/lang/StringBuilder;
        //   212: dup            
        //   213: invokespecial   java/lang/StringBuilder.<init>:()V
        //   216: ldc             "The exception handler "
        //   218: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   221: aload_0         /* this */
        //   222: getfield        org/apache/activemq/store/jdbc/DefaultDatabaseLocker.exceptionHandler:Lorg/apache/activemq/util/Handler;
        //   225: invokevirtual   java/lang/Object.getClass:()Ljava/lang/Class;
        //   228: invokevirtual   java/lang/Class.getCanonicalName:()Ljava/lang/String;
        //   231: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   234: ldc             " threw this exception: "
        //   236: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   239: aload_3         /* handlerException */
        //   240: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   243: ldc             " while trying to handle this exception: "
        //   245: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   248: aload_2         /* e */
        //   249: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   252: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   255: aload_3         /* handlerException */
        //   256: invokeinterface org/slf4j/Logger.error:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   261: goto            292
        //   264: getstatic       org/apache/activemq/store/jdbc/DefaultDatabaseLocker.LOG:Lorg/slf4j/Logger;
        //   267: new             Ljava/lang/StringBuilder;
        //   270: dup            
        //   271: invokespecial   java/lang/StringBuilder.<init>:()V
        //   274: ldc             "Lock failure: "
        //   276: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   279: aload_2         /* e */
        //   280: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   283: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   286: aload_2         /* e */
        //   287: invokeinterface org/slf4j/Logger.debug:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   292: aconst_null    
        //   293: aload_0         /* this */
        //   294: getfield        org/apache/activemq/store/jdbc/DefaultDatabaseLocker.connection:Ljava/sql/Connection;
        //   297: if_acmpeq       496
        //   300: aload_0         /* this */
        //   301: getfield        org/apache/activemq/store/jdbc/DefaultDatabaseLocker.connection:Ljava/sql/Connection;
        //   304: invokeinterface java/sql/Connection.rollback:()V
        //   309: goto            341
        //   312: astore_3        /* e1 */
        //   313: getstatic       org/apache/activemq/store/jdbc/DefaultDatabaseLocker.LOG:Lorg/slf4j/Logger;
        //   316: new             Ljava/lang/StringBuilder;
        //   319: dup            
        //   320: invokespecial   java/lang/StringBuilder.<init>:()V
        //   323: ldc             "Caught exception during rollback on connection: "
        //   325: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   328: aload_3         /* e1 */
        //   329: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   332: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   335: aload_3         /* e1 */
        //   336: invokeinterface org/slf4j/Logger.debug:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   341: aload_0         /* this */
        //   342: getfield        org/apache/activemq/store/jdbc/DefaultDatabaseLocker.connection:Ljava/sql/Connection;
        //   345: invokeinterface java/sql/Connection.close:()V
        //   350: goto            382
        //   353: astore_3        /* e1 */
        //   354: getstatic       org/apache/activemq/store/jdbc/DefaultDatabaseLocker.LOG:Lorg/slf4j/Logger;
        //   357: new             Ljava/lang/StringBuilder;
        //   360: dup            
        //   361: invokespecial   java/lang/StringBuilder.<init>:()V
        //   364: ldc             "Caught exception while closing connection: "
        //   366: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   369: aload_3         /* e1 */
        //   370: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   373: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   376: aload_3         /* e1 */
        //   377: invokeinterface org/slf4j/Logger.debug:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   382: aload_0         /* this */
        //   383: aconst_null    
        //   384: putfield        org/apache/activemq/store/jdbc/DefaultDatabaseLocker.connection:Ljava/sql/Connection;
        //   387: goto            496
        //   390: astore          4
        //   392: aconst_null    
        //   393: aload_0         /* this */
        //   394: getfield        org/apache/activemq/store/jdbc/DefaultDatabaseLocker.connection:Ljava/sql/Connection;
        //   397: if_acmpeq       493
        //   400: aload_0         /* this */
        //   401: getfield        org/apache/activemq/store/jdbc/DefaultDatabaseLocker.connection:Ljava/sql/Connection;
        //   404: invokeinterface java/sql/Connection.rollback:()V
        //   409: goto            444
        //   412: astore          e1
        //   414: getstatic       org/apache/activemq/store/jdbc/DefaultDatabaseLocker.LOG:Lorg/slf4j/Logger;
        //   417: new             Ljava/lang/StringBuilder;
        //   420: dup            
        //   421: invokespecial   java/lang/StringBuilder.<init>:()V
        //   424: ldc             "Caught exception during rollback on connection: "
        //   426: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   429: aload           e1
        //   431: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   434: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   437: aload           e1
        //   439: invokeinterface org/slf4j/Logger.debug:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   444: aload_0         /* this */
        //   445: getfield        org/apache/activemq/store/jdbc/DefaultDatabaseLocker.connection:Ljava/sql/Connection;
        //   448: invokeinterface java/sql/Connection.close:()V
        //   453: goto            488
        //   456: astore          e1
        //   458: getstatic       org/apache/activemq/store/jdbc/DefaultDatabaseLocker.LOG:Lorg/slf4j/Logger;
        //   461: new             Ljava/lang/StringBuilder;
        //   464: dup            
        //   465: invokespecial   java/lang/StringBuilder.<init>:()V
        //   468: ldc             "Caught exception while closing connection: "
        //   470: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   473: aload           e1
        //   475: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   478: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   481: aload           e1
        //   483: invokeinterface org/slf4j/Logger.debug:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   488: aload_0         /* this */
        //   489: aconst_null    
        //   490: putfield        org/apache/activemq/store/jdbc/DefaultDatabaseLocker.connection:Ljava/sql/Connection;
        //   493: aload           4
        //   495: athrow         
        //   496: aconst_null    
        //   497: aload_0         /* this */
        //   498: getfield        org/apache/activemq/store/jdbc/DefaultDatabaseLocker.lockCreateStatement:Ljava/sql/PreparedStatement;
        //   501: if_acmpeq       615
        //   504: aload_0         /* this */
        //   505: getfield        org/apache/activemq/store/jdbc/DefaultDatabaseLocker.lockCreateStatement:Ljava/sql/PreparedStatement;
        //   508: invokeinterface java/sql/PreparedStatement.close:()V
        //   513: goto            545
        //   516: astore_2        /* e1 */
        //   517: getstatic       org/apache/activemq/store/jdbc/DefaultDatabaseLocker.LOG:Lorg/slf4j/Logger;
        //   520: new             Ljava/lang/StringBuilder;
        //   523: dup            
        //   524: invokespecial   java/lang/StringBuilder.<init>:()V
        //   527: ldc             "Caught while closing statement: "
        //   529: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   532: aload_2         /* e1 */
        //   533: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   536: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   539: aload_2         /* e1 */
        //   540: invokeinterface org/slf4j/Logger.debug:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   545: aload_0         /* this */
        //   546: aconst_null    
        //   547: putfield        org/apache/activemq/store/jdbc/DefaultDatabaseLocker.lockCreateStatement:Ljava/sql/PreparedStatement;
        //   550: goto            615
        //   553: astore          6
        //   555: aconst_null    
        //   556: aload_0         /* this */
        //   557: getfield        org/apache/activemq/store/jdbc/DefaultDatabaseLocker.lockCreateStatement:Ljava/sql/PreparedStatement;
        //   560: if_acmpeq       612
        //   563: aload_0         /* this */
        //   564: getfield        org/apache/activemq/store/jdbc/DefaultDatabaseLocker.lockCreateStatement:Ljava/sql/PreparedStatement;
        //   567: invokeinterface java/sql/PreparedStatement.close:()V
        //   572: goto            607
        //   575: astore          e1
        //   577: getstatic       org/apache/activemq/store/jdbc/DefaultDatabaseLocker.LOG:Lorg/slf4j/Logger;
        //   580: new             Ljava/lang/StringBuilder;
        //   583: dup            
        //   584: invokespecial   java/lang/StringBuilder.<init>:()V
        //   587: ldc             "Caught while closing statement: "
        //   589: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   592: aload           e1
        //   594: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   597: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   600: aload           e1
        //   602: invokeinterface org/slf4j/Logger.debug:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   607: aload_0         /* this */
        //   608: aconst_null    
        //   609: putfield        org/apache/activemq/store/jdbc/DefaultDatabaseLocker.lockCreateStatement:Ljava/sql/PreparedStatement;
        //   612: aload           6
        //   614: athrow         
        //   615: getstatic       org/apache/activemq/store/jdbc/DefaultDatabaseLocker.LOG:Lorg/slf4j/Logger;
        //   618: new             Ljava/lang/StringBuilder;
        //   621: dup            
        //   622: invokespecial   java/lang/StringBuilder.<init>:()V
        //   625: ldc             "Failed to acquire lock.  Sleeping for "
        //   627: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   630: aload_0         /* this */
        //   631: getfield        org/apache/activemq/store/jdbc/DefaultDatabaseLocker.lockAcquireSleepInterval:J
        //   634: invokevirtual   java/lang/StringBuilder.append:(J)Ljava/lang/StringBuilder;
        //   637: ldc             " milli(s) before trying again..."
        //   639: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   642: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   645: invokeinterface org/slf4j/Logger.info:(Ljava/lang/String;)V
        //   650: aload_0         /* this */
        //   651: getfield        org/apache/activemq/store/jdbc/DefaultDatabaseLocker.lockAcquireSleepInterval:J
        //   654: invokestatic    java/lang/Thread.sleep:(J)V
        //   657: goto            45
        //   660: astore_2        /* ie */
        //   661: getstatic       org/apache/activemq/store/jdbc/DefaultDatabaseLocker.LOG:Lorg/slf4j/Logger;
        //   664: ldc             "Master lock retry sleep interrupted"
        //   666: aload_2         /* ie */
        //   667: invokeinterface org/slf4j/Logger.warn:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   672: goto            45
        //   675: getstatic       org/apache/activemq/store/jdbc/DefaultDatabaseLocker.LOG:Lorg/slf4j/Logger;
        //   678: new             Ljava/lang/StringBuilder;
        //   681: dup            
        //   682: invokespecial   java/lang/StringBuilder.<init>:()V
        //   685: ldc             "Becoming the master on dataSource: "
        //   687: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   690: aload_0         /* this */
        //   691: getfield        org/apache/activemq/store/jdbc/DefaultDatabaseLocker.dataSource:Ljavax/sql/DataSource;
        //   694: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   697: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   700: invokeinterface org/slf4j/Logger.info:(Ljava/lang/String;)V
        //   705: return         
        //    Exceptions:
        //  throws java.lang.Exception
        //    StackMapTable: 00 1C FC 00 2D 07 00 62 F7 00 42 07 00 63 1C 47 07 00 64 FC 00 23 07 00 64 53 07 00 65 3A 1B 53 07 00 63 1C 4B 07 00 63 1C 47 07 00 65 FF 00 15 00 05 07 00 66 07 00 62 07 00 64 00 07 00 65 00 01 07 00 63 1F 4B 07 00 63 1F 04 F8 00 02 53 07 00 63 1C 47 07 00 65 FF 00 15 00 07 07 00 66 07 00 62 00 00 00 00 07 00 65 00 01 07 00 63 1F 04 FF 00 02 00 02 07 00 66 07 00 62 00 00 6C 07 00 67 0E
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                            
        //  -----  -----  -----  -----  --------------------------------
        //  100    109    112    141    Ljava/sql/SQLException;
        //  45     92     149    675    Ljava/lang/Exception;
        //  192    202    205    264    Ljava/lang/Throwable;
        //  300    309    312    341    Ljava/sql/SQLException;
        //  341    350    353    382    Ljava/sql/SQLException;
        //  150    292    390    496    Any
        //  400    409    412    444    Ljava/sql/SQLException;
        //  444    453    456    488    Ljava/sql/SQLException;
        //  390    392    390    496    Any
        //  504    513    516    545    Ljava/sql/SQLException;
        //  45     92     553    615    Any
        //  149    496    553    615    Any
        //  563    572    575    607    Ljava/sql/SQLException;
        //  553    555    553    615    Any
        //  650    657    660    675    Ljava/lang/InterruptedException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index 287 out of bounds for length 287
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
    
    public void doStop(final ServiceStopper stopper) throws Exception {
        try {
            if (this.lockCreateStatement != null) {
                this.lockCreateStatement.cancel();
            }
        }
        catch (SQLFeatureNotSupportedException e) {
            DefaultDatabaseLocker.LOG.warn("Failed to cancel locking query on dataSource" + this.dataSource, e);
        }
        try {
            if (this.lockUpdateStatement != null) {
                this.lockUpdateStatement.cancel();
            }
        }
        catch (SQLFeatureNotSupportedException e) {
            DefaultDatabaseLocker.LOG.warn("Failed to cancel locking query on dataSource" + this.dataSource, e);
        }
        if (this.connection != null) {
            try {
                this.connection.rollback();
            }
            catch (SQLException sqle) {
                DefaultDatabaseLocker.LOG.warn("Exception while rollbacking the connection on shutdown. This exception is ignored.", sqle);
            }
            finally {
                try {
                    this.connection.close();
                }
                catch (SQLException ignored) {
                    DefaultDatabaseLocker.LOG.debug("Exception while closing connection on shutdown. This exception is ignored.", ignored);
                }
                this.lockCreateStatement = null;
            }
        }
    }
    
    @Override
    public boolean keepAlive() throws IOException {
        boolean result = false;
        try {
            (this.lockUpdateStatement = this.connection.prepareStatement(this.getStatements().getLockUpdateStatement())).setLong(1, System.currentTimeMillis());
            this.setQueryTimeout(this.lockUpdateStatement);
            final int rows = this.lockUpdateStatement.executeUpdate();
            if (rows == 1) {
                result = true;
            }
        }
        catch (Exception e) {
            DefaultDatabaseLocker.LOG.error("Failed to update database lock: " + e, e);
        }
        finally {
            if (this.lockUpdateStatement != null) {
                try {
                    this.lockUpdateStatement.close();
                }
                catch (SQLException e2) {
                    DefaultDatabaseLocker.LOG.error("Failed to close statement", e2);
                }
                this.lockUpdateStatement = null;
            }
        }
        return result;
    }
    
    @Override
    public long getLockAcquireSleepInterval() {
        return this.lockAcquireSleepInterval;
    }
    
    @Override
    public void setLockAcquireSleepInterval(final long lockAcquireSleepInterval) {
        this.lockAcquireSleepInterval = lockAcquireSleepInterval;
    }
    
    public Handler getExceptionHandler() {
        return this.exceptionHandler;
    }
    
    public void setExceptionHandler(final Handler exceptionHandler) {
        this.exceptionHandler = (Handler<Exception>)exceptionHandler;
    }
    
    static {
        LOG = LoggerFactory.getLogger(DefaultDatabaseLocker.class);
    }
}
