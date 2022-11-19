package autumn.core.model;

import lombok.Data;

import java.util.List;

@Data
public class Consumer {
    private String service;
    private String poolType;
    private Long poolTimeout;
    private Long connectionTimeout;
    private Long socketTimeout;
    private List<ConsumerInstance> instances;
}
