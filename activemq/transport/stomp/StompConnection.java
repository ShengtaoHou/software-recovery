// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.stomp;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.net.Socket;

public class StompConnection
{
    public static final long RECEIVE_TIMEOUT = 10000L;
    private Socket stompSocket;
    private ByteArrayOutputStream inputBuffer;
    private String version;
    
    public StompConnection() {
        this.inputBuffer = new ByteArrayOutputStream();
        this.version = "1.0";
    }
    
    public void open(final String host, final int port) throws IOException, UnknownHostException {
        this.open(new Socket(host, port));
    }
    
    public void open(final Socket socket) {
        this.stompSocket = socket;
    }
    
    public void close() throws IOException {
        if (this.stompSocket != null) {
            this.stompSocket.close();
            this.stompSocket = null;
        }
    }
    
    public void sendFrame(final String data) throws Exception {
        final byte[] bytes = data.getBytes("UTF-8");
        final OutputStream outputStream = this.stompSocket.getOutputStream();
        outputStream.write(bytes);
        outputStream.flush();
    }
    
    public void sendFrame(final String frame, final byte[] data) throws Exception {
        final byte[] bytes = frame.getBytes("UTF-8");
        final OutputStream outputStream = this.stompSocket.getOutputStream();
        outputStream.write(bytes);
        outputStream.write(data);
        outputStream.flush();
    }
    
    public StompFrame receive() throws Exception {
        return this.receive(10000L);
    }
    
    public StompFrame receive(final long timeOut) throws Exception {
        this.stompSocket.setSoTimeout((int)timeOut);
        final InputStream is = this.stompSocket.getInputStream();
        final StompWireFormat wf = new StompWireFormat();
        wf.setStompVersion(this.version);
        final DataInputStream dis = new DataInputStream(is);
        return (StompFrame)wf.unmarshal(dis);
    }
    
    public String receiveFrame() throws Exception {
        return this.receiveFrame(10000L);
    }
    
    public String receiveFrame(final long timeOut) throws Exception {
        this.stompSocket.setSoTimeout((int)timeOut);
        final InputStream is = this.stompSocket.getInputStream();
        int c = 0;
        while (true) {
            c = is.read();
            if (c < 0) {
                throw new IOException("socket closed.");
            }
            if (c == 0) {
                c = is.read();
                if (c == 10) {
                    return this.stringFromBuffer(this.inputBuffer);
                }
                this.inputBuffer.write(0);
                this.inputBuffer.write(c);
            }
            else {
                this.inputBuffer.write(c);
            }
        }
    }
    
    private String stringFromBuffer(final ByteArrayOutputStream inputBuffer) throws Exception {
        final byte[] ba = inputBuffer.toByteArray();
        inputBuffer.reset();
        return new String(ba, "UTF-8");
    }
    
    public Socket getStompSocket() {
        return this.stompSocket;
    }
    
    public void setStompSocket(final Socket stompSocket) {
        this.stompSocket = stompSocket;
    }
    
    public void connect(final String username, final String password) throws Exception {
        this.connect(username, password, null);
    }
    
    public void connect(final String username, final String password, final String client) throws Exception {
        final HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("login", username);
        headers.put("passcode", password);
        if (client != null) {
            headers.put("client-id", client);
        }
        this.connect(headers);
    }
    
    public void connect(final HashMap<String, String> headers) throws Exception {
        final StompFrame frame = new StompFrame("CONNECT", headers);
        this.sendFrame(frame.format());
        final StompFrame connect = this.receive();
        if (!connect.getAction().equals("CONNECTED")) {
            throw new Exception("Not connected: " + connect.getBody());
        }
    }
    
    public void disconnect() throws Exception {
        this.disconnect(null);
    }
    
    public void disconnect(final String receiptId) throws Exception {
        final StompFrame frame = new StompFrame("DISCONNECT");
        if (receiptId != null && !receiptId.isEmpty()) {
            frame.getHeaders().put("receipt", receiptId);
        }
        this.sendFrame(frame.format());
    }
    
    public void send(final String destination, final String message) throws Exception {
        this.send(destination, message, null, null);
    }
    
    public void send(final String destination, final String message, final String transaction, HashMap<String, String> headers) throws Exception {
        if (headers == null) {
            headers = new HashMap<String, String>();
        }
        headers.put("destination", destination);
        if (transaction != null) {
            headers.put("transaction", transaction);
        }
        final StompFrame frame = new StompFrame("SEND", headers, message.getBytes());
        this.sendFrame(frame.format());
    }
    
    public void subscribe(final String destination) throws Exception {
        this.subscribe(destination, null, null);
    }
    
    public void subscribe(final String destination, final String ack) throws Exception {
        this.subscribe(destination, ack, new HashMap<String, String>());
    }
    
    public void subscribe(final String destination, final String ack, HashMap<String, String> headers) throws Exception {
        if (headers == null) {
            headers = new HashMap<String, String>();
        }
        headers.put("destination", destination);
        if (ack != null) {
            headers.put("ack", ack);
        }
        final StompFrame frame = new StompFrame("SUBSCRIBE", headers);
        this.sendFrame(frame.format());
    }
    
    public void unsubscribe(final String destination) throws Exception {
        this.unsubscribe(destination, null);
    }
    
    public void unsubscribe(final String destination, HashMap<String, String> headers) throws Exception {
        if (headers == null) {
            headers = new HashMap<String, String>();
        }
        headers.put("destination", destination);
        final StompFrame frame = new StompFrame("UNSUBSCRIBE", headers);
        this.sendFrame(frame.format());
    }
    
    public void begin(final String transaction) throws Exception {
        final HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("transaction", transaction);
        final StompFrame frame = new StompFrame("BEGIN", headers);
        this.sendFrame(frame.format());
    }
    
    public void abort(final String transaction) throws Exception {
        final HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("transaction", transaction);
        final StompFrame frame = new StompFrame("ABORT", headers);
        this.sendFrame(frame.format());
    }
    
    public void commit(final String transaction) throws Exception {
        final HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("transaction", transaction);
        final StompFrame frame = new StompFrame("COMMIT", headers);
        this.sendFrame(frame.format());
    }
    
    public void ack(final StompFrame frame) throws Exception {
        this.ack(frame.getHeaders().get("message-id"), null);
    }
    
    public void ack(final StompFrame frame, final String transaction) throws Exception {
        this.ack(frame.getHeaders().get("message-id"), transaction);
    }
    
    public void ack(final String messageId) throws Exception {
        this.ack(messageId, null);
    }
    
    public void ack(final String messageId, final String transaction) throws Exception {
        final HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("message-id", messageId);
        if (transaction != null) {
            headers.put("transaction", transaction);
        }
        final StompFrame frame = new StompFrame("ACK", headers);
        this.sendFrame(frame.format());
    }
    
    public void keepAlive() throws Exception {
        final OutputStream outputStream = this.stompSocket.getOutputStream();
        outputStream.write(10);
        outputStream.flush();
    }
    
    protected String appendHeaders(final HashMap<String, Object> headers) {
        final StringBuilder result = new StringBuilder();
        for (final String key : headers.keySet()) {
            result.append(key + ":" + headers.get(key) + "\n");
        }
        result.append("\n");
        return result.toString();
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public void setVersion(final String version) {
        this.version = version;
    }
}
