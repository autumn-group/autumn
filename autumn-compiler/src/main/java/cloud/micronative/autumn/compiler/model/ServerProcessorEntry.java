package cloud.micronative.autumn.compiler.model;

import lombok.Data;

@Data
public class ServerProcessorEntry {
    private String implClassName;
    private String interfaceName;
    private String paramName;
    private String registerName;
}
