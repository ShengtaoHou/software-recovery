// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.reliable;

import org.slf4j.LoggerFactory;
import org.apache.activemq.command.Response;
import org.apache.activemq.transport.FutureResponse;
import org.apache.activemq.transport.ResponseCallback;
import org.apache.activemq.command.ReplayCommand;
import java.io.IOException;
import java.util.Comparator;
import java.util.TreeSet;
import org.apache.activemq.openwire.CommandIdComparator;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.transport.udp.UdpTransport;
import org.apache.activemq.command.Command;
import java.util.SortedSet;
import org.slf4j.Logger;
import org.apache.activemq.transport.ResponseCorrelator;

@Deprecated
public class ReliableTransport extends ResponseCorrelator
{
    private static final Logger LOG;
    private ReplayStrategy replayStrategy;
    private final SortedSet<Command> commands;
    private int expectedCounter;
    private int replayBufferCommandCount;
    private int requestTimeout;
    private ReplayBuffer replayBuffer;
    private Replayer replayer;
    private UdpTransport udpTransport;
    
    public ReliableTransport(final Transport next, final ReplayStrategy replayStrategy) {
        super(next);
        this.commands = new TreeSet<Command>(new CommandIdComparator());
        this.expectedCounter = 1;
        this.replayBufferCommandCount = 50;
        this.requestTimeout = 2000;
        this.replayStrategy = replayStrategy;
    }
    
    public ReliableTransport(final Transport next, final UdpTransport udpTransport) throws IOException {
        super(next, udpTransport.getSequenceGenerator());
        this.commands = new TreeSet<Command>(new CommandIdComparator());
        this.expectedCounter = 1;
        this.replayBufferCommandCount = 50;
        this.requestTimeout = 2000;
        this.udpTransport = udpTransport;
        this.replayer = udpTransport.createReplayer();
    }
    
    public void requestReplay(final int fromCommandId, final int toCommandId) {
        final ReplayCommand replay = new ReplayCommand();
        replay.setFirstNakNumber(fromCommandId);
        replay.setLastNakNumber(toCommandId);
        try {
            this.oneway(replay);
        }
        catch (IOException e) {
            this.getTransportListener().onException(e);
        }
    }
    
    @Override
    public Object request(final Object o) throws IOException {
        final Command command = (Command)o;
        final FutureResponse response = this.asyncRequest(command, null);
        Response result;
        while (true) {
            result = response.getResult(this.requestTimeout);
            if (result != null) {
                break;
            }
            this.onMissingResponse(command, response);
        }
        return result;
    }
    
    @Override
    public Object request(final Object o, int timeout) throws IOException {
        final Command command = (Command)o;
        final FutureResponse response = this.asyncRequest(command, null);
        while (timeout > 0) {
            int time;
            if ((time = timeout) > this.requestTimeout) {
                time = this.requestTimeout;
            }
            final Response result = response.getResult(time);
            if (result != null) {
                return result;
            }
            this.onMissingResponse(command, response);
            timeout -= time;
        }
        return response.getResult(0);
    }
    
    @Override
    public void onCommand(final Object o) {
        Command command = (Command)o;
        if (command.isWireFormatInfo()) {
            super.onCommand(command);
            return;
        }
        if (command.getDataStructureType() == 65) {
            this.replayCommands((ReplayCommand)command);
            return;
        }
        final int actualCounter = command.getCommandId();
        boolean valid = this.expectedCounter == actualCounter;
        if (!valid) {
            synchronized (this.commands) {
                int nextCounter = actualCounter;
                final boolean empty = this.commands.isEmpty();
                if (!empty) {
                    final Command nextAvailable = this.commands.first();
                    nextCounter = nextAvailable.getCommandId();
                }
                try {
                    final boolean keep = this.replayStrategy.onDroppedPackets(this, this.expectedCounter, actualCounter, nextCounter);
                    if (keep) {
                        if (ReliableTransport.LOG.isDebugEnabled()) {
                            ReliableTransport.LOG.debug("Received out of order command which is being buffered for later: " + command);
                        }
                        this.commands.add(command);
                    }
                }
                catch (IOException e) {
                    this.onException(e);
                }
                if (!empty) {
                    command = this.commands.first();
                    valid = (this.expectedCounter == command.getCommandId());
                    if (valid) {
                        this.commands.remove(command);
                    }
                }
            }
        }
        while (valid) {
            this.replayStrategy.onReceivedPacket(this, this.expectedCounter);
            ++this.expectedCounter;
            super.onCommand(command);
            synchronized (this.commands) {
                valid = !this.commands.isEmpty();
                if (!valid) {
                    continue;
                }
                command = this.commands.first();
                valid = (this.expectedCounter == command.getCommandId());
                if (!valid) {
                    continue;
                }
                this.commands.remove(command);
            }
        }
    }
    
    public int getBufferedCommandCount() {
        synchronized (this.commands) {
            return this.commands.size();
        }
    }
    
    public int getExpectedCounter() {
        return this.expectedCounter;
    }
    
    public void setExpectedCounter(final int expectedCounter) {
        this.expectedCounter = expectedCounter;
    }
    
    public int getRequestTimeout() {
        return this.requestTimeout;
    }
    
    public void setRequestTimeout(final int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }
    
    public ReplayStrategy getReplayStrategy() {
        return this.replayStrategy;
    }
    
    public ReplayBuffer getReplayBuffer() {
        if (this.replayBuffer == null) {
            this.replayBuffer = this.createReplayBuffer();
        }
        return this.replayBuffer;
    }
    
    public void setReplayBuffer(final ReplayBuffer replayBuffer) {
        this.replayBuffer = replayBuffer;
    }
    
    public int getReplayBufferCommandCount() {
        return this.replayBufferCommandCount;
    }
    
    public void setReplayBufferCommandCount(final int replayBufferSize) {
        this.replayBufferCommandCount = replayBufferSize;
    }
    
    public void setReplayStrategy(final ReplayStrategy replayStrategy) {
        this.replayStrategy = replayStrategy;
    }
    
    public Replayer getReplayer() {
        return this.replayer;
    }
    
    public void setReplayer(final Replayer replayer) {
        this.replayer = replayer;
    }
    
    @Override
    public String toString() {
        return this.next.toString();
    }
    
    @Override
    public void start() throws Exception {
        if (this.udpTransport != null) {
            this.udpTransport.setReplayBuffer(this.getReplayBuffer());
        }
        if (this.replayStrategy == null) {
            throw new IllegalArgumentException("Property replayStrategy not specified");
        }
        super.start();
    }
    
    protected void onMissingResponse(final Command command, final FutureResponse response) {
        ReliableTransport.LOG.debug("Still waiting for response on: " + this + " to command: " + command + " sending replay message");
        final int commandId = command.getCommandId();
        this.requestReplay(commandId, commandId);
    }
    
    protected ReplayBuffer createReplayBuffer() {
        return new DefaultReplayBuffer(this.getReplayBufferCommandCount());
    }
    
    protected void replayCommands(final ReplayCommand command) {
        try {
            if (this.replayer == null) {
                this.onException(new IOException("Cannot replay commands. No replayer property configured"));
            }
            if (ReliableTransport.LOG.isDebugEnabled()) {
                ReliableTransport.LOG.debug("Processing replay command: " + command);
            }
            this.getReplayBuffer().replayMessages(command.getFirstNakNumber(), command.getLastNakNumber(), this.replayer);
        }
        catch (IOException e) {
            this.onException(e);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(ReliableTransport.class);
    }
}
