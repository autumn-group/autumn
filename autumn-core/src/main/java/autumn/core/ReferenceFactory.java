package autumn.core;

import org.apache.thrift.TServiceClient;

import autumn.core.config.ReferenceConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: baoxin.zhao
 * @date: 2024/10/9
 */
@Slf4j
public class ReferenceFactory {
    private ReferenceFactory() {

    }

    public static <T extends TServiceClient> T makeClient(ReferenceConfig<T> config) {

        return null;
    }


    public static <T extends TServiceClient> void releaseClient(T client) {
        client.getInputProtocol().getTransport().close();
        client.getOutputProtocol().getTransport().close();
    }

}
