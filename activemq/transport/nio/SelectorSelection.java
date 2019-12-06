// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.nio;

import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;
import java.nio.channels.SelectionKey;

public final class SelectorSelection
{
    private final SelectorWorker worker;
    private final SelectorManager.Listener listener;
    private int interest;
    private SelectionKey key;
    private AtomicBoolean closed;
    
    public SelectorSelection(final SelectorWorker worker, final SocketChannel socketChannel, final SelectorManager.Listener listener) throws ClosedChannelException {
        this.closed = new AtomicBoolean();
        this.worker = worker;
        this.listener = listener;
        worker.addIoTask(new Runnable() {
            @Override
            public void run() {
                try {
                    SelectorSelection.this.key = socketChannel.register(worker.selector, 0, SelectorSelection.this);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    public void setInterestOps(final int ops) {
        this.interest = ops;
    }
    
    public void enable() {
        this.worker.addIoTask(new Runnable() {
            @Override
            public void run() {
                try {
                    SelectorSelection.this.key.interestOps(SelectorSelection.this.interest);
                }
                catch (CancelledKeyException ex) {}
            }
        });
    }
    
    public void disable() {
        this.worker.addIoTask(new Runnable() {
            @Override
            public void run() {
                try {
                    SelectorSelection.this.key.interestOps(0);
                }
                catch (CancelledKeyException ex) {}
            }
        });
    }
    
    public void close() {
        if (this.closed.compareAndSet(false, true)) {
            this.worker.addIoTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        SelectorSelection.this.key.cancel();
                    }
                    catch (CancelledKeyException ex) {}
                    SelectorSelection.this.worker.release();
                }
            });
        }
    }
    
    public void onSelect() {
        this.listener.onSelect(this);
    }
    
    public void onError(final Throwable e) {
        this.listener.onError(this, e);
    }
}
