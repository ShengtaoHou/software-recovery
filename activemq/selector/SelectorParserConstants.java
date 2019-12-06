// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.selector;

public interface SelectorParserConstants
{
    public static final int EOF = 0;
    public static final int LINE_COMMENT = 6;
    public static final int BLOCK_COMMENT = 7;
    public static final int NOT = 8;
    public static final int AND = 9;
    public static final int OR = 10;
    public static final int BETWEEN = 11;
    public static final int LIKE = 12;
    public static final int ESCAPE = 13;
    public static final int IN = 14;
    public static final int IS = 15;
    public static final int TRUE = 16;
    public static final int FALSE = 17;
    public static final int NULL = 18;
    public static final int XPATH = 19;
    public static final int XQUERY = 20;
    public static final int DECIMAL_LITERAL = 21;
    public static final int HEX_LITERAL = 22;
    public static final int OCTAL_LITERAL = 23;
    public static final int FLOATING_POINT_LITERAL = 24;
    public static final int EXPONENT = 25;
    public static final int STRING_LITERAL = 26;
    public static final int ID = 27;
    public static final int DEFAULT = 0;
    public static final String[] tokenImage = { "<EOF>", "\" \"", "\"\\t\"", "\"\\n\"", "\"\\r\"", "\"\\f\"", "<LINE_COMMENT>", "<BLOCK_COMMENT>", "\"NOT\"", "\"AND\"", "\"OR\"", "\"BETWEEN\"", "\"LIKE\"", "\"ESCAPE\"", "\"IN\"", "\"IS\"", "\"TRUE\"", "\"FALSE\"", "\"NULL\"", "\"XPATH\"", "\"XQUERY\"", "<DECIMAL_LITERAL>", "<HEX_LITERAL>", "<OCTAL_LITERAL>", "<FLOATING_POINT_LITERAL>", "<EXPONENT>", "<STRING_LITERAL>", "<ID>", "\"=\"", "\"<>\"", "\">\"", "\">=\"", "\"<\"", "\"<=\"", "\"(\"", "\",\"", "\")\"", "\"+\"", "\"-\"", "\"*\"", "\"/\"", "\"%\"" };
}
