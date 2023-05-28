package autumn.core.model;

import lombok.Data;

import java.util.List;

@Data
public class Service {
    private String name;
    private String namespace;
    private String referService;
    private String poolType;
    private Long poolTimeout;
    private Long connectionTimeout;
    private Long socketTimeout;
    private List<ConsumerInstance> instances;
}
