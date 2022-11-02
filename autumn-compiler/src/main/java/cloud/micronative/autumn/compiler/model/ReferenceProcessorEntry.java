package cloud.micronative.autumn.compiler.model;

import lombok.Data;

import java.util.List;

@Data
public class ReferenceProcessorEntry {
    private String referApplication;
    private String localApplication;
    private String interfaceName;
    private Integer minConnection;
    private Integer maxConnection;
    private Integer idleConnection;
    private String targetPackage;
    private List<MethodElement> methodElements;
}
