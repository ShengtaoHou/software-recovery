// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf.compiler.parser;

public interface ProtoParserConstants
{
    public static final int EOF = 0;
    public static final int IMPORT = 8;
    public static final int PACKAGE = 9;
    public static final int SERVICE = 10;
    public static final int RPC = 11;
    public static final int OPTION = 12;
    public static final int MESSAGE = 13;
    public static final int EXTENSIONS = 14;
    public static final int EXTEND = 15;
    public static final int ENUM = 16;
    public static final int GROUP = 17;
    public static final int REQURIED = 18;
    public static final int OPTIONAL = 19;
    public static final int REPEATED = 20;
    public static final int RETURNS = 21;
    public static final int TO = 22;
    public static final int MAX = 23;
    public static final int LBRACE = 24;
    public static final int RBRACE = 25;
    public static final int EQUALS = 26;
    public static final int SEMICOLON = 27;
    public static final int LBRACKET = 28;
    public static final int RBRACKET = 29;
    public static final int LPAREN = 30;
    public static final int RPAREN = 31;
    public static final int PERIOD = 32;
    public static final int COMMA = 33;
    public static final int INTEGER = 34;
    public static final int DECIMAL_LITERAL = 35;
    public static final int HEX_LITERAL = 36;
    public static final int OCTAL_LITERAL = 37;
    public static final int FLOAT = 38;
    public static final int EXPONENT = 39;
    public static final int STRING = 40;
    public static final int ID = 41;
    public static final int DEFAULT = 0;
    public static final int COMMENT = 1;
    public static final String[] tokenImage = { "<EOF>", "\" \"", "\"\\t\"", "\"\\n\"", "\"\\r\"", "\"//\"", "<token of kind 6>", "<token of kind 7>", "\"import\"", "\"package\"", "\"service\"", "\"rpc\"", "\"option\"", "\"message\"", "\"extensions\"", "\"extend\"", "\"enum\"", "\"group\"", "\"required\"", "\"optional\"", "\"repeated\"", "\"returns\"", "\"to\"", "\"max\"", "\"{\"", "\"}\"", "\"=\"", "\";\"", "\"[\"", "\"]\"", "\"(\"", "\")\"", "\".\"", "\",\"", "<INTEGER>", "<DECIMAL_LITERAL>", "<HEX_LITERAL>", "<OCTAL_LITERAL>", "<FLOAT>", "<EXPONENT>", "<STRING>", "<ID>" };
}
