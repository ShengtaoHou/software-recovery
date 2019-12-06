// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.logwriters;

import java.io.IOException;
import org.slf4j.Logger;
import org.apache.activemq.transport.LogWriter;

public class DefaultLogWriter implements LogWriter
{
    @Override
    public void initialMessage(final Logger log) {
    }
    
    @Override
    public void logRequest(final Logger log, final Object command) {
        log.debug("SENDING REQUEST: " + command);
    }
    
    @Override
    public void logResponse(final Logger log, final Object response) {
        log.debug("GOT RESPONSE: " + response);
    }
    
    @Override
    public void logAsyncRequest(final Logger log, final Object command) {
        log.debug("SENDING ASNYC REQUEST: " + command);
    }
    
    @Override
    public void logOneWay(final Logger log, final Object command) {
        log.debug("SENDING: " + command);
    }
    
    @Override
    public void logReceivedCommand(final Logger log, final Object command) {
        log.debug("RECEIVED: " + command);
    }
    
    @Override
    public void logReceivedException(final Logger log, final IOException error) {
        log.debug("RECEIVED Exception: " + error, error);
    }
}
