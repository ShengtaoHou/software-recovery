// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store;

import java.io.File;
import org.apache.activemq.Service;

public interface PListStore extends Service
{
    File getDirectory();
    
    void setDirectory(final File p0);
    
    PList getPList(final String p0) throws Exception;
    
    boolean removePList(final String p0) throws Exception;
    
    long size();
}
