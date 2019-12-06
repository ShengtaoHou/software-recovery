// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.stomp;

import java.io.PushbackInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.io.DataInput;
import java.io.InputStream;
import java.io.DataInputStream;
import org.apache.activemq.util.ByteArrayInputStream;
import java.io.IOException;
import java.io.DataOutput;
import java.io.OutputStream;
import java.io.DataOutputStream;
import org.apache.activemq.util.ByteArrayOutputStream;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.wireformat.WireFormat;

public class StompWireFormat implements WireFormat
{
    private static final byte[] NO_DATA;
    private static final byte[] END_OF_FRAME;
    private static final int MAX_COMMAND_LENGTH = 1024;
    private static final int MAX_HEADER_LENGTH = 10240;
    private static final int MAX_HEADERS = 1000;
    private static final int MAX_DATA_LENGTH = 104857600;
    private int version;
    private String stompVersion;
    
    public StompWireFormat() {
        this.version = 1;
        this.stompVersion = "1.0";
    }
    
    @Override
    public ByteSequence marshal(final Object command) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(baos);
        this.marshal(command, dos);
        dos.close();
        return baos.toByteSequence();
    }
    
    @Override
    public Object unmarshal(final ByteSequence packet) throws IOException {
        final ByteArrayInputStream stream = new ByteArrayInputStream(packet);
        final DataInputStream dis = new DataInputStream(stream);
        return this.unmarshal(dis);
    }
    
    @Override
    public void marshal(final Object command, final DataOutput os) throws IOException {
        final StompFrame stomp = (StompFrame)command;
        if (stomp.getAction().equals("KEEPALIVE")) {
            os.write(10);
            return;
        }
        final StringBuilder buffer = new StringBuilder();
        buffer.append(stomp.getAction());
        buffer.append("\n");
        for (final Map.Entry<String, String> entry : stomp.getHeaders().entrySet()) {
            buffer.append(entry.getKey());
            buffer.append(":");
            buffer.append(this.encodeHeader(entry.getValue()));
            buffer.append("\n");
        }
        buffer.append("\n");
        os.write(buffer.toString().getBytes("UTF-8"));
        os.write(stomp.getContent());
        os.write(StompWireFormat.END_OF_FRAME);
    }
    
    @Override
    public Object unmarshal(final DataInput in) throws IOException {
        try {
            final String action = this.parseAction(in);
            final HashMap<String, String> headers = this.parseHeaders(in);
            byte[] data = StompWireFormat.NO_DATA;
            final String contentLength = headers.get("content-length");
            if ((action.equals("SEND") || action.equals("MESSAGE")) && contentLength != null) {
                final int length = this.parseContentLength(contentLength);
                data = new byte[length];
                in.readFully(data);
                if (in.readByte() != 0) {
                    throw new ProtocolException("content-length bytes were read and there was no trailing null byte", true);
                }
            }
            else {
                ByteArrayOutputStream baos = null;
                byte b;
                while ((b = in.readByte()) != 0) {
                    if (baos == null) {
                        baos = new ByteArrayOutputStream();
                    }
                    else if (baos.size() > 104857600) {
                        throw new ProtocolException("The maximum data length was exceeded", true);
                    }
                    baos.write(b);
                }
                if (baos != null) {
                    baos.close();
                    data = baos.toByteArray();
                }
            }
            return new StompFrame(action, headers, data);
        }
        catch (ProtocolException e) {
            return new StompFrameError(e);
        }
    }
    
    private String readLine(final DataInput in, final int maxLength, final String errorMessage) throws IOException {
        final ByteSequence sequence = this.readHeaderLine(in, maxLength, errorMessage);
        return new String(sequence.getData(), sequence.getOffset(), sequence.getLength(), "UTF-8").trim();
    }
    
    private ByteSequence readHeaderLine(final DataInput in, final int maxLength, final String errorMessage) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(maxLength);
        byte b;
        while ((b = in.readByte()) != 10) {
            if (baos.size() > maxLength) {
                baos.close();
                throw new ProtocolException(errorMessage, true);
            }
            baos.write(b);
        }
        baos.close();
        final ByteSequence line = baos.toByteSequence();
        if (this.stompVersion.equals("1.0") || this.stompVersion.equals("1.2")) {
            final int lineLength = line.getLength();
            if (lineLength > 0 && line.data[lineLength - 1] == 13) {
                line.setLength(lineLength - 1);
            }
        }
        return line;
    }
    
    protected String parseAction(final DataInput in) throws IOException {
        String action = null;
        do {
            action = this.readLine(in, 1024, "The maximum command length was exceeded");
            if (action == null) {
                throw new IOException("connection was closed");
            }
            action = action.trim();
        } while (action.length() <= 0);
        return action;
    }
    
    protected HashMap<String, String> parseHeaders(final DataInput in) throws IOException {
        final HashMap<String, String> headers = new HashMap<String, String>(25);
        while (true) {
            final ByteSequence line = this.readHeaderLine(in, 10240, "The maximum header length was exceeded");
            if (line == null || line.length <= 1) {
                return headers;
            }
            if (headers.size() > 1000) {
                throw new ProtocolException("The maximum number of headers was exceeded", true);
            }
            try {
                final ByteArrayInputStream headerLine = new ByteArrayInputStream(line);
                final ByteArrayOutputStream stream = new ByteArrayOutputStream(line.length);
                int result = -1;
                while ((result = headerLine.read()) != -1 && result != 58) {
                    stream.write(result);
                }
                final ByteSequence nameSeq = stream.toByteSequence();
                final String name = new String(nameSeq.getData(), nameSeq.getOffset(), nameSeq.getLength(), "UTF-8");
                String value = this.decodeHeader(headerLine);
                if (this.stompVersion.equals("1.0")) {
                    value = value.trim();
                }
                if (!headers.containsKey(name)) {
                    headers.put(name, value);
                }
                stream.close();
            }
            catch (Exception e) {
                throw new ProtocolException("Unable to parser header line [" + line + "]", true);
            }
        }
    }
    
    protected int parseContentLength(final String contentLength) throws ProtocolException {
        int length;
        try {
            length = Integer.parseInt(contentLength.trim());
        }
        catch (NumberFormatException e) {
            throw new ProtocolException("Specified content-length is not a valid integer", true);
        }
        if (length > 104857600) {
            throw new ProtocolException("The maximum data length was exceeded", true);
        }
        return length;
    }
    
    private String encodeHeader(final String header) throws IOException {
        String result = header;
        if (!this.stompVersion.equals("1.0")) {
            final byte[] utf8buf = header.getBytes("UTF-8");
            final ByteArrayOutputStream stream = new ByteArrayOutputStream(utf8buf.length);
            for (final byte val : utf8buf) {
                switch (val) {
                    case 92: {
                        stream.write(Stomp.ESCAPE_ESCAPE_SEQ);
                        break;
                    }
                    case 10: {
                        stream.write(Stomp.NEWLINE_ESCAPE_SEQ);
                        break;
                    }
                    case 58: {
                        stream.write(Stomp.COLON_ESCAPE_SEQ);
                        break;
                    }
                    default: {
                        stream.write(val);
                        break;
                    }
                }
            }
            result = new String(stream.toByteArray(), "UTF-8");
        }
        return result;
    }
    
    private String decodeHeader(final InputStream header) throws IOException {
        final ByteArrayOutputStream decoded = new ByteArrayOutputStream();
        final PushbackInputStream stream = new PushbackInputStream(header);
        int value = -1;
        while ((value = stream.read()) != -1) {
            if (value == 92) {
                final int next = stream.read();
                if (next != -1) {
                    switch (next) {
                        case 110: {
                            decoded.write(10);
                            continue;
                        }
                        case 99: {
                            decoded.write(58);
                            continue;
                        }
                        case 92: {
                            decoded.write(92);
                            continue;
                        }
                        default: {
                            stream.unread(next);
                            decoded.write(value);
                            continue;
                        }
                    }
                }
                else {
                    decoded.write(value);
                }
            }
            else {
                decoded.write(value);
            }
        }
        return new String(decoded.toByteArray(), "UTF-8");
    }
    
    @Override
    public int getVersion() {
        return this.version;
    }
    
    @Override
    public void setVersion(final int version) {
        this.version = version;
    }
    
    public String getStompVersion() {
        return this.stompVersion;
    }
    
    public void setStompVersion(final String stompVersion) {
        this.stompVersion = stompVersion;
    }
    
    static {
        NO_DATA = new byte[0];
        END_OF_FRAME = new byte[] { 0, 10 };
    }
}
