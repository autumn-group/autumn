package autumn.core.test;

import org.junit.jupiter.api.Test;

import autumn.core.config.ApplicationConfig;
import autumn.core.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigTest {
    private String CONFIG_PATH = "/opt/autumn/autumn-config.yml";


    @Test
    void test2() {
        ApplicationConfig config = new ApplicationConfig();
        config.setName("hello");

        String ipAddress = CommonUtil.getHostIpAddress();
        log.info("============ip:{}", ipAddress);
    }
}
