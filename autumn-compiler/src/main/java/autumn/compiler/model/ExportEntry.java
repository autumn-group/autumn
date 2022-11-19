package autumn.compiler.model;

import lombok.Data;

@Data
public class ExportEntry {
    private String implClassName;
    private String interfaceName;
    private String paramName;
    private String registerName;
}
