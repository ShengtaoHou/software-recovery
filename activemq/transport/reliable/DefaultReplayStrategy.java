// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.reliable;

import java.io.IOException;

public class DefaultReplayStrategy implements ReplayStrategy
{
    private int maximumDifference;
    
    public DefaultReplayStrategy() {
        this.maximumDifference = 5;
    }
    
    public DefaultReplayStrategy(final int maximumDifference) {
        this.maximumDifference = 5;
        this.maximumDifference = maximumDifference;
    }
    
    @Override
    public boolean onDroppedPackets(final ReliableTransport transport, final int expectedCounter, final int actualCounter, final int nextAvailableCounter) throws IOException {
        final int difference = actualCounter - expectedCounter;
        final long count = Math.abs(difference);
        if (count > this.maximumDifference) {
            int upperLimit = actualCounter - 1;
            if (upperLimit < expectedCounter) {
                upperLimit = expectedCounter;
            }
            transport.requestReplay(expectedCounter, upperLimit);
        }
        return difference > 0;
    }
    
    @Override
    public void onReceivedPacket(final ReliableTransport transport, final long expectedCounter) {
    }
    
    public int getMaximumDifference() {
        return this.maximumDifference;
    }
    
    public void setMaximumDifference(final int maximumDifference) {
        this.maximumDifference = maximumDifference;
    }
}
