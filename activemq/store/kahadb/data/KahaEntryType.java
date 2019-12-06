// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.data;

import org.apache.activemq.protobuf.Message;

public enum KahaEntryType
{
    KAHA_TRACE_COMMAND("KAHA_TRACE_COMMAND", 0), 
    KAHA_ADD_MESSAGE_COMMAND("KAHA_ADD_MESSAGE_COMMAND", 1), 
    KAHA_REMOVE_MESSAGE_COMMAND("KAHA_REMOVE_MESSAGE_COMMAND", 2), 
    KAHA_PREPARE_COMMAND("KAHA_PREPARE_COMMAND", 3), 
    KAHA_COMMIT_COMMAND("KAHA_COMMIT_COMMAND", 4), 
    KAHA_ROLLBACK_COMMAND("KAHA_ROLLBACK_COMMAND", 5), 
    KAHA_REMOVE_DESTINATION_COMMAND("KAHA_REMOVE_DESTINATION_COMMAND", 6), 
    KAHA_SUBSCRIPTION_COMMAND("KAHA_SUBSCRIPTION_COMMAND", 7), 
    KAHA_PRODUCER_AUDIT_COMMAND("KAHA_PRODUCER_AUDIT_COMMAND", 8), 
    KAHA_ACK_MESSAGE_FILE_MAP_COMMAND("KAHA_ACK_MESSAGE_FILE_MAP_COMMAND", 9), 
    KAHA_UPDATE_MESSAGE_COMMAND("KAHA_UPDATE_MESSAGE_COMMAND", 10);
    
    private final String name;
    private final int value;
    
    private KahaEntryType(final String name, final int value) {
        this.name = name;
        this.value = value;
    }
    
    public final int getNumber() {
        return this.value;
    }
    
    @Override
    public final String toString() {
        return this.name;
    }
    
    public static KahaEntryType valueOf(final int value) {
        switch (value) {
            case 0: {
                return KahaEntryType.KAHA_TRACE_COMMAND;
            }
            case 1: {
                return KahaEntryType.KAHA_ADD_MESSAGE_COMMAND;
            }
            case 2: {
                return KahaEntryType.KAHA_REMOVE_MESSAGE_COMMAND;
            }
            case 3: {
                return KahaEntryType.KAHA_PREPARE_COMMAND;
            }
            case 4: {
                return KahaEntryType.KAHA_COMMIT_COMMAND;
            }
            case 5: {
                return KahaEntryType.KAHA_ROLLBACK_COMMAND;
            }
            case 6: {
                return KahaEntryType.KAHA_REMOVE_DESTINATION_COMMAND;
            }
            case 7: {
                return KahaEntryType.KAHA_SUBSCRIPTION_COMMAND;
            }
            case 8: {
                return KahaEntryType.KAHA_PRODUCER_AUDIT_COMMAND;
            }
            case 9: {
                return KahaEntryType.KAHA_ACK_MESSAGE_FILE_MAP_COMMAND;
            }
            case 10: {
                return KahaEntryType.KAHA_UPDATE_MESSAGE_COMMAND;
            }
            default: {
                return null;
            }
        }
    }
    
    public Message createMessage() {
        switch (this) {
            case KAHA_TRACE_COMMAND: {
                return new KahaTraceCommand();
            }
            case KAHA_ADD_MESSAGE_COMMAND: {
                return new KahaAddMessageCommand();
            }
            case KAHA_REMOVE_MESSAGE_COMMAND: {
                return new KahaRemoveMessageCommand();
            }
            case KAHA_PREPARE_COMMAND: {
                return new KahaPrepareCommand();
            }
            case KAHA_COMMIT_COMMAND: {
                return new KahaCommitCommand();
            }
            case KAHA_ROLLBACK_COMMAND: {
                return new KahaRollbackCommand();
            }
            case KAHA_REMOVE_DESTINATION_COMMAND: {
                return new KahaRemoveDestinationCommand();
            }
            case KAHA_SUBSCRIPTION_COMMAND: {
                return new KahaSubscriptionCommand();
            }
            case KAHA_PRODUCER_AUDIT_COMMAND: {
                return new KahaProducerAuditCommand();
            }
            case KAHA_ACK_MESSAGE_FILE_MAP_COMMAND: {
                return new KahaAckMessageFileMapCommand();
            }
            case KAHA_UPDATE_MESSAGE_COMMAND: {
                return new KahaUpdateMessageCommand();
            }
            default: {
                return null;
            }
        }
    }
}
