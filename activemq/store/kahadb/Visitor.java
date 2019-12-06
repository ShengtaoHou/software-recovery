// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb;

import org.apache.activemq.store.kahadb.data.KahaUpdateMessageCommand;
import org.apache.activemq.store.kahadb.data.KahaAckMessageFileMapCommand;
import org.apache.activemq.store.kahadb.data.KahaProducerAuditCommand;
import org.apache.activemq.store.kahadb.data.KahaSubscriptionCommand;
import org.apache.activemq.store.kahadb.data.KahaRemoveDestinationCommand;
import org.apache.activemq.store.kahadb.data.KahaAddMessageCommand;
import org.apache.activemq.store.kahadb.data.KahaCommitCommand;
import org.apache.activemq.store.kahadb.data.KahaPrepareCommand;
import org.apache.activemq.store.kahadb.data.KahaRemoveMessageCommand;
import java.io.IOException;
import org.apache.activemq.store.kahadb.data.KahaRollbackCommand;
import org.apache.activemq.store.kahadb.data.KahaTraceCommand;

public class Visitor
{
    public void visit(final KahaTraceCommand command) {
    }
    
    public void visit(final KahaRollbackCommand command) throws IOException {
    }
    
    public void visit(final KahaRemoveMessageCommand command) throws IOException {
    }
    
    public void visit(final KahaPrepareCommand command) throws IOException {
    }
    
    public void visit(final KahaCommitCommand command) throws IOException {
    }
    
    public void visit(final KahaAddMessageCommand command) throws IOException {
    }
    
    public void visit(final KahaRemoveDestinationCommand command) throws IOException {
    }
    
    public void visit(final KahaSubscriptionCommand kahaUpdateSubscriptionCommand) throws IOException {
    }
    
    public void visit(final KahaProducerAuditCommand kahaProducerAuditCommand) throws IOException {
    }
    
    public void visit(final KahaAckMessageFileMapCommand kahaProducerAuditCommand) throws IOException {
    }
    
    public void visit(final KahaUpdateMessageCommand kahaUpdateMessageCommand) throws IOException {
    }
}
