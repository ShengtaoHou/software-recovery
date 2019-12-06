// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated;

import org.fusesource.hawtbuf.Buffer;
import scala.collection.immutable.TreeMap;
import scala.runtime.BoxesRunTime;
import scala.Function1;
import scala.runtime.BoxedUnit;
import org.apache.activemq.leveldb.LevelDBClient$;
import org.apache.activemq.leveldb.util.FileSupport$;
import java.io.File;
import sun.nio.ch.DirectBuffer;
import java.nio.MappedByteBuffer;
import org.fusesource.hawtbuf.AsciiBuffer;

public final class ReplicationSupport$
{
    public static final ReplicationSupport$ MODULE$;
    private final AsciiBuffer WAL_ACTION;
    private final AsciiBuffer LOGIN_ACTION;
    private final AsciiBuffer SYNC_ACTION;
    private final AsciiBuffer GET_ACTION;
    private final AsciiBuffer ACK_ACTION;
    private final AsciiBuffer OK_ACTION;
    private final AsciiBuffer DISCONNECT_ACTION;
    private final AsciiBuffer ERROR_ACTION;
    private final AsciiBuffer LOG_DELETE_ACTION;
    
    static {
        new ReplicationSupport$();
    }
    
    public AsciiBuffer WAL_ACTION() {
        return this.WAL_ACTION;
    }
    
    public AsciiBuffer LOGIN_ACTION() {
        return this.LOGIN_ACTION;
    }
    
    public AsciiBuffer SYNC_ACTION() {
        return this.SYNC_ACTION;
    }
    
    public AsciiBuffer GET_ACTION() {
        return this.GET_ACTION;
    }
    
    public AsciiBuffer ACK_ACTION() {
        return this.ACK_ACTION;
    }
    
    public AsciiBuffer OK_ACTION() {
        return this.OK_ACTION;
    }
    
    public AsciiBuffer DISCONNECT_ACTION() {
        return this.DISCONNECT_ACTION;
    }
    
    public AsciiBuffer ERROR_ACTION() {
        return this.ERROR_ACTION;
    }
    
    public AsciiBuffer LOG_DELETE_ACTION() {
        return this.LOG_DELETE_ACTION;
    }
    
    public void unmap(final MappedByteBuffer buffer) {
        try {
            ((DirectBuffer)buffer).cleaner().clean();
        }
        finally {}
    }
    
