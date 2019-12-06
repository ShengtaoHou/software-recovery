// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.jndi;

import java.util.Properties;
import javax.naming.Referenceable;

public interface JNDIStorableInterface extends Referenceable
{
    void setProperties(final Properties p0);
    
    Properties getProperties();
}
