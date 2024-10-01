package autumn.core.config;

import java.util.Objects;
import java.util.Properties;

import lombok.Getter;
import lombok.ToString;

/**
 * @author: baoxin.zhao
 * @date: 2024/5/8
 */
@Getter
@ToString
public class ApplicationConfig {
    private static volatile ApplicationConfig instance;

    private ApplicationConfig() {

    }
    private String name;

    public static ApplicationConfig getInstance() {
        if(Objects.isNull(instance)) {
            synchronized (ApplicationConfig.class) {
                if(Objects.isNull(instance)) {
                    instance = new ApplicationConfig();
                }
            }
        }
        return instance;
    }

    public void init(Properties properties) {
        String appName = properties.getProperty("spring.application.name");
        if(Objects.isNull(appName) || appName.length() < 1) {
            appName = properties.getProperty("autumn.name");
        }
        this.name = appName;
    }

}
