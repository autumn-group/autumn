package cloud.micronative.autumn.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * reference remote service
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RUNTIME)
public @interface Reference {
    /**
     * register center service name
     *
     * @return
     */
    String refer() default "";

    /**
     * direct connect remote ip and port
     * @return
     */
    String ipPort() default "";
}
