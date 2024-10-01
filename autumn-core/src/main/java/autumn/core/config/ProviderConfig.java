package autumn.core.config;

import java.util.Objects;
import java.util.Properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author: baoxin.zhao
 * @date: 2024/5/9
 */
@Getter
@Setter
@ToString
public class ProviderConfig {
    private static volatile ProviderConfig instance;
    private Integer port;
    private Integer minThreads;
    private Integer maxThreads;
    private Integer workerKeepAliveTime;
    private Integer threadQueueSize;
    private Integer timeout;
    private Boolean enabled;
    private String name;

    public static ProviderConfig getInstance() {
        if(Objects.isNull(instance)) {
            synchronized (ProviderConfig.class) {
                if(Objects.isNull(instance)) {
                    instance = new ProviderConfig();
                }
            }
        }
        return instance;
    }

    public void init(Properties properties) {
        String enabledValue = properties.getProperty("autumn.provider.enabled");
        if(Objects.nonNull(enabledValue) && enabledValue.length() > 0) {
            Boolean inner_enabled = Boolean.valueOf(enabledValue);
            this.enabled = inner_enabled;
        }
        String portValue = properties.getProperty("autumn.provider.port");
        if(Objects.nonNull(portValue) && portValue.length() > 0) {
            Integer inner_port = Integer.valueOf(portValue);
            this.port = inner_port;
        }
        String minThreadsValue = properties.getProperty("autumn.provider.min-threads");
        if(Objects.nonNull(minThreadsValue) && minThreadsValue.length() > 0) {
            Integer inner_minThreads = Integer.valueOf(minThreadsValue);
            this.minThreads = inner_minThreads;
        }
        String maxThreadsValue = properties.getProperty("autumn.provider.max-threads");
        if(Objects.nonNull(maxThreadsValue) && maxThreadsValue.length() > 0) {
            Integer inner_maxThreads = Integer.valueOf(maxThreadsValue);
            this.maxThreads = inner_maxThreads;
        }

        String workerKeepAliveTimeValue = properties.getProperty("autumn.provider.worker-keep-alive-time");
        if(Objects.nonNull(workerKeepAliveTimeValue) && workerKeepAliveTimeValue.length() > 0) {
            Integer inner_workerKeepAliveTime = Integer.valueOf(workerKeepAliveTimeValue);
            this.workerKeepAliveTime = inner_workerKeepAliveTime;
        }
        String threadQueueSizeValue = properties.getProperty("autumn.provider.thread-queue-size");
        if(Objects.nonNull(threadQueueSizeValue) && threadQueueSizeValue.length() > 0) {
            Integer inner_threadQueueSize = Integer.valueOf(threadQueueSizeValue);
            this.threadQueueSize = inner_threadQueueSize;
        }
        String timeoutValue = properties.getProperty("autumn.provider.timeout");
        if(Objects.nonNull(timeoutValue) && timeoutValue.length() > 0) {
            Integer inner_timeout = Integer.valueOf(timeoutValue);
            this.timeout = inner_timeout;
        }
        String appName = properties.getProperty("spring.application.name");
        if(Objects.isNull(appName) || appName.length() < 1) {
            appName = properties.getProperty("autumn.name");
        }
        this.name = appName;
    }
}
