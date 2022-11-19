package autumn.core.util;

import autumn.core.model.AutumnConfig;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class CommonUtil {
    public final static String CONFIG_PATH = "/opt/autumn/autumn-config.yml";

    private static AutumnConfig config;
    private CommonUtil() {

    }

    public static String getConnectionPath(String service, String ipPort) {
        String interfacePath = service.concat("/")
                .concat(ipPort);
        return interfacePath;
    }

    public static AutumnConfig getConfig() {
        if(null != config) {
            return config;
        }
        config = readConfig();
        return config;
    }

    public static AutumnConfig readConfig() {
        File file = new File(CONFIG_PATH);
        if(!file.exists()) {
            String path = CommonUtil.class.getClassLoader().getResource("").getPath();
            file = new File(path.concat("/autumn-config.yml"));
        }
        Yaml yaml = new Yaml();
        AutumnConfig config = null;
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            config = yaml.loadAs(new FileInputStream(file), AutumnConfig.class);
        } catch (IOException e) {
            log.warn("config file not exist, default path: /opt/autumn/autumn-config.yml. please check autumn-config.yml!", e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        log.info("read config:{}", config);
        return config;
    }

}
