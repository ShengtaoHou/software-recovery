// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.amqp;

import java.io.IOException;
import java.util.Iterator;
import org.apache.activemq.command.Command;
import java.util.ArrayList;

public class AMQPProtocolDiscriminator implements IAmqpProtocolConverter
{
    private final AmqpTransport transport;
    private static final ArrayList<Discriminator> DISCRIMINATORS;
    private static final ArrayList<Command> pendingCommands;
    
    public AMQPProtocolDiscriminator(final AmqpTransport transport) {
        this.transport = transport;
    }
    
    @Override
    public void onAMQPData(final Object command) throws Exception {
        if (command.getClass() == AmqpHeader.class) {
            final AmqpHeader header = (AmqpHeader)command;
            Discriminator match = null;
            for (final Discriminator discriminator : AMQPProtocolDiscriminator.DISCRIMINATORS) {
                if (discriminator.matches(header)) {
                    match = discriminator;
                }
            }
            if (match == null) {
                match = AMQPProtocolDiscriminator.DISCRIMINATORS.get(0);
            }
            final IAmqpProtocolConverter next = match.create(this.transport);
            this.transport.setProtocolConverter(next);
            for (final Command send : AMQPProtocolDiscriminator.pendingCommands) {
                next.onActiveMQCommand(send);
            }
            AMQPProtocolDiscriminator.pendingCommands.clear();
            next.onAMQPData(command);
            return;
        }
        throw new IllegalStateException();
    }
    
    @Override
    public void onAMQPException(final IOException error) {
    }
    
    @Override
    public void onActiveMQCommand(final Command command) throws Exception {
        AMQPProtocolDiscriminator.pendingCommands.add(command);
    }
    
    @Override
    public void updateTracer() {
    }
    
    static {
        (DISCRIMINATORS = new ArrayList<Discriminator>()).add(new Discriminator() {
            @Override
            public IAmqpProtocolConverter create(final AmqpTransport transport) {
                return new AmqpProtocolConverter(transport);
            }
            
            @Override
            public boolean matches(final AmqpHeader header) {
                switch (header.getProtocolId()) {
                    case 0:
                    case 3: {
                        if (header.getMajor() == 1 && header.getMinor() == 0 && header.getRevision() == 0) {
                            return true;
                        }
                        break;
                    }
                }
                return false;
            }
        });
        pendingCommands = new ArrayList<Command>();
    }
    
    interface Discriminator
    {
        boolean matches(final AmqpHeader p0);
        
        IAmqpProtocolConverter create(final AmqpTransport p0);
    }
}
