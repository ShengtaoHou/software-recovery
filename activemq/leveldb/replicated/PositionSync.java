// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated;

import scala.reflect.ScalaSignature;
import java.util.concurrent.CountDownLatch;

@ScalaSignature(bytes = "\u0006\u0001=2A!\u0001\u0002\u0001\u001b\ta\u0001k\\:ji&|gnU=oG*\u00111\u0001B\u0001\u000be\u0016\u0004H.[2bi\u0016$'BA\u0003\u0007\u0003\u001daWM^3mI\nT!a\u0002\u0005\u0002\u0011\u0005\u001cG/\u001b<f[FT!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sO\u000e\u00011C\u0001\u0001\u000f!\tya#D\u0001\u0011\u0015\t\t\"#\u0001\u0006d_:\u001cWO\u001d:f]RT!a\u0005\u000b\u0002\tU$\u0018\u000e\u001c\u0006\u0002+\u0005!!.\u0019<b\u0013\t9\u0002C\u0001\bD_VtG\u000fR8x]2\u000bGo\u00195\t\u0011e\u0001!Q1A\u0005\u0002i\t\u0001\u0002]8tSRLwN\\\u000b\u00027A\u0011AdH\u0007\u0002;)\ta$A\u0003tG\u0006d\u0017-\u0003\u0002!;\t!Aj\u001c8h\u0011!\u0011\u0003A!A!\u0002\u0013Y\u0012!\u00039pg&$\u0018n\u001c8!\u0011!!\u0003A!A!\u0002\u0013)\u0013!B2pk:$\bC\u0001\u000f'\u0013\t9SDA\u0002J]RDQ!\u000b\u0001\u0005\u0002)\na\u0001P5oSRtDcA\u0016.]A\u0011A\u0006A\u0007\u0002\u0005!)\u0011\u0004\u000ba\u00017!)A\u0005\u000ba\u0001K\u0001")
public class PositionSync extends CountDownLatch
{
    private final long position;
    
    public long position() {
        return this.position;
    }
    
    public PositionSync(final long position, final int count) {
        this.position = position;
        super(count);
    }
}
