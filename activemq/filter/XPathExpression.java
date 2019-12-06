// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter;

import org.apache.activemq.command.Message;
import org.slf4j.LoggerFactory;
import javax.jms.JMSException;
import java.io.IOException;
import org.apache.activemq.util.JMSExceptionSupport;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;
import org.slf4j.Logger;

public final class XPathExpression implements BooleanExpression
{
    private static final Logger LOG;
    private static final String EVALUATOR_SYSTEM_PROPERTY = "org.apache.activemq.XPathEvaluatorClassName";
    private static final String DEFAULT_EVALUATOR_CLASS_NAME = "org.apache.activemq.filter.XalanXPathEvaluator";
    private static final Constructor EVALUATOR_CONSTRUCTOR;
    private final String xpath;
    private final XPathEvaluator evaluator;
    
    XPathExpression(final String xpath) {
        this.xpath = xpath;
        this.evaluator = this.createEvaluator(xpath);
    }
    
    private static Constructor getXPathEvaluatorConstructor(final String cn) throws ClassNotFoundException, SecurityException, NoSuchMethodException {
        final Class c = XPathExpression.class.getClassLoader().loadClass(cn);
        if (!XPathEvaluator.class.isAssignableFrom(c)) {
            throw new ClassCastException("" + c + " is not an instance of " + XPathEvaluator.class);
        }
        return c.getConstructor(String.class);
    }
    
    private XPathEvaluator createEvaluator(final String xpath2) {
        try {
            return XPathExpression.EVALUATOR_CONSTRUCTOR.newInstance(this.xpath);
        }
        catch (InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            throw new RuntimeException("Invalid XPath Expression: " + this.xpath + " reason: " + e.getMessage(), e);
        }
        catch (Throwable e2) {
            throw new RuntimeException("Invalid XPath Expression: " + this.xpath + " reason: " + e2.getMessage(), e2);
        }
    }
    
    @Override
    public Object evaluate(final MessageEvaluationContext message) throws JMSException {
        try {
            if (message.isDropped()) {
                return null;
            }
            return this.evaluator.evaluate(message.getMessage()) ? Boolean.TRUE : Boolean.FALSE;
        }
        catch (IOException e) {
            throw JMSExceptionSupport.create(e);
        }
    }
    
    @Override
    public String toString() {
        return "XPATH " + ConstantExpression.encodeString(this.xpath);
    }
    
    @Override
    public boolean matches(final MessageEvaluationContext message) throws JMSException {
        final Object object = this.evaluate(message);
        return object != null && object == Boolean.TRUE;
    }
    
    static {
        LOG = LoggerFactory.getLogger(XPathExpression.class);
        String cn = System.getProperty("org.apache.activemq.XPathEvaluatorClassName", "org.apache.activemq.filter.XalanXPathEvaluator");
        Constructor m = null;
        try {
            m = getXPathEvaluatorConstructor(cn);
        }
        catch (Throwable e) {
            XPathExpression.LOG.warn("Invalid " + XPathEvaluator.class.getName() + " implementation: " + cn + ", reason: " + e, e);
            cn = "org.apache.activemq.filter.XalanXPathEvaluator";
            try {
                m = getXPathEvaluatorConstructor(cn);
            }
            catch (Throwable e2) {
                XPathExpression.LOG.error("Default XPath evaluator could not be loaded", e);
            }
        }
        finally {
            EVALUATOR_CONSTRUCTOR = m;
        }
    }
    
    public interface XPathEvaluator
    {
        boolean evaluate(final Message p0) throws JMSException;
    }
}
