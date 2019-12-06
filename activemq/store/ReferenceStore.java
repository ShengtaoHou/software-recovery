// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store;

import java.util.concurrent.locks.Lock;
import java.io.IOException;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.broker.ConnectionContext;

public interface ReferenceStore extends MessageStore
{
    boolean addMessageReference(final ConnectionContext p0, final MessageId p1, final ReferenceData p2) throws IOException;
    
    ReferenceData getMessageReference(final MessageId p0) throws IOException;
    
    boolean supportsExternalBatchControl();
    
    void setBatch(final MessageId p0);
    
    Lock getStoreLock();
    
    public static class ReferenceData
    {
        long expiration;
        int fileId;
        int offset;
        
        public long getExpiration() {
            return this.expiration;
        }
        
        public void setExpiration(final long expiration) {
            this.expiration = expiration;
        }
        
        public int getFileId() {
            return this.fileId;
        }
        
        public void setFileId(final int file) {
            this.fileId = file;
        }
        
        public int getOffset() {
            return this.offset;
        }
        
        public void setOffset(final int offset) {
            this.offset = offset;
        }
        
        @Override
        public String toString() {
            return "ReferenceData fileId=" + this.fileId + ", offset=" + this.offset + ", expiration=" + this.expiration;
        }
    }
}
