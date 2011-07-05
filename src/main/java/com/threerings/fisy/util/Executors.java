package com.threerings.fisy.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Executors
{
    /**
     * Creates a {@link ThreadPoolExecutor} containing the given number of threads with
     * {@link ThreadPoolExecutor#allowsCoreThreadTimeOut} enabled.  This means the pool will keep
     * an application from exiting while it's executing tasks, but it will allow it to exit if it's
     * idle.
     */
    public static ExecutorService newExitingFixedThreadPool (String nameBase, int threads)
    {
        ThreadPoolExecutor exec = new ThreadPoolExecutor(threads, threads, 1, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(), new NamingThreadFactory(nameBase));
        exec.allowCoreThreadTimeOut(true);
        return exec;
    }

    /**
     * Creates an ExecutorService with
     * {@link java.util.concurrent.Executors#newSingleThreadExecutor()} that uses a daemon thread.
     */
    public static ExecutorService newSingleDaemonThreadExecutor (String nameBase)
    {
        ThreadFactory factory = new DaemonThreadFactory(nameBase);
        return java.util.concurrent.Executors.newSingleThreadExecutor(factory);
    }

    public static class NamingThreadFactory
        implements ThreadFactory
    {
        public NamingThreadFactory (String nameBase) {
            _name = nameBase;
        }

        @Override
        public Thread newThread (Runnable r) {
            Thread t = _delegate.newThread(r);
            t.setName( _name + "-" + _created.incrementAndGet());
            return t;
        }

        protected final String _name;

        protected final AtomicInteger _created = new AtomicInteger();

        protected final ThreadFactory _delegate =
            java.util.concurrent.Executors.defaultThreadFactory();
    }

    public static class DaemonThreadFactory
        extends NamingThreadFactory
    {
        public DaemonThreadFactory (String nameBase) {
            super(nameBase);
        }

        @Override public Thread newThread (Runnable r) {
            Thread t = super.newThread(r);
            t.setDaemon(true);
            return t;
        }
    }
}
