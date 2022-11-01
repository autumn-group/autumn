package cloud.micronative.autumn.core.pool;

import cloud.micronative.autumn.core.util.AutumnException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class AutumnPool<T extends ConcurrentBagEntry> {
    private volatile static AutumnPool singleton = null;
    private ConcurrentHashMap<Class, ConcurrentBag> mapping;

    private AutumnPool() {

    }

    private void init() {
        mapping = new ConcurrentHashMap<>();
    }

    public static AutumnPool getSigSingleton() {
        if (singleton == null) {
            synchronized (AutumnPool.class) {
                if (singleton == null) {
                    singleton = new AutumnPool();
                    singleton.init();
                    return singleton;
                }
            }
        }
        return singleton;
    }

    public ConcurrentBagEntry getConnection(T clazz) {
        ConcurrentBag bagEntry = mapping.get(clazz);
        try {
            ConcurrentBagEntry entry = bagEntry.borrow(100, TimeUnit.MILLISECONDS);
            return entry;
        } catch (InterruptedException e) {
            throw new AutumnException("autumn pool get connection timeout", e);
        }
    }

    public  void release(T clazz, ConcurrentBagEntry entry) {
        ConcurrentBag bagEntry = mapping.get(clazz);
        bagEntry.requite(entry);
    }

}
