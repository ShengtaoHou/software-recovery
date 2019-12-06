// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import java.util.regex.Pattern;

public final class JMXSupport
{
    private static final Pattern PART_1;
    private static final Pattern PART_2;
    private static final Pattern PART_3;
    private static final Pattern PART_4;
    
    private JMXSupport() {
    }
    
    public static String encodeObjectNamePart(final String part) {
        String answer = JMXSupport.PART_1.matcher(part).replaceAll("_");
        answer = JMXSupport.PART_2.matcher(answer).replaceAll("&qe;");
        answer = JMXSupport.PART_3.matcher(answer).replaceAll("&amp;");
        answer = JMXSupport.PART_4.matcher(answer).replaceAll("&ast;");
        return answer;
    }
    
    static {
        PART_1 = Pattern.compile("[\\:\\,\\'\\\"]");
        PART_2 = Pattern.compile("\\?");
        PART_3 = Pattern.compile("=");
        PART_4 = Pattern.compile("\\*");
    }
}
