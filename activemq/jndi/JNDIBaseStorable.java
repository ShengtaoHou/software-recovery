// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.jndi;

import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;
import javax.naming.NamingException;
import javax.naming.Reference;
import java.util.Properties;
import java.io.Externalizable;

public abstract class JNDIBaseStorable implements JNDIStorableInterface, Externalizable
{
    private Properties properties;
    
    protected abstract void buildFromProperties(final Properties p0);
    
    protected abstract void populateProperties(final Properties p0);
    
    @Override
    public synchronized void setProperties(final Properties props) {
        this.buildFromProperties(this.properties = props);
    }
    
    @Override
    public synchronized Properties getProperties() {
        if (this.properties == null) {
            this.properties = new Properties();
        }
        this.populateProperties(this.properties);
        return this.properties;
    }
    
    @Override
    public Reference getReference() throws NamingException {
        return JNDIReferenceFactory.createReference(this.getClass().getName(), this);
    }
    
    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        final Properties props = (Properties)in.readObject();
        if (props != null) {
            this.setProperties(props);
        }
    }
    
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeObject(this.getProperties());
    }
}
