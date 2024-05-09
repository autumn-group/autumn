package autumn.core.util;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import autumn.core.config.ProviderConfig;

/**
 * @author: baoxin.zhao
 * @date: 2024/5/8
 */
public class Singleton {
    private static volatile Singleton instance;
    private volatile ExecutorService workerExecutor;
    private volatile ScheduledExecutorService scheduledExecutorService;
    private volatile TMultiplexedProcessor multiplexedProcessor;

    private static final String THREAD_POOL_NAME_WORKER = "autumn-thread-pool";

    private Singleton() {

    }
    public static Singleton getInstance() {
        if(Objects.isNull(instance)) {
            synchronized (Singleton.class) {
                if(Objects.isNull(instance)) {
                    instance = new Singleton();
                }
            }
        }

        Executors.newSingleThreadScheduledExecutor();
        return instance;
    }

    public TMultiplexedProcessor getMultiplexedProcessor() {
        return multiplexedProcessor;
    }

    public void registerProcessor(String name, TProcessor processor) {
        if (Objects.isNull(multiplexedProcessor)) {
            synchronized (this) {
                if(Objects.isNull(multiplexedProcessor)) {
                    multiplexedProcessor = new TMultiplexedProcessor();
                }
            }
        }
        multiplexedProcessor.registerProcessor(name, processor);
    }

    public void scheduleWithFixedDelay(Runnable runnable, Long delay) {
        if (Objects.isNull(scheduledExecutorService)) {
            synchronized (this) {
                if(Objects.isNull(scheduledExecutorService)) {
                    scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
                }
            }
        }
        scheduledExecutorService.scheduleWithFixedDelay(runnable, delay, delay, TimeUnit.SECONDS);
    }


    public ExecutorService getWorkerExecutor(ProviderConfig applicationConfig) {
        if(Objects.isNull(workerExecutor)) {
            synchronized (this) {
                if(Objects.isNull(workerExecutor)) {
                    Integer minThreads = applicationConfig.getMinThreads();
                    Integer maxThreads = applicationConfig.getMaxThreads();
                    Integer workerKeepAliveTime = applicationConfig.getWorkerKeepAliveTime();

                    minThreads = (minThreads >= 0? minThreads: 0);
                    maxThreads = (maxThreads > 100? 100: maxThreads);
                    maxThreads = (maxThreads > 0? maxThreads: 1);
                    minThreads = (minThreads > maxThreads? maxThreads: minThreads);
                    workerKeepAliveTime = (workerKeepAliveTime < 0? 100: workerKeepAliveTime);
                    workerKeepAliveTime = (workerKeepAliveTime > 60 * 1000? 60 * 1000: workerKeepAliveTime);

                    workerExecutor = new ThreadPoolExecutor(minThreads, maxThreads,
                            workerKeepAliveTime, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
                            new ThreadFactoryWithGarbageCleanup(THREAD_POOL_NAME_WORKER));;
                }
            }
        }
        return workerExecutor;
    }


    class ThreadFactoryWithGarbageCleanup implements ThreadFactory {
        private final String namePrefix;

        public ThreadFactoryWithGarbageCleanup(String threadPoolName){
            namePrefix = threadPoolName;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread newThread = new ThreadWithGarbageCleanup(r);
            newThread.setName(namePrefix + ":Thread-" + newThread.getId());
            return newThread;
        }
    }

    class ThreadWithGarbageCleanup extends Thread {
        public static final Logger log = LoggerFactory.getLogger(ThreadWithGarbageCleanup.class);
        public ThreadWithGarbageCleanup(){

        }

        public ThreadWithGarbageCleanup(Runnable runnable){
            super(runnable);
        }

        @Override
        protected void finalize() throws Throwable {
            if(log.isDebugEnabled()) {
                log.debug("clean-up thread: {}", super.getName());
            }
            super.finalize();
        }
    }



}
