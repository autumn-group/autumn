package cloud.micronative.autumn.core.pool.impl;

import lombok.Data;

@Data
public class ConnectionConfig {
    private String service;
    private String ip;
    private String port;
}
