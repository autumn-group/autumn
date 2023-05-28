package autumn.core.controller;

import autumn.core.model.AutumnConfig;
import autumn.core.model.Service;
import autumn.core.pool.AutumnPool;
import autumn.core.pool.ConnectionFactory;
import autumn.core.pool.impl.ConnectionConfig;
import autumn.core.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class AutumnFileController {
    public void applyConsumerConfig() {
        AutumnConfig config = CommonUtil.getConfig();
        List<Service> services = config.getConsumer().getServices();
        ConnectionFactory factory = ConnectionFactory.getInstance();
        AutumnPool pool = AutumnPool.getInstance();
        services.forEach(service -> {
            List<ConnectionConfig> connectionConfigs = ConnectionConfig.convert(service);
            connectionConfigs.forEach(connectionConfig -> {
                factory.createConnection(connectionConfig);
            });
            pool.setServiceTimeout(service.getName(), service.getConnectionTimeout());
        });
    }


}
