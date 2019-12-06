// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf;

public final class WireFormat
{
    public static final int WIRETYPE_VARINT = 0;
    public static final int WIRETYPE_FIXED64 = 1;
    public static final int WIRETYPE_LENGTH_DELIMITED = 2;
    public static final int WIRETYPE_START_GROUP = 3;
    public static final int WIRETYPE_END_GROUP = 4;
    public static final int WIRETYPE_FIXED32 = 5;
    public static final int TAG_TYPE_BITS = 3;
    public static final int TAG_TYPE_MASK = 7;
    public static final int MESSAGE_SET_ITEM = 1;
    public static final int MESSAGE_SET_TYPE_ID = 2;
    public static final int MESSAGE_SET_MESSAGE = 3;
    public static final int MESSAGE_SET_ITEM_TAG;
    public static final int MESSAGE_SET_ITEM_END_TAG;
    public static final int MESSAGE_SET_TYPE_ID_TAG;
    public static final int MESSAGE_SET_MESSAGE_TAG;
    
    private WireFormat() {
    }
    
    public static int getTagWireType(final int tag) {
        return tag & 0x7;
    }
    
    public static int getTagFieldNumber(final int tag) {
        return tag >>> 3;
    }
    
    public static int makeTag(final int fieldNumber, final int wireType) {
        return fieldNumber << 3 | wireType;
    }
    
    static {
        MESSAGE_SET_ITEM_TAG = makeTag(1, 3);
        MESSAGE_SET_ITEM_END_TAG = makeTag(1, 4);
        MESSAGE_SET_TYPE_ID_TAG = makeTag(2, 0);
        MESSAGE_SET_MESSAGE_TAG = makeTag(3, 2);
    }
}
