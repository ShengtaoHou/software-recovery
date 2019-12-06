// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb;

import org.apache.activemq.command.ConnectionId;
import org.apache.activemq.protobuf.Buffer;
import org.apache.activemq.store.kahadb.data.KahaXATransactionId;
import org.apache.activemq.command.XATransactionId;
import org.apache.activemq.store.kahadb.data.KahaLocalTransactionId;
import org.apache.activemq.command.LocalTransactionId;
import org.apache.activemq.store.kahadb.data.KahaTransactionInfo;
import org.apache.activemq.command.TransactionId;

public class TransactionIdConversion
{
    static KahaTransactionInfo convertToLocal(final TransactionId tx) {
        final KahaTransactionInfo rc = new KahaTransactionInfo();
        final LocalTransactionId t = (LocalTransactionId)tx;
        final KahaLocalTransactionId kahaTxId = new KahaLocalTransactionId();
        kahaTxId.setConnectionId(t.getConnectionId().getValue());
        kahaTxId.setTransactionId(t.getValue());
        rc.setLocalTransactionId(kahaTxId);
        return rc;
    }
    
    static KahaTransactionInfo convert(final TransactionId txid) {
        if (txid == null) {
            return null;
        }
        KahaTransactionInfo rc;
        if (txid.isLocalTransaction()) {
            rc = convertToLocal(txid);
        }
        else {
            rc = new KahaTransactionInfo();
            final XATransactionId t = (XATransactionId)txid;
            final KahaXATransactionId kahaTxId = new KahaXATransactionId();
            kahaTxId.setBranchQualifier(new Buffer(t.getBranchQualifier()));
            kahaTxId.setGlobalTransactionId(new Buffer(t.getGlobalTransactionId()));
            kahaTxId.setFormatId(t.getFormatId());
            rc.setXaTransactionId(kahaTxId);
        }
        return rc;
    }
    
    static TransactionId convert(final KahaTransactionInfo transactionInfo) {
        if (transactionInfo.hasLocalTransactionId()) {
            final KahaLocalTransactionId tx = transactionInfo.getLocalTransactionId();
            final LocalTransactionId rc = new LocalTransactionId();
            rc.setConnectionId(new ConnectionId(tx.getConnectionId()));
            rc.setValue(tx.getTransactionId());
            return rc;
        }
        final KahaXATransactionId tx2 = transactionInfo.getXaTransactionId();
        final XATransactionId rc2 = new XATransactionId();
        rc2.setBranchQualifier(tx2.getBranchQualifier().toByteArray());
        rc2.setGlobalTransactionId(tx2.getGlobalTransactionId().toByteArray());
        rc2.setFormatId(tx2.getFormatId());
        return rc2;
    }
}
