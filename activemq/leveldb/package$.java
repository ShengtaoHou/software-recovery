// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb;

import scala.Function1;
import scala.runtime.BoxedUnit;
import scala.runtime.AbstractFunction1;
import scala.Serializable;
import scala.Predef$;
import scala.runtime.ObjectRef;

public final class package$
{
    public static final package$ MODULE$;
    private final package.SnappyTrait Snappy;
    
    static {
        new package$();
    }
    
    public final package.SnappyTrait Snappy() {
        return this.Snappy;
    }
    
    private package$() {
        MODULE$ = this;
        final ObjectRef attempt = ObjectRef.create((Object)null);
        Predef$.MODULE$.refArrayOps((Object[])System.getProperty("leveldb.snappy", "iq80,xerial").split(",")).foreach((Function1)new Serializable(attempt) {
            public static final long serialVersionUID = 0L;
            private final ObjectRef attempt$1;
            
            public final Object apply(final String x) {
                while (true) {
                    if (this.attempt$1.elem == null) {
                        BoxedUnit unit = null;
                        try {
                            String name = x.trim();
                            final String lowerCase = name.toLowerCase();
                            String s;
                            if ("xerial".equals(lowerCase)) {
                                s = "org.apache.activemq.leveldb.XerialSnappy";
                            }
                            else if ("iq80".equals(lowerCase)) {
                                s = "org.apache.activemq.leveldb.IQ80Snappy";
                            }
                            else {
                                s = name;
                            }
                            name = s;
                            this.attempt$1.elem = (SnappyTrait)Thread.currentThread().getContextClassLoader().loadClass(name).newInstance();
                            ((SnappyTrait)this.attempt$1.elem).compress("test");
                            return unit;
                            BoxedUnit.UNIT;
                        }
                        finally {
                            this.attempt$1.elem = null;
                            unit = BoxedUnit.UNIT;
                        }
                        return unit;
                    }
                    continue;
                }
            }
        });
        this.Snappy = (package.SnappyTrait)attempt.elem;
    }
}
