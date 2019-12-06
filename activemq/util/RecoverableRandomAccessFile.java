// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import java.nio.channels.FileChannel;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.Closeable;
import java.io.DataInput;
import java.io.DataOutput;

public class RecoverableRandomAccessFile implements DataOutput, DataInput, Closeable
{
    private static final boolean SKIP_METADATA_UPDATE;
    RandomAccessFile raf;
    File file;
    String mode;
    
    public RecoverableRandomAccessFile(final File file, final String mode) throws FileNotFoundException {
        this.file = file;
        this.mode = mode;
        this.raf = new RandomAccessFile(file, mode);
    }
    
    public RecoverableRandomAccessFile(final String name, final String mode) throws FileNotFoundException {
        this.file = new File(name);
        this.mode = mode;
        this.raf = new RandomAccessFile(this.file, mode);
    }
    
    protected RandomAccessFile getRaf() throws IOException {
        if (this.raf == null) {
            this.raf = new RandomAccessFile(this.file, this.mode);
        }
        return this.raf;
    }
    
    protected void handleException() throws IOException {
        try {
            if (this.raf != null) {
                this.raf.close();
            }
        }
        catch (Throwable t) {}
        finally {
            this.raf = null;
        }
    }
    
    @Override
    public void close() throws IOException {
        if (this.raf != null) {
            this.raf.close();
        }
    }
    
    @Override
    public void readFully(final byte[] bytes) throws IOException {
        try {
            this.getRaf().readFully(bytes);
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    @Override
    public void readFully(final byte[] bytes, final int i, final int i2) throws IOException {
        try {
            this.getRaf().readFully(bytes, i, i2);
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    @Override
    public int skipBytes(final int i) throws IOException {
        try {
            return this.getRaf().skipBytes(i);
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    @Override
    public boolean readBoolean() throws IOException {
        try {
            return this.getRaf().readBoolean();
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    @Override
    public byte readByte() throws IOException {
        try {
            return this.getRaf().readByte();
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    @Override
    public int readUnsignedByte() throws IOException {
        try {
            return this.getRaf().readUnsignedByte();
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    @Override
    public short readShort() throws IOException {
        try {
            return this.getRaf().readShort();
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    @Override
    public int readUnsignedShort() throws IOException {
        try {
            return this.getRaf().readUnsignedShort();
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    @Override
    public char readChar() throws IOException {
        try {
            return this.getRaf().readChar();
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    @Override
    public int readInt() throws IOException {
        try {
            return this.getRaf().readInt();
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    @Override
    public long readLong() throws IOException {
        try {
            return this.getRaf().readLong();
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    @Override
    public float readFloat() throws IOException {
        try {
            return this.getRaf().readFloat();
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    @Override
    public double readDouble() throws IOException {
        try {
            return this.getRaf().readDouble();
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    @Override
    public String readLine() throws IOException {
        try {
            return this.getRaf().readLine();
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    @Override
    public String readUTF() throws IOException {
        try {
            return this.getRaf().readUTF();
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    @Override
    public void write(final int i) throws IOException {
        try {
            this.getRaf().write(i);
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    @Override
    public void write(final byte[] bytes) throws IOException {
        try {
            this.getRaf().write(bytes);
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    @Override
    public void write(final byte[] bytes, final int i, final int i2) throws IOException {
        try {
            this.getRaf().write(bytes, i, i2);
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    @Override
    public void writeBoolean(final boolean b) throws IOException {
        try {
            this.getRaf().writeBoolean(b);
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    @Override
    public void writeByte(final int i) throws IOException {
        try {
            this.getRaf().writeByte(i);
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    @Override
    public void writeShort(final int i) throws IOException {
        try {
            this.getRaf().writeShort(i);
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    @Override
    public void writeChar(final int i) throws IOException {
        try {
            this.getRaf().writeChar(i);
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    @Override
    public void writeInt(final int i) throws IOException {
        try {
            this.getRaf().writeInt(i);
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    @Override
    public void writeLong(final long l) throws IOException {
        try {
            this.getRaf().writeLong(l);
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    @Override
    public void writeFloat(final float v) throws IOException {
        try {
            this.getRaf().writeFloat(v);
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    @Override
    public void writeDouble(final double v) throws IOException {
        try {
            this.getRaf().writeDouble(v);
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    @Override
    public void writeBytes(final String s) throws IOException {
        try {
            this.getRaf().writeBytes(s);
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    @Override
    public void writeChars(final String s) throws IOException {
        try {
            this.getRaf().writeChars(s);
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    @Override
    public void writeUTF(final String s) throws IOException {
        try {
            this.getRaf().writeUTF(s);
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    public long length() throws IOException {
        try {
            return this.getRaf().length();
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    public void setLength(final long length) throws IOException {
        try {
            this.getRaf().setLength(length);
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    public void seek(final long pos) throws IOException {
        try {
            this.getRaf().seek(pos);
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    public FileDescriptor getFD() throws IOException {
        try {
            return this.getRaf().getFD();
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    public void sync() throws IOException {
        try {
            this.getRaf().getChannel().force(!RecoverableRandomAccessFile.SKIP_METADATA_UPDATE);
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    public FileChannel getChannel() throws IOException {
        try {
            return this.getRaf().getChannel();
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    public int read(final byte[] b, final int off, final int len) throws IOException {
        try {
            return this.getRaf().read(b, off, len);
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    public int read(final byte[] b) throws IOException {
        try {
            return this.getRaf().read(b);
        }
        catch (IOException ioe) {
            this.handleException();
            throw ioe;
        }
    }
    
    static {
        SKIP_METADATA_UPDATE = Boolean.getBoolean("org.apache.activemq.kahaDB.files.skipMetadataUpdate");
    }
}
