package autumn.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    public static String providerToQueryString(ProviderConfig provider) {
        String queryString = "";
        if(Objects.isNull(provider)) {
            return queryString;
        }
        if(Objects.isNull(provider.getEnabled())) {
            queryString = queryString.concat("enabled=")
                    .concat(provider.getEnabled().toString())
                    .concat("&");
        }
        if(Objects.isNull(provider.getName())) {
            queryString = queryString.concat("name=")
                    .concat(provider.getName())
                    .concat("&");
        }
        if(Objects.isNull(provider.getThread())) {
            queryString = queryString.concat("thread=")
                    .concat(provider.getThread().toString())
                    .concat("&");
        }
        if(Objects.isNull(provider.getPort())) {
            queryString = queryString.concat("port=")
                    .concat(provider.getPort().toString())
                    .concat("&");
        }

        if(queryString.length() > 0) {
            queryString = queryString.substring(0, queryString.length() - 1);
        }

        return queryString;
    }

    public static ProviderConfig queryStringToProvider(String queryString) {
        Map<String, String> mapping = getUrlParams(queryString);
        if(mapping.isEmpty()) {
            return null;
        }

        ProviderConfig provider = new ProviderConfig();
        if(mapping.containsKey("enabled")) {
            Boolean enabled = Boolean.valueOf(mapping.get("enabled"));
            provider.setEnabled(enabled);
        }
        if(mapping.containsKey("port")) {
            Integer port = Integer.valueOf(mapping.get("port"));
            provider.setPort(port);
        }
        if(mapping.containsKey("thread")) {
            Integer thread = Integer.valueOf(mapping.get("thread"));
            provider.setThread(thread);
        }
        if(mapping.containsKey("name")) {
            String name = mapping.get("name");
            provider.setName(name);
        }
        return provider;
    }
}
