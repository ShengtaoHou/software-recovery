// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated.groups;

import scala.reflect.ScalaSignature;

@ScalaSignature(bytes = "\u0006\u0001\u00012q!\u0001\u0002\u0011\u0002G\u0005qB\u0001\bDQ\u0006tw-\u001a'jgR,g.\u001a:\u000b\u0005\r!\u0011AB4s_V\u00048O\u0003\u0002\u0006\r\u0005Q!/\u001a9mS\u000e\fG/\u001a3\u000b\u0005\u001dA\u0011a\u00027fm\u0016dGM\u0019\u0006\u0003\u0013)\t\u0001\"Y2uSZ,W.\u001d\u0006\u0003\u00171\ta!\u00199bG\",'\"A\u0007\u0002\u0007=\u0014xm\u0001\u0001\u0014\u0005\u0001\u0001\u0002CA\t\u0015\u001b\u0005\u0011\"\"A\n\u0002\u000bM\u001c\u0017\r\\1\n\u0005U\u0011\"AB!osJ+g\rC\u0003\u0018\u0001\u0019\u0005\u0001$A\u0004dQ\u0006tw-\u001a3\u0016\u0003e\u0001\"!\u0005\u000e\n\u0005m\u0011\"\u0001B+oSRDQ!\b\u0001\u0007\u0002a\t\u0011bY8o]\u0016\u001cG/\u001a3\t\u000b}\u0001a\u0011\u0001\r\u0002\u0019\u0011L7oY8o]\u0016\u001cG/\u001a3")
public interface ChangeListener
{
    void changed();
    
    void connected();
    
    void disconnected();
}
