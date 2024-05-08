package autumn.core;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.layered.TFramedTransport;

import autumn.core.config.ApplicationConfig;
import autumn.core.util.AutumnException;
import autumn.core.util.Singleton;

/**
 * @author: baoxin.zhao
 * @date: 2024/5/8
 */
public class Bootstrap {
    private static volatile Bootstrap instance;
    private static volatile TServer server;
    private ApplicationConfig applicationConfig;

    public static Bootstrap getInstance() {
        if(Objects.isNull(instance)) {
            synchronized (Bootstrap.class) {
                if(Objects.isNull(instance)) {
                    instance = new Bootstrap();
                }
            }
        }

        return instance;
    }

    private Bootstrap() {

    }

    public void export() {
        if(Objects.isNull(applicationConfig)) {
            throw new AutumnException("must config application");
        }

//        TProcessor processor = new HelloThriftServer.Processor<>(service);

        Singleton singleton = Singleton.getInstance();
        ExecutorService executorService = singleton.getWorkerExecutor(applicationConfig);
        try {
            TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(applicationConfig.getPort());
            TThreadedSelectorServer.Args tArgs = new TThreadedSelectorServer.Args(serverTransport);
            //tArgs.processor(tprocessor);
            tArgs.transportFactory(new TFramedTransport.Factory());
            tArgs.protocolFactory(new TBinaryProtocol.Factory());
            tArgs.executorService(executorService);
            TServer server = new TThreadedSelectorServer(tArgs);
            System.out.println("autumn server start....");
            server.serve();
        } catch (TTransportException e) {
            throw new RuntimeException(e);
        }

    }

}
