package autumn.boot.starter;

import autumn.core.extension.AttachableBinaryProtocol;
import autumn.core.model.AutumnConfig;
import autumn.core.model.provider.AutumnProcessor;
import autumn.core.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

@Slf4j
public class AutumnRunner implements ApplicationRunner {
    private AutumnProcessor autumnProcessor;

    public AutumnRunner(AutumnProcessor autumnProcessor) {
        this.autumnProcessor = autumnProcessor;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception  {
        AutumnConfig config = CommonUtil.getConfig();
        log.info("autumn runner begin");
        Integer port = Integer.valueOf(config.getProvider().getPort());

        TNonblockingServerSocket serverTransport = null;
        try {
            serverTransport = new TNonblockingServerSocket(port);
        } catch (TTransportException e) {
            throw new RuntimeException(e);
        }
        TTransportFactory transportFactory = new TFramedTransport.Factory();
        TProtocolFactory proFactory = new AttachableBinaryProtocol.Factory();

        TServer server = new TThreadedSelectorServer(new
                TThreadedSelectorServer.Args(serverTransport)
                .transportFactory(transportFactory)
                .protocolFactory(proFactory)
                .processor(autumnProcessor.multiplexedProcessor())
        );
        log.info("autumn runner started on port:{}", config.getProvider().getPort());
        server.serve();
    }
}
