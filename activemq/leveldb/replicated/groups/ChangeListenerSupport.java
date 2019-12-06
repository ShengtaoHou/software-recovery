// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated.groups;

import scala.Function0;
import scala.runtime.TraitSetter;
import scala.collection.immutable.List;
import scala.reflect.ScalaSignature;

@ScalaSignature(bytes = "\u0006\u00019<Q!\u0001\u0002\t\u0002=\tQc\u00115b]\u001e,G*[:uK:,'oU;qa>\u0014HO\u0003\u0002\u0004\t\u00051qM]8vaNT!!\u0002\u0004\u0002\u0015I,\u0007\u000f\\5dCR,GM\u0003\u0002\b\u0011\u00059A.\u001a<fY\u0012\u0014'BA\u0005\u000b\u0003!\t7\r^5wK6\f(BA\u0006\r\u0003\u0019\t\u0007/Y2iK*\tQ\"A\u0002pe\u001e\u001c\u0001\u0001\u0005\u0002\u0011#5\t!AB\u0003\u0013\u0005!\u00051CA\u000bDQ\u0006tw-\u001a'jgR,g.\u001a:TkB\u0004xN\u001d;\u0014\u0005E!\u0002CA\u000b\u0019\u001b\u00051\"\"A\f\u0002\u000bM\u001c\u0017\r\\1\n\u0005e1\"AB!osJ+g\rC\u0003\u001c#\u0011\u0005A$\u0001\u0004=S:LGO\u0010\u000b\u0002\u001f!9a$\u0005b\u0001\n\u0003y\u0012a\u0001'P\u000fV\t\u0001\u0005\u0005\u0002\"I5\t!E\u0003\u0002$\u0019\u0005)1\u000f\u001c45U&\u0011QE\t\u0002\u0007\u0019><w-\u001a:\t\r\u001d\n\u0002\u0015!\u0003!\u0003\u0011auj\u0012\u0011\u0007\u000fI\u0011\u0001\u0013aA\u0001SM\u0011\u0001\u0006\u0006\u0005\u0006W!\"\t\u0001L\u0001\u0007I%t\u0017\u000e\u001e\u0013\u0015\u00035\u0002\"!\u0006\u0018\n\u0005=2\"\u0001B+oSRDq!\r\u0015A\u0002\u0013\u0005!'A\u0005mSN$XM\\3sgV\t1\u0007E\u00025smj\u0011!\u000e\u0006\u0003m]\n\u0011\"[7nkR\f'\r\\3\u000b\u0005a2\u0012AC2pY2,7\r^5p]&\u0011!(\u000e\u0002\u0005\u0019&\u001cH\u000f\u0005\u0002\u0011y%\u0011QH\u0001\u0002\u000f\u0007\"\fgnZ3MSN$XM\\3s\u0011\u001dy\u0004\u00061A\u0005\u0002\u0001\u000bQ\u0002\\5ti\u0016tWM]:`I\u0015\fHCA\u0017B\u0011\u001d\u0011e(!AA\u0002M\n1\u0001\u001f\u00132\u0011\u0019!\u0005\u0006)Q\u0005g\u0005QA.[:uK:,'o\u001d\u0011\t\u000b\u0019Cc\u0011A$\u0002\u0013\r|gN\\3di\u0016$W#\u0001%\u0011\u0005UI\u0015B\u0001&\u0017\u0005\u001d\u0011un\u001c7fC:DQ\u0001\u0014\u0015\u0005\u00025\u000b1!\u00193e)\tic\nC\u0003P\u0017\u0002\u00071(\u0001\u0005mSN$XM\\3s\u0011\u0015\t\u0006\u0006\"\u0001S\u0003\u0019\u0011X-\\8wKR\u0011Qf\u0015\u0005\u0006\u001fB\u0003\ra\u000f\u0005\u0006+\"\"\t\u0001L\u0001\u000eM&\u0014XmQ8o]\u0016\u001cG/\u001a3\t\u000b]CC\u0011\u0001\u0017\u0002!\u0019L'/\u001a#jg\u000e|gN\\3di\u0016$\u0007\"B-)\t\u0003a\u0013a\u00034je\u0016\u001c\u0005.\u00198hK\u0012DQa\u0017\u0015\u0005\u0002q\u000b!c\u00195fG.|V\r\\1qg\u0016$w\f^5nKV\u0011Q\f\u0019\u000b\u0003=&\u0004\"a\u00181\r\u0001\u0011)\u0011M\u0017b\u0001E\n\tA+\u0005\u0002dMB\u0011Q\u0003Z\u0005\u0003KZ\u0011qAT8uQ&tw\r\u0005\u0002\u0016O&\u0011\u0001N\u0006\u0002\u0004\u0003:L\bB\u00026[\t\u0003\u00071.\u0001\u0003gk:\u001c\u0007cA\u000bm=&\u0011QN\u0006\u0002\ty\tLh.Y7f}\u0001")
public interface ChangeListenerSupport
{
    List<ChangeListener> listeners();
    
    @TraitSetter
    void listeners_$eq(final List<ChangeListener> p0);
    
    boolean connected();
    
    void add(final ChangeListener p0);
    
    void remove(final ChangeListener p0);
    
    void fireConnected();
    
    void fireDisconnected();
    
    void fireChanged();
    
     <T> T check_elapsed_time(final Function0<T> p0);
}
