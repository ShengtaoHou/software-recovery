// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import javax.jms.ExceptionListener;

public interface AsyncCallback extends ExceptionListener
{
    void onSuccess();
}
