// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.slf4j.Logger;

public final class ThreadPoolUtils
{
    private static final Logger LOG;
    public static final long DEFAULT_SHUTDOWN_AWAIT_TERMINATION = 10000L;
    
    public static void shutdown(final ExecutorService executorService) {
        doShutdown(executorService, 0L);
    }
    
    public static List<Runnable> shutdownNow(final ExecutorService executorService) {
        List<Runnable> answer = null;
        if (!executorService.isShutdown()) {
            ThreadPoolUtils.LOG.debug("Forcing shutdown of ExecutorService: {}", executorService);
            answer = executorService.shutdownNow();
            if (ThreadPoolUtils.LOG.isTraceEnabled()) {
                ThreadPoolUtils.LOG.trace("Shutdown of ExecutorService: {} is shutdown: {} and terminated: {}.", executorService, executorService.isShutdown(), executorService.isTerminated());
            }
        }
        return answer;
    }
    
    public static void shutdownGraceful(final ExecutorService executorService) {
        doShutdown(executorService, 10000L);
    }
    
    public static void shutdownGraceful(final ExecutorService executorService, final long shutdownAwaitTermination) {
        doShutdown(executorService, shutdownAwaitTermination);
    }
    
    private static void doShutdown(final ExecutorService executorService, final long shutdownAwaitTermination) {
        if (executorService == null) {
            return;
        }
        if (!executorService.isShutdown()) {
            boolean warned = false;
            final StopWatch watch = new StopWatch();
            ThreadPoolUtils.LOG.trace("Shutdown of ExecutorService: {} with await termination: {} millis", executorService, shutdownAwaitTermination);
            executorService.shutdown();
            if (shutdownAwaitTermination > 0L) {
                try {
                    if (!awaitTermination(executorService, shutdownAwaitTermination)) {
                        warned = true;
                        ThreadPoolUtils.LOG.warn("Forcing shutdown of ExecutorService: {} due first await termination elapsed.", executorService);
                        executorService.shutdownNow();
                        if (!awaitTermination(executorService, shutdownAwaitTermination)) {
                            ThreadPoolUtils.LOG.warn("Cannot completely force shutdown of ExecutorService: {} due second await termination elapsed.", executorService);
                        }
                    }
                }
                catch (InterruptedException e) {
                    warned = true;
                    ThreadPoolUtils.LOG.warn("Forcing shutdown of ExecutorService: {} due interrupted.", executorService);
                    executorService.shutdownNow();
                }
            }
            if (warned) {
                ThreadPoolUtils.LOG.info("Shutdown of ExecutorService: {} is shutdown: {} and terminated: {} took: {}.", executorService, executorService.isShutdown(), executorService.isTerminated(), TimeUtils.printDuration((double)watch.taken()));
            }
            else if (ThreadPoolUtils.LOG.isDebugEnabled()) {
                ThreadPoolUtils.LOG.debug("Shutdown of ExecutorService: {} is shutdown: {} and terminated: {} took: {}.", executorService, executorService.isShutdown(), executorService.isTerminated(), TimeUtils.printDuration((double)watch.taken()));
            }
        }
    }
    
    public static boolean awaitTermination(final ExecutorService executorService, final long shutdownAwaitTermination) throws InterruptedException {
        final StopWatch watch = new StopWatch();
        long interval = Math.min(2000L, shutdownAwaitTermination);
        boolean done = false;
        while (!done && interval > 0L) {
            if (executorService.awaitTermination(interval, TimeUnit.MILLISECONDS)) {
                done = true;
            }
            else {
                ThreadPoolUtils.LOG.info("Waited {} for ExecutorService: {} to terminate...", TimeUtils.printDuration((double)watch.taken()), executorService);
                interval = Math.min(2000L, shutdownAwaitTermination - watch.taken());
            }
        }
        return done;
    }
    
    static {
        LOG = LoggerFactory.getLogger(ThreadPoolUtils.class);
    }
}
