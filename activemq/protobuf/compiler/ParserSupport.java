// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf.compiler;

import org.apache.activemq.protobuf.compiler.parser.ParseException;
import org.apache.activemq.protobuf.compiler.parser.Token;

public class ParserSupport
{
    public static String decodeString(final Token token) throws ParseException {
        try {
            return TextFormat.unescapeText(token.image.substring(1, token.image.length() - 1));
        }
        catch (TextFormat.InvalidEscapeSequence e) {
            throw new ParseException("Invalid string litteral at line " + token.next.beginLine + ", column " + token.next.beginColumn + ": " + e.getMessage());
        }
    }
}