    public MappedByteBuffer map(final File file, final long offset, final long length, final boolean readOnly) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: dup            
        //     4: aload_1         /* file */
        //     5: iload           readOnly
        //     7: ifeq            15
        //    10: ldc             "r"
        //    12: goto            17
        //    15: ldc             "rw"
        //    17: invokespecial   java/io/RandomAccessFile.<init>:(Ljava/io/File;Ljava/lang/String;)V
        //    20: astore          raf
        //    22: iload           readOnly
        //    24: ifeq            33
        //    27: getstatic       java/nio/channels/FileChannel$MapMode.READ_ONLY:Ljava/nio/channels/FileChannel$MapMode;
        //    30: goto            36
        //    33: getstatic       java/nio/channels/FileChannel$MapMode.READ_WRITE:Ljava/nio/channels/FileChannel$MapMode;
        //    36: astore          mode
        //    38: aload           raf
        //    40: invokevirtual   java/io/RandomAccessFile.getChannel:()Ljava/nio/channels/FileChannel;
        //    43: aload           mode
        //    45: lload_2         /* offset */
        //    46: lload           length
        //    48: invokevirtual   java/nio/channels/FileChannel.map:(Ljava/nio/channels/FileChannel$MapMode;JJ)Ljava/nio/MappedByteBuffer;
        //    51: aload           raf
        //    53: invokevirtual   java/io/RandomAccessFile.close:()V
        //    56: areturn        
        //    57: astore          8
        //    59: aload           7
        //    61: invokevirtual   java/io/RandomAccessFile.close:()V
        //    64: aload           8
        //    66: athrow         
        //    StackMapTable: 00 05 FF 00 0F 00 05 07 00 02 07 00 67 04 04 01 00 03 08 00 00 08 00 00 07 00 67 FF 00 01 00 05 07 00 02 07 00 67 04 04 01 00 04 08 00 00 08 00 00 07 00 67 07 00 69 FC 00 0F 07 00 3F 42 07 00 48 54 07 00 3B
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  22     51     57     67     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.decompiler.ast.AstBuilder.convertLocalVariables(AstBuilder.java:2895)
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2445)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
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
    
    public void stash(final File directory) {
        directory.mkdirs();
        final File tmp_stash = FileSupport$.MODULE$.toRichFile(directory).$div("stash.tmp");
        final File stash = FileSupport$.MODULE$.toRichFile(directory).$div("stash");
        FileSupport$.MODULE$.toRichFile(stash).recursiveDelete();
        FileSupport$.MODULE$.toRichFile(tmp_stash).recursiveDelete();
        tmp_stash.mkdirs();
        this.copy_store_dir(directory, tmp_stash);
        tmp_stash.renameTo(stash);
    }
    
    public void copy_store_dir(final File from, final File to) {
        final TreeMap log_files = LevelDBClient$.MODULE$.find_sequence_files(from, ".log");
        if (log_files.isEmpty()) {
            final BoxedUnit unit = BoxedUnit.UNIT;
        }
        else {
            final File append_file = (File)log_files.last()._2();
            log_files.values().withFilter((Function1)new ReplicationSupport$$anonfun$copy_store_dir.ReplicationSupport$$anonfun$copy_store_dir$1(append_file)).foreach((Function1)new ReplicationSupport$$anonfun$copy_store_dir.ReplicationSupport$$anonfun$copy_store_dir$2(to));
            BoxesRunTime.boxToLong(FileSupport$.MODULE$.toRichFile(append_file).copyTo(FileSupport$.MODULE$.toRichFile(to).$div(append_file.getName())));
        }
        final TreeMap index_dirs = LevelDBClient$.MODULE$.find_sequence_files(from, ".index");
        if (!index_dirs.isEmpty()) {
            final File index_file = (File)index_dirs.last()._2();
            final File target = FileSupport$.MODULE$.toRichFile(to).$div(index_file.getName());
            target.mkdirs();
            LevelDBClient$.MODULE$.copyIndex(index_file, target);
        }
    }
    
    public void stash_clear(final File directory) {
        final File stash = FileSupport$.MODULE$.toRichFile(directory).$div("stash");
        FileSupport$.MODULE$.toRichFile(stash).recursiveDelete();
    }
    
    public void unstash(final File directory) {
        final File tmp_stash = FileSupport$.MODULE$.toRichFile(directory).$div("stash.tmp");
        FileSupport$.MODULE$.toRichFile(tmp_stash).recursiveDelete();
        final File stash = FileSupport$.MODULE$.toRichFile(directory).$div("stash");
        if (stash.exists()) {
            this.delete_store(directory);
            this.copy_store_dir(stash, directory);
            FileSupport$.MODULE$.toRichFile(stash).recursiveDelete();
        }
    }
    
    public void delete_store(final File directory) {
        final TreeMap t = LevelDBClient$.MODULE$.find_sequence_files(directory, ".log");
        t.foreach((Function1)new ReplicationSupport$$anonfun$delete_store.ReplicationSupport$$anonfun$delete_store$1(directory));
        LevelDBClient$.MODULE$.find_sequence_files(directory, ".index").foreach((Function1)new ReplicationSupport$$anonfun$delete_store.ReplicationSupport$$anonfun$delete_store$2());
    }
    
    private ReplicationSupport$() {
        MODULE$ = this;
        this.WAL_ACTION = Buffer.ascii("wal");
        this.LOGIN_ACTION = Buffer.ascii("LevelDB Store Replication v1:login");
        this.SYNC_ACTION = Buffer.ascii("sync");
        this.GET_ACTION = Buffer.ascii("get");
        this.ACK_ACTION = Buffer.ascii("ack");
        this.OK_ACTION = Buffer.ascii("ok");
        this.DISCONNECT_ACTION = Buffer.ascii("disconnect");
        this.ERROR_ACTION = Buffer.ascii("error");
        this.LOG_DELETE_ACTION = Buffer.ascii("rm");
    }
}
