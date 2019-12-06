// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.view;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.FileWriter;
import org.slf4j.LoggerFactory;
import org.apache.activemq.broker.Broker;
import org.slf4j.Logger;
import org.apache.activemq.broker.BrokerFilter;

public abstract class DotFileInterceptorSupport extends BrokerFilter
{
    private final Logger log;
    private String file;
    
    public DotFileInterceptorSupport(final Broker next, final String file) {
        super(next);
        this.log = LoggerFactory.getLogger(DotFileInterceptorSupport.class);
        this.file = file;
    }
    
    protected void generateFile() throws Exception {
        this.log.debug("Creating DOT file at: {}", this.file);
        final PrintWriter writer = new PrintWriter(new FileWriter(this.file));
        try {
            this.generateFile(writer);
        }
        finally {
            writer.close();
        }
    }
    
    protected abstract void generateFile(final PrintWriter p0) throws Exception;
}
