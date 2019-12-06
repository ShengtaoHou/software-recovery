// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated.dto;

import javax.xml.bind.annotation.XmlAttribute;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "login")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Login
{
    @XmlAttribute(name = "node_id")
    public String node_id;
    @XmlAttribute(name = "security_token")
    public String security_token;
}
