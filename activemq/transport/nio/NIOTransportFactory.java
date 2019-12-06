// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.nio;

import java.nio.channels.SocketChannel;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import org.apache.activemq.transport.tcp.TcpTransport;
import javax.net.SocketFactory;
import java.net.URISyntaxException;
import java.io.IOException;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.wireformat.WireFormat;
import java.net.Socket;
import org.apache.activemq.transport.tcp.TcpTransportServer;
import javax.net.ServerSocketFactory;
import java.net.URI;
import org.apache.activemq.transport.tcp.TcpTransportFactory;

public class NIOTransportFactory extends TcpTransportFactory
{
    @Override
    protected TcpTransportServer createTcpTransportServer(final URI location, final ServerSocketFactory serverSocketFactory) throws IOException, URISyntaxException {
        return new TcpTransportServer(this, location, serverSocketFactory) {
            @Override
            protected Transport createTransport(final Socket socket, final WireFormat format) throws IOException {
                return new NIOTransport(format, socket);
            }
        };
    }
    
    @Override
    protected TcpTransport createTcpTransport(final WireFormat wf, final SocketFactory socketFactory, final URI location, final URI localLocation) throws UnknownHostException, IOException {
        return new NIOTransport(wf, socketFactory, location, localLocation);
    }
    
    @Override
    protected ServerSocketFactory createServerSocketFactory() {
        return new ServerSocketFactory() {
            @Override
            public ServerSocket createServerSocket(final int port) throws IOException {
                final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.socket().bind(new InetSocketAddress(port));
                return serverSocketChannel.socket();
            }
            
            @Override
            public ServerSocket createServerSocket(final int port, final int backlog) throws IOException {
                final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.socket().bind(new InetSocketAddress(port), backlog);
                return serverSocketChannel.socket();
            }
            
            @Override
            public ServerSocket createServerSocket(final int port, final int backlog, final InetAddress ifAddress) throws IOException {
                final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.socket().bind(new InetSocketAddress(ifAddress, port), backlog);
                return serverSocketChannel.socket();
            }
        };
    }
    
    @Override
    protected SocketFactory createSocketFactory() throws IOException {
        return new SocketFactory() {
            @Override
            public Socket createSocket() throws IOException {
                final SocketChannel channel = SocketChannel.open();
                return channel.socket();
            }
            
            @Override
            public Socket createSocket(final String host, final int port) throws IOException, UnknownHostException {
                final SocketChannel channel = SocketChannel.open();
                channel.connect(new InetSocketAddress(host, port));
                return channel.socket();
            }
            
            @Override
            public Socket createSocket(final InetAddress address, final int port) throws IOException {
                final SocketChannel channel = SocketChannel.open();
                channel.connect(new InetSocketAddress(address, port));
                return channel.socket();
            }
            
            @Override
            public Socket createSocket(final String address, final int port, final InetAddress localAddresss, final int localPort) throws IOException, UnknownHostException {
                final SocketChannel channel = SocketChannel.open();
                channel.socket().bind(new InetSocketAddress(localAddresss, localPort));
                channel.connect(new InetSocketAddress(address, port));
                return channel.socket();
            }
            
            @Override
            public Socket createSocket(final InetAddress address, final int port, final InetAddress localAddresss, final int localPort) throws IOException {
                final SocketChannel channel = SocketChannel.open();
                channel.socket().bind(new InetSocketAddress(localAddresss, localPort));
                channel.connect(new InetSocketAddress(address, port));
                return channel.socket();
            }
        };
    }
}
