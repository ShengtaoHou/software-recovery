// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.MBeanException;
import javax.management.NotCompliantMBeanException;
import java.util.concurrent.ExecutorService;

public class AsyncAnnotatedMBean extends AnnotatedMBean
{
    private ExecutorService executor;
    private long timeout;
    
    public <T> AsyncAnnotatedMBean(final ExecutorService executor, final long timeout, final T impl, final Class<T> mbeanInterface) throws NotCompliantMBeanException {
        super(impl, mbeanInterface);
        this.timeout = 0L;
        this.executor = executor;
        this.timeout = timeout;
    }
    
    protected AsyncAnnotatedMBean(final Class<?> mbeanInterface) throws NotCompliantMBeanException {
        super(mbeanInterface);
        this.timeout = 0L;
    }
    
    protected Object asyncInvole(final String s, final Object[] objects, final String[] strings) throws MBeanException, ReflectionException {
        return super.invoke(s, objects, strings);
    }
    
    public static void registerMBean(final ExecutorService executor, final long timeout, final ManagementContext context, final Object object, final ObjectName objectName) throws Exception {
        if (timeout < 0L && executor != null) {
            throw new IllegalArgumentException("async timeout cannot be negative.");
        }
        if (timeout > 0L && executor == null) {
            throw new NullPointerException("timeout given but no ExecutorService instance given.");
        }
        final String mbeanName = object.getClass().getName() + "MBean";
        for (final Class c : object.getClass().getInterfaces()) {
            if (mbeanName.equals(c.getName())) {
                if (timeout == 0L) {
                    context.registerMBean(new AnnotatedMBean(object, c), objectName);
                }
                else {
                    context.registerMBean(new AsyncAnnotatedMBean(executor, timeout, object, c), objectName);
                }
                return;
            }
        }
        context.registerMBean(object, objectName);
    }
    
    @Override
    public Object invoke(final String s, final Object[] objects, final String[] strings) throws MBeanException, ReflectionException {
        final String action = s;
        final Object[] params = objects;
        final String[] signature = strings;
        final Future<Object> task = this.executor.submit((Callable<Object>)new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return AsyncAnnotatedMBean.this.asyncInvole(action, params, signature);
            }
        });
        try {
            return task.get(this.timeout, TimeUnit.MILLISECONDS);
        }
        catch (ExecutionException e) {
            if (e.getCause() instanceof MBeanException) {
                throw (MBeanException)e.getCause();
            }
            throw new MBeanException(e);
        }
        catch (Exception e2) {
            throw new MBeanException(e2);
        }
        finally {
            if (!task.isDone()) {
                task.cancel(true);
            }
        }
    }
}
