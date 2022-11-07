package cloud.micronative.autumn.controller.model;

import lombok.Data;

@Data
public class ConsumerInstance {
    private String ip;
    private String port;
    private Integer connections;
}
