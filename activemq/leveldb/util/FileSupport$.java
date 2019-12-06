// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import scala.Function1;
import java.io.OutputStream;
import java.io.InputStream;
import scala.Tuple3;
import scala.None$;
import scala.Function0;
import scala.MatchError;
import scala.runtime.BoxesRunTime;
import scala.collection.Seq;
import scala.Predef$;
import scala.runtime.BoxedUnit;
import java.io.File;

public final class FileSupport$
{
    public static final FileSupport$ MODULE$;
    private final boolean onWindows;
    private int linkStrategy;
    private final Log LOG;
    
    static {
        new FileSupport$();
    }
    
    public FileSupport.RichFile toRichFile(final File file) {
        return new FileSupport.RichFile(file);
    }
    
    public boolean onWindows() {
        return this.onWindows;
    }
    
    private int linkStrategy() {
        return this.linkStrategy;
    }
    
    private void linkStrategy_$eq(final int x$1) {
        this.linkStrategy = x$1;
    }
    
    private Log LOG() {
        return this.LOG;
    }
    
    public void link(final File source, final File target) {
        switch (this.linkStrategy()) {
            default: {
                this.toRichFile(source).copyTo(target);
                final BoxedUnit boxedUnit = BoxedUnit.UNIT;
                break;
            }
            case 5: {
                try {
                    if (this.onWindows()) {
                        final Tuple3<Object, byte[], byte[]> system = ProcessSupport$.MODULE$.system((Seq<String>)Predef$.MODULE$.wrapRefArray((Object[])new String[] { "fsutil", "hardlink", "create", target.getCanonicalPath(), source.getCanonicalPath() }));
                        if (system != null && 0 == BoxesRunTime.unboxToInt(system._1())) {
                            final BoxedUnit unit = BoxedUnit.UNIT;
                        }
                        else {
                            if (system == null) {
                                throw new MatchError((Object)system);
                            }
                            this.LOG().debug((Function0<String>)new FileSupport$$anonfun$link.FileSupport$$anonfun$link$2(), (Seq<Object>)Predef$.MODULE$.genericWrapArray((Object)new Object[0]));
                            this.linkStrategy_$eq(10);
                            this.link(source, target);
                            final BoxedUnit unit2 = BoxedUnit.UNIT;
                        }
                        break;
                    }
                    final Tuple3<Object, byte[], byte[]> system2 = ProcessSupport$.MODULE$.system((Seq<String>)Predef$.MODULE$.wrapRefArray((Object[])new String[] { "ln", source.getCanonicalPath(), target.getCanonicalPath() }));
                    if (system2 != null && 0 == BoxesRunTime.unboxToInt(system2._1())) {
                        final BoxedUnit unit3 = BoxedUnit.UNIT;
                    }
                    else {
                        if (system2 == null) {
                            throw new MatchError((Object)system2);
                        }
                        final None$ module$ = None$.MODULE$;
                        this.LOG().debug((Function0<String>)new FileSupport$$anonfun$link.FileSupport$$anonfun$link$3(), (Seq<Object>)Predef$.MODULE$.genericWrapArray((Object)new Object[0]));
                        this.linkStrategy_$eq(2);
                        this.link(source, target);
                        final BoxedUnit unit4 = BoxedUnit.UNIT;
                    }
                    break;
                }
                finally {
                    final BoxedUnit boxedUnit = BoxedUnit.UNIT;
                }
                break;
            }
            case 0: {
                goto Label_0378;
                goto Label_0378;
            }
        }
    }
    
