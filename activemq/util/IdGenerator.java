// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import java.io.IOException;
import java.net.ServerSocket;
import org.slf4j.LoggerFactory;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;

public class IdGenerator
{
    private static final Logger LOG;
    private static final String UNIQUE_STUB;
    private static int instanceCount;
    private static String hostName;
    private String seed;
    private final AtomicLong sequence;
    private int length;
    public static final String PROPERTY_IDGENERATOR_PORT = "activemq.idgenerator.port";
    
    public IdGenerator(final String prefix) {
        this.sequence = new AtomicLong(1L);
        synchronized (IdGenerator.UNIQUE_STUB) {
            this.seed = prefix + IdGenerator.UNIQUE_STUB + IdGenerator.instanceCount++ + ":";
            this.length = this.seed.length() + "9223372036854775807".length();
        }
    }
    
    public IdGenerator() {
        this("ID:" + IdGenerator.hostName);
    }
    
    public static String getHostName() {
        return IdGenerator.hostName;
    }
    
    public synchronized String generateId() {
        final StringBuilder sb = new StringBuilder(this.length);
        sb.append(this.seed);
        sb.append(this.sequence.getAndIncrement());
        return sb.toString();
    }
    
    public static String sanitizeHostName(final String hostName) {
        boolean changed = false;
        final StringBuilder sb = new StringBuilder();
        for (final char ch : hostName.toCharArray()) {
            if (ch < '\u007f') {
                sb.append(ch);
            }
            else {
                changed = true;
            }
        }
        if (changed) {
            final String newHost = sb.toString();
            IdGenerator.LOG.info("Sanitized hostname from: {} to: {}", hostName, newHost);
            return newHost;
        }
        return hostName;
    }
    
    public String generateSanitizedId() {
        String result = this.generateId();
        result = result.replace(':', '-');
        result = result.replace('_', '-');
        result = result.replace('.', '-');
        return result;
    }
    
    public static String getSeedFromId(final String id) {
        String result = id;
        if (id != null) {
            final int index = id.lastIndexOf(58);
            if (index > 0 && index + 1 < id.length()) {
                result = id.substring(0, index);
            }
        }
        return result;
    }
    
    public static long getSequenceFromId(final String id) {
        long result = -1L;
        if (id != null) {
            final int index = id.lastIndexOf(58);
            if (index > 0 && index + 1 < id.length()) {
                final String numStr = id.substring(index + 1, id.length());
                result = Long.parseLong(numStr);
            }
        }
        return result;
    }
    
    public static int compare(final String id1, final String id2) {
        int result = -1;
        final String seed1 = getSeedFromId(id1);
        final String seed2 = getSeedFromId(id2);
        if (seed1 != null && seed2 != null) {
            result = seed1.compareTo(seed2);
            if (result == 0) {
                final long count1 = getSequenceFromId(id1);
                final long count2 = getSequenceFromId(id2);
                result = (int)(count1 - count2);
            }
        }
        return result;
    }
    
    static {
        LOG = LoggerFactory.getLogger(IdGenerator.class);
        String stub = "";
        boolean canAccessSystemProps = true;
        try {
            final SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                sm.checkPropertiesAccess();
            }
        }
        catch (SecurityException se) {
            canAccessSystemProps = false;
        }
        if (canAccessSystemProps) {
            int idGeneratorPort = 0;
            ServerSocket ss = null;
            try {
                idGeneratorPort = Integer.parseInt(System.getProperty("activemq.idgenerator.port", "0"));
                IdGenerator.LOG.trace("Using port {}", (Object)idGeneratorPort);
                IdGenerator.hostName = InetAddressUtil.getLocalHostName();
                ss = new ServerSocket(idGeneratorPort);
                stub = "-" + ss.getLocalPort() + "-" + System.currentTimeMillis() + "-";
                Thread.sleep(100L);
            }
            catch (Exception e) {
                if (IdGenerator.LOG.isTraceEnabled()) {
                    IdGenerator.LOG.trace("could not generate unique stub by using DNS and binding to local port", e);
                }
                else {
                    IdGenerator.LOG.warn("could not generate unique stub by using DNS and binding to local port: {} {}", e.getClass().getCanonicalName(), e.getMessage());
                }
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
                if (ss != null) {
                    try {
                        ss.close();
                    }
                    catch (IOException ioe) {
                        if (IdGenerator.LOG.isTraceEnabled()) {
                            IdGenerator.LOG.trace("Closing the server socket failed", ioe);
                        }
                        else {
                            IdGenerator.LOG.warn("Closing the server socket failed due " + ioe.getMessage());
                        }
                    }
                }
            }
            finally {
                if (ss != null) {
                    try {
                        ss.close();
                    }
                    catch (IOException ioe2) {
                        if (IdGenerator.LOG.isTraceEnabled()) {
                            IdGenerator.LOG.trace("Closing the server socket failed", ioe2);
                        }
                        else {
                            IdGenerator.LOG.warn("Closing the server socket failed due " + ioe2.getMessage());
                        }
                    }
                }
            }
        }
        if (IdGenerator.hostName == null) {
            IdGenerator.hostName = "localhost";
        }
        IdGenerator.hostName = sanitizeHostName(IdGenerator.hostName);
        if (stub.length() == 0) {
            stub = "-1-" + System.currentTimeMillis() + "-";
        }
        UNIQUE_STUB = stub;
    }
}
