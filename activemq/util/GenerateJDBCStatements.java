// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.apache.activemq.store.jdbc.Statements;

public class GenerateJDBCStatements
{
    public static String returnStatement(final Object statement) {
        return ((String)statement).replace("<", "&lt;").replace(">", "&gt;");
    }
    
    public static void main(final String[] args) throws Exception {
        final Statements s = new Statements();
        s.setTablePrefix("ACTIVEMQ.");
        final String[] stats = s.getCreateSchemaStatements();
        System.out.println("<bean id=\"statements\" class=\"org.apache.activemq.store.jdbc.Statements\">");
        System.out.println("<property name=\"createSchemaStatements\">");
        System.out.println("<list>");
        for (int i = 0; i < stats.length; ++i) {
            System.out.println("<value>" + stats[i] + "</value>");
        }
        System.out.println("</list>");
        System.out.println("</property>");
        final Method[] methods = Statements.class.getMethods();
        final Pattern sPattern = Pattern.compile("get.*Statement$");
        final Pattern setPattern = Pattern.compile("set.*Statement$");
        final ArrayList<String> setMethods = new ArrayList<String>();
        for (int j = 0; j < methods.length; ++j) {
            if (setPattern.matcher(methods[j].getName()).find()) {
                setMethods.add(methods[j].getName());
            }
        }
        for (int j = 0; j < methods.length; ++j) {
            if (sPattern.matcher(methods[j].getName()).find() && setMethods.contains(methods[j].getName().replace("get", "set"))) {
                System.out.println("<property name=\"" + methods[j].getName().substring(3, 4).toLowerCase(Locale.ENGLISH) + methods[j].getName().substring(4) + "\" value=\"" + returnStatement(methods[j].invoke(s, (Object[])null)) + "\" />");
            }
        }
        final Pattern sPattern2 = Pattern.compile("get.*Statment$");
        for (int k = 0; k < methods.length; ++k) {
            if (sPattern2.matcher(methods[k].getName()).find()) {
                System.out.println("<property name=\"" + methods[k].getName().substring(3, 4).toLowerCase(Locale.ENGLISH) + methods[k].getName().substring(4) + "\" value=\"" + returnStatement(methods[k].invoke(s, (Object[])null)) + "\" />");
            }
        }
        final String[] statsDrop = s.getDropSchemaStatements();
        System.out.println("<property name=\"dropSchemaStatements\">");
        System.out.println("<list>");
        for (int l = 0; l < statsDrop.length; ++l) {
            System.out.println("<value>" + statsDrop[l] + "</value>");
        }
        System.out.println("</list>");
        System.out.println("</property>");
        System.out.println("</bean>");
    }
}
