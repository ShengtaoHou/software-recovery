// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.logwriters;

import org.apache.activemq.command.ProducerId;
import org.apache.activemq.command.WireFormatInfo;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.command.ProducerAck;
import org.apache.activemq.command.MessageDispatch;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.BaseCommand;
import java.io.IOException;
import org.slf4j.Logger;
import org.apache.activemq.transport.LogWriter;

public class CustomLogWriter implements LogWriter
{
    @Override
    public void initialMessage(final Logger log) {
    }
    
    @Override
    public void logRequest(final Logger log, final Object command) {
        log.debug("$$ SENDREQ: " + commandToString(command));
    }
    
    @Override
    public void logResponse(final Logger log, final Object response) {
        log.debug("$$ GOT_RESPONSE: " + response);
    }
    
    @Override
    public void logAsyncRequest(final Logger log, final Object command) {
        log.debug("$$ SENDING_ASNYC_REQUEST: " + command);
    }
    
    @Override
    public void logOneWay(final Logger log, final Object command) {
        log.debug("$$ SENDING: " + commandToString(command));
    }
    
    @Override
    public void logReceivedCommand(final Logger log, final Object command) {
        log.debug("$$ RECEIVED: " + commandToString(command));
    }
    
    @Override
    public void logReceivedException(final Logger log, final IOException error) {
        log.debug("$$ RECEIVED_EXCEPTION: " + error, error);
    }
    
    private static String commandToString(final Object command) {
        final StringBuilder sb = new StringBuilder();
        if (command instanceof BaseCommand) {
            final BaseCommand bc = (BaseCommand)command;
            sb.append(command.getClass().getSimpleName());
            sb.append(' ');
            sb.append(bc.isResponseRequired() ? 'T' : 'F');
            Message m = null;
            if (bc instanceof Message) {
                m = (Message)bc;
            }
            if (bc instanceof MessageDispatch) {
                m = ((MessageDispatch)bc).getMessage();
            }
            if (m != null) {
                sb.append(' ');
                sb.append(m.getMessageId());
                sb.append(',');
                sb.append(m.getCommandId());
                final ProducerId pid = m.getProducerId();
                final long sid = pid.getSessionId();
                sb.append(',');
                sb.append(pid.getConnectionId());
                sb.append(',');
                sb.append(sid);
                sb.append(',');
                sb.append(pid.getValue());
                sb.append(',');
                sb.append(m.getCorrelationId());
                sb.append(',');
                sb.append(m.getType());
            }
            if (bc instanceof MessageDispatch) {
                sb.append(" toConsumer:");
                sb.append(((MessageDispatch)bc).getConsumerId());
            }
            if (bc instanceof ProducerAck) {
                sb.append(" ProducerId:");
                sb.append(((ProducerAck)bc).getProducerId());
            }
            if (bc instanceof MessageAck) {
                final MessageAck ma = (MessageAck)bc;
                sb.append(" ConsumerID:");
                sb.append(ma.getConsumerId());
                sb.append(" ack:");
                sb.append(ma.getFirstMessageId());
                sb.append('-');
                sb.append(ma.getLastMessageId());
            }
            if (bc instanceof ConnectionInfo) {
                final ConnectionInfo ci = (ConnectionInfo)bc;
                sb.append(' ');
                sb.append(ci.getConnectionId());
            }
        }
        else if (command instanceof WireFormatInfo) {
            sb.append("WireFormatInfo");
        }
        else {
            sb.append("Unrecognized_object ");
            sb.append(command.toString());
        }
        return sb.toString();
    }
}
