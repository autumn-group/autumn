package autumn.core.config;

import java.util.Objects;
import java.util.Properties;

import autumn.core.util.CommonUtil;
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
    private String ip;
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
            Boolean _enabled = Boolean.valueOf(enabledValue);
            this.enabled = _enabled;
        } else {
            this.enabled = true;
        }
        String portValue = properties.getProperty("autumn.provider.port");
        if(Objects.nonNull(portValue) && portValue.length() > 0) {
            Integer _port = Integer.valueOf(portValue);
            this.port = _port;
        } else {
            this.port = 30880;
        }
        String minThreadsValue = properties.getProperty("autumn.provider.min-threads");
        if(Objects.nonNull(minThreadsValue) && minThreadsValue.length() > 0) {
            Integer _minThreads = Integer.valueOf(minThreadsValue);
            this.minThreads = _minThreads;
        } else {
            this.minThreads = 0;
        }
        String maxThreadsValue = properties.getProperty("autumn.provider.max-threads");
        if(Objects.nonNull(maxThreadsValue) && maxThreadsValue.length() > 0) {
            Integer _maxThreads = Integer.valueOf(maxThreadsValue);
            this.maxThreads = _maxThreads;
        } else {
            this.maxThreads = 1;
        }

        String workerKeepAliveTimeValue = properties.getProperty("autumn.provider.worker-keep-alive-time");
        if(Objects.nonNull(workerKeepAliveTimeValue) && workerKeepAliveTimeValue.length() > 0) {
            Integer inner_workerKeepAliveTime = Integer.valueOf(workerKeepAliveTimeValue);
            this.workerKeepAliveTime = inner_workerKeepAliveTime;
        } else {
            this.workerKeepAliveTime = 60;
        }
        String threadQueueSizeValue = properties.getProperty("autumn.provider.thread-queue-size");
        if(Objects.nonNull(threadQueueSizeValue) && threadQueueSizeValue.length() > 0) {
            Integer _threadQueueSize = Integer.valueOf(threadQueueSizeValue);
            this.threadQueueSize = _threadQueueSize;
        } else {
            this.threadQueueSize = 10;
        }
        String timeoutValue = properties.getProperty("autumn.provider.timeout");
        if(Objects.nonNull(timeoutValue) && timeoutValue.length() > 0) {
            Integer _timeout = Integer.valueOf(timeoutValue);
            this.timeout = _timeout;
        } else {
            this.timeout = 3;
        }
        String appName = properties.getProperty("spring.application.name");
        if(Objects.isNull(appName) || appName.length() < 1) {
            appName = properties.getProperty("autumn.name");
        }
        this.name = appName;

        String _ip = properties.getProperty("autumn.provider.ip");
        if(Objects.nonNull(_ip) && _ip.length() > 0) {
            this.ip = _ip;
        } else {
            this.ip = CommonUtil.getHostIpAddress();
        }

    }
}
