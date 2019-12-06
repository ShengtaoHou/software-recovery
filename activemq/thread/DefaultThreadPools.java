// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.thread;

@Deprecated
public final class DefaultThreadPools
{
    private static final TaskRunnerFactory DEFAULT_TASK_RUNNER_FACTORY;
    
    private DefaultThreadPools() {
    }
    
    @Deprecated
    public static TaskRunnerFactory getDefaultTaskRunnerFactory() {
        return DefaultThreadPools.DEFAULT_TASK_RUNNER_FACTORY;
    }
    
    public static void shutdown() {
        DefaultThreadPools.DEFAULT_TASK_RUNNER_FACTORY.shutdown();
    }
    
    static {
        DEFAULT_TASK_RUNNER_FACTORY = new TaskRunnerFactory();
    }
}
