// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.stomp;

import java.util.Iterator;
import java.util.Arrays;
import org.apache.activemq.util.MarshallingSupport;
import java.util.Locale;
import org.apache.activemq.command.Response;
import org.apache.activemq.state.CommandVisitor;
import org.apache.activemq.command.Endpoint;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import org.apache.activemq.command.Command;

public class StompFrame implements Command
{
    public static final byte[] NO_DATA;
    private String action;
    private Map<String, String> headers;
    private byte[] content;
    private transient Object transportContext;
    
    public StompFrame(final String command) {
        this(command, null, null);
    }
    
    public StompFrame(final String command, final Map<String, String> headers) {
        this(command, headers, null);
    }
    
    public StompFrame(final String command, final Map<String, String> headers, final byte[] data) {
        this.headers = new HashMap<String, String>();
        this.content = StompFrame.NO_DATA;
        this.transportContext = null;
        this.action = command;
        if (headers != null) {
            this.headers = headers;
        }
        if (data != null) {
            this.content = data;
        }
    }
    
    public StompFrame() {
        this.headers = new HashMap<String, String>();
        this.content = StompFrame.NO_DATA;
        this.transportContext = null;
    }
    
    public String getAction() {
        return this.action;
    }
    
    public void setAction(final String command) {
        this.action = command;
    }
    
    public byte[] getContent() {
        return this.content;
    }
    
    public String getBody() {
        try {
            return new String(this.content, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            return new String(this.content);
        }
    }
    
    public void setContent(final byte[] data) {
        this.content = data;
    }
    
    public Map<String, String> getHeaders() {
        return this.headers;
    }
    
    public void setHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }
    
    @Override
    public int getCommandId() {
        return 0;
    }
    
    @Override
    public Endpoint getFrom() {
        return null;
    }
    
    @Override
    public Endpoint getTo() {
        return null;
    }
    
    @Override
    public boolean isBrokerInfo() {
        return false;
    }
    
    @Override
    public boolean isMessage() {
        return false;
    }
    
    @Override
    public boolean isMessageAck() {
        return false;
    }
    
    @Override
    public boolean isMessageDispatch() {
        return false;
    }
    
    @Override
    public boolean isMessageDispatchNotification() {
        return false;
    }
    
    @Override
    public boolean isResponse() {
        return false;
    }
    
    @Override
    public boolean isResponseRequired() {
        return false;
    }
    
    @Override
    public boolean isShutdownInfo() {
        return false;
    }
    
    @Override
    public boolean isConnectionControl() {
        return false;
    }
    
    @Override
    public boolean isWireFormatInfo() {
        return false;
    }
    
    @Override
    public void setCommandId(final int value) {
    }
    
    @Override
    public void setFrom(final Endpoint from) {
    }
    
    @Override
    public void setResponseRequired(final boolean responseRequired) {
    }
    
    @Override
    public void setTo(final Endpoint to) {
    }
    
    @Override
    public Response visit(final CommandVisitor visitor) throws Exception {
        return null;
    }
    
    @Override
    public byte getDataStructureType() {
        return 0;
    }
    
    @Override
    public boolean isMarshallAware() {
        return false;
    }
    
    @Override
    public String toString() {
        return this.format(true);
    }
    
    public String format() {
        return this.format(false);
    }
    
    public String format(final boolean forLogging) {
        if (!forLogging && this.getAction().equals("KEEPALIVE")) {
            return "\n";
        }
        final StringBuilder buffer = new StringBuilder();
        buffer.append(this.getAction());
        buffer.append("\n");
        final Map<String, String> headers = this.getHeaders();
        for (final Map.Entry<String, String> entry : headers.entrySet()) {
            buffer.append(entry.getKey());
            buffer.append(":");
            if (forLogging && entry.getKey().toString().toLowerCase(Locale.ENGLISH).contains("passcode")) {
                buffer.append("*****");
            }
            else {
                buffer.append(entry.getValue());
            }
            buffer.append("\n");
        }
        buffer.append("\n");
        if (this.getContent() != null) {
            try {
                String contentString = new String(this.getContent(), "UTF-8");
                if (forLogging) {
                    contentString = MarshallingSupport.truncate64(contentString);
                }
                buffer.append(contentString);
            }
            catch (Throwable e) {
                buffer.append(Arrays.toString(this.getContent()));
            }
        }
        buffer.append('\0');
        return buffer.toString();
    }
    
    public Object getTransportContext() {
        return this.transportContext;
    }
    
    public void setTransportContext(final Object transportContext) {
        this.transportContext = transportContext;
    }
    
    static {
        NO_DATA = new byte[0];
    }
}
