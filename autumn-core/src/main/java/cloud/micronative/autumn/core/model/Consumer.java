package cloud.micronative.autumn.core.model;

import lombok.Data;

import java.util.List;

@Data
public class Consumer {
    private String service;
    private List<ConsumerInstance> instances;
}
