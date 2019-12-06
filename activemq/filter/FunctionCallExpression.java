// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter;

import org.apache.activemq.filter.function.BuiltinFunctionRegistry;
import java.util.Iterator;
import javax.jms.JMSException;
import java.util.Collection;
import java.util.List;
import org.apache.activemq.filter.function.FilterFunction;
import java.util.ArrayList;
import java.util.HashMap;

public class FunctionCallExpression implements Expression
{
    protected static final HashMap<String, functionRegistration> functionRegistry;
    protected String functionName;
    protected ArrayList arguments;
    protected FilterFunction filterFunc;
    
    public static boolean registerFunction(final String name, final FilterFunction impl) {
        boolean result = true;
        synchronized (FunctionCallExpression.functionRegistry) {
            if (FunctionCallExpression.functionRegistry.containsKey(name)) {
                result = false;
            }
            else {
                FunctionCallExpression.functionRegistry.put(name, new functionRegistration(impl));
            }
        }
        return result;
    }
    
    public static void deregisterFunction(final String name) {
        synchronized (FunctionCallExpression.functionRegistry) {
            FunctionCallExpression.functionRegistry.remove(name);
        }
    }
    
    protected FunctionCallExpression(final String func_name, final List<Expression> args) throws invalidFunctionExpressionException {
        final functionRegistration func_reg;
        synchronized (FunctionCallExpression.functionRegistry) {
            func_reg = FunctionCallExpression.functionRegistry.get(func_name);
        }
        if (func_reg != null) {
            (this.arguments = new ArrayList()).addAll(args);
            this.functionName = func_name;
            this.filterFunc = func_reg.getFilterFunction();
            return;
        }
        throw new invalidFunctionExpressionException("invalid function name, \"" + func_name + "\"");
    }
    
    public static FunctionCallExpression createFunctionCall(final String func_name, final List<Expression> args) throws invalidFunctionExpressionException {
        FunctionCallExpression result = new FunctionCallExpression(func_name, args);
        if (result.filterFunc.isValid(result)) {
            if (result.filterFunc.returnsBoolean(result)) {
                result = new BooleanFunctionCallExpr(func_name, args);
            }
            return result;
        }
        throw new invalidFunctionExpressionException("invalid call of function " + func_name);
    }
    
    public int getNumArguments() {
        return this.arguments.size();
    }
    
    public Expression getArgument(final int which) {
        return this.arguments.get(which);
    }
    
    @Override
    public Object evaluate(final MessageEvaluationContext message_ctx) throws JMSException {
        return this.filterFunc.evaluate(this, message_ctx);
    }
    
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append(this.functionName);
        result.append("(");
        boolean first_f = true;
        for (final Object arg : this.arguments) {
            if (first_f) {
                first_f = false;
            }
            else {
                result.append(", ");
            }
            result.append(arg.toString());
        }
        result.append(")");
        return result.toString();
    }
    
    static {
        functionRegistry = new HashMap<String, functionRegistration>();
        BuiltinFunctionRegistry.register();
    }
    
    protected static class functionRegistration
    {
        protected FilterFunction filterFunction;
        
        public functionRegistration(final FilterFunction func) {
            this.filterFunction = func;
        }
        
        public FilterFunction getFilterFunction() {
            return this.filterFunction;
        }
        
        public void setFilterFunction(final FilterFunction func) {
            this.filterFunction = func;
        }
    }
    
    public static class invalidFunctionExpressionException extends Exception
    {
        public invalidFunctionExpressionException(final String msg) {
            super(msg);
        }
        
        public invalidFunctionExpressionException(final String msg, final Throwable cause) {
            super(msg, cause);
        }
    }
}
