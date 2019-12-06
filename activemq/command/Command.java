// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import org.apache.activemq.state.CommandVisitor;

public interface Command extends DataStructure
{
    void setCommandId(final int p0);
    
    int getCommandId();
    
    void setResponseRequired(final boolean p0);
    
    boolean isResponseRequired();
    
    boolean isResponse();
    
    boolean isMessageDispatch();
    
    boolean isBrokerInfo();
    
    boolean isWireFormatInfo();
    
    boolean isMessage();
    
    boolean isMessageAck();
    
    boolean isMessageDispatchNotification();
    
    boolean isShutdownInfo();
    
    boolean isConnectionControl();
    
    Response visit(final CommandVisitor p0) throws Exception;
    
    Endpoint getFrom();
    
    void setFrom(final Endpoint p0);
    
    Endpoint getTo();
    
    void setTo(final Endpoint p0);
}
