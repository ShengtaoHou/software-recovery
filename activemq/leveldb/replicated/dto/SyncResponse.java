// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated.dto;

import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "sync_response")
@XmlAccessorType(XmlAccessType.FIELD)
public class SyncResponse
{
    @XmlAttribute(name = "snapshot_position")
    public long snapshot_position;
    @XmlAttribute(name = "wal_append_position")
    public long wal_append_position;
    @XmlAttribute(name = "index_files")
    public Set<FileInfo> index_files;
    @XmlAttribute(name = "log_files")
    public Set<FileInfo> log_files;
    @XmlAttribute(name = "append_log")
    public String append_log;
    
    public SyncResponse() {
        this.index_files = new HashSet<FileInfo>();
        this.log_files = new HashSet<FileInfo>();
    }
}
