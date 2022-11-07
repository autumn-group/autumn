package cloud.micronative.autumn.core.pool;

import cloud.micronative.autumn.core.pool.impl.ConcurrentBag;
import cloud.micronative.autumn.core.pool.impl.ConcurrentBagEntry;
import cloud.micronative.autumn.core.util.AutumnException;
import org.apache.thrift.TServiceClient;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class AutumnPool<T extends ConcurrentBagEntry> {
    private volatile static AutumnPool singleton = null;
    private ConcurrentHashMap<String, ConcurrentBag> mapping;

    private AutumnPool() {

    }

    private void init() {
        mapping = new ConcurrentHashMap<>();
    }

    public static AutumnPool getInstance() {
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

    public <T extends TServiceClient> T getConnection(String service) {
        ConcurrentBag bagEntry = mapping.get(service);
        try {
            ConcurrentBagEntry<T> entry = bagEntry.borrow(100, TimeUnit.MILLISECONDS);
            return entry.getEntry();
        } catch (InterruptedException e) {
            throw new AutumnException("Autumn Pool Get Connection timeout", e);
        }
    }

    public  void release(String service, ConcurrentBagEntry entry) {
        ConcurrentBag bagEntry = mapping.get(service);
        bagEntry.requite(entry);
    }

}
