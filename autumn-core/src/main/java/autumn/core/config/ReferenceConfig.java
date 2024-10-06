package autumn.core.config;

import java.util.List;

import lombok.Data;

/**
 * @author: baoxin.zhao
 * @date: 2024/5/9
 */
@Data
public class ReferenceConfig {
    private String name;
    private String namespace;
    private Long poolTimeout;
    private Long connectionTimeout;
    private Long socketTimeout;
    private List<ConsumerConfig> instances;

}
