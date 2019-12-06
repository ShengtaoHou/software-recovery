// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import java.io.IOException;
import org.apache.activemq.state.CommandVisitor;

public class RemoveInfo extends BaseCommand
{
    public static final byte DATA_STRUCTURE_TYPE = 12;
    protected DataStructure objectId;
    protected long lastDeliveredSequenceId;
    
    public RemoveInfo() {
    }
    
    public RemoveInfo(final DataStructure objectId) {
        this.objectId = objectId;
    }
    
    @Override
    public byte getDataStructureType() {
        return 12;
    }
    
    public DataStructure getObjectId() {
        return this.objectId;
    }
    
    public void setObjectId(final DataStructure objectId) {
        this.objectId = objectId;
    }
    
    public long getLastDeliveredSequenceId() {
        return this.lastDeliveredSequenceId;
    }
    
    public void setLastDeliveredSequenceId(final long lastDeliveredSequenceId) {
        this.lastDeliveredSequenceId = lastDeliveredSequenceId;
    }
    
    @Override
    public Response visit(final CommandVisitor visitor) throws Exception {
        switch (this.objectId.getDataStructureType()) {
            case 120: {
                return visitor.processRemoveConnection((ConnectionId)this.objectId, this.lastDeliveredSequenceId);
            }
            case 121: {
                return visitor.processRemoveSession((SessionId)this.objectId, this.lastDeliveredSequenceId);
            }
            case 122: {
                return visitor.processRemoveConsumer((ConsumerId)this.objectId, this.lastDeliveredSequenceId);
            }
            case 123: {
                return visitor.processRemoveProducer((ProducerId)this.objectId);
            }
            default: {
                throw new IOException("Unknown remove command type: " + this.objectId.getDataStructureType());
            }
        }
    }
    
    public boolean isConnectionRemove() {
        return this.objectId.getDataStructureType() == 120;
    }
    
    public boolean isSessionRemove() {
        return this.objectId.getDataStructureType() == 121;
    }
    
    public boolean isConsumerRemove() {
        return this.objectId.getDataStructureType() == 122;
    }
    
    public boolean isProducerRemove() {
        return this.objectId.getDataStructureType() == 123;
    }
}
