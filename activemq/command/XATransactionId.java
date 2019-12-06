// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import java.util.Arrays;
import org.apache.activemq.util.DataByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.activemq.util.DataByteArrayOutputStream;
import javax.transaction.xa.Xid;

public class XATransactionId extends TransactionId implements Xid, Comparable
{
    public static final byte DATA_STRUCTURE_TYPE = 112;
    private int formatId;
    private byte[] branchQualifier;
    private byte[] globalTransactionId;
    private transient DataByteArrayOutputStream outputStream;
    private transient byte[] encodedXidBytes;
    private transient int hash;
    private transient String transactionKey;
    private transient ArrayList<MessageAck> preparedAcks;
    final int XID_PREFIX_SIZE = 16;
    
    public XATransactionId() {
    }
    
    public XATransactionId(final Xid xid) {
        this.formatId = xid.getFormatId();
        this.globalTransactionId = xid.getGlobalTransactionId();
        this.branchQualifier = xid.getBranchQualifier();
    }
    
    public XATransactionId(final byte[] encodedBytes) throws IOException {
        this.encodedXidBytes = encodedBytes;
        this.initFromEncodedBytes();
    }
    
    @Override
    public byte getDataStructureType() {
        return 112;
    }
    
    private void initFromEncodedBytes() throws IOException {
        final DataByteArrayInputStream inputStream = new DataByteArrayInputStream(this.encodedXidBytes);
        inputStream.skipBytes(10);
        this.formatId = inputStream.readInt();
        final int globalLength = inputStream.readShort();
        this.globalTransactionId = new byte[globalLength];
        try {
            inputStream.read(this.globalTransactionId);
            inputStream.read(this.branchQualifier = new byte[inputStream.available()]);
        }
        catch (IOException fatal) {
            throw new RuntimeException(this + ", failed to decode:", fatal);
        }
    }
    
    public synchronized byte[] getEncodedXidBytes() {
        if (this.encodedXidBytes == null) {
            (this.outputStream = new DataByteArrayOutputStream(16 + this.globalTransactionId.length + this.branchQualifier.length)).position(10);
            this.outputStream.writeInt(this.formatId);
            this.outputStream.writeShort(this.globalTransactionId.length);
            try {
                this.outputStream.write(this.globalTransactionId);
                this.outputStream.write(this.branchQualifier);
            }
            catch (IOException fatal) {
                throw new RuntimeException(this + ", failed to encode:", fatal);
            }
            this.encodedXidBytes = this.outputStream.getData();
        }
        return this.encodedXidBytes;
    }
    
    public DataByteArrayOutputStream internalOutputStream() {
        return this.outputStream;
    }
    
    @Override
    public synchronized String getTransactionKey() {
        if (this.transactionKey == null) {
            final StringBuffer s = new StringBuffer();
            s.append("XID:[" + this.formatId + ",globalId=");
            s.append(this.stringForm(this.formatId, this.globalTransactionId));
            s.append(",branchId=");
            s.append(this.stringForm(this.formatId, this.branchQualifier));
            s.append("]");
            this.transactionKey = s.toString();
        }
        return this.transactionKey;
    }
    
    private String stringForm(final int format, final byte[] uid) {
        final StringBuffer s = new StringBuffer();
        switch (format) {
            case 131077: {
                this.stringFormArj(s, uid);
                break;
            }
            default: {
                this.stringFormDefault(s, uid);
                break;
            }
        }
        return s.toString();
    }
    
    private void stringFormDefault(final StringBuffer s, final byte[] uid) {
        for (int i = 0; i < uid.length; ++i) {
            s.append(Integer.toHexString(uid[i]));
        }
    }
    
    private void stringFormArj(final StringBuffer s, final byte[] uid) {
        try {
            final DataByteArrayInputStream byteArrayInputStream = new DataByteArrayInputStream(uid);
            s.append(Long.toString(byteArrayInputStream.readLong(), 16));
            s.append(':');
            s.append(Long.toString(byteArrayInputStream.readLong(), 16));
            s.append(':');
            s.append(Integer.toString(byteArrayInputStream.readInt(), 16));
            s.append(':');
            s.append(Integer.toString(byteArrayInputStream.readInt(), 16));
            s.append(':');
            s.append(Integer.toString(byteArrayInputStream.readInt(), 16));
        }
        catch (Exception ignored) {
            this.stringFormDefault(s, uid);
        }
    }
    
    @Override
    public String toString() {
        return this.getTransactionKey();
    }
    
    @Override
    public boolean isXATransaction() {
        return true;
    }
    
    @Override
    public boolean isLocalTransaction() {
        return false;
    }
    
    @Override
    public int getFormatId() {
        return this.formatId;
    }
    
    @Override
    public byte[] getGlobalTransactionId() {
        return this.globalTransactionId;
    }
    
    @Override
    public byte[] getBranchQualifier() {
        return this.branchQualifier;
    }
    
    public void setBranchQualifier(final byte[] branchQualifier) {
        this.branchQualifier = branchQualifier;
        this.hash = 0;
    }
    
    public void setFormatId(final int formatId) {
        this.formatId = formatId;
        this.hash = 0;
    }
    
    public void setGlobalTransactionId(final byte[] globalTransactionId) {
        this.globalTransactionId = globalTransactionId;
        this.hash = 0;
    }
    
    @Override
    public int hashCode() {
        if (this.hash == 0) {
            this.hash = this.formatId;
            this.hash = hash(this.globalTransactionId, this.hash);
            this.hash = hash(this.branchQualifier, this.hash);
            if (this.hash == 0) {
                this.hash = 11332302;
            }
        }
        return this.hash;
    }
    
    private static int hash(final byte[] bytes, int hash) {
        for (int size = bytes.length, i = 0; i < size; ++i) {
            hash ^= bytes[i] << i % 4 * 8;
        }
        return hash;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null || o.getClass() != XATransactionId.class) {
            return false;
        }
        final XATransactionId xid = (XATransactionId)o;
        return xid.formatId == this.formatId && Arrays.equals(xid.globalTransactionId, this.globalTransactionId) && Arrays.equals(xid.branchQualifier, this.branchQualifier);
    }
    
    @Override
    public int compareTo(final Object o) {
        if (o == null || o.getClass() != XATransactionId.class) {
            return -1;
        }
        final XATransactionId xid = (XATransactionId)o;
        return this.getTransactionKey().compareTo(xid.getTransactionKey());
    }
    
    public void setPreparedAcks(final ArrayList<MessageAck> preparedAcks) {
        this.preparedAcks = preparedAcks;
    }
    
    public ArrayList<MessageAck> getPreparedAcks() {
        return this.preparedAcks;
    }
}
