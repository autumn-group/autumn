package autumn.core.config;

import lombok.Data;

/**
 * @author: baoxin.zhao
 * @date: 2024/5/8
 */
@Data
public class ApplicationConfig {
    private String name;
    private Integer port;
    private Boolean registered;
    private Boolean discovered;
    private Integer minThreads;
    private Integer maxThreads;
    private Integer workerKeepAliveTime;

}
