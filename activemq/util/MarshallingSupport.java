// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.io.UTFDataFormatException;
import java.io.DataOutput;
import java.io.DataInput;
import org.fusesource.hawtbuf.UTF8Buffer;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.io.DataOutputStream;
import java.util.Map;

public final class MarshallingSupport
{
    public static final byte NULL = 0;
    public static final byte BOOLEAN_TYPE = 1;
    public static final byte BYTE_TYPE = 2;
    public static final byte CHAR_TYPE = 3;
    public static final byte SHORT_TYPE = 4;
    public static final byte INTEGER_TYPE = 5;
    public static final byte LONG_TYPE = 6;
    public static final byte DOUBLE_TYPE = 7;
    public static final byte FLOAT_TYPE = 8;
    public static final byte STRING_TYPE = 9;
    public static final byte BYTE_ARRAY_TYPE = 10;
    public static final byte MAP_TYPE = 11;
    public static final byte LIST_TYPE = 12;
    public static final byte BIG_STRING_TYPE = 13;
    
    private MarshallingSupport() {
    }
    
    public static void marshalPrimitiveMap(final Map<String, Object> map, final DataOutputStream out) throws IOException {
        if (map == null) {
            out.writeInt(-1);
        }
        else {
            out.writeInt(map.size());
            for (final String name : map.keySet()) {
                out.writeUTF(name);
                final Object value = map.get(name);
                marshalPrimitive(out, value);
            }
        }
    }
    
    public static Map<String, Object> unmarshalPrimitiveMap(final DataInputStream in) throws IOException {
        return unmarshalPrimitiveMap(in, Integer.MAX_VALUE);
    }
    
    public static Map<String, Object> unmarshalPrimitiveMap(final DataInputStream in, final boolean force) throws IOException {
        return unmarshalPrimitiveMap(in, Integer.MAX_VALUE, force);
    }
    
    public static Map<String, Object> unmarshalPrimitiveMap(final DataInputStream in, final int maxPropertySize) throws IOException {
        return unmarshalPrimitiveMap(in, maxPropertySize, false);
    }
    
    public static Map<String, Object> unmarshalPrimitiveMap(final DataInputStream in, final int maxPropertySize, final boolean force) throws IOException {
        final int size = in.readInt();
        if (size > maxPropertySize) {
            throw new IOException("Primitive map is larger than the allowed size: " + size);
        }
        if (size < 0) {
            return null;
        }
        final Map<String, Object> rc = new HashMap<String, Object>(size);
        for (int i = 0; i < size; ++i) {
            final String name = in.readUTF();
            rc.put(name, unmarshalPrimitive(in, force));
        }
        return rc;
    }
    
    public static void marshalPrimitiveList(final List<Object> list, final DataOutputStream out) throws IOException {
        out.writeInt(list.size());
        for (final Object element : list) {
            marshalPrimitive(out, element);
        }
    }
    
    public static List<Object> unmarshalPrimitiveList(final DataInputStream in) throws IOException {
        return unmarshalPrimitiveList(in, false);
    }
    
    public static List<Object> unmarshalPrimitiveList(final DataInputStream in, final boolean force) throws IOException {
        int size = in.readInt();
        final List<Object> answer = new ArrayList<Object>(size);
        while (size-- > 0) {
            answer.add(unmarshalPrimitive(in, force));
        }
        return answer;
    }
    
    public static void marshalPrimitive(final DataOutputStream out, final Object value) throws IOException {
        if (value == null) {
            marshalNull(out);
        }
        else if (value.getClass() == Boolean.class) {
            marshalBoolean(out, (boolean)value);
        }
        else if (value.getClass() == Byte.class) {
            marshalByte(out, (byte)value);
        }
        else if (value.getClass() == Character.class) {
            marshalChar(out, (char)value);
        }
        else if (value.getClass() == Short.class) {
            marshalShort(out, (short)value);
        }
        else if (value.getClass() == Integer.class) {
            marshalInt(out, (int)value);
        }
        else if (value.getClass() == Long.class) {
            marshalLong(out, (long)value);
        }
        else if (value.getClass() == Float.class) {
            marshalFloat(out, (float)value);
        }
        else if (value.getClass() == Double.class) {
            marshalDouble(out, (double)value);
        }
        else if (value.getClass() == byte[].class) {
            marshalByteArray(out, (byte[])value);
        }
        else if (value.getClass() == String.class) {
            marshalString(out, (String)value);
        }
        else if (value.getClass() == UTF8Buffer.class) {
            marshalString(out, value.toString());
        }
        else if (value instanceof Map) {
            out.writeByte(11);
            marshalPrimitiveMap((Map<String, Object>)value, out);
        }
        else {
            if (!(value instanceof List)) {
                throw new IOException("Object is not a primitive: " + value);
            }
            out.writeByte(12);
            marshalPrimitiveList((List<Object>)value, out);
        }
    }
    
