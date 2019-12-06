// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.mqtt;

import javax.jms.JMSException;
import java.io.IOException;
import org.fusesource.mqtt.codec.MQTTFrame;
import org.fusesource.hawtbuf.DataByteArrayInputStream;
import org.fusesource.hawtbuf.DataByteArrayOutputStream;
import org.apache.activemq.transport.tcp.TcpTransport;

public class MQTTCodec
{
    TcpTransport transport;
    DataByteArrayOutputStream currentCommand;
    boolean processedHeader;
    String action;
    byte header;
    int contentLength;
    int previousByte;
    int payLoadRead;
    
    public MQTTCodec(final TcpTransport transport) {
        this.currentCommand = new DataByteArrayOutputStream();
        this.processedHeader = false;
        this.contentLength = -1;
        this.previousByte = -1;
        this.payLoadRead = 0;
        this.transport = transport;
    }
    
    public void parse(final DataByteArrayInputStream input, final int readSize) throws Exception {
        int i = 0;
        while (i++ < readSize) {
            final byte b = input.readByte();
            if (!this.processedHeader && b == 0) {
                this.previousByte = 0;
            }
            else {
                if (!this.processedHeader) {
                    i += this.processHeader(b, input);
                    if (this.contentLength == 0) {
                        this.processCommand();
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
                else if (this.payLoadRead == this.contentLength) {
                    this.processCommand();
                    i += this.processHeader(b, input);
                }
                else {
                    this.currentCommand.write(b);
                    ++this.payLoadRead;
                }
                this.previousByte = b;
            }
        }
        if (this.processedHeader && this.payLoadRead == this.contentLength) {
            this.processCommand();
        }
    }
    
    private int processHeader(final byte header, final DataByteArrayInputStream input) {
        this.header = header;
        int multiplier = 1;
        int read = 0;
        int length = 0;
        byte digit;
        do {
            digit = input.readByte();
            length += (digit & 0x7F) * multiplier;
            multiplier <<= 7;
            ++read;
        } while ((digit & 0x80) != 0x0);
        this.contentLength = length;
        this.processedHeader = true;
        return read;
    }
    
    private void processCommand() throws Exception {
        final MQTTFrame frame = new MQTTFrame(this.currentCommand.toBuffer().deepCopy()).header(this.header);
        this.transport.doConsume(frame);
        this.processedHeader = false;
        this.currentCommand.reset();
        this.contentLength = -1;
        this.payLoadRead = 0;
    }
    
    public static String commandType(final byte header) throws IOException, JMSException {
        final byte messageType = (byte)((header & 0xF0) >>> 4);
        switch (messageType) {
            case 12: {
                return "PINGREQ";
            }
            case 1: {
                return "CONNECT";
            }
            case 14: {
                return "DISCONNECT";
            }
            case 8: {
                return "SUBSCRIBE";
            }
            case 10: {
                return "UNSUBSCRIBE";
            }
            case 3: {
                return "PUBLISH";
            }
            case 4: {
                return "PUBACK";
            }
            case 5: {
                return "PUBREC";
            }
            case 6: {
                return "PUBREL";
            }
            case 7: {
                return "PUBCOMP";
            }
            default: {
                return "UNKNOWN";
            }
        }
    }
}
