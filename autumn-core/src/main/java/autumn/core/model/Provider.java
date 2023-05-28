package autumn.core.model;

import lombok.Data;

@Data
public class Provider {
    private Boolean enabled;
    private String port;
    private Integer thread;
}