    public static Object unmarshalPrimitive(final DataInputStream in) throws IOException {
        return unmarshalPrimitive(in, false);
    }
    
    public static Object unmarshalPrimitive(final DataInputStream in, final boolean force) throws IOException {
        Object value = null;
        final byte type = in.readByte();
        switch (type) {
            case 2: {
                value = in.readByte();
                break;
            }
            case 1: {
                value = (in.readBoolean() ? Boolean.TRUE : Boolean.FALSE);
                break;
            }
            case 3: {
                value = in.readChar();
                break;
            }
            case 4: {
                value = in.readShort();
                break;
            }
            case 5: {
                value = in.readInt();
                break;
            }
            case 6: {
                value = in.readLong();
                break;
            }
            case 8: {
                value = new Float(in.readFloat());
                break;
            }
            case 7: {
                value = new Double(in.readDouble());
                break;
            }
            case 10: {
                value = new byte[in.readInt()];
                in.readFully((byte[])value);
                break;
            }
            case 9: {
                if (force) {
                    value = in.readUTF();
                    break;
                }
                value = readUTF(in, in.readUnsignedShort());
                break;
            }
            case 13: {
                if (force) {
                    value = readUTF8(in);
                    break;
                }
                value = readUTF(in, in.readInt());
                break;
            }
            case 11: {
                value = unmarshalPrimitiveMap(in, true);
                break;
            }
            case 12: {
                value = unmarshalPrimitiveList(in, true);
                break;
            }
            case 0: {
                value = null;
                break;
            }
            default: {
                throw new IOException("Unknown primitive type: " + type);
            }
        }
        return value;
    }
    
    public static UTF8Buffer readUTF(final DataInputStream in, final int length) throws IOException {
        final byte[] data = new byte[length];
        in.readFully(data);
        return new UTF8Buffer(data);
    }
    
    public static void marshalNull(final DataOutputStream out) throws IOException {
        out.writeByte(0);
    }
    
    public static void marshalBoolean(final DataOutputStream out, final boolean value) throws IOException {
        out.writeByte(1);
        out.writeBoolean(value);
    }
    
    public static void marshalByte(final DataOutputStream out, final byte value) throws IOException {
        out.writeByte(2);
        out.writeByte(value);
    }
    
    public static void marshalChar(final DataOutputStream out, final char value) throws IOException {
        out.writeByte(3);
        out.writeChar(value);
    }
    
    public static void marshalShort(final DataOutputStream out, final short value) throws IOException {
        out.writeByte(4);
        out.writeShort(value);
    }
    
    public static void marshalInt(final DataOutputStream out, final int value) throws IOException {
        out.writeByte(5);
        out.writeInt(value);
    }
    
    public static void marshalLong(final DataOutputStream out, final long value) throws IOException {
        out.writeByte(6);
        out.writeLong(value);
    }
    
    public static void marshalFloat(final DataOutputStream out, final float value) throws IOException {
        out.writeByte(8);
        out.writeFloat(value);
    }
    
    public static void marshalDouble(final DataOutputStream out, final double value) throws IOException {
        out.writeByte(7);
        out.writeDouble(value);
    }
    
    public static void marshalByteArray(final DataOutputStream out, final byte[] value) throws IOException {
        marshalByteArray(out, value, 0, value.length);
    }
    
    public static void marshalByteArray(final DataOutputStream out, final byte[] value, final int offset, final int length) throws IOException {
        out.writeByte(10);
        out.writeInt(length);
        out.write(value, offset, length);
    }
    
    public static void marshalString(final DataOutputStream out, final String s) throws IOException {
        if (s.length() < 8191) {
            out.writeByte(9);
            out.writeUTF(s);
        }
        else {
            out.writeByte(13);
            writeUTF8(out, s);
        }
    }
    
