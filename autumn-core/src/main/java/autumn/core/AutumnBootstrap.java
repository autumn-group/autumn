package autumn.core;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.layered.TFramedTransport;

import autumn.core.config.ApplicationConfig;
import autumn.core.config.ProviderConfig;
import autumn.core.config.RegistryConfig;
import autumn.core.util.AutumnException;
import autumn.core.util.CommonUtil;
import autumn.core.util.Singleton;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: baoxin.zhao
 * @date: 2024/5/8
 */
@Slf4j
public class AutumnBootstrap {
    private static volatile AutumnBootstrap instance;
    private TServer server;
    private ApplicationConfig applicationConfig;
    private ProviderConfig providerConfig;
    private RegistryConfig registryConfig;

    private AutumnBootstrap() {}

    public static AutumnBootstrap getInstance() {
        if(Objects.isNull(instance)) {
            synchronized (AutumnBootstrap.class) {
                if(Objects.isNull(instance)) {
                    instance = new AutumnBootstrap();
                }
            }
        }
        return instance;
    }

    public ApplicationConfig getApplicationConfig() {
        return applicationConfig;
    }

    public void setApplicationConfig(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    public ProviderConfig getProviderConfig() {
        return providerConfig;
    }

    public void setProviderConfig(ProviderConfig providerConfig) {
        this.providerConfig = providerConfig;
        handleDefaultProviderConfig(providerConfig);
    }

    public RegistryConfig getRegistryConfig() {
        return registryConfig;
    }

    public void setRegistryConfig(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
        handleDefaultRegistryConfig(registryConfig);
    }

    private void handleDefaultRegistryConfig(RegistryConfig registryConfig) {
        String ipAddress = CommonUtil.getHostIpAddress();
        String name = applicationConfig.getName();
        String instanceId = name.concat(":")
                .concat(ipAddress)
                .concat(":")
                .concat(providerConfig.getPort().toString());
        registryConfig.setInstanceId(instanceId);
        Integer healthCheckInterval = registryConfig.getHealthCheckInterval();
        if(Objects.isNull(healthCheckInterval)) {
            healthCheckInterval = 10;
        } else {
            healthCheckInterval = healthCheckInterval > 0? healthCheckInterval: 10;
            healthCheckInterval = healthCheckInterval < 60? healthCheckInterval: 60;
        }
        registryConfig.setHealthCheckInterval(healthCheckInterval);
    }


    private void handleDefaultProviderConfig(ProviderConfig providerConfig) {
        if(Objects.isNull(providerConfig)) {
            return;
        }
        Integer minThreads = providerConfig.getMinThreads();
        Integer maxThreads = providerConfig.getMaxThreads();
        Integer workerKeepAliveTime = providerConfig.getWorkerKeepAliveTime();

        if(Objects.isNull(minThreads)) {
            minThreads = 0;
        }

        if(Objects.isNull(maxThreads)) {
            maxThreads = Runtime.getRuntime().availableProcessors();;
        }

        minThreads = (minThreads >= 0? minThreads: 0);
        maxThreads = (maxThreads > 100? 100: maxThreads);
        maxThreads = (maxThreads > 0? maxThreads: 1);
        minThreads = (minThreads > maxThreads? maxThreads: minThreads);
        workerKeepAliveTime = (workerKeepAliveTime < 0? 60: workerKeepAliveTime);
        workerKeepAliveTime = (workerKeepAliveTime > 60 * 10? 60 * 10: workerKeepAliveTime);

        providerConfig.setMinThreads(minThreads);
        providerConfig.setMaxThreads(maxThreads);
        providerConfig.setWorkerKeepAliveTime(workerKeepAliveTime);
    }

    public TServer getServer() {
        return server;
    }

    public void serve() {
        start();
        registry();
    }

    private void start() {
        if(Objects.isNull(applicationConfig)) {
            throw new AutumnException("must config application");
        }

        if(Objects.isNull(providerConfig)) {
            throw new AutumnException("must config service");
        }

        Singleton singleton = Singleton.getInstance();
        ExecutorService executorService = singleton.getWorkerExecutor(providerConfig);
        try {
            TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(providerConfig.getPort());
            TThreadedSelectorServer.Args tArgs = new TThreadedSelectorServer.Args(serverTransport);
            TMultiplexedProcessor processor = singleton.getMultiplexedProcessor();
            tArgs.transportFactory(new TFramedTransport.Factory());
            tArgs.protocolFactory(new TBinaryProtocol.Factory());
            tArgs.executorService(executorService);
            tArgs.processor(processor);
            server = new TThreadedSelectorServer(tArgs);
            server.serve();
        } catch (TTransportException e) {
            log.warn("autumn server start exception, exception:", e);
            throw new RuntimeException(e);
        }
    }

    private void registry() {
        if(Objects.isNull(registryConfig) || !Boolean.TRUE.equals(registryConfig) ) {
            log.info("autumn not config register-info, not registry");
        }
        if(!Boolean.TRUE.equals(registryConfig.getRegister())) {
            log.info("autumn not config register-info, not registry");
            return;
        }


    }

    private void reference() {

    }



}
