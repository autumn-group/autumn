package cloud.micronative.autumn.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * export service
 */
@Target({ElementType.TYPE})
@Retention(RUNTIME)
public @interface Export {
}
