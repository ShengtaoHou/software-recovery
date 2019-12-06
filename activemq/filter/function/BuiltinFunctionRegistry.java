// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter.function;

import org.apache.activemq.filter.FunctionCallExpression;

public class BuiltinFunctionRegistry
{
    public static void register() {
        FunctionCallExpression.registerFunction("INLIST", new inListFunction());
        FunctionCallExpression.registerFunction("MAKELIST", new makeListFunction());
        FunctionCallExpression.registerFunction("REGEX", new regexMatchFunction());
        FunctionCallExpression.registerFunction("REPLACE", new replaceFunction());
        FunctionCallExpression.registerFunction("SPLIT", new splitFunction());
    }
}
