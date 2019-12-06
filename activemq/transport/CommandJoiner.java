// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

import org.slf4j.LoggerFactory;
import org.apache.activemq.command.LastPartialCommand;
import java.io.DataInput;
import java.io.InputStream;
import java.io.DataInputStream;
import org.apache.activemq.util.ByteArrayInputStream;
import java.io.IOException;
import org.apache.activemq.command.PartialCommand;
import org.apache.activemq.command.Command;
import org.apache.activemq.openwire.OpenWireFormat;
import java.io.ByteArrayOutputStream;
import org.slf4j.Logger;

public class CommandJoiner extends TransportFilter
{
    private static final Logger LOG;
    private ByteArrayOutputStream out;
    private OpenWireFormat wireFormat;
    
    public CommandJoiner(final Transport next, final OpenWireFormat wireFormat) {
        super(next);
        this.out = new ByteArrayOutputStream();
        this.wireFormat = wireFormat;
    }
    
    @Override
    public void onCommand(final Object o) {
        final Command command = (Command)o;
        final byte type = command.getDataStructureType();
        if (type == 60 || type == 61) {
            final PartialCommand header = (PartialCommand)command;
            final byte[] partialData = header.getData();
            try {
                this.out.write(partialData);
            }
            catch (IOException e) {
                this.getTransportListener().onException(e);
            }
            if (type == 61) {
                try {
                    final byte[] fullData = this.out.toByteArray();
                    this.out.reset();
                    final DataInputStream dataIn = new DataInputStream(new ByteArrayInputStream(fullData));
                    final Command completeCommand = (Command)this.wireFormat.unmarshal(dataIn);
                    final LastPartialCommand lastCommand = (LastPartialCommand)command;
                    lastCommand.configure(completeCommand);
                    this.getTransportListener().onCommand(completeCommand);
                }
                catch (IOException e) {
                    CommandJoiner.LOG.warn("Failed to unmarshal partial command: " + command);
                    this.getTransportListener().onException(e);
                }
            }
        }
        else {
            this.getTransportListener().onCommand(command);
        }
    }
    
    @Override
    public void stop() throws Exception {
        super.stop();
        this.out = null;
    }
    
    @Override
    public String toString() {
        return this.next.toString();
    }
    
    static {
        LOG = LoggerFactory.getLogger(CommandJoiner.class);
    }
}
