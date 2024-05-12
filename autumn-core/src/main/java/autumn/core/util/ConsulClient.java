package autumn.core.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;

import autumn.core.config.RegistryConfig;

/**
 * @author: baoxin.zhao
 * @date: 2024/5/12
 */
public class ConsulClient {
    private static volatile ConsulClient instance;
    private ConsulClient() {

    }
    public static ConsulClient getInstance() {
        if(Objects.isNull(instance)) {
            synchronized (ConsulClient.class) {
                if(Objects.isNull(instance)) {
                    instance = new ConsulClient();
                }
            }
        }
        return instance;
    }

    public Boolean register(String consulUrl,
                            String instanceId,
                            String name,
                            List<String> tags,
                            String address,
                            Integer port,
                            Map<String, String> meta) {
        String body = handleRequestBody(instanceId, name, tags, address, port, meta);
        Map<String, String> uriParams = new HashMap<>();
        uriParams.put("replace-existing-check", "true");
        CommonUtil.doPut(consulUrl, uriParams, body);
        return false;
    }

    public String handleRequestBody(String instanceId,
                                     String name,
                                     List<String> tags,
                                     String address,
                                     Integer port,
                                     Map<String, String> meta) {
        String tcp = address.concat(":").concat(port.toString());
        String tagString = "";

        if(Objects.nonNull(tags) && tags.size() > 0) {
            for (String it: tags) {
                tagString = tagString.concat("\"")
                        .concat(it)
                        .concat("\", ");
            }
            tagString = tagString.substring(0, tagString.length() - 2);
        } else {
            tagString = "";
        }

        String metaString = "";
        if(Objects.nonNull(meta) && meta.size() > 0) {
            for(Map.Entry<String, String> entry: meta.entrySet()) {
                metaString = metaString.concat("\"")
                        .concat(entry.getKey())
                        .concat("\": \"")
                        .concat(entry.getValue())
                        .concat("\", ");
            }
            metaString = metaString.substring(0, metaString.length() - 2);
        } else {
            metaString = "";
        }

        String json = """
                {
                    "ID": "%s",
                    "Name": "%s",
                    "Tags": [%s],
                    "Address": "%s",
                    "Port": %d,
                    "Meta": {%s},
                    "EnableTagOverride": true,
                    "Check": {
                        "DeregisterCriticalServiceAfter": "10m",
                        "TCP": "%s",
                        "Interval": "10s",
                        "Timeout": "5s"
                    },
                  "Weights": {
                    "Passing": 3,
                    "Warning": 1
                  }
                }
                """;
        String result = String.format(json, instanceId, name, tagString, address, port, metaString, tcp);
        return result;
    }

    public void list() {

    }

}
