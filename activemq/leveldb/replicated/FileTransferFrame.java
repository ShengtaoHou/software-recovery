// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated;

import java.io.File;
import scala.reflect.ScalaSignature;

@ScalaSignature(bytes = "\u0006\u0001u2A!\u0001\u0002\u0001\u001b\t\tb)\u001b7f)J\fgn\u001d4fe\u001a\u0013\u0018-\\3\u000b\u0005\r!\u0011A\u0003:fa2L7-\u0019;fI*\u0011QAB\u0001\bY\u00164X\r\u001c3c\u0015\t9\u0001\"\u0001\u0005bGRLg/Z7r\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0004\u0001M\u0011\u0001A\u0004\t\u0003\u001fIi\u0011\u0001\u0005\u0006\u0002#\u0005)1oY1mC&\u00111\u0003\u0005\u0002\u0007\u0003:L(+\u001a4\t\u0011U\u0001!Q1A\u0005\u0002Y\tAAZ5mKV\tq\u0003\u0005\u0002\u0019;5\t\u0011D\u0003\u0002\u001b7\u0005\u0011\u0011n\u001c\u0006\u00029\u0005!!.\u0019<b\u0013\tq\u0012D\u0001\u0003GS2,\u0007\u0002\u0003\u0011\u0001\u0005\u0003\u0005\u000b\u0011B\f\u0002\u000b\u0019LG.\u001a\u0011\t\u0011\t\u0002!Q1A\u0005\u0002\r\naa\u001c4gg\u0016$X#\u0001\u0013\u0011\u0005=)\u0013B\u0001\u0014\u0011\u0005\u0011auN\\4\t\u0011!\u0002!\u0011!Q\u0001\n\u0011\nqa\u001c4gg\u0016$\b\u0005\u0003\u0005+\u0001\t\u0005\r\u0011\"\u0001$\u0003\u0019aWM\\4uQ\"AA\u0006\u0001BA\u0002\u0013\u0005Q&\u0001\u0006mK:<G\u000f[0%KF$\"AL\u0019\u0011\u0005=y\u0013B\u0001\u0019\u0011\u0005\u0011)f.\u001b;\t\u000fIZ\u0013\u0011!a\u0001I\u0005\u0019\u0001\u0010J\u0019\t\u0011Q\u0002!\u0011!Q!\n\u0011\nq\u0001\\3oORD\u0007\u0005C\u00037\u0001\u0011\u0005q'\u0001\u0004=S:LGO\u0010\u000b\u0005qiZD\b\u0005\u0002:\u00015\t!\u0001C\u0003\u0016k\u0001\u0007q\u0003C\u0003#k\u0001\u0007A\u0005C\u0003+k\u0001\u0007A\u0005")
public class FileTransferFrame
{
    private final File file;
    private final long offset;
    private long length;
    
    public File file() {
        return this.file;
    }
    
    public long offset() {
        return this.offset;
    }
    
    public long length() {
        return this.length;
    }
    
    public void length_$eq(final long x$1) {
        this.length = x$1;
    }
    
    public FileTransferFrame(final File file, final long offset, final long length) {
        this.file = file;
        this.offset = offset;
        this.length = length;
    }
}
