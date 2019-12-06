// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.command.store;

import org.apache.activemq.console.command.store.proto.MapEntryPB;
import org.apache.activemq.console.command.store.proto.MessagePB;
import org.apache.activemq.console.command.store.proto.QueueEntryPB;
import org.apache.activemq.console.command.store.proto.QueuePB;
import org.fusesource.hawtbuf.proto.MessageBuffer;
import org.apache.activemq.console.command.store.tar.TarEntry;
import java.io.IOException;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.AsciiBuffer;
import java.util.zip.GZIPOutputStream;
import org.apache.activemq.console.command.store.tar.TarOutputStream;
import java.io.OutputStream;

public class ExportStreamManager
{
    private final OutputStream target;
    private final int version;
    TarOutputStream stream;
    long seq;
    
    ExportStreamManager(final OutputStream target, final int version) throws IOException {
        this.seq = 0L;
        this.target = target;
        this.version = version;
        this.stream = new TarOutputStream(new GZIPOutputStream(target));
        this.store("ver", new AsciiBuffer("" + version));
    }
    
    public void finish() throws IOException {
        this.stream.close();
    }
    
    private void store(final String ext, final Buffer value) throws IOException {
        final TarEntry entry = new TarEntry(this.seq + "." + ext);
        ++this.seq;
        entry.setSize(value.length());
        this.stream.putNextEntry(entry);
        value.writeTo(this.stream);
        this.stream.closeEntry();
    }
    
    private void store(final String ext, final MessageBuffer<?, ?> value) throws IOException {
        final TarEntry entry = new TarEntry(this.seq + "." + ext);
        ++this.seq;
        entry.setSize(value.serializedSizeFramed());
        this.stream.putNextEntry(entry);
        value.writeFramed((OutputStream)this.stream);
        this.stream.closeEntry();
    }
    
    public void store_queue(final QueuePB.Getter value) throws IOException {
        this.store("que", (MessageBuffer<?, ?>)value.freeze());
    }
    
    public void store_queue_entry(final QueueEntryPB.Getter value) throws IOException {
        this.store("qen", (MessageBuffer<?, ?>)value.freeze());
    }
    
    public void store_message(final MessagePB.Getter value) throws IOException {
        this.store("msg", (MessageBuffer<?, ?>)value.freeze());
    }
    
    public void store_map_entry(final MapEntryPB.Getter value) throws IOException {
        this.store("map", (MessageBuffer<?, ?>)value.freeze());
    }
}
