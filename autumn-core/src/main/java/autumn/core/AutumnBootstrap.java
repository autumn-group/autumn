package autumn.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.layered.TFramedTransport;

import autumn.core.config.ApplicationConfig;
import autumn.core.config.ConsulConfig;
import autumn.core.config.ProviderConfig;
import autumn.core.config.ReferenceConfig;
import autumn.core.config.ServiceConfig;
import autumn.core.discovery.MulticastDiscovery;
import autumn.core.extension.AttachableProcessor;
import autumn.core.util.AutumnException;
import autumn.core.util.CommonUtil;
import autumn.core.util.ThreadUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: baoxin.zhao
 * @date: 2024/5/8
 */
@Slf4j
@Setter
@Getter
public class AutumnBootstrap {
    private static volatile AutumnBootstrap instance;
    private TServer server;
    private ApplicationConfig applicationConfig;
    private ProviderConfig providerConfig;
    private ConsulConfig registryConfig;
    private TMultiplexedProcessor processor;
    private Map<Class, ServiceConfig> services = new ConcurrentHashMap<>();
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

    private void handleDefaultRegistryConfig(ConsulConfig registryConfig) {
        if(Objects.isNull(registryConfig)) {
            registryConfig = new ConsulConfig();
        }

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
            providerConfig = new ProviderConfig();
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
        if(Objects.isNull(workerKeepAliveTime)) {
            workerKeepAliveTime = 60;
        }
        if(Objects.isNull(providerConfig.getTimeout())) {
            providerConfig.setTimeout(3);
        }
        if(Objects.isNull(providerConfig.getThreadQueueSize())) {
            providerConfig.setThreadQueueSize(10);
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

    private void export(String name, AttachableProcessor serviceProcessor) {
        if(Objects.isNull(processor)) {
            processor = new TMultiplexedProcessor();
        }
        if(!services.containsKey(name)) {
            return;
        }
        processor.registerProcessor(name, serviceProcessor);
    }


    public void serve() {
        start();
        registry();
    }

    public void start() {
        handleDefaultProviderConfig(providerConfig);
        handleDefaultRegistryConfig(registryConfig);
        if(Objects.isNull(applicationConfig)) {
            throw new AutumnException("must config application!");
        }

        if(Objects.isNull(providerConfig)) {
            throw new AutumnException("must config service!");
        }

        if(services.isEmpty()) {
            throw new AutumnException("must export one service at lease!");
        }

        ThreadUtil singleton = ThreadUtil.getInstance();
        ExecutorService executorService = singleton.getWorkerExecutor(providerConfig);
        try {
            TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(providerConfig.getPort());
            TThreadedSelectorServer.Args tArgs = new TThreadedSelectorServer.Args(serverTransport);
            tArgs.transportFactory(new TFramedTransport.Factory());
            tArgs.protocolFactory(new TBinaryProtocol.Factory());
            tArgs.executorService(executorService);
            tArgs.acceptQueueSizePerThread(10);
            tArgs.stopTimeoutVal(3);
            tArgs.stopTimeoutUnit(TimeUnit.SECONDS);
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

    public <T> AutumnBootstrap service(ServiceConfig<T> serviceConfig) {
        export(serviceConfig.getInterfaceClass().getName(), serviceConfig.getRef());
        services.put(serviceConfig.getInterfaceClass(), serviceConfig);
        return this;
    }

    public <T extends TServiceClient> AutumnBootstrap reference(ReferenceConfig<T> referenceConfig) {
        MulticastDiscovery multicastDiscovery = MulticastDiscovery.getInstance();
        multicastDiscovery.addRefer(referenceConfig.getName(), referenceConfig);
        return this;
    }

    public <T extends TServiceClient> T getClient(T interfaceClass) {

        return null;
    }

    public <T extends TServiceClient> void release(T interfaceClass) {

    }
}
