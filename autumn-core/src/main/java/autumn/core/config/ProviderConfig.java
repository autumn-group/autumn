package autumn.core.config;

import lombok.Data;

/**
 * @author: baoxin.zhao
 * @date: 2024/5/9
 */

@Data
public class ProviderConfig {
    private Integer port;
    private Integer minThreads;
    private Integer maxThreads;
    private Integer workerKeepAliveTime;
    private Integer threadQueueSize;
    private Integer timeout;
}
