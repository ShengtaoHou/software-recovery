// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

import java.util.Iterator;
import org.slf4j.LoggerFactory;
import java.net.Socket;
import org.apache.activemq.transport.tcp.TimeStampStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.slf4j.Logger;

public class WriteTimeoutFilter extends TransportFilter
{
    private static final Logger LOG;
    protected static ConcurrentLinkedQueue<WriteTimeoutFilter> writers;
    protected static AtomicInteger messageCounter;
    protected static TimeoutThread timeoutThread;
    protected static long sleep;
    protected long writeTimeout;
    
    public WriteTimeoutFilter(final Transport next) {
        super(next);
        this.writeTimeout = -1L;
    }
    
    @Override
    public void oneway(final Object command) throws IOException {
        try {
            registerWrite(this);
            super.oneway(command);
        }
        catch (IOException x) {
            throw x;
        }
        finally {
            deRegisterWrite(this, false, null);
        }
    }
    
    public long getWriteTimeout() {
        return this.writeTimeout;
    }
    
    public void setWriteTimeout(final long writeTimeout) {
        this.writeTimeout = writeTimeout;
    }
    
    public static long getSleep() {
        return WriteTimeoutFilter.sleep;
    }
    
    public static void setSleep(final long sleep) {
        WriteTimeoutFilter.sleep = sleep;
    }
    
    protected TimeStampStream getWriter() {
        return this.next.narrow(TimeStampStream.class);
    }
    
    protected Socket getSocket() {
        return this.next.narrow(Socket.class);
    }
    
    protected static void registerWrite(final WriteTimeoutFilter filter) {
        WriteTimeoutFilter.writers.add(filter);
    }
    
    protected static boolean deRegisterWrite(final WriteTimeoutFilter filter, final boolean fail, final IOException iox) {
        final boolean result = WriteTimeoutFilter.writers.remove(filter);
        if (result && fail) {
            final String message = "Forced write timeout for:" + filter.getNext().getRemoteAddress();
            WriteTimeoutFilter.LOG.warn(message);
            final Socket sock = filter.getSocket();
            if (sock == null) {
                WriteTimeoutFilter.LOG.error("Destination socket is null, unable to close socket.(" + message + ")");
            }
            else {
                try {
                    sock.close();
                }
                catch (IOException ex) {}
            }
        }
        return result;
    }
    
    @Override
    public void start() throws Exception {
        super.start();
    }
    
    @Override
    public void stop() throws Exception {
        super.stop();
    }
    
    static {
        LOG = LoggerFactory.getLogger(WriteTimeoutFilter.class);
        WriteTimeoutFilter.writers = new ConcurrentLinkedQueue<WriteTimeoutFilter>();
        WriteTimeoutFilter.messageCounter = new AtomicInteger(0);
        WriteTimeoutFilter.timeoutThread = new TimeoutThread();
        WriteTimeoutFilter.sleep = 5000L;
    }
    
    protected static class TimeoutThread extends Thread
    {
        static AtomicInteger instance;
        boolean run;
        
        public TimeoutThread() {
            this.run = true;
            this.setName("WriteTimeoutFilter-Timeout-" + TimeoutThread.instance.incrementAndGet());
            this.setDaemon(true);
            this.setPriority(1);
            this.start();
        }
        
        @Override
        public void run() {
            while (this.run) {
                boolean error = false;
                try {
                    if (!Thread.interrupted()) {
                        final Iterator<WriteTimeoutFilter> filters = WriteTimeoutFilter.writers.iterator();
                        while (this.run && filters.hasNext()) {
                            final WriteTimeoutFilter filter = filters.next();
                            if (filter.getWriteTimeout() <= 0L) {
                                continue;
                            }
                            final long writeStart = filter.getWriter().getWriteTimestamp();
                            final long delta = (filter.getWriter().isWriting() && writeStart > 0L) ? (System.currentTimeMillis() - writeStart) : -1L;
                            if (delta <= filter.getWriteTimeout()) {
                                continue;
                            }
                            WriteTimeoutFilter.deRegisterWrite(filter, true, null);
                        }
                    }
                    try {
                        Thread.sleep(WriteTimeoutFilter.getSleep());
                        error = false;
                    }
                    catch (InterruptedException ex) {}
                }
                catch (Throwable t) {
                    if (error) {
                        continue;
                    }
                    WriteTimeoutFilter.LOG.error("WriteTimeout thread unable validate existing sockets.", t);
                    error = true;
                }
            }
        }
        
        static {
            TimeoutThread.instance = new AtomicInteger(0);
        }
    }
}
