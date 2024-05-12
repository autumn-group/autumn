package autumn.core.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import autumn.core.util.CommonUtil;
import autumn.core.util.ConsulClient;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: baoxin.zhao
 * @date: 2024/5/12
 */

@Slf4j
public class ConsulClientTest {

    @Test
    void test() {
        ConsulClient client = ConsulClient.getInstance();
        String ip = CommonUtil.getHostIpAddress();
        Map<String, String> meta = new HashMap<>();
        meta.put("a", "1");
        meta.put("b", "2");
        String result = client.handleRequestBody("a", "b", Arrays.asList("primary", "dev"), ip, 30880, meta);
        log.info("result:{}", result);
    }

    @Test
    void testRegister() {
        String ip = CommonUtil.getHostIpAddress();
        ConsulClient client = ConsulClient.getInstance();
        String consulUrl = "http://127.0.0.1:8500/v1/agent/service/register";
        client.register(consulUrl, "a", "b", null, ip, 30880, null);
    }

}
