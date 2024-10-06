package autumn.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import autumn.core.config.ConsumerConfig;
import autumn.core.config.ProviderConfig;

/**
 * @author: baoxin.zhao
 * @date: 2024/10/1
 */
public class ConverterUtil {
    private ConverterUtil() {

    }

    public static Map<String, String> getUrlParams(String queryString) {
        Map<String, String> map = new HashMap<>(0);
        if (Objects.isNull(queryString)) {
            return map;
        }
        String[] params = queryString.split("&");
        for (int i = 0; i < params.length; i++) {
            String[] p = params[i].split("=");
            if (p.length == 2) {
                map.put(p[0], p[1]);
            }
        }
        return map;
    }

    public static String convertToQueryString(ProviderConfig config) {
        String queryString = "";
        if(Objects.isNull(config)) {
            return queryString;
        }
        if(Objects.isNull(config.getName())) {
            queryString = queryString.concat("name=")
                    .concat(config.getName())
                    .concat("&");
        }
        if(Objects.isNull(config.getIp())) {
            queryString = queryString.concat("ip=")
                    .concat(config.getIp())
                    .concat("&");
        }
        if(Objects.isNull(config.getPort())) {
            queryString = queryString.concat("port=")
                    .concat(config.getPort().toString())
                    .concat("&");
        }

        if(queryString.length() > 0) {
            queryString = queryString.substring(0, queryString.length() - 1);
        }

        return queryString;
    }

    public static ConsumerConfig queryStringToProvider(String queryString) {
        Map<String, String> mapping = getUrlParams(queryString);
        if(mapping.isEmpty()) {
            return null;
        }
        ConsumerConfig config = new ConsumerConfig();
        if(mapping.containsKey("port")) {
            Integer port = Integer.valueOf(mapping.get("port"));
            config.setPort(port);
        }
        if(mapping.containsKey("name")) {
            String name = mapping.get("name");
            config.setName(name);
        }
        if(mapping.containsKey("ip")) {
            String ip = mapping.get("ip");
            config.setIp(ip);
        }
        return config;
    }
}
