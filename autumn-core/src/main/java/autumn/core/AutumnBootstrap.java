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
import autumn.core.util.AutumnException;
import autumn.core.util.Singleton;

/**
 * @author: baoxin.zhao
 * @date: 2024/5/8
 */
public class AutumnBootstrap {
    private static volatile AutumnBootstrap instance;
    private TServer server;
    private ApplicationConfig applicationConfig;
    private ProviderConfig providerConfig;

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
    }

    public TServer getServer() {
        return server;
    }

    public void serve() {
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
            throw new RuntimeException(e);
        }

        if(Boolean.TRUE.equals(applicationConfig.getRegistered())) {

        }

    }



}
