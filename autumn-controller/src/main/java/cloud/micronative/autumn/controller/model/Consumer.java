package cloud.micronative.autumn.controller.model;

import lombok.Data;

import java.util.List;

@Data
public class Consumer {
    private String service;
    private List<ConsumerInstance> instances;
}
