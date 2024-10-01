package autumn.core.test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import autumn.core.config.ApplicationConfig;
import autumn.core.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigTest {
    private String CONFIG_PATH = "/opt/autumn/autumn-config.yml";


    @Test
    void test2() {
        String ipAddress = CommonUtil.getHostIpAddress();
        log.info("============ip:{}", ipAddress);
    }

    @Test
    void test3() {
        Path path = null;
        URI uri = null;
        URL url = this.getClass().getClassLoader()
                .getResource("application.properties");
        if(Objects.isNull(url)) {
            uri = new File("/application2.properties").toURI();
        } else {
            try {
                uri = url.toURI();
            } catch (URISyntaxException e) {
                log.info("url to uri exception: ", e);
            }
        }
        path = Path.of(uri);

        try {
            String content = Files.readString(path);
            log.info("================:{}", content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void test4() {
        Properties properties = CommonUtil.readClasspath("application.properties");
        String value = properties.getProperty("spring.application.name");
        log.info("==============: {}", value);
    }
}
