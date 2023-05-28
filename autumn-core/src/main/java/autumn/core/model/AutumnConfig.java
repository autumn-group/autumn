package autumn.core.model;

import lombok.Data;

import java.util.List;

@Data
public class AutumnConfig {
    private Compiler compiler;
    private Consumer consumer;
    private Provider provider;
}
