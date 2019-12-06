// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.nio;

import org.slf4j.LoggerFactory;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.security.cert.X509Certificate;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.util.ServiceStopper;
import java.io.DataInput;
import java.io.InputStream;
import java.io.DataInputStream;
import org.apache.activemq.openwire.OpenWireFormat;
import java.io.EOFException;
import org.apache.activemq.util.IOExceptionSupport;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.nio.channels.WritableByteChannel;
import java.nio.ByteBuffer;
import java.net.Socket;
import java.io.IOException;
import java.net.UnknownHostException;
import java.net.URI;
import javax.net.SocketFactory;
import org.apache.activemq.wireformat.WireFormat;
import org.apache.activemq.thread.TaskRunnerFactory;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLContext;
import org.slf4j.Logger;

public class NIOSSLTransport extends NIOTransport
{
    private static final Logger LOG;
    protected boolean needClientAuth;
    protected boolean wantClientAuth;
    protected String[] enabledCipherSuites;
    protected SSLContext sslContext;
    protected SSLEngine sslEngine;
    protected SSLSession sslSession;
    protected volatile boolean handshakeInProgress;
    protected SSLEngineResult.Status status;
    protected SSLEngineResult.HandshakeStatus handshakeStatus;
    protected TaskRunnerFactory taskRunnerFactory;
    
    public NIOSSLTransport(final WireFormat wireFormat, final SocketFactory socketFactory, final URI remoteLocation, final URI localLocation) throws UnknownHostException, IOException {
        super(wireFormat, socketFactory, remoteLocation, localLocation);
        this.handshakeInProgress = false;
        this.status = null;
        this.handshakeStatus = null;
    }
    
    public NIOSSLTransport(final WireFormat wireFormat, final Socket socket) throws IOException {
        super(wireFormat, socket);
        this.handshakeInProgress = false;
        this.status = null;
        this.handshakeStatus = null;
    }
    
    public void setSslContext(final SSLContext sslContext) {
        this.sslContext = sslContext;
    }
    
