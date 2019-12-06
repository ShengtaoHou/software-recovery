// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.tcp;

import java.util.HashMap;
import java.net.SocketException;
import java.net.Socket;
import java.util.Map;

public class QualityOfServiceUtils
{
    private static final int MAX_DIFF_SERV = 63;
    private static final int MIN_DIFF_SERV = 0;
    private static final Map<String, Integer> DIFF_SERV_NAMES;
    private static final int MAX_TOS = 255;
    private static final int MIN_TOS = 0;
    
    public static int getDSCP(final String value) throws IllegalArgumentException {
        int intValue = -1;
        if (QualityOfServiceUtils.DIFF_SERV_NAMES.containsKey(value)) {
            intValue = QualityOfServiceUtils.DIFF_SERV_NAMES.get(value);
        }
        else {
            try {
                intValue = Integer.parseInt(value);
                if (intValue > 63 || intValue < 0) {
                    throw new IllegalArgumentException("Differentiated Services value: " + intValue + " not in legal range [" + 0 + ", " + 63 + "].");
                }
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("No such Differentiated Services name: " + value);
            }
        }
        return adjustDSCPForECN(intValue);
    }
    
    public static int getToS(final int value) throws IllegalArgumentException {
        if (value > 255 || value < 0) {
            throw new IllegalArgumentException("Type of Service value: " + value + " not in legal range [" + 0 + ", " + 255 + ".");
        }
        return value;
    }
    
    private static int adjustDSCPForECN(final int dscp) throws IllegalArgumentException {
        final Socket socket = new Socket();
        try {
            final int systemTrafficClass = socket.getTrafficClass();
            return dscp << 2 | (systemTrafficClass & 0x3);
        }
        catch (SocketException e) {
            throw new IllegalArgumentException("Setting Differentiated Services not supported: " + e);
        }
    }
    
    static {
        (DIFF_SERV_NAMES = new HashMap<String, Integer>()).put("CS0", 0);
        QualityOfServiceUtils.DIFF_SERV_NAMES.put("CS1", 8);
        QualityOfServiceUtils.DIFF_SERV_NAMES.put("CS2", 16);
        QualityOfServiceUtils.DIFF_SERV_NAMES.put("CS3", 24);
        QualityOfServiceUtils.DIFF_SERV_NAMES.put("CS4", 32);
        QualityOfServiceUtils.DIFF_SERV_NAMES.put("CS5", 40);
        QualityOfServiceUtils.DIFF_SERV_NAMES.put("CS6", 48);
        QualityOfServiceUtils.DIFF_SERV_NAMES.put("CS7", 56);
        QualityOfServiceUtils.DIFF_SERV_NAMES.put("AF11", 10);
        QualityOfServiceUtils.DIFF_SERV_NAMES.put("AF12", 12);
        QualityOfServiceUtils.DIFF_SERV_NAMES.put("AF13", 14);
        QualityOfServiceUtils.DIFF_SERV_NAMES.put("AF21", 18);
        QualityOfServiceUtils.DIFF_SERV_NAMES.put("AF22", 20);
        QualityOfServiceUtils.DIFF_SERV_NAMES.put("AF23", 22);
        QualityOfServiceUtils.DIFF_SERV_NAMES.put("AF31", 26);
        QualityOfServiceUtils.DIFF_SERV_NAMES.put("AF32", 28);
        QualityOfServiceUtils.DIFF_SERV_NAMES.put("AF33", 30);
        QualityOfServiceUtils.DIFF_SERV_NAMES.put("AF41", 34);
        QualityOfServiceUtils.DIFF_SERV_NAMES.put("AF42", 36);
        QualityOfServiceUtils.DIFF_SERV_NAMES.put("AF43", 38);
        QualityOfServiceUtils.DIFF_SERV_NAMES.put("EF", 46);
    }
}
