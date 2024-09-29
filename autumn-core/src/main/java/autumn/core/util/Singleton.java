package autumn.core.util;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import autumn.core.config.ProviderConfig;

/**
 * @author: baoxin.zhao
 * @date: 2024/5/8
 */
public class Singleton {
    private static volatile Singleton instance;
    private ExecutorService workerExecutor;
    private ScheduledExecutorService scheduledExecutorService;
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
        return instance;
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

    public ExecutorService getWorkerExecutor(ProviderConfig providerConfig) {
        if(Objects.isNull(workerExecutor)) {
            synchronized (this) {
                if(Objects.isNull(workerExecutor)) {
                    Integer minThreads = providerConfig.getMinThreads();
                    Integer maxThreads = providerConfig.getMaxThreads();
                    Integer workerKeepAliveTime = providerConfig.getWorkerKeepAliveTime();
                    workerExecutor = new ThreadPoolExecutor(minThreads,
                            maxThreads,
                            workerKeepAliveTime,
                            TimeUnit.SECONDS,
                            new SynchronousQueue<Runnable>(),
                            new ThreadFactoryWithGarbageCleanup(THREAD_POOL_NAME_WORKER));
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
            Thread newThread = new Thread(r);
            newThread.setName(namePrefix + ":Thread-" + newThread.getId());
            return newThread;
        }
    }

}
