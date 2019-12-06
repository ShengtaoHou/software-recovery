// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.security;

import java.util.Iterator;
import javax.annotation.PostConstruct;
import org.apache.activemq.filter.DestinationMapEntry;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;

public class XBeanAuthorizationMap extends DefaultAuthorizationMap implements InitializingBean
{
    protected List<DestinationMapEntry> authorizationEntries;
    
    @PostConstruct
    private void postConstruct() {
        try {
            this.afterPropertiesSet();
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void afterPropertiesSet() throws Exception {
        for (final DestinationMapEntry entry : this.authorizationEntries) {
            if (((XBeanAuthorizationEntry)entry).getGroupClass() == null) {
                ((XBeanAuthorizationEntry)entry).setGroupClass(this.groupClass);
            }
            ((XBeanAuthorizationEntry)entry).afterPropertiesSet();
        }
        super.setEntries(this.authorizationEntries);
    }
    
    @Override
    public void setAuthorizationEntries(final List<DestinationMapEntry> entries) {
        this.authorizationEntries = entries;
    }
}