    public static void writeUTF8(final DataOutput dataOut, final String text) throws IOException {
        if (text != null) {
            final int strlen = text.length();
            int utflen = 0;
            final char[] charr = new char[strlen];
            int c = 0;
            int count = 0;
            text.getChars(0, strlen, charr, 0);
            for (int i = 0; i < strlen; ++i) {
                c = charr[i];
                if (c >= 1 && c <= 127) {
                    ++utflen;
                }
                else if (c > 2047) {
                    utflen += 3;
                }
                else {
                    utflen += 2;
                }
            }
            final byte[] bytearr = new byte[utflen + 4];
            bytearr[count++] = (byte)(utflen >>> 24 & 0xFF);
            bytearr[count++] = (byte)(utflen >>> 16 & 0xFF);
            bytearr[count++] = (byte)(utflen >>> 8 & 0xFF);
            bytearr[count++] = (byte)(utflen >>> 0 & 0xFF);
            for (int j = 0; j < strlen; ++j) {
                c = charr[j];
                if (c >= 1 && c <= 127) {
                    bytearr[count++] = (byte)c;
                }
                else if (c > 2047) {
                    bytearr[count++] = (byte)(0xE0 | (c >> 12 & 0xF));
                    bytearr[count++] = (byte)(0x80 | (c >> 6 & 0x3F));
                    bytearr[count++] = (byte)(0x80 | (c >> 0 & 0x3F));
                }
                else {
                    bytearr[count++] = (byte)(0xC0 | (c >> 6 & 0x1F));
                    bytearr[count++] = (byte)(0x80 | (c >> 0 & 0x3F));
                }
            }
            dataOut.write(bytearr);
        }
        else {
            dataOut.writeInt(-1);
        }
    }
    
    public static String readUTF8(final DataInput dataIn) throws IOException {
        final int utflen = dataIn.readInt();
        if (utflen > -1) {
            final StringBuffer str = new StringBuffer(utflen);
            final byte[] bytearr = new byte[utflen];
            int count = 0;
            dataIn.readFully(bytearr, 0, utflen);
            while (count < utflen) {
                final int c = bytearr[count] & 0xFF;
                switch (c >> 4) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7: {
                        ++count;
                        str.append((char)c);
                        continue;
                    }
                    case 12:
                    case 13: {
                        count += 2;
                        if (count > utflen) {
                            throw new UTFDataFormatException();
                        }
                        final int char2 = bytearr[count - 1];
                        if ((char2 & 0xC0) != 0x80) {
                            throw new UTFDataFormatException();
                        }
                        str.append((char)((c & 0x1F) << 6 | (char2 & 0x3F)));
                        continue;
                    }
                    case 14: {
                        count += 3;
                        if (count > utflen) {
                            throw new UTFDataFormatException();
                        }
                        final int char2 = bytearr[count - 2];
                        final int char3 = bytearr[count - 1];
                        if ((char2 & 0xC0) != 0x80 || (char3 & 0xC0) != 0x80) {
                            throw new UTFDataFormatException();
                        }
                        str.append((char)((c & 0xF) << 12 | (char2 & 0x3F) << 6 | (char3 & 0x3F) << 0));
                        continue;
                    }
                    default: {
                        throw new UTFDataFormatException();
                    }
                }
            }
            return new String(str);
        }
        return null;
    }
    
    public static String propertiesToString(final Properties props) throws IOException {
        String result = "";
        if (props != null) {
            final DataByteArrayOutputStream dataOut = new DataByteArrayOutputStream();
            props.store(dataOut, "");
            result = new String(dataOut.getData(), 0, dataOut.size());
            dataOut.close();
        }
        return result;
    }
    
    public static Properties stringToProperties(final String str) throws IOException {
        final Properties result = new Properties();
        if (str != null && str.length() > 0) {
            final DataByteArrayInputStream dataIn = new DataByteArrayInputStream(str.getBytes());
            result.load(dataIn);
            dataIn.close();
        }
        return result;
    }
    
    public static String truncate64(String text) {
        if (text.length() > 63) {
            text = text.substring(0, 45) + "..." + text.substring(text.length() - 12);
        }
        return text;
    }
}
