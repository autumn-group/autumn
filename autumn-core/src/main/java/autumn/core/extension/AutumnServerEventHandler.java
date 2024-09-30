package autumn.core.extension;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.ServerContext;
import org.apache.thrift.server.TServerEventHandler;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import autumn.core.devops.ContextHolder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: baoxin.zhao
 * @date: 2024/9/30
 */
@Slf4j
public class AutumnServerEventHandler implements TServerEventHandler {
    @Override
    public void preServe() {

    }

    @Override
    public ServerContext createContext(TProtocol input, TProtocol output) {
        TSocket socket = (TSocket) input.getTransport();
        String ipAndPort = socket.getSocket().getRemoteSocketAddress().toString();
        log.info("autumn client connect server, ip-port:{}", ipAndPort);
        ContextHolder.addClient(ipAndPort);
        return null;
    }

    @Override
    public void deleteContext(ServerContext serverContext, TProtocol input, TProtocol output) {
        TSocket socket = (TSocket) input.getTransport();
        String ipAndPort = socket.getSocket().getRemoteSocketAddress().toString();
        log.info("autumn client disconnect server, ip-port:{}", ipAndPort);
        ContextHolder.removeClient(ipAndPort);
    }

    @Override
    public void processContext(ServerContext serverContext, TTransport inputTransport, TTransport outputTransport) {

    }
}
