package autumn.core.model;

import lombok.Data;

import java.util.List;

@Data
public class Consumer {
    private Boolean enabled;
    List<Service> services;
}
