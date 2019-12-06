// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire;

import org.apache.activemq.command.Command;
import java.util.Comparator;

public class CommandIdComparator implements Comparator<Command>
{
    @Override
    public int compare(final Command c1, final Command c2) {
        return c1.getCommandId() - c2.getCommandId();
    }
}
