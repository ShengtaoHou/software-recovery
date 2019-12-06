// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import org.slf4j.LoggerFactory;
import java.util.HashMap;
import javax.management.ReflectionException;
import javax.management.MBeanException;
import org.apache.activemq.broker.util.AuditLogEntry;
import java.util.Iterator;
import org.apache.activemq.broker.util.JMXAuditLogEntry;
import java.security.Principal;
import javax.security.auth.Subject;
import java.security.AccessController;
import java.lang.annotation.Annotation;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanOperationInfo;
import java.lang.reflect.Method;
import javax.management.MBeanAttributeInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import org.apache.activemq.broker.util.AuditLogService;
import org.slf4j.Logger;
import java.util.Map;
import javax.management.StandardMBean;

public class AnnotatedMBean extends StandardMBean
{
    private static final Map<String, Class<?>> primitives;
    private static final Logger LOG;
    private static boolean audit;
    private static AuditLogService auditLog;
    
    public static void registerMBean(final ManagementContext context, final Object object, final ObjectName objectName) throws Exception {
        final String mbeanName = object.getClass().getName() + "MBean";
        for (final Class c : object.getClass().getInterfaces()) {
            if (mbeanName.equals(c.getName())) {
                context.registerMBean(new AnnotatedMBean(object, c), objectName);
                return;
            }
        }
        context.registerMBean(object, objectName);
    }
    
    public <T> AnnotatedMBean(final T impl, final Class<T> mbeanInterface) throws NotCompliantMBeanException {
        super(impl, mbeanInterface);
    }
    
    protected AnnotatedMBean(final Class<?> mbeanInterface) throws NotCompliantMBeanException {
        super(mbeanInterface);
    }
    
    @Override
    protected String getDescription(final MBeanAttributeInfo info) {
        String descr = info.getDescription();
        Method m = getMethod(this.getMBeanInterface(), "get" + info.getName().substring(0, 1).toUpperCase() + info.getName().substring(1), new String[0]);
        if (m == null) {
            m = getMethod(this.getMBeanInterface(), "is" + info.getName().substring(0, 1).toUpperCase() + info.getName().substring(1), new String[0]);
        }
        if (m == null) {
            m = getMethod(this.getMBeanInterface(), "does" + info.getName().substring(0, 1).toUpperCase() + info.getName().substring(1), new String[0]);
        }
        if (m != null) {
            final MBeanInfo d = m.getAnnotation(MBeanInfo.class);
            if (d != null) {
                descr = d.value();
            }
        }
        return descr;
    }
    
    @Override
    protected String getDescription(final MBeanOperationInfo op) {
        String descr = op.getDescription();
        final Method m = this.getMethod(op);
        if (m != null) {
            final MBeanInfo d = m.getAnnotation(MBeanInfo.class);
            if (d != null) {
                descr = d.value();
            }
        }
        return descr;
    }
    
    @Override
    protected String getParameterName(final MBeanOperationInfo op, final MBeanParameterInfo param, final int paramNo) {
        String name = param.getName();
        final Method m = this.getMethod(op);
        if (m != null) {
            for (final Annotation a : m.getParameterAnnotations()[paramNo]) {
                if (MBeanInfo.class.isInstance(a)) {
                    name = MBeanInfo.class.cast(a).value();
                }
            }
        }
        return name;
    }
    
    private Method getMethod(final MBeanOperationInfo op) {
        final MBeanParameterInfo[] params = op.getSignature();
        final String[] paramTypes = new String[params.length];
        for (int i = 0; i < params.length; ++i) {
            paramTypes[i] = params[i].getType();
        }
        return getMethod(this.getMBeanInterface(), op.getName(), paramTypes);
    }
    
    private static Method getMethod(final Class<?> mbean, final String method, final String... params) {
        try {
            final ClassLoader loader = mbean.getClassLoader();
            final Class<?>[] paramClasses = (Class<?>[])new Class[params.length];
            for (int i = 0; i < params.length; ++i) {
                paramClasses[i] = AnnotatedMBean.primitives.get(params[i]);
                if (paramClasses[i] == null) {
                    paramClasses[i] = Class.forName(params[i], false, loader);
                }
            }
            return mbean.getMethod(method, paramClasses);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e2) {
            return null;
        }
    }
    
    @Override
    public Object invoke(final String s, final Object[] objects, final String[] strings) throws MBeanException, ReflectionException {
        if (AnnotatedMBean.audit) {
            final Subject subject = Subject.getSubject(AccessController.getContext());
            String caller = "anonymous";
            if (subject != null) {
                caller = "";
                for (final Principal principal : subject.getPrincipals()) {
                    caller = caller + principal.getName() + " ";
                }
            }
            final AuditLogEntry entry = new JMXAuditLogEntry();
            entry.setUser(caller);
            entry.setTimestamp(System.currentTimeMillis());
            entry.setOperation(this.getMBeanInfo().getClassName() + "." + s);
            entry.getParameters().put("arguments", objects);
            AnnotatedMBean.auditLog.log(entry);
        }
        return super.invoke(s, objects, strings);
    }
    
    static {
        primitives = new HashMap<String, Class<?>>();
        LOG = LoggerFactory.getLogger("org.apache.activemq.audit");
        final Class[] array;
        final Class<?>[] p = (Class<?>[])(array = new Class[] { Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Character.TYPE, Boolean.TYPE });
        for (final Class<?> c : array) {
            AnnotatedMBean.primitives.put(c.getName(), c);
        }
        AnnotatedMBean.audit = "true".equalsIgnoreCase(System.getProperty("org.apache.activemq.audit"));
        if (AnnotatedMBean.audit) {
            AnnotatedMBean.auditLog = AuditLogService.getAuditLog();
        }
    }
}
