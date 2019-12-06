// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated.dto;

import javax.xml.bind.annotation.XmlAttribute;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "log_write")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogWrite
{
    @XmlAttribute(name = "file")
    public long file;
    @XmlAttribute(name = "offset")
    public long offset;
    @XmlAttribute(name = "length")
    public long length;
    @XmlAttribute(name = "sync")
    public boolean sync;
    @XmlAttribute(name = "date")
    public long date;
    
    public LogWrite() {
        this.sync = false;
    }
}
