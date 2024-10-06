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
    private String registryType;
    private String multicastIp;
    private Integer multicastPort;

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
        String registry_type = properties.getProperty("autumn.registry-type");
        if(Objects.isNull(registryType) || registryType.length() < 1) {
            registry_type = "multicast";
        }
        this.registryType = registry_type;

        String multicast_ip = properties.getProperty("autumn.multicast.ip");
        if(Objects.isNull(multicast_ip) || multicast_ip.length() < 1) {
            multicast_ip = "224.5.6.7";
        }
        this.multicastIp = multicast_ip;

        String multicast_port = properties.getProperty("autumn.multicast.port");
        if(Objects.isNull(multicast_port) || multicast_port.length() < 1) {
            multicast_port = "1234";
        }
        this.multicastPort = Integer.valueOf(multicast_port);
    }

}
