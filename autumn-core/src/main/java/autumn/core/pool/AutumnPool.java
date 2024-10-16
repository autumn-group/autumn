package autumn.core.pool;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.transport.TTransport;

import autumn.core.config.ReferenceConfig;
import autumn.core.pool.impl.ConcurrentBag;
import autumn.core.pool.impl.ConcurrentBagEntry;

public final class AutumnPool {
    private volatile static AutumnPool singleton = null;
    private ConcurrentHashMap<String, ReferenceConfig<? extends TServiceClient>> configMapping;
    private ConcurrentHashMap<String, ConcurrentBag> bagMapping;
    private ConcurrentHashMap<String, ConcurrentBagEntry> mapping;
    private AutumnPool() {

    }

    private void init() {
        configMapping = new ConcurrentHashMap();
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


    public void remove(String service, String ip) {
        ConcurrentBag bag = bagMapping.get(service);
        bag.removeByIp(ip);


        mapping.forEach((k, v) -> {
            if(!service.contains(v.getService())) {
                return;
            }


            ConcurrentBagEntry entry = v;
            v.setState(ConcurrentBagEntry.STATE_REMOVED);
        });



    }
}
