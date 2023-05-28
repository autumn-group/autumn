package autumn.core.pool.impl;

import autumn.core.model.Consumer;
import autumn.core.model.ConsumerInstance;
import autumn.core.model.Service;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ConnectionConfig {
    private String service;
    private String ip;
    private String port;
    private long connectionTimeout;
    private long socketTimeout;

    public static List<ConnectionConfig> convert(Service consumer) {
        List<ConnectionConfig> configs = new ArrayList<>();
        String service = consumer.getName();
        long connectionTimeout = consumer.getConnectionTimeout();
        long socketTimeout = consumer.getSocketTimeout();
        List<ConsumerInstance> instances = consumer.getInstances();
        instances.forEach(ins -> {
            int connections = ins.getConnections();
            if(connections > 0) {
                for(int i = 0; i < connections; i++) {
                    ConnectionConfig config = new ConnectionConfig();
                    config.setIp(ins.getIp());
                    config.setPort(ins.getPort());
                    config.setConnectionTimeout(connectionTimeout);
                    config.setSocketTimeout(socketTimeout);
                    config.setService(service);
                    configs.add(config);
                }
            }
        });
        return configs;
    }
}
