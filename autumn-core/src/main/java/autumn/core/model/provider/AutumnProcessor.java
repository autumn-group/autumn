package autumn.core.model.provider;

import org.apache.thrift.TProcessor;

public interface AutumnProcessor {

    TProcessor multiplexedProcessor();

}
