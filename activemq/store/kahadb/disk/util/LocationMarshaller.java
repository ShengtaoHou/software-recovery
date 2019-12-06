// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.util;

import java.io.DataOutput;
import java.io.IOException;
import java.io.DataInput;
import org.apache.activemq.store.kahadb.disk.journal.Location;

public class LocationMarshaller implements Marshaller<Location>
{
    public static final LocationMarshaller INSTANCE;
    
    @Override
    public Location readPayload(final DataInput dataIn) throws IOException {
        final Location rc = new Location();
        rc.setDataFileId(dataIn.readInt());
        rc.setOffset(dataIn.readInt());
        return rc;
    }
    
    @Override
    public void writePayload(final Location object, final DataOutput dataOut) throws IOException {
        dataOut.writeInt(object.getDataFileId());
        dataOut.writeInt(object.getOffset());
    }
    
    @Override
    public int getFixedSize() {
        return 8;
    }
    
    @Override
    public Location deepCopy(final Location source) {
        return new Location(source);
    }
    
    @Override
    public boolean isDeepCopySupported() {
        return true;
    }
    
    static {
        INSTANCE = new LocationMarshaller();
    }
}
