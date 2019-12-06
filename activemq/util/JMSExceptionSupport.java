// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import javax.jms.MessageFormatException;
import javax.jms.MessageEOFException;
import javax.jms.JMSSecurityException;
import javax.jms.JMSException;

public final class JMSExceptionSupport
{
    private JMSExceptionSupport() {
    }
    
    public static JMSException create(final String msg, final Throwable cause) {
        final JMSException exception = new JMSException(msg);
        exception.initCause(cause);
        return exception;
    }
    
    public static JMSException create(final String msg, final Exception cause) {
        final JMSException exception = new JMSException(msg);
        exception.setLinkedException(cause);
        exception.initCause(cause);
        return exception;
    }
    
    public static JMSException create(final Throwable cause) {
        if (cause instanceof JMSException) {
            return (JMSException)cause;
        }
        String msg = cause.getMessage();
        if (msg == null || msg.length() == 0) {
            msg = cause.toString();
        }
        JMSException exception;
        if (cause instanceof SecurityException) {
            exception = new JMSSecurityException(msg);
        }
        else {
            exception = new JMSException(msg);
        }
        exception.initCause(cause);
        return exception;
    }
    
    public static JMSException create(final Exception cause) {
        if (cause instanceof JMSException) {
            return (JMSException)cause;
        }
        String msg = cause.getMessage();
        if (msg == null || msg.length() == 0) {
            msg = cause.toString();
        }
        JMSException exception;
        if (cause instanceof SecurityException) {
            exception = new JMSSecurityException(msg);
        }
        else {
            exception = new JMSException(msg);
        }
        exception.setLinkedException(cause);
        exception.initCause(cause);
        return exception;
    }
    
    public static MessageEOFException createMessageEOFException(final Exception cause) {
        String msg = cause.getMessage();
        if (msg == null || msg.length() == 0) {
            msg = cause.toString();
        }
        final MessageEOFException exception = new MessageEOFException(msg);
        exception.setLinkedException(cause);
        exception.initCause(cause);
        return exception;
    }
    
    public static MessageFormatException createMessageFormatException(final Exception cause) {
        String msg = cause.getMessage();
        if (msg == null || msg.length() == 0) {
            msg = cause.toString();
        }
        final MessageFormatException exception = new MessageFormatException(msg);
        exception.setLinkedException(cause);
        exception.initCause(cause);
        return exception;
    }
}
