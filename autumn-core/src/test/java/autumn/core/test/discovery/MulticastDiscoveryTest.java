package autumn.core.test.discovery;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import autumn.core.config.ApplicationConfig;
import autumn.core.config.ProviderConfig;
import autumn.core.discovery.MulticastDiscovery;
import autumn.core.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: baoxin.zhao
 * @date: 2024/9/30
 */
@Slf4j
public class MulticastDiscoveryTest {

    @Test
    void testNetworkInterface() {
        try {
            Enumeration<NetworkInterface> interfaces =  NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<InetAddress> inetAddressEnumeration = networkInterface.getInetAddresses();

                log.info("interface name:{}, display-name:{}, addr:{}", networkInterface.getName(), networkInterface.getDisplayName(), networkInterface.getInetAddresses());
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }


    void init() {
        Properties properties = CommonUtil.readClasspath("application.properties");
        ApplicationConfig applicationConfig = ApplicationConfig.getInstance();
        applicationConfig.init(properties);
        ProviderConfig providerConfig = ProviderConfig.getInstance();
        providerConfig.init(properties);
        MulticastDiscovery multicastDiscovery = MulticastDiscovery.getInstance();
        multicastDiscovery.init();
    }

}
