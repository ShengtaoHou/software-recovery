// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.stomp;

import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Map;
import java.io.DataInput;
import org.apache.activemq.util.DataByteArrayInputStream;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import org.apache.activemq.util.ByteArrayOutputStream;
import org.apache.activemq.transport.tcp.TcpTransport;

public class StompCodec
{
    static final byte[] crlfcrlf;
    TcpTransport transport;
    ByteArrayOutputStream currentCommand;
    boolean processedHeaders;
    String action;
    HashMap<String, String> headers;
    int contentLength;
    int readLength;
    int previousByte;
    boolean awaitingCommandStart;
    String version;
    
    public StompCodec(final TcpTransport transport) {
        this.currentCommand = new ByteArrayOutputStream();
        this.processedHeaders = false;
        this.contentLength = -1;
        this.readLength = 0;
        this.previousByte = -1;
        this.awaitingCommandStart = true;
        this.version = "1.0";
        this.transport = transport;
    }
    
    public void parse(final ByteArrayInputStream input, final int readSize) throws Exception {
        int i = 0;
        while (i++ < readSize) {
            final int b = input.read();
            if (!this.processedHeaders && this.previousByte == 0 && b == 0) {
                continue;
            }
            if (!this.processedHeaders) {
                if (this.awaitingCommandStart && b == 10) {
                    continue;
                }
                this.awaitingCommandStart = false;
                this.currentCommand.write(b);
                if (b == 10 && (this.previousByte == 10 || this.currentCommand.endsWith(StompCodec.crlfcrlf))) {
                    final StompWireFormat wf = (StompWireFormat)this.transport.getWireFormat();
                    final DataByteArrayInputStream data = new DataByteArrayInputStream(this.currentCommand.toByteArray());
                    this.action = wf.parseAction(data);
                    this.headers = wf.parseHeaders(data);
                    try {
                        final String contentLengthHeader = this.headers.get("content-length");
                        if ((this.action.equals("SEND") || this.action.equals("MESSAGE")) && contentLengthHeader != null) {
                            this.contentLength = wf.parseContentLength(contentLengthHeader);
                        }
                        else {
                            this.contentLength = -1;
                        }
                    }
                    catch (ProtocolException ex) {}
                    this.processedHeaders = true;
                    this.currentCommand.reset();
                }
            }
            else if (this.contentLength == -1) {
                if (b == 0) {
                    this.processCommand();
                }
                else {
                    this.currentCommand.write(b);
                }
            }
            else if (this.readLength++ == this.contentLength) {
                this.processCommand();
                this.readLength = 0;
            }
            else {
                this.currentCommand.write(b);
            }
            this.previousByte = b;
        }
    }
    
    protected void processCommand() throws Exception {
        final StompFrame frame = new StompFrame(this.action, this.headers, this.currentCommand.toByteArray());
        this.transport.doConsume(frame);
        this.processedHeaders = false;
        this.awaitingCommandStart = true;
        this.currentCommand.reset();
        this.contentLength = -1;
    }
    
    public static String detectVersion(final Map<String, String> headers) throws ProtocolException {
        String accepts = headers.get("accept-version");
        if (accepts == null) {
            accepts = "1.0";
        }
        final HashSet<String> acceptsVersions = new HashSet<String>(Arrays.asList(accepts.trim().split(",")));
        acceptsVersions.retainAll(Arrays.asList(Stomp.SUPPORTED_PROTOCOL_VERSIONS));
        if (acceptsVersions.isEmpty()) {
            throw new ProtocolException("Invalid Protocol version[" + accepts + "], supported versions are: " + Arrays.toString(Stomp.SUPPORTED_PROTOCOL_VERSIONS), true);
        }
        return Collections.max((Collection<? extends String>)acceptsVersions);
    }
    
    static {
        crlfcrlf = new byte[] { 13, 10, 13, 10 };
    }
}
