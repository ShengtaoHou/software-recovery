// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated.dto;

import javax.xml.bind.annotation.XmlAttribute;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "transfer_request")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transfer
{
    @XmlAttribute(name = "file")
    public String file;
    @XmlAttribute(name = "offset")
    public long offset;
    @XmlAttribute(name = "length")
    public long length;
}
