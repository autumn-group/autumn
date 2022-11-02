package cloud.micronative.autumn.compiler.util;

import org.apache.commons.lang3.StringUtils;

public class ClassNameUtil {

    public static String getSimpleClassName(String className) {
        int lastDot = className.lastIndexOf('.');
        String simpleClassName = className.substring(lastDot + 1);
        return simpleClassName;
    }

    public static String getPackageName(String className) {
        String packageName = "";
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }
        return packageName;
    }

    public static String getParamName(String simpleClassName) {
        if(StringUtils.isEmpty(simpleClassName)) {
            return simpleClassName;
        }
        if(simpleClassName.length() == 1) {
            return simpleClassName.toLowerCase();
        }
        String beginName = simpleClassName.substring(0, 1);
        String endName = simpleClassName.substring(1);
        return beginName.toLowerCase() + endName;
    }
}
