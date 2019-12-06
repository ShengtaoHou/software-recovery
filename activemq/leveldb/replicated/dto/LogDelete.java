// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated.dto;

import javax.xml.bind.annotation.XmlAttribute;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "remove_request")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogDelete
{
    @XmlAttribute(name = "log")
    public long log;
}
