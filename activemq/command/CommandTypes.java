// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

public interface CommandTypes
{
    public static final byte PROTOCOL_VERSION = 10;
    public static final byte PROTOCOL_STORE_VERSION = 6;
    public static final byte NULL = 0;
    public static final byte WIREFORMAT_INFO = 1;
    public static final byte BROKER_INFO = 2;
    public static final byte CONNECTION_INFO = 3;
    public static final byte SESSION_INFO = 4;
    public static final byte CONSUMER_INFO = 5;
    public static final byte PRODUCER_INFO = 6;
    public static final byte TRANSACTION_INFO = 7;
    public static final byte DESTINATION_INFO = 8;
    public static final byte REMOVE_SUBSCRIPTION_INFO = 9;
    public static final byte KEEP_ALIVE_INFO = 10;
    public static final byte SHUTDOWN_INFO = 11;
    public static final byte REMOVE_INFO = 12;
    public static final byte CONTROL_COMMAND = 14;
    public static final byte FLUSH_COMMAND = 15;
    public static final byte CONNECTION_ERROR = 16;
    public static final byte CONSUMER_CONTROL = 17;
    public static final byte CONNECTION_CONTROL = 18;
    public static final byte PRODUCER_ACK = 19;
    public static final byte MESSAGE_PULL = 20;
    public static final byte MESSAGE_DISPATCH = 21;
    public static final byte MESSAGE_ACK = 22;
    public static final byte ACTIVEMQ_MESSAGE = 23;
    public static final byte ACTIVEMQ_BYTES_MESSAGE = 24;
    public static final byte ACTIVEMQ_MAP_MESSAGE = 25;
    public static final byte ACTIVEMQ_OBJECT_MESSAGE = 26;
    public static final byte ACTIVEMQ_STREAM_MESSAGE = 27;
    public static final byte ACTIVEMQ_TEXT_MESSAGE = 28;
    public static final byte ACTIVEMQ_BLOB_MESSAGE = 29;
    public static final byte RESPONSE = 30;
    public static final byte EXCEPTION_RESPONSE = 31;
    public static final byte DATA_RESPONSE = 32;
    public static final byte DATA_ARRAY_RESPONSE = 33;
    public static final byte INTEGER_RESPONSE = 34;
    public static final byte DISCOVERY_EVENT = 40;
    public static final byte JOURNAL_ACK = 50;
    public static final byte JOURNAL_REMOVE = 52;
    public static final byte JOURNAL_TRACE = 53;
    public static final byte JOURNAL_TRANSACTION = 54;
    public static final byte DURABLE_SUBSCRIPTION_INFO = 55;
    public static final byte PARTIAL_COMMAND = 60;
    public static final byte PARTIAL_LAST_COMMAND = 61;
    public static final byte REPLAY = 65;
    public static final byte BYTE_TYPE = 70;
    public static final byte CHAR_TYPE = 71;
    public static final byte SHORT_TYPE = 72;
    public static final byte INTEGER_TYPE = 73;
    public static final byte LONG_TYPE = 74;
    public static final byte DOUBLE_TYPE = 75;
    public static final byte FLOAT_TYPE = 76;
    public static final byte STRING_TYPE = 77;
    public static final byte BOOLEAN_TYPE = 78;
    public static final byte BYTE_ARRAY_TYPE = 79;
    public static final byte MESSAGE_DISPATCH_NOTIFICATION = 90;
    public static final byte NETWORK_BRIDGE_FILTER = 91;
    public static final byte ACTIVEMQ_QUEUE = 100;
    public static final byte ACTIVEMQ_TOPIC = 101;
    public static final byte ACTIVEMQ_TEMP_QUEUE = 102;
    public static final byte ACTIVEMQ_TEMP_TOPIC = 103;
    public static final byte MESSAGE_ID = 110;
    public static final byte ACTIVEMQ_LOCAL_TRANSACTION_ID = 111;
    public static final byte ACTIVEMQ_XA_TRANSACTION_ID = 112;
    public static final byte CONNECTION_ID = 120;
    public static final byte SESSION_ID = 121;
    public static final byte CONSUMER_ID = 122;
    public static final byte PRODUCER_ID = 123;
    public static final byte BROKER_ID = 124;
}
