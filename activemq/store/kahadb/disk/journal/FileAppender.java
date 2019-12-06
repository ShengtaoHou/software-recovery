// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.journal;

import java.io.IOException;
import org.apache.activemq.util.ByteSequence;

public interface FileAppender
{
    public static final String PROPERTY_LOG_WRITE_STAT_WINDOW = "org.apache.kahadb.journal.appender.WRITE_STAT_WINDOW";
    public static final int maxStat = Integer.parseInt(System.getProperty("org.apache.kahadb.journal.appender.WRITE_STAT_WINDOW", "0"));
    
    Location storeItem(final ByteSequence p0, final byte p1, final boolean p2) throws IOException;
    
    Location storeItem(final ByteSequence p0, final byte p1, final Runnable p2) throws IOException;
    
    void close() throws IOException;
}
