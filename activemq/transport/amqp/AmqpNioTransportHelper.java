// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.amqp;

import org.slf4j.LoggerFactory;
import org.fusesource.hawtbuf.Buffer;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import org.apache.activemq.transport.TransportSupport;
import java.nio.ByteBuffer;
import org.slf4j.Logger;
import java.io.DataInputStream;

public class AmqpNioTransportHelper
{
    private final DataInputStream amqpHeaderValue;
    private final Integer AMQP_HEADER_VALUE;
    private static final Logger LOG;
    protected int nextFrameSize;
    protected ByteBuffer currentBuffer;
    private boolean magicConsumed;
    private final TransportSupport transportSupport;
    
    public AmqpNioTransportHelper(final TransportSupport transportSupport) throws IOException {
        this.amqpHeaderValue = new DataInputStream(new ByteArrayInputStream(new byte[] { 65, 77, 81, 80 }));
        this.nextFrameSize = -1;
        this.magicConsumed = false;
        this.AMQP_HEADER_VALUE = this.amqpHeaderValue.readInt();
        this.transportSupport = transportSupport;
    }
    
    protected void processCommand(final ByteBuffer plain) throws Exception {
        if (this.nextFrameSize == -1) {
            if (plain.remaining() < 4) {
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
        }
        while (true) {
            if (this.nextFrameSize == this.AMQP_HEADER_VALUE) {
                this.nextFrameSize = this.handleAmqpHeader(plain);
                if (this.nextFrameSize == -1) {
                    return;
                }
            }
            this.validateFrameSize(this.nextFrameSize);
            if (this.currentBuffer == null || this.currentBuffer.limit() == 4) {
                (this.currentBuffer = ByteBuffer.allocate(this.nextFrameSize)).putInt(this.nextFrameSize);
            }
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
            AmqpNioTransportHelper.LOG.debug("Calling doConsume with position {} limit {}", (Object)this.currentBuffer.position(), this.currentBuffer.limit());
            this.transportSupport.doConsume(AmqpSupport.toBuffer(this.currentBuffer));
            this.currentBuffer = null;
            this.nextFrameSize = -1;
            if (!plain.hasRemaining()) {
                return;
            }
            if (plain.remaining() < 4) {
                this.currentBuffer = ByteBuffer.allocate(4);
                while (this.currentBuffer.hasRemaining() && plain.hasRemaining()) {
                    this.currentBuffer.put(plain.get());
                }
                return;
            }
            this.nextFrameSize = plain.getInt();
        }
    }
    
    private void validateFrameSize(final int frameSize) throws IOException {
        if (this.nextFrameSize > 1048576) {
            throw new IOException("Frame size of " + this.nextFrameSize + "larger than max allowed " + 1048576);
        }
    }
    
    private int handleAmqpHeader(final ByteBuffer plain) {
        AmqpNioTransportHelper.LOG.debug("Consuming AMQP_HEADER");
        (this.currentBuffer = ByteBuffer.allocate(8)).putInt(this.AMQP_HEADER_VALUE);
        while (this.currentBuffer.hasRemaining()) {
            this.currentBuffer.put(plain.get());
        }
        this.currentBuffer.flip();
        if (!this.magicConsumed) {
            this.transportSupport.doConsume(new AmqpHeader(new Buffer(this.currentBuffer)));
            this.magicConsumed = true;
        }
        else {
            this.transportSupport.doConsume(AmqpSupport.toBuffer(this.currentBuffer));
        }
        this.currentBuffer = null;
        int nextFrameSize;
        if (plain.hasRemaining()) {
            if (plain.remaining() < 4) {
                nextFrameSize = 4;
            }
            else {
                nextFrameSize = plain.getInt();
            }
        }
        else {
            nextFrameSize = -1;
        }
        return nextFrameSize;
    }
    
    static {
        LOG = LoggerFactory.getLogger(AmqpNioTransportHelper.class);
    }
}
