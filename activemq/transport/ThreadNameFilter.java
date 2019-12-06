// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

import java.io.IOException;

public class ThreadNameFilter extends TransportFilter
{
    public ThreadNameFilter(final Transport next) {
        super(next);
    }
    
    @Override
    public void oneway(final Object command) throws IOException {
        final String address = (this.next != null) ? this.next.getRemoteAddress() : null;
        if (address != null) {
            final String name = Thread.currentThread().getName();
            try {
                final String sendname = name + " - SendTo:" + address;
                Thread.currentThread().setName(sendname);
                super.oneway(command);
            }
            finally {
                Thread.currentThread().setName(name);
            }
        }
        else {
            super.oneway(command);
        }
    }
}
