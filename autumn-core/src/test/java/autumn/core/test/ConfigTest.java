package autumn.core.test;

import autumn.core.config.ApplicationConfig;
import autumn.core.model.AutumnConfig;
import autumn.core.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class ConfigTest {
    private String CONFIG_PATH = "/opt/autumn/autumn-config.yml";
    @Test
    void readConfig() {
        String result = CommonUtil.getConfig().toString();
        log.info("==============={}", result);
    }

    void test2() {
        ApplicationConfig config = new ApplicationConfig();
        config.setName("hello");
        config.setPort(30880);


    }
}
