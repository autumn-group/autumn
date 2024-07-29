package autumn.core.pool;

import autumn.core.pool.impl.ConcurrentBag;
import autumn.core.pool.impl.ConcurrentBagEntry;
import autumn.core.util.AutumnException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class AutumnPool {
    private volatile static AutumnPool singleton = null;
    private ConnectionFactory connectionFactory;

    private ConcurrentHashMap<String, Long> timeouts;
    private AutumnPool() {

    }

    private void init() {
        connectionFactory = ConnectionFactory.getInstance();
        timeouts = new ConcurrentHashMap<>();
    }

    public void setServiceTimeout(String service, Long timeout) {
        timeouts.put(service, timeout);
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




//    public <T> ConcurrentBagEntry<T> getConnection(String service) {
//        ConcurrentBag bagEntry = connectionFactory.getBag(service);
//        Long timeout = 1000L;
//        if(timeouts.containsKey(service)) {
//            timeout = timeouts.get(service);
//        }
//        try {
//            ConcurrentBagEntry<T> entry = bagEntry.borrow(timeout, TimeUnit.MILLISECONDS);
//            return entry;
//        } catch (InterruptedException e) {
//            throw new AutumnException("Autumn Pool Get Connection timeout", e);
//        }
//    }
//
//    public void release(String service, ConcurrentBagEntry entry) {
//        connectionFactory.release(service, entry);
//    }
//
//    public void evict(String service, ConcurrentBagEntry entry) {
//        connectionFactory.evictEntry(service, entry);
//    }

}