    @Override
    protected void initializeStreams() throws IOException {
        NIOOutputStream outputStream = null;
        try {
            (this.channel = this.socket.getChannel()).configureBlocking(false);
            if (this.sslContext == null) {
                this.sslContext = SSLContext.getDefault();
            }
            String remoteHost = null;
            int remotePort = -1;
            try {
                final URI remoteAddress = new URI(this.getRemoteAddress());
                remoteHost = remoteAddress.getHost();
                remotePort = remoteAddress.getPort();
            }
            catch (Exception ex) {}
            if (remoteHost != null && remotePort != -1) {
                this.sslEngine = this.sslContext.createSSLEngine(remoteHost, remotePort);
            }
            else {
                this.sslEngine = this.sslContext.createSSLEngine();
            }
            this.sslEngine.setUseClientMode(false);
            if (this.enabledCipherSuites != null) {
                this.sslEngine.setEnabledCipherSuites(this.enabledCipherSuites);
            }
            if (this.wantClientAuth) {
                this.sslEngine.setWantClientAuth(this.wantClientAuth);
            }
            if (this.needClientAuth) {
                this.sslEngine.setNeedClientAuth(this.needClientAuth);
            }
            this.sslSession = this.sslEngine.getSession();
            (this.inputBuffer = ByteBuffer.allocate(this.sslSession.getPacketBufferSize())).clear();
            outputStream = new NIOOutputStream(this.channel);
            outputStream.setEngine(this.sslEngine);
            this.dataOut = new DataOutputStream(outputStream);
            this.buffOut = outputStream;
            this.sslEngine.beginHandshake();
            this.handshakeStatus = this.sslEngine.getHandshakeStatus();
            this.doHandshake();
        }
        catch (Exception e) {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                super.closeStreams();
            }
            catch (Exception ex2) {}
            throw new IOException(e);
        }
    }
    
    protected void finishHandshake() throws Exception {
        if (this.handshakeInProgress) {
            this.handshakeInProgress = false;
            this.nextFrameSize = -1;
            this.sslSession = this.sslEngine.getSession();
            this.selection = SelectorManager.getInstance().register(this.channel, new SelectorManager.Listener() {
                @Override
                public void onSelect(final SelectorSelection selection) {
                    NIOSSLTransport.this.serviceRead();
                }
                
                @Override
                public void onError(final SelectorSelection selection, final Throwable error) {
                    if (error instanceof IOException) {
                        NIOSSLTransport.this.onException((IOException)error);
                    }
                    else {
                        NIOSSLTransport.this.onException(IOExceptionSupport.create(error));
                    }
                }
            });
        }
    }
    
    @Override
    protected void serviceRead() {
        try {
            if (this.handshakeInProgress) {
                this.doHandshake();
            }
            final ByteBuffer plain = ByteBuffer.allocate(this.sslSession.getApplicationBufferSize());
            plain.position(plain.limit());
            while (true) {
                if (!plain.hasRemaining()) {
                    final int readCount = this.secureRead(plain);
                    if (readCount == 0) {
                        break;
                    }
                    if (readCount == -1) {
                        this.onException(new EOFException());
                        this.selection.close();
                        break;
                    }
                    this.receiveCounter += readCount;
                }
                if (this.status == SSLEngineResult.Status.OK && this.handshakeStatus != SSLEngineResult.HandshakeStatus.NEED_UNWRAP) {
                    this.processCommand(plain);
                }
            }
        }
        catch (IOException e) {
            this.onException(e);
        }
        catch (Throwable e2) {
            this.onException(IOExceptionSupport.create(e2));
        }
    }
    
    protected void processCommand(final ByteBuffer plain) throws Exception {
        if (this.nextFrameSize == -1) {
            if (plain.remaining() < 32) {
                if (this.currentBuffer == null) {
                    this.currentBuffer = ByteBuffer.allocate(4);
                }
                while (this.currentBuffer.hasRemaining() && plain.hasRemaining()) {
                    this.currentBuffer.put(plain.get());
                }
                if (this.currentBuffer.hasRemaining()) {
                    return;
                }
                this.currentBuffer.flip();
                this.nextFrameSize = this.currentBuffer.getInt();
            }
            else if (this.currentBuffer != null) {
                while (this.currentBuffer.hasRemaining()) {
                    this.currentBuffer.put(plain.get());
                }
                this.currentBuffer.flip();
                this.nextFrameSize = this.currentBuffer.getInt();
            }
            else {
                this.nextFrameSize = plain.getInt();
            }
            if (this.wireFormat instanceof OpenWireFormat) {
                final long maxFrameSize = ((OpenWireFormat)this.wireFormat).getMaxFrameSize();
                if (this.nextFrameSize > maxFrameSize) {
                    throw new IOException("Frame size of " + this.nextFrameSize / 1048576 + " MB larger than max allowed " + maxFrameSize / 1048576L + " MB");
                }
            }
            (this.currentBuffer = ByteBuffer.allocate(this.nextFrameSize + 4)).putInt(this.nextFrameSize);
        }
        else {
            if (this.currentBuffer.remaining() >= plain.remaining()) {
                this.currentBuffer.put(plain);
            }
            else {
                final byte[] fill = new byte[this.currentBuffer.remaining()];
                plain.get(fill);
                this.currentBuffer.put(fill);
            }
            if (this.currentBuffer.hasRemaining()) {
                return;
            }
            this.currentBuffer.flip();
            final Object command = this.wireFormat.unmarshal(new DataInputStream(new NIOInputStream(this.currentBuffer)));
            this.doConsume(command);
            this.nextFrameSize = -1;
            this.currentBuffer = null;
        }
    }
    
    protected int secureRead(final ByteBuffer plain) throws Exception {
        if (this.inputBuffer.position() == 0 || !this.inputBuffer.hasRemaining() || this.status == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
            final int bytesRead = this.channel.read(this.inputBuffer);
            if (bytesRead == 0) {
                return 0;
            }
            if (bytesRead == -1) {
                this.sslEngine.closeInbound();
                if (this.inputBuffer.position() == 0 || this.status == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                    return -1;
                }
            }
        }
        plain.clear();
        this.inputBuffer.flip();
        SSLEngineResult res;
        do {
            res = this.sslEngine.unwrap(this.inputBuffer, plain);
        } while (res.getStatus() == SSLEngineResult.Status.OK && res.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_UNWRAP && res.bytesProduced() == 0);
        if (res.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.FINISHED) {
            this.finishHandshake();
        }
        this.status = res.getStatus();
        this.handshakeStatus = res.getHandshakeStatus();
        if (this.status == SSLEngineResult.Status.CLOSED) {
            this.sslEngine.closeInbound();
            return -1;
        }
        this.inputBuffer.compact();
        plain.flip();
        return plain.remaining();
    }
    
    protected void doHandshake() throws Exception {
        this.handshakeInProgress = true;
    Label_0112:
        while (true) {
            switch (this.sslEngine.getHandshakeStatus()) {
                case NEED_UNWRAP: {
                    this.secureRead(ByteBuffer.allocate(this.sslSession.getApplicationBufferSize()));
                    continue;
                }
                case NEED_TASK: {
                    Runnable task;
                    while ((task = this.sslEngine.getDelegatedTask()) != null) {
                        this.taskRunnerFactory.execute(task);
                    }
                    continue;
                }
                case NEED_WRAP: {
                    ((NIOOutputStream)this.buffOut).write(ByteBuffer.allocate(0));
                    continue;
                }
                case FINISHED:
                case NOT_HANDSHAKING: {
                    break Label_0112;
                }
            }
        }
        this.finishHandshake();
    }
    
    @Override
    protected void doStart() throws Exception {
        this.taskRunnerFactory = new TaskRunnerFactory("ActiveMQ NIOSSLTransport Task");
        super.doStart();
    }
    
    @Override
    protected void doStop(final ServiceStopper stopper) throws Exception {
        if (this.taskRunnerFactory != null) {
            this.taskRunnerFactory.shutdownNow();
            this.taskRunnerFactory = null;
        }
        if (this.channel != null) {
            this.channel.close();
            this.channel = null;
        }
        super.doStop(stopper);
    }
    
    @Override
    public void doConsume(final Object command) {
        if (command instanceof ConnectionInfo) {
            final ConnectionInfo connectionInfo = (ConnectionInfo)command;
            connectionInfo.setTransportContext(this.getPeerCertificates());
        }
        super.doConsume(command);
    }
    
    public X509Certificate[] getPeerCertificates() {
        X509Certificate[] clientCertChain = null;
        try {
            if (this.sslEngine.getSession() != null) {
                clientCertChain = (X509Certificate[])this.sslEngine.getSession().getPeerCertificates();
            }
        }
        catch (SSLPeerUnverifiedException e) {
            if (NIOSSLTransport.LOG.isTraceEnabled()) {
                NIOSSLTransport.LOG.trace("Failed to get peer certificates.", e);
            }
        }
        return clientCertChain;
    }
    
    public boolean isNeedClientAuth() {
        return this.needClientAuth;
    }
    
    public void setNeedClientAuth(final boolean needClientAuth) {
        this.needClientAuth = needClientAuth;
    }
    
    public boolean isWantClientAuth() {
        return this.wantClientAuth;
    }
    
    public void setWantClientAuth(final boolean wantClientAuth) {
        this.wantClientAuth = wantClientAuth;
    }
    
    public String[] getEnabledCipherSuites() {
        return this.enabledCipherSuites;
    }
    
    public void setEnabledCipherSuites(final String[] enabledCipherSuites) {
        this.enabledCipherSuites = enabledCipherSuites;
    }
    
    static {
        LOG = LoggerFactory.getLogger(NIOSSLTransport.class);
    }
}
