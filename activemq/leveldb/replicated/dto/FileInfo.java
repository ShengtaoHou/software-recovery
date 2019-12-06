// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated.dto;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "file_info")
@XmlAccessorType(XmlAccessType.FIELD)
public class FileInfo
{
    @XmlAttribute(name = "file")
    public String file;
    @XmlAttribute(name = "length")
    public long length;
    @XmlAttribute(name = "crc32")
    public long crc32;
}
