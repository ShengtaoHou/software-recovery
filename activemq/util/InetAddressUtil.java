// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import java.net.UnknownHostException;
import java.net.InetAddress;

public class InetAddressUtil
{
    public static String getLocalHostName() throws UnknownHostException {
        try {
            return InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException uhe) {
            final String host = uhe.getMessage();
            if (host != null) {
                final int colon = host.indexOf(58);
                if (colon > 0) {
                    return host.substring(0, colon);
                }
            }
            throw uhe;
        }
    }
}
