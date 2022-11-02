package cloud.micronative.autumn.core.test.pool;

import cloud.micronative.autumn.core.pool.AutumnPool;
import io.training.thrift.api.SomeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class AutumnPoolTest {

    @Test
    void test() {
        log.info("==================");

        AutumnPool pool = AutumnPool.getSigSingleton();
//        pool.getConnection(SomeService.Client.class);

    }

}
