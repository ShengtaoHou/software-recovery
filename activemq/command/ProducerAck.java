// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import org.apache.activemq.state.CommandVisitor;

public class ProducerAck extends BaseCommand
{
    public static final byte DATA_STRUCTURE_TYPE = 19;
    protected ProducerId producerId;
    protected int size;
    
    public ProducerAck() {
    }
    
    public ProducerAck(final ProducerId producerId, final int size) {
        this.producerId = producerId;
        this.size = size;
    }
    
    public void copy(final ProducerAck copy) {
        super.copy(copy);
        copy.producerId = this.producerId;
        copy.size = this.size;
    }
    
    @Override
    public byte getDataStructureType() {
        return 19;
    }
    
    @Override
    public Response visit(final CommandVisitor visitor) throws Exception {
        return visitor.processProducerAck(this);
    }
    
    public ProducerId getProducerId() {
        return this.producerId;
    }
    
    public void setProducerId(final ProducerId producerId) {
        this.producerId = producerId;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public void setSize(final int size) {
        this.size = size;
    }
}
