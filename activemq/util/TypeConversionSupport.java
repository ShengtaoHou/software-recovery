// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import java.net.URISyntaxException;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.Date;
import java.math.BigInteger;
import java.net.URI;
import org.fusesource.hawtbuf.UTF8Buffer;
import java.util.HashMap;
import java.util.Map;

public final class TypeConversionSupport
{
    private static final Converter IDENTITY_CONVERTER;
    private static final Map<ConversionKey, Converter> CONVERSION_MAP;
    
    private TypeConversionSupport() {
    }
    
    public static Object convert(final Object value, final Class<?> to) {
        if (value == null) {
            if (Boolean.TYPE.isAssignableFrom(to)) {
                return Boolean.FALSE;
            }
            return null;
        }
        else {
            if (to.isInstance(value)) {
                return to.cast(value);
            }
            final Converter c = lookupConverter(value.getClass(), to);
            if (c != null) {
                return c.convert(value);
            }
            return null;
        }
    }
    
    public static Converter lookupConverter(Class<?> from, Class<?> to) {
        if (from.isPrimitive()) {
            from = convertPrimitiveTypeToWrapperType(from);
        }
        if (to.isPrimitive()) {
            to = convertPrimitiveTypeToWrapperType(to);
        }
        if (from.equals(to)) {
            return TypeConversionSupport.IDENTITY_CONVERTER;
        }
        return TypeConversionSupport.CONVERSION_MAP.get(new ConversionKey(from, to));
    }
    
    private static Class<?> convertPrimitiveTypeToWrapperType(final Class<?> type) {
        Class<?> rc = type;
        if (type.isPrimitive()) {
            if (type == Integer.TYPE) {
                rc = Integer.class;
            }
            else if (type == Long.TYPE) {
                rc = Long.class;
            }
            else if (type == Double.TYPE) {
                rc = Double.class;
            }
            else if (type == Float.TYPE) {
                rc = Float.class;
            }
            else if (type == Short.TYPE) {
                rc = Short.class;
            }
            else if (type == Byte.TYPE) {
                rc = Byte.class;
            }
            else if (type == Boolean.TYPE) {
                rc = Boolean.class;
            }
        }
        return rc;
    }
    
