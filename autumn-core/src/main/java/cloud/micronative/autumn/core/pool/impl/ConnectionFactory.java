package cloud.micronative.autumn.core.pool.impl;

import cloud.micronative.autumn.core.util.AutumnException;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TServiceClient;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@Slf4j
public class ConnectionFactory {
    private ConcurrentHashMap<String, Function<ConnectionConfig, ? extends TServiceClient>> mapping;
    private ConcurrentHashMap<String, AtomicInteger> monitor;
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
     * 注册实现
     *
     * @param service
     * @param consumer
     */
    public void registry(String service, Function<ConnectionConfig, ? extends TServiceClient> consumer) {
        mapping.put(service, consumer);
    }

    /**
     * 获取客户端连接
     *
     * @param config
     * @return
     * @param <T>
     */
    public <T extends TServiceClient> T getConnection(ConnectionConfig config) {
        String service = config.getService();
        if(!mapping.contains(service)) {
            log.warn("Service Not Registry Yet, service:{}", service);
            throw new AutumnException("Service Not Registry Yet!");
        }
        Function<ConnectionConfig, ? extends TServiceClient> function = mapping.get(service);
        try {
            TServiceClient client = function.apply(config);
            return (T) client;
        } catch (Exception e) {
            log.warn("Service Init Connection Exception, error:", e);
            throw new AutumnException("Service Init Connection Exception!", e);
        }
    }




}
