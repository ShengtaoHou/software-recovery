// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf;

import java.io.IOException;

public abstract class DeferredDecodeMessage<T> extends BaseMessage<T>
{
    protected Buffer encodedForm;
    protected boolean decoded;
    
    public DeferredDecodeMessage() {
        this.decoded = true;
    }
    
    @Override
    public T mergeFramed(final CodedInputStream input) throws IOException {
        final int length = input.readRawVarint32();
        final int oldLimit = input.pushLimit(length);
        final T rc = this.mergeUnframed(input.readRawBytes(length));
        input.popLimit(oldLimit);
        return rc;
    }
    
    @Override
    public T mergeUnframed(final Buffer data) throws InvalidProtocolBufferException {
        this.encodedForm = data;
        this.decoded = false;
        return (T)this;
    }
    
    @Override
    public Buffer toUnframedBuffer() {
        if (this.encodedForm == null) {
            this.encodedForm = super.toUnframedBuffer();
        }
        return this.encodedForm;
    }
    
    @Override
    protected void load() {
        if (!this.decoded) {
            this.decoded = true;
            try {
                final Buffer originalForm = this.encodedForm;
                this.encodedForm = null;
                final CodedInputStream input = new CodedInputStream(originalForm);
                this.mergeUnframed(input);
                input.checkLastTagWas(0);
                this.encodedForm = originalForm;
                this.checktInitialized();
            }
            catch (Throwable e) {
                throw new RuntimeException("Deferred message decoding failed: " + e.getMessage(), e);
            }
        }
    }
    
    @Override
    protected void loadAndClear() {
        super.loadAndClear();
        this.load();
        this.encodedForm = null;
    }
    
    @Override
    public void clear() {
        super.clear();
        this.encodedForm = null;
        this.decoded = true;
    }
    
    public boolean isDecoded() {
        return this.decoded;
    }
    
    public boolean isEncoded() {
        return this.encodedForm != null;
    }
}
