// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf.compiler;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.apache.activemq.protobuf.UTF8Buffer;
import org.apache.activemq.protobuf.Buffer;
import java.io.IOException;
import java.nio.CharBuffer;
import java.math.BigInteger;

public final class TextFormat
{
    private static final int BUFFER_SIZE = 4096;
    
    private static String unsignedToString(final int value) {
        if (value >= 0) {
            return Integer.toString(value);
        }
        return Long.toString((long)value & 0xFFFFFFFFL);
    }
    
    private static String unsignedToString(final long value) {
        if (value >= 0L) {
            return Long.toString(value);
        }
        return BigInteger.valueOf(value & Long.MAX_VALUE).setBit(63).toString();
    }
    
    private static StringBuilder toStringBuilder(final Readable input) throws IOException {
        final StringBuilder text = new StringBuilder();
        final CharBuffer buffer = CharBuffer.allocate(4096);
        while (true) {
            final int n = input.read(buffer);
            if (n == -1) {
                break;
            }
            buffer.flip();
            text.append(buffer, 0, n);
        }
        return text;
    }
    
    static String escapeBytes(final Buffer input) {
        final StringBuilder builder = new StringBuilder(input.getLength());
        for (int i = 0; i < input.getLength(); ++i) {
            final byte b = input.byteAt(i);
            switch (b) {
                case 7: {
                    builder.append("\\a");
                    break;
                }
                case 8: {
                    builder.append("\\b");
                    break;
                }
                case 12: {
                    builder.append("\\f");
                    break;
                }
                case 10: {
                    builder.append("\\n");
                    break;
                }
                case 13: {
                    builder.append("\\r");
                    break;
                }
                case 9: {
                    builder.append("\\t");
                    break;
                }
                case 11: {
                    builder.append("\\v");
                    break;
                }
                case 92: {
                    builder.append("\\\\");
                    break;
                }
                case 39: {
                    builder.append("\\'");
                    break;
                }
                case 34: {
                    builder.append("\\\"");
                    break;
                }
                default: {
                    if (b >= 32) {
                        builder.append((char)b);
                        break;
                    }
                    builder.append('\\');
                    builder.append((char)(48 + (b >>> 6 & 0x3)));
                    builder.append((char)(48 + (b >>> 3 & 0x7)));
                    builder.append((char)(48 + (b & 0x7)));
                    break;
                }
            }
        }
        return builder.toString();
    }
    
