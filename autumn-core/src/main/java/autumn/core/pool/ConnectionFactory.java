package autumn.core.pool;

import autumn.core.pool.impl.ConcurrentBag;
import autumn.core.pool.impl.ConcurrentBagEntry;
import autumn.core.pool.impl.ConcurrentBagEntryImpl;
import autumn.core.pool.impl.ConnectionConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

import static autumn.core.pool.impl.ConcurrentBagEntry.STATE_RESERVED;

@Slf4j
public class ConnectionFactory {
    private ConcurrentHashMap<String, ConcurrentBag> mapping;
    private ConcurrentHashMap<String, CopyOnWriteArrayList<ConcurrentBagEntry>> monitor;


    private ConnectionFactory() {}
    private static ConnectionFactory singleton;

    private void init() {
        mapping = new ConcurrentHashMap<>();
        monitor = new ConcurrentHashMap<>();
    }
    public static ConnectionFactory getInstance() {
        if (singleton == null) {
            synchronized (ConnectionFactory.class) {
                if (singleton == null) {
                    singleton = new ConnectionFactory();
                    singleton.init();
                    return singleton;
                }
            }
        }
        return singleton;
    }


    /**
     * 获取客户端连接
     *
     * @param config
     * @return
     */
    public void createConnection(ConnectionConfig config) {
        String service = config.getService();
        String ip = config.getIp();
        String port = config.getPort();
        String ipPort = ip.concat(":")
                .concat(port);
//        String connectionPath = CommonUtil.getConnectionPath(service, ipPort);
        TTransport socket = new TSocket(ip, Integer.valueOf(port));
        TTransport transport = new TFramedTransport(socket);
        try {
            socket.open();
        } catch (TTransportException e) {
            log.warn("init client exception, config:{}, exception:", config);
            return;
        }

        if(mapping.containsKey(service)) {
            CopyOnWriteArrayList<ConcurrentBagEntry> entries = monitor.get(service);
            ConcurrentBag bag = mapping.get(service);
            ConcurrentBagEntry<? extends TServiceClient> bagEntry = new ConcurrentBagEntryImpl<>(service, ipPort, transport);
            bag.add(bagEntry);
            entries.add(bagEntry);
            return;
        }
        CopyOnWriteArrayList<ConcurrentBagEntry> entries = new CopyOnWriteArrayList<>();
        ConcurrentBag bag = new ConcurrentBag();
        ConcurrentBagEntry<? extends TServiceClient> bagEntry = new ConcurrentBagEntryImpl<>(service, ipPort, transport);
        bag.add(bagEntry);
        entries.add(bagEntry);
        mapping.put(service, bag);
        monitor.put(service, entries);
    }

    public ConcurrentBag getBag(String service) {
        ConcurrentBag bag = mapping.get(service);
        return bag;
    }

    public void release(String service, ConcurrentBagEntry entry) {
        ConcurrentBag bagEntry =  mapping.get(service);
        bagEntry.requite(entry);
    }

    public void evictEntry(String service, ConcurrentBagEntry entry) {
        CopyOnWriteArrayList<ConcurrentBagEntry> entries = monitor.get(service);
        entries.removeIf(it -> it.getId().endsWith(entry.getId()));
        TTransport socket = (TTransport) entry.getEntry();
        entry.setState(STATE_RESERVED);
        if(socket.isOpen()) {
            socket.close();
        }
    }

    public void evictEntries(String service, String ipPort) {
        CopyOnWriteArrayList<ConcurrentBagEntry> entries = monitor.get(service);
        Predicate<ConcurrentBagEntry> judge = (it) -> {
            String entryService = it.getService();
            String entryIpPort = it.getIpPort();
            if(service.equals(entryService) && ipPort.equals(entryIpPort)) {
                TTransport socket = (TTransport) it.getEntry();
                it.setState(STATE_RESERVED);
                if(socket.isOpen()) {
                    socket.close();
                }
                return true;
            }
          return false;
        };
        entries.removeIf(judge);
    }
}
