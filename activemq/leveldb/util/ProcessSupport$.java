// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.util;

import scala.runtime.BoxesRunTime;
import scala.Tuple3;
import scala.Function1;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import scala.runtime.BoxedUnit;
import scala.Function3;
import scala.reflect.ClassTag$;
import scala.collection.Seq;

public final class ProcessSupport$
{
    public static final ProcessSupport$ MODULE$;
    
    static {
        new ProcessSupport$();
    }
    
    public ProcessSupport.RichProcessBuilder toRichProcessBuilder(final ProcessBuilder self) {
        return new ProcessSupport.RichProcessBuilder(self);
    }
    
    public ProcessSupport.RichProcess toRichProcess(final Process self) {
        return new ProcessSupport.RichProcess(self);
    }
    
    public ProcessBuilder toProcessBuilder(final Seq<String> args) {
        return new ProcessBuilder(new String[0]).command((String[])args.toArray(ClassTag$.MODULE$.apply((Class)String.class)));
    }
    
    public void launch(final Seq<String> command, final Function3<Object, byte[], byte[], BoxedUnit> func) {
        this.launch(this.toProcessBuilder(command), this.launch$default$2(), func);
    }
    
    public void launch(final ProcessBuilder p, final InputStream in, final Function3<Object, byte[], byte[], BoxedUnit> func) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ByteArrayOutputStream err = new ByteArrayOutputStream();
        this.toRichProcess(this.toRichProcessBuilder(p).start(out, err, in)).onExit((Function1<Object, BoxedUnit>)new ProcessSupport$$anonfun$launch.ProcessSupport$$anonfun$launch$1((Function3)func, out, err));
    }
    
    public InputStream launch$default$2() {
        return null;
    }
    
    public Tuple3<Object, byte[], byte[]> system(final Seq<String> command) {
        return this.system(this.toProcessBuilder(command), this.system$default$2());
    }
    
    public Tuple3<Object, byte[], byte[]> system(final ProcessBuilder p, final InputStream in) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ByteArrayOutputStream err = new ByteArrayOutputStream();
        final Process process = this.toRichProcessBuilder(p).start(out, err, in);
        process.waitFor();
        return (Tuple3<Object, byte[], byte[]>)new Tuple3((Object)BoxesRunTime.boxToInteger(process.exitValue()), (Object)out.toByteArray(), (Object)err.toByteArray());
    }
    
    public InputStream system$default$2() {
        return null;
    }
    
    private ProcessSupport$() {
        MODULE$ = this;
    }
}
