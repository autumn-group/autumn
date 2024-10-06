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
    public final static String CONSTANT_REGISTRY= "registry";
    public final static String CONSTANT_PROTOCOL= "_protocol_";
    public final static String CONSTANT_URL_PATH= "_url_path_";
    public final static String MULTICAST_REQUEST= "multicast-request";
    public final static String MULTICAST_RESPONSE= "multicast-response";
    private ConverterUtil() {

    }

    public static Map<String, String> getUrlParams(String url) {
        Map<String, String> map = new HashMap<>(0);
        if (Objects.isNull(url)) {
            return map;
        }

        String[] parts_1= url.split("://");
        if(parts_1.length > 1) {
            String _protocol = parts_1[0];
            map.put(CONSTANT_PROTOCOL, _protocol);
            String parts_2 = parts_1[1];
            String queryString = handleUrlPath(map, parts_2);
            handleQueryString(map, queryString);
        } else {
            String parts_2 = parts_1[0];
            String queryString = handleUrlPath(map, parts_2);
            handleQueryString(map, queryString);
        }
        return map;
    }

    private static String handleUrlPath(Map<String, String> map, String urlPath) {
        String[] params = urlPath.split("?");
        if(params.length > 1) {
            String part_1 = params[0];
            map.put(CONSTANT_URL_PATH, part_1);
            String part_2 = params[1];
            return part_2;
        }

        return params[0];
    }

    private static void handleQueryString(Map<String, String> map, String queryString) {
        String[] params = queryString.split("&");
        for (int i = 0; i < params.length; i++) {
            String[] p = params[i].split("=");
            if (p.length == 2) {
                map.put(p[0], p[1]);
            }
        }
    }

    public static String registryResponse(ProviderConfig config) {
        String queryString = CONSTANT_REGISTRY.concat("://")
                .concat(MULTICAST_RESPONSE)
                .concat("?");
        if(Objects.nonNull(config.getName())) {
            queryString = queryString.concat("name=")
                    .concat(config.getName())
                    .concat("&");
        }
        if(Objects.nonNull(config.getIp())) {
            queryString = queryString.concat("ip=")
                    .concat(config.getIp())
                    .concat("&");
        }
        if(Objects.nonNull(config.getPort())) {
            queryString = queryString.concat("port=")
                    .concat(config.getPort().toString())
                    .concat("&");
        }

        if(queryString.length() > 0) {
            queryString = queryString.substring(0, queryString.length() - 1);
        }

        return queryString;
    }

    public static String registryRequest(ProviderConfig config) {
        String queryString = CONSTANT_REGISTRY.concat("://")
                .concat(MULTICAST_REQUEST)
                .concat("?");
        if(Objects.nonNull(config.getName())) {
            queryString = queryString.concat("name=")
                    .concat(config.getName())
                    .concat("&");
        }
        if(Objects.nonNull(config.getIp())) {
            queryString = queryString.concat("ip=")
                    .concat(config.getIp())
                    .concat("&");
        }
        if(Objects.nonNull(config.getPort())) {
            queryString = queryString.concat("port=")
                    .concat(config.getPort().toString())
                    .concat("&");
        }

        if(queryString.length() > 0) {
            queryString = queryString.substring(0, queryString.length() - 1);
        }

        return queryString;
    }


    public static ConsumerConfig queryStringToProvider(String url) {
        Map<String, String> mapping = getUrlParams(url);
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
