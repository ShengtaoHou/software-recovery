// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.AsciiBuffer;
import scala.reflect.ScalaSignature;

@ScalaSignature(bytes = "\u0006\u0001A2A!\u0001\u0002\u0001\u001b\t\u0001\"+\u001a9mS\u000e\fG/[8o\rJ\fW.\u001a\u0006\u0003\u0007\u0011\t!B]3qY&\u001c\u0017\r^3e\u0015\t)a!A\u0004mKZ,G\u000e\u001a2\u000b\u0005\u001dA\u0011\u0001C1di&4X-\\9\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\u0002\u0001'\t\u0001a\u0002\u0005\u0002\u0010%5\t\u0001CC\u0001\u0012\u0003\u0015\u00198-\u00197b\u0013\t\u0019\u0002C\u0001\u0004B]f\u0014VM\u001a\u0005\t+\u0001\u0011)\u0019!C\u0001-\u00051\u0011m\u0019;j_:,\u0012a\u0006\t\u00031ui\u0011!\u0007\u0006\u00035m\tq\u0001[1xi\n,hM\u0003\u0002\u001d\u0015\u0005Qa-^:fg>,(oY3\n\u0005yI\"aC!tG&L')\u001e4gKJD\u0001\u0002\t\u0001\u0003\u0002\u0003\u0006IaF\u0001\bC\u000e$\u0018n\u001c8!\u0011!\u0011\u0003A!A!\u0002\u0013\u0019\u0013!B0c_\u0012L\bC\u0001\r%\u0013\t)\u0013D\u0001\u0004Ck\u001a4WM\u001d\u0005\u0006O\u0001!\t\u0001K\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0007%ZC\u0006\u0005\u0002+\u00015\t!\u0001C\u0003\u0016M\u0001\u0007q\u0003C\u0003#M\u0001\u00071\u0005C\u0003/\u0001\u0011\u0005q&\u0001\u0003c_\u0012LX#A\u0012")
public class ReplicationFrame
{
    private final AsciiBuffer action;
    private final Buffer _body;
    
    public AsciiBuffer action() {
        return this.action;
    }
    
    public Buffer body() {
        return this._body;
    }
    
    public ReplicationFrame(final AsciiBuffer action, final Buffer _body) {
        this.action = action;
        this._body = _body;
    }
}
