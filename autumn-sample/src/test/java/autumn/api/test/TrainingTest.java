package autumn.api.test;

import org.apache.thrift.TProcessor;
import org.junit.jupiter.api.Test;

import autumn.core.extension.AttachableProcessor;
import autumn.sample.api.SomeService;
import autumn.core.AutumnBootstrap;
import autumn.core.config.ReferenceConfig;
import autumn.core.config.ServiceConfig;
import autumn.sample.service.SomeServiceImpl;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: baoxin.zhao
 * @date: 2024/10/8
 */

@Slf4j
public class TrainingTest {

    @Test
    void testConsumer() {
        log.info("========================");
        ServiceConfig<SomeService.Iface> fooServiceConfig = new ServiceConfig<>();
        fooServiceConfig.setInterfaceClass(SomeService.Iface.class);
        fooServiceConfig.setProviderId(SomeService.class.getName());
        TProcessor tprocessor = new SomeService.Processor<SomeService.Iface>(new SomeServiceImpl());
        AttachableProcessor attachableProcessor = new AttachableProcessor(tprocessor);
        fooServiceConfig.setRef(attachableProcessor);

        ReferenceConfig<SomeService.Client> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setName("training-a");
        referenceConfig.setNamespace("default");
        referenceConfig.setInterfaceClass(SomeService.Client.class);
//        referenceConfig.setClient();
        AutumnBootstrap autumnBootstrap = AutumnBootstrap.getInstance();
        autumnBootstrap
                .service(fooServiceConfig)
                .reference(referenceConfig)
                .start();

        SomeService.Iface someService = autumnBootstrap.get(SomeService.Iface.class);


    }

}