    static Buffer unescapeBytes(final CharSequence input) throws InvalidEscapeSequence {
        final byte[] result = new byte[input.length()];
        int pos = 0;
        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            if (c == '\\') {
                if (i + 1 >= input.length()) {
                    throw new InvalidEscapeSequence("Invalid escape sequence: '\\' at end of string.");
                }
                ++i;
                c = input.charAt(i);
                if (isOctal(c)) {
                    int code = digitValue(c);
                    if (i + 1 < input.length() && isOctal(input.charAt(i + 1))) {
                        ++i;
                        code = code * 8 + digitValue(input.charAt(i));
                    }
                    if (i + 1 < input.length() && isOctal(input.charAt(i + 1))) {
                        ++i;
                        code = code * 8 + digitValue(input.charAt(i));
                    }
                    result[pos++] = (byte)code;
                }
                else {
                    switch (c) {
                        case 'a': {
                            result[pos++] = 7;
                            break;
                        }
                        case 'b': {
                            result[pos++] = 8;
                            break;
                        }
                        case 'f': {
                            result[pos++] = 12;
                            break;
                        }
                        case 'n': {
                            result[pos++] = 10;
                            break;
                        }
                        case 'r': {
                            result[pos++] = 13;
                            break;
                        }
                        case 't': {
                            result[pos++] = 9;
                            break;
                        }
                        case 'v': {
                            result[pos++] = 11;
                            break;
                        }
                        case '\\': {
                            result[pos++] = 92;
                            break;
                        }
                        case '\'': {
                            result[pos++] = 39;
                            break;
                        }
                        case '\"': {
                            result[pos++] = 34;
                            break;
                        }
                        case 'x': {
                            int code = 0;
                            if (i + 1 < input.length() && isHex(input.charAt(i + 1))) {
                                ++i;
                                code = digitValue(input.charAt(i));
                                if (i + 1 < input.length() && isHex(input.charAt(i + 1))) {
                                    ++i;
                                    code = code * 16 + digitValue(input.charAt(i));
                                }
                                result[pos++] = (byte)code;
                                break;
                            }
                            throw new InvalidEscapeSequence("Invalid escape sequence: '\\x' with no digits");
                        }
                        default: {
                            throw new InvalidEscapeSequence("Invalid escape sequence: '\\" + c + "'");
                        }
                    }
                }
            }
            else {
                result[pos++] = (byte)c;
            }
        }
        return new Buffer(result, 0, pos);
    }
    
    static String escapeText(final String input) {
        return escapeBytes(new UTF8Buffer(input));
    }
    
    static String unescapeText(final String input) throws InvalidEscapeSequence {
        return new UTF8Buffer(unescapeBytes(input)).toString();
    }
    
    private static boolean isOctal(final char c) {
        return '0' <= c && c <= '7';
    }
    
    private static boolean isHex(final char c) {
        return ('0' <= c && c <= '9') || ('a' <= c && c <= 'f') || ('A' <= c && c <= 'F');
    }
    
    private static int digitValue(final char c) {
        if ('0' <= c && c <= '9') {
            return c - '0';
        }
        if ('a' <= c && c <= 'z') {
            return c - 'a' + 10;
        }
        return c - 'A' + 10;
    }
    
    static int parseInt32(final String text) throws NumberFormatException {
        return (int)parseInteger(text, true, false);
    }
    
    static int parseUInt32(final String text) throws NumberFormatException {
        return (int)parseInteger(text, false, false);
    }
    
    static long parseInt64(final String text) throws NumberFormatException {
        return parseInteger(text, true, true);
    }
    
    static long parseUInt64(final String text) throws NumberFormatException {
        return parseInteger(text, false, true);
    }
    
    private static long parseInteger(final String text, final boolean isSigned, final boolean isLong) throws NumberFormatException {
        int pos = 0;
        boolean negative = false;
        if (text.startsWith("-", pos)) {
            if (!isSigned) {
                throw new NumberFormatException("Number must be positive: " + text);
            }
            ++pos;
            negative = true;
        }
        int radix = 10;
        if (text.startsWith("0x", pos)) {
            pos += 2;
            radix = 16;
        }
        else if (text.startsWith("0", pos)) {
            radix = 8;
        }
        final String numberText = text.substring(pos);
        long result = 0L;
        if (numberText.length() < 16) {
            result = Long.parseLong(numberText, radix);
            if (negative) {
                result = -result;
            }
            if (!isLong) {
                if (isSigned) {
                    if (result > 2147483647L || result < -2147483648L) {
                        throw new NumberFormatException("Number out of range for 32-bit signed integer: " + text);
                    }
                }
                else if (result >= 4294967296L || result < 0L) {
                    throw new NumberFormatException("Number out of range for 32-bit unsigned integer: " + text);
                }
            }
        }
        else {
            BigInteger bigValue = new BigInteger(numberText, radix);
            if (negative) {
                bigValue = bigValue.negate();
            }
            if (!isLong) {
                if (isSigned) {
                    if (bigValue.bitLength() > 31) {
                        throw new NumberFormatException("Number out of range for 32-bit signed integer: " + text);
                    }
                }
                else if (bigValue.bitLength() > 32) {
                    throw new NumberFormatException("Number out of range for 32-bit unsigned integer: " + text);
                }
            }
            else if (isSigned) {
                if (bigValue.bitLength() > 63) {
                    throw new NumberFormatException("Number out of range for 64-bit signed integer: " + text);
                }
            }
            else if (bigValue.bitLength() > 64) {
                throw new NumberFormatException("Number out of range for 64-bit unsigned integer: " + text);
            }
            result = bigValue.longValue();
        }
        return result;
    }
    
    private static final class Tokenizer
    {
        private final CharSequence text;
        private final Matcher matcher;
        private String currentToken;
        private int pos;
        private int line;
        private int column;
        private int previousLine;
        private int previousColumn;
        private static Pattern WHITESPACE;
        private static Pattern TOKEN;
        private static Pattern DOUBLE_INFINITY;
        private static Pattern FLOAT_INFINITY;
        private static Pattern FLOAT_NAN;
        
        public Tokenizer(final CharSequence text) {
            this.pos = 0;
            this.line = 0;
            this.column = 0;
            this.previousLine = 0;
            this.previousColumn = 0;
            this.text = text;
            this.matcher = Tokenizer.WHITESPACE.matcher(text);
            this.skipWhitespace();
            this.nextToken();
        }
        
        public boolean atEnd() {
            return this.currentToken.length() == 0;
        }
        
        public void nextToken() {
            this.previousLine = this.line;
            this.previousColumn = this.column;
            while (this.pos < this.matcher.regionStart()) {
                if (this.text.charAt(this.pos) == '\n') {
                    ++this.line;
                    this.column = 0;
                }
                else {
                    ++this.column;
                }
                ++this.pos;
            }
            if (this.matcher.regionStart() == this.matcher.regionEnd()) {
                this.currentToken = "";
            }
            else {
                this.matcher.usePattern(Tokenizer.TOKEN);
                if (this.matcher.lookingAt()) {
                    this.currentToken = this.matcher.group();
                    this.matcher.region(this.matcher.end(), this.matcher.regionEnd());
                }
                else {
                    this.currentToken = String.valueOf(this.text.charAt(this.pos));
                    this.matcher.region(this.pos + 1, this.matcher.regionEnd());
                }
                this.skipWhitespace();
            }
        }
        
        private void skipWhitespace() {
            this.matcher.usePattern(Tokenizer.WHITESPACE);
            if (this.matcher.lookingAt()) {
                this.matcher.region(this.matcher.end(), this.matcher.regionEnd());
            }
        }
        
        public boolean tryConsume(final String token) {
            if (this.currentToken.equals(token)) {
                this.nextToken();
                return true;
            }
            return false;
        }
        
        public void consume(final String token) throws ParseException {
            if (!this.tryConsume(token)) {
                throw this.parseException("Expected \"" + token + "\".");
            }
        }
        
        public boolean lookingAtInteger() {
            if (this.currentToken.length() == 0) {
                return false;
            }
            final char c = this.currentToken.charAt(0);
            return ('0' <= c && c <= '9') || c == '-' || c == '+';
        }
        
        public String consumeIdentifier() throws ParseException {
            for (int i = 0; i < this.currentToken.length(); ++i) {
                final char c = this.currentToken.charAt(i);
                if (('a' > c || c > 'z') && ('A' > c || c > 'Z') && ('0' > c || c > '9') && c != '_' && c != '.') {
                    throw this.parseException("Expected identifier.");
                }
            }
            final String result = this.currentToken;
            this.nextToken();
            return result;
        }
        
        public int consumeInt32() throws ParseException {
            try {
                final int result = TextFormat.parseInt32(this.currentToken);
                this.nextToken();
                return result;
            }
            catch (NumberFormatException e) {
                throw this.integerParseException(e);
            }
        }
        
        public int consumeUInt32() throws ParseException {
            try {
                final int result = TextFormat.parseUInt32(this.currentToken);
                this.nextToken();
                return result;
            }
            catch (NumberFormatException e) {
                throw this.integerParseException(e);
            }
        }
        
        public long consumeInt64() throws ParseException {
            try {
                final long result = TextFormat.parseInt64(this.currentToken);
                this.nextToken();
                return result;
            }
            catch (NumberFormatException e) {
                throw this.integerParseException(e);
            }
        }
        
        public long consumeUInt64() throws ParseException {
            try {
                final long result = TextFormat.parseUInt64(this.currentToken);
                this.nextToken();
                return result;
            }
            catch (NumberFormatException e) {
                throw this.integerParseException(e);
            }
        }
        
        public double consumeDouble() throws ParseException {
            if (Tokenizer.DOUBLE_INFINITY.matcher(this.currentToken).matches()) {
                final boolean negative = this.currentToken.startsWith("-");
                this.nextToken();
                return negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
            }
            if (this.currentToken.equalsIgnoreCase("nan")) {
                this.nextToken();
                return Double.NaN;
            }
            try {
                final double result = Double.parseDouble(this.currentToken);
                this.nextToken();
                return result;
            }
            catch (NumberFormatException e) {
                throw this.floatParseException(e);
            }
        }
        
        public float consumeFloat() throws ParseException {
            if (Tokenizer.FLOAT_INFINITY.matcher(this.currentToken).matches()) {
                final boolean negative = this.currentToken.startsWith("-");
                this.nextToken();
                return negative ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
            }
            if (Tokenizer.FLOAT_NAN.matcher(this.currentToken).matches()) {
                this.nextToken();
                return Float.NaN;
            }
            try {
                final float result = Float.parseFloat(this.currentToken);
                this.nextToken();
                return result;
            }
            catch (NumberFormatException e) {
                throw this.floatParseException(e);
            }
        }
        
        public boolean consumeBoolean() throws ParseException {
            if (this.currentToken.equals("true")) {
                this.nextToken();
                return true;
            }
            if (this.currentToken.equals("false")) {
                this.nextToken();
                return false;
            }
            throw this.parseException("Expected \"true\" or \"false\".");
        }
        
        public String consumeString() throws ParseException {
            return new UTF8Buffer(this.consumeBuffer()).toString();
        }
        
        public Buffer consumeBuffer() throws ParseException {
            final char quote = (this.currentToken.length() > 0) ? this.currentToken.charAt(0) : '\0';
            if (quote != '\"' && quote != '\'') {
                throw this.parseException("Expected string.");
            }
            if (this.currentToken.length() < 2 || this.currentToken.charAt(this.currentToken.length() - 1) != quote) {
                throw this.parseException("String missing ending quote.");
            }
            try {
                final String escaped = this.currentToken.substring(1, this.currentToken.length() - 1);
                final Buffer result = TextFormat.unescapeBytes(escaped);
                this.nextToken();
                return result;
            }
            catch (InvalidEscapeSequence e) {
                throw this.parseException(e.getMessage());
            }
        }
        
        public ParseException parseException(final String description) {
            return new ParseException(this.line + 1 + ":" + (this.column + 1) + ": " + description);
        }
        
        public ParseException parseExceptionPreviousToken(final String description) {
            return new ParseException(this.previousLine + 1 + ":" + (this.previousColumn + 1) + ": " + description);
        }
        
        private ParseException integerParseException(final NumberFormatException e) {
            return this.parseException("Couldn't parse integer: " + e.getMessage());
        }
        
        private ParseException floatParseException(final NumberFormatException e) {
            return this.parseException("Couldn't parse number: " + e.getMessage());
        }
        
        static {
            Tokenizer.WHITESPACE = Pattern.compile("(\\s|(#.*$))+", 8);
            Tokenizer.TOKEN = Pattern.compile("[a-zA-Z_][0-9a-zA-Z_+-]*|[0-9+-][0-9a-zA-Z_.+-]*|\"([^\"\n\\\\]|\\\\.)*(\"|\\\\?$)|'([^\"\n\\\\]|\\\\.)*('|\\\\?$)", 8);
            Tokenizer.DOUBLE_INFINITY = Pattern.compile("-?inf(inity)?", 2);
            Tokenizer.FLOAT_INFINITY = Pattern.compile("-?inf(inity)?f?", 2);
            Tokenizer.FLOAT_NAN = Pattern.compile("nanf?", 2);
        }
    }
    
    public static class ParseException extends IOException
    {
        public ParseException(final String message) {
            super(message);
        }
    }
    
    static class InvalidEscapeSequence extends IOException
    {
        public InvalidEscapeSequence(final String description) {
            super(description);
        }
    }
}