    static {
        IDENTITY_CONVERTER = new Converter() {
            @Override
            public Object convert(final Object value) {
                return value;
            }
        };
        CONVERSION_MAP = new HashMap<ConversionKey, Converter>();
        final Converter toStringConverter = new Converter() {
            @Override
            public Object convert(final Object value) {
                return value.toString();
            }
        };
        TypeConversionSupport.CONVERSION_MAP.put(new ConversionKey(Boolean.class, String.class), toStringConverter);
        TypeConversionSupport.CONVERSION_MAP.put(new ConversionKey(Byte.class, String.class), toStringConverter);
        TypeConversionSupport.CONVERSION_MAP.put(new ConversionKey(Short.class, String.class), toStringConverter);
        TypeConversionSupport.CONVERSION_MAP.put(new ConversionKey(Integer.class, String.class), toStringConverter);
        TypeConversionSupport.CONVERSION_MAP.put(new ConversionKey(Long.class, String.class), toStringConverter);
        TypeConversionSupport.CONVERSION_MAP.put(new ConversionKey(Float.class, String.class), toStringConverter);
        TypeConversionSupport.CONVERSION_MAP.put(new ConversionKey(Double.class, String.class), toStringConverter);
        TypeConversionSupport.CONVERSION_MAP.put(new ConversionKey(UTF8Buffer.class, String.class), toStringConverter);
        TypeConversionSupport.CONVERSION_MAP.put(new ConversionKey(URI.class, String.class), toStringConverter);
        TypeConversionSupport.CONVERSION_MAP.put(new ConversionKey(BigInteger.class, String.class), toStringConverter);
        TypeConversionSupport.CONVERSION_MAP.put(new ConversionKey(String.class, Boolean.class), new Converter() {
            @Override
            public Object convert(final Object value) {
                return Boolean.valueOf((String)value);
            }
        });
        TypeConversionSupport.CONVERSION_MAP.put(new ConversionKey(String.class, Byte.class), new Converter() {
            @Override
            public Object convert(final Object value) {
                return Byte.valueOf((String)value);
            }
        });
        TypeConversionSupport.CONVERSION_MAP.put(new ConversionKey(String.class, Short.class), new Converter() {
            @Override
            public Object convert(final Object value) {
                return Short.valueOf((String)value);
            }
        });
        TypeConversionSupport.CONVERSION_MAP.put(new ConversionKey(String.class, Integer.class), new Converter() {
            @Override
            public Object convert(final Object value) {
                return Integer.valueOf((String)value);
            }
        });
        TypeConversionSupport.CONVERSION_MAP.put(new ConversionKey(String.class, Long.class), new Converter() {
            @Override
            public Object convert(final Object value) {
                return Long.valueOf((String)value);
            }
        });
        TypeConversionSupport.CONVERSION_MAP.put(new ConversionKey(String.class, Float.class), new Converter() {
            @Override
            public Object convert(final Object value) {
                return Float.valueOf((String)value);
            }
        });
        TypeConversionSupport.CONVERSION_MAP.put(new ConversionKey(String.class, Double.class), new Converter() {
            @Override
            public Object convert(final Object value) {
                return Double.valueOf((String)value);
            }
        });
        final Converter longConverter = new Converter() {
            @Override
            public Object convert(final Object value) {
                return ((Number)value).longValue();
            }
        };
        TypeConversionSupport.CONVERSION_MAP.put(new ConversionKey(Byte.class, Long.class), longConverter);
        TypeConversionSupport.CONVERSION_MAP.put(new ConversionKey(Short.class, Long.class), longConverter);
        TypeConversionSupport.CONVERSION_MAP.put(new ConversionKey(Integer.class, Long.class), longConverter);
        TypeConversionSupport.CONVERSION_MAP.put(new ConversionKey(Date.class, Long.class), new Converter() {
            @Override
            public Object convert(final Object value) {
                return ((Date)value).getTime();
            }
        });
        final Converter intConverter = new Converter() {
            @Override
            public Object convert(final Object value) {
                return ((Number)value).intValue();
            }
        };
        TypeConversionSupport.CONVERSION_MAP.put(new ConversionKey(Byte.class, Integer.class), intConverter);
        TypeConversionSupport.CONVERSION_MAP.put(new ConversionKey(Short.class, Integer.class), intConverter);
        TypeConversionSupport.CONVERSION_MAP.put(new ConversionKey(Byte.class, Short.class), new Converter() {
            @Override
            public Object convert(final Object value) {
                return ((Number)value).shortValue();
            }
        });
        TypeConversionSupport.CONVERSION_MAP.put(new ConversionKey(Float.class, Double.class), new Converter() {
            @Override
            public Object convert(final Object value) {
                return new Double(((Number)value).doubleValue());
            }
        });
        TypeConversionSupport.CONVERSION_MAP.put(new ConversionKey(String.class, ActiveMQDestination.class), new Converter() {
            @Override
            public Object convert(final Object value) {
                return ActiveMQDestination.createDestination((String)value, (byte)1);
            }
        });
        TypeConversionSupport.CONVERSION_MAP.put(new ConversionKey(String.class, URI.class), new Converter() {
            @Override
            public Object convert(final Object value) {
                final String text = value.toString();
                try {
                    return new URI(text);
                }
                catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    
    private static class ConversionKey
    {
        final Class<?> from;
        final Class<?> to;
        final int hashCode;
        
        public ConversionKey(final Class<?> from, final Class<?> to) {
            this.from = from;
            this.to = to;
            this.hashCode = (from.hashCode() ^ to.hashCode() << 1);
        }
        
        @Override
        public boolean equals(final Object o) {
            final ConversionKey x = (ConversionKey)o;
            return x.from == this.from && x.to == this.to;
        }
        
        @Override
        public int hashCode() {
            return this.hashCode;
        }
    }
    
    public interface Converter
    {
        Object convert(final Object p0);
    }
}
