package autumn.core.extension;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class AttachableProcessor implements TProcessor {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private TProcessor realProcessor;
    public AttachableProcessor(TProcessor realProcessor) {
        this.realProcessor = realProcessor;
    }

    @Override
    public void process(TProtocol in, TProtocol out) throws TException {
        if (in instanceof AttachableBinaryProtocol) {
            AttachableBinaryProtocol serverProtocol = (AttachableBinaryProtocol) in;
            TMessage message = serverProtocol.readMessageBegin();
            serverProtocol.readFieldZero();
            Map<String, String> headInfo = serverProtocol.getAttachment();
            log.info("读取到的隐式参数:{}", headInfo);
//            String traceId = headInfo.get(TRACE_ID.getValue());
//            String parentSpanId = headInfo.get(PARENT_SPAN_ID.getValue());
//            String isSampled = headInfo.get(IS_SAMPLED.getValue());
//            Boolean sampled = isSampled == null || Boolean.parseBoolean(isSampled);
//
//            if (traceId != null && parentSpanId != null) {
//                TraceUtils.startLocalTracer("rpc.thrift receive", traceId, parentSpanId, sampled);
//                String methodName = tMessage.name;
//                TraceUtils.submitAdditionalAnnotation(Constants.TRACE_THRIFT_METHOD, methodName);
//                TTransport transport = in.getTransport();
//                String hostAddress = ((TSocket) transport).getSocket().getRemoteSocketAddress().toString();
//                TraceUtils.submitAdditionalAnnotation(Constants.TRACE_THRIFT_SERVER, hostAddress);
//            }
            serverProtocol.resetMultiTFramedTransport(in);
        }
        realProcessor.process(in, out);
    }

}