    public File systemDir(final String name) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokestatic    java/lang/System.getProperty:(Ljava/lang/String;)Ljava/lang/String;
        //     4: astore_2        /* baseValue */
        //     5: aload_2         /* baseValue */
        //     6: ifnonnull       48
        //     9: getstatic       scala/sys/package$.MODULE$:Lscala/sys/package$;
        //    12: new             Lscala/collection/immutable/StringOps;
        //    15: dup            
        //    16: getstatic       scala/Predef$.MODULE$:Lscala/Predef$;
        //    19: ldc             "The the %s system property is not set."
        //    21: invokevirtual   scala/Predef$.augmentString:(Ljava/lang/String;)Ljava/lang/String;
        //    24: invokespecial   scala/collection/immutable/StringOps.<init>:(Ljava/lang/String;)V
        //    27: getstatic       scala/Predef$.MODULE$:Lscala/Predef$;
        //    30: iconst_1       
        //    31: anewarray       Ljava/lang/Object;
        //    34: dup            
        //    35: iconst_0       
        //    36: aload_1         /* name */
        //    37: aastore        
        //    38: invokevirtual   scala/Predef$.genericWrapArray:(Ljava/lang/Object;)Lscala/collection/mutable/WrappedArray;
        //    41: invokevirtual   scala/collection/immutable/StringOps.format:(Lscala/collection/Seq;)Ljava/lang/String;
        //    44: invokevirtual   scala/sys/package$.error:(Ljava/lang/String;)Lscala/runtime/Nothing$;
        //    47: athrow         
        //    48: new             Ljava/io/File;
        //    51: dup            
        //    52: aload_2         /* baseValue */
        //    53: invokespecial   java/io/File.<init>:(Ljava/lang/String;)V
        //    56: astore_3        /* file */
        //    57: aload_3         /* file */
        //    58: invokevirtual   java/io/File.isDirectory:()Z
        //    61: ifeq            66
        //    64: aload_3         /* file */
        //    65: areturn        
        //    66: getstatic       scala/sys/package$.MODULE$:Lscala/sys/package$;
        //    69: new             Lscala/collection/immutable/StringOps;
        //    72: dup            
        //    73: getstatic       scala/Predef$.MODULE$:Lscala/Predef$;
        //    76: ldc             "The the %s system property is not set to valid directory path %s"
        //    78: invokevirtual   scala/Predef$.augmentString:(Ljava/lang/String;)Ljava/lang/String;
        //    81: invokespecial   scala/collection/immutable/StringOps.<init>:(Ljava/lang/String;)V
        //    84: getstatic       scala/Predef$.MODULE$:Lscala/Predef$;
        //    87: iconst_2       
        //    88: anewarray       Ljava/lang/Object;
        //    91: dup            
        //    92: iconst_0       
        //    93: aload_1         /* name */
        //    94: aastore        
        //    95: dup            
        //    96: iconst_1       
        //    97: aload_2        
        //    98: aastore        
        //    99: invokevirtual   scala/Predef$.genericWrapArray:(Ljava/lang/Object;)Lscala/collection/mutable/WrappedArray;
        //   102: invokevirtual   scala/collection/immutable/StringOps.format:(Lscala/collection/Seq;)Ljava/lang/String;
        //   105: invokevirtual   scala/sys/package$.error:(Ljava/lang/String;)Lscala/runtime/Nothing$;
        //   108: athrow         
        //    StackMapTable: 00 02 FC 00 30 07 00 59 FC 00 11 07 00 61
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
    
    public long copy(final InputStream in, final OutputStream out) {
        long bytesCopied = 0L;
        final byte[] buffer = new byte[8192];
        for (int bytes = in.read(buffer); bytes >= 0; bytes = in.read(buffer)) {
            out.write(buffer, 0, bytes);
            bytesCopied += bytes;
        }
        return bytesCopied;
    }
    
    public <R, C extends Closeable> R using(final C closable, final Function1<C, R> proc) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: aload_1         /* closable */
        //     2: invokeinterface scala/Function1.apply:(Ljava/lang/Object;)Ljava/lang/Object;
        //     7: astore_3       
        //     8: aload_1         /* closable */
        //     9: invokeinterface java/io/Closeable.close:()V
        //    14: goto            35
        //    17: astore          4
        //    19: aload_1         /* closable */
        //    20: invokeinterface java/io/Closeable.close:()V
        //    25: goto            30
        //    28: astore          5
        //    30: aload           4
        //    32: athrow         
        //    33: astore          5
        //    35: aload_3        
        //    36: areturn        
        //    Signature:
        //  <R:Ljava/lang/Object;C::Ljava/io/Closeable;>(TC;Lscala/Function1<TC;TR;>;)TR;
        //    StackMapTable: 00 05 51 07 00 96 FF 00 0A 00 05 07 00 02 07 00 E0 07 00 DA 00 07 00 96 00 01 07 00 96 01 FF 00 02 00 04 07 00 02 07 00 E0 07 00 DA 07 00 04 00 01 07 00 96 01
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  8      17     33     35     Any
        //  19     28     28     30     Any
        //  0      7      17     33     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0030:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2596)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
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
    
    public String readText(final InputStream in, final String charset) {
        return new String(this.readBytes(in), charset);
    }
    
    public String readText$default$2() {
        return "UTF-8";
    }
    
    public byte[] readBytes(final InputStream in) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.copy(in, out);
        return out.toByteArray();
    }
    
    public void writeText(final OutputStream out, final String value, final String charset) {
        this.writeBytes(out, value.getBytes(charset));
    }
    
    public String writeText$default$3() {
        return "UTF-8";
    }
    
    public void writeBytes(final OutputStream out, final byte[] data) {
        this.copy(new ByteArrayInputStream(data), out);
    }
    
    private FileSupport$() {
        MODULE$ = this;
        this.onWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        this.linkStrategy = 0;
        this.LOG = Log$.MODULE$.apply(this.getClass());
    }
}
