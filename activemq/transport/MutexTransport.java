// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

public class MutexTransport extends TransportFilter
{
    private final ReentrantLock writeLock;
    private boolean syncOnCommand;
    
    public MutexTransport(final Transport next) {
        super(next);
        this.writeLock = new ReentrantLock();
        this.syncOnCommand = false;
    }
    
    public MutexTransport(final Transport next, final boolean syncOnCommand) {
        super(next);
        this.writeLock = new ReentrantLock();
        this.syncOnCommand = syncOnCommand;
    }
    
    @Override
    public void onCommand(final Object command) {
        if (this.syncOnCommand) {
            this.writeLock.lock();
            try {
                this.transportListener.onCommand(command);
            }
            finally {
                this.writeLock.unlock();
            }
        }
        else {
            this.transportListener.onCommand(command);
        }
    }
    
    @Override
    public FutureResponse asyncRequest(final Object command, final ResponseCallback responseCallback) throws IOException {
        this.writeLock.lock();
        try {
            return this.next.asyncRequest(command, null);
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public void oneway(final Object command) throws IOException {
        this.writeLock.lock();
        try {
            this.next.oneway(command);
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public Object request(final Object command) throws IOException {
        this.writeLock.lock();
        try {
            return this.next.request(command);
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public Object request(final Object command, final int timeout) throws IOException {
        this.writeLock.lock();
        try {
            return this.next.request(command, timeout);
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public String toString() {
        return this.next.toString();
    }
    
    public boolean isSyncOnCommand() {
        return this.syncOnCommand;
    }
    
    public void setSyncOnCommand(final boolean syncOnCommand) {
        this.syncOnCommand = syncOnCommand;
    }
}
