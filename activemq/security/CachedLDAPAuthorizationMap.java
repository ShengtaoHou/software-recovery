// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.security;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class CachedLDAPAuthorizationMap extends SimpleCachedLDAPAuthorizationMap implements InitializingBean, DisposableBean
{
    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
    }
    
    @Override
    public void destroy() throws Exception {
        super.destroy();
    }
}
