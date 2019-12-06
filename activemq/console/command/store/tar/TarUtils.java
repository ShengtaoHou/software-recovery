// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.command.store.tar;

public class TarUtils
{
    private static final int BYTE_MASK = 255;
    
    public static long parseOctal(final byte[] header, final int offset, final int length) {
        long result = 0L;
        boolean stillPadding = true;
        for (int end = offset + length, i = offset; i < end; ++i) {
            if (header[i] == 0) {
                break;
            }
            if (header[i] == 32 || header[i] == 48) {
                if (stillPadding) {
                    continue;
                }
                if (header[i] == 32) {
                    break;
                }
            }
            stillPadding = false;
            result = (result << 3) + (header[i] - 48);
        }
        return result;
    }
    
    public static StringBuffer parseName(final byte[] header, final int offset, final int length) {
        final StringBuffer result = new StringBuffer(length);
        for (int end = offset + length, i = offset; i < end && header[i] != 0; ++i) {
            result.append((char)header[i]);
        }
        return result;
    }
    
    public static int getNameBytes(final StringBuffer name, final byte[] buf, final int offset, final int length) {
        int i;
        for (i = 0; i < length && i < name.length(); ++i) {
            buf[offset + i] = (byte)name.charAt(i);
        }
        while (i < length) {
            buf[offset + i] = 0;
            ++i;
        }
        return offset + length;
    }
    
    public static int getOctalBytes(final long value, final byte[] buf, final int offset, final int length) {
        int idx = length - 1;
        buf[offset + idx] = 0;
        --idx;
        buf[offset + idx] = 32;
        --idx;
        if (value == 0L) {
            buf[offset + idx] = 48;
            --idx;
        }
        else {
            for (long val = value; idx >= 0 && val > 0L; val >>= 3, --idx) {
                buf[offset + idx] = (byte)(48 + (byte)(val & 0x7L));
            }
        }
        while (idx >= 0) {
            buf[offset + idx] = 32;
            --idx;
        }
        return offset + length;
    }
    
    public static int getLongOctalBytes(final long value, final byte[] buf, final int offset, final int length) {
        final byte[] temp = new byte[length + 1];
        getOctalBytes(value, temp, 0, length + 1);
        System.arraycopy(temp, 0, buf, offset, length);
        return offset + length;
    }
    
    public static int getCheckSumOctalBytes(final long value, final byte[] buf, final int offset, final int length) {
        getOctalBytes(value, buf, offset, length);
        buf[offset + length - 1] = 32;
        buf[offset + length - 2] = 0;
        return offset + length;
    }
    
    public static long computeCheckSum(final byte[] buf) {
        long sum = 0L;
        for (int i = 0; i < buf.length; ++i) {
            sum += (0xFF & buf[i]);
        }
        return sum;
    }
}
