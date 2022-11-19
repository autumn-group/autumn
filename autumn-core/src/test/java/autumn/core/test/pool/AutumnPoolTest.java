package autumn.core.test.pool;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;


@Slf4j
public class AutumnPoolTest {

    @Test
    void test() {
        String path = this.getClass()
                .getClassLoader()
                .getResource("")
                .toString();
        log.info("==================, path:{}", path);
    }

}
