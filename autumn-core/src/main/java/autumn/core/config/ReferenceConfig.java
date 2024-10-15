package autumn.core.config;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TMultiplexedProtocol;

import autumn.core.enums.RegistryTypeEnum;
import lombok.Data;

/**
 * @author: baoxin.zhao
 * @date: 2024/5/9
 */
@Data
public class ReferenceConfig<R extends TServiceClient> {
    private String name;
    private String namespace;
    private Long poolTimeout;
    private Long connectionTimeout;
    private Long socketTimeout;
    private RegistryTypeEnum registryTypeEnum;
    private List<ConsumerConfig> instances;
    private Class<R> interfaceClass;
    private Function<TMultiplexedProtocol, R> converter;
}
