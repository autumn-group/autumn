package autumn.core.discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import autumn.core.config.ApplicationConfig;
import autumn.core.config.ConsumerConfig;
import autumn.core.config.ProviderConfig;
import autumn.core.config.ReferenceConfig;
import autumn.core.pool.AutumnPool;
import autumn.core.util.CommonUtil;
import autumn.core.util.ConverterUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: baoxin.zhao
 * @date: 2024/9/30
 */
@Slf4j
@Getter
public class MulticastDiscovery {
    private volatile static MulticastDiscovery singleton = null;
    private ConcurrentHashMap<String, ReferenceConfig> refers = new ConcurrentHashMap<>();
    private AtomicBoolean initStatus = new AtomicBoolean(false);
    private MulticastSocket mc;
    private MulticastDiscovery() {

    }

    public static MulticastDiscovery getInstance() {
        if (singleton == null) {
            synchronized (AutumnPool.class) {
                if (singleton == null) {
                    singleton = new MulticastDiscovery();
                    singleton.init();
                    return singleton;
                }
            }
        }
        return singleton;
    }

    public void init() {
        if(Boolean.TRUE.equals(initStatus.get())) {
            return;
        }
        initStatus.compareAndSet(false, true);
        ApplicationConfig applicationConfig = ApplicationConfig.getInstance();
        String ip = applicationConfig.getMulticastIp();
        Integer port = applicationConfig.getMulticastPort();

        try {
            mc = new MulticastSocket(port);
            InetAddress group = InetAddress.getByName(ip);
            MulticastSocket mcs = new MulticastSocket(port);
            discovery(mc, group, port);
            registry(mcs, group, port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addRefer(String refer) {
        if(refers.contains(refer)) {
            return;
        }
        ReferenceConfig referenceConfig = new ReferenceConfig();
        referenceConfig.setName(refer);
        referenceConfig.setNamespace("default");
        referenceConfig.setInstances(new CopyOnWriteArrayList<>());
        refers.put(refer, referenceConfig);
    }

    public void addInstance(String name, ConsumerConfig consumerConfig) {
        if(!refers.contains(name)) {
            addRefer(name);
        }
        ReferenceConfig referenceConfig = refers.get(name);
        List<ConsumerConfig> consumers = referenceConfig.getInstances();
        if(consumers.contains(consumerConfig)) {
            return;
        }
        consumers.add(consumerConfig);
    }

    private void registry(MulticastSocket ms, InetAddress group, Integer port) {
        ProviderConfig config = ProviderConfig.getInstance();
        String registryRequest = ConverterUtil.registryRequest(config);
        try {
            ms.joinGroup(group);
            byte[] buffer = registryRequest.getBytes();
            DatagramPacket dp = new DatagramPacket(buffer, buffer.length, group, port);
            ms.send(dp);
            Arrays.fill(buffer, (byte) 0);
        } catch (Exception e) {
            log.warn("autumn-multicast-registry receive exception: ", e);
        } finally {
            if (ms != null) {
                try {
                    ms.leaveGroup(group);
                    ms.close();
                } catch (IOException e) {
                    log.warn("autumn-multicast-registry receive exception: ", e);
                }

            }
        }
    }

    private void discovery(MulticastSocket ms, InetAddress group, Integer port) {
        Runnable runnable = () -> {
            try {
                ms.joinGroup(group);
                byte[] buffer = new byte[8192];
                while (true) {
                    DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                    ms.receive(dp);
                    String ip = dp.getAddress().getHostAddress();
                    String data = new String(dp.getData(), 0, dp.getLength());
                    int i = data.indexOf('\n');
                    if (i > 0) {
                        data = data.substring(0, i).trim();
                    }
                    receive(ip, data);
                    Arrays.fill(buffer, (byte) 0);

                    Map<String, String> params = CommonUtil.getUrlParams(data);
                    if(ConverterUtil.MULTICAST_REQUEST.equals(params.get(ConverterUtil.CONSTANT_URL_PATH))) {
                        ProviderConfig config = ProviderConfig.getInstance();
                        String registryResponse = ConverterUtil.registryResponse(config);
                        byte[] sendBuff = registryResponse.getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(sendBuff, sendBuff.length, group, port);
                        mc.send(sendPacket);
                        Arrays.fill(sendBuff, (byte) 0);
                    }
                }
            } catch (IOException e) {
                log.warn("autumn-multicast-discovery receive exception: ", e);
            } finally {
                if (ms != null) {
                    try {
                        ms.leaveGroup(group);
                        ms.close();
                    } catch (IOException e) {
                        log.warn("autumn-multicast-discovery receive exception: ", e);
                    }
                }
            }
        };

        Thread thread = new Thread(runnable, "autumn-multicast-registry-receiver");
        thread.setDaemon(true);
        thread.start();
        log.info("autumn-multicast-registry begin listening");
    }

    private void checkMulticastAddress(InetAddress multicastAddress) {
        if (!multicastAddress.isMulticastAddress()) {
            String message = "Invalid multicast address " + multicastAddress;
            if (multicastAddress instanceof Inet4Address) {
                throw new IllegalArgumentException(message + ", " +
                        "ipv4 multicast address scope: 224.0.0.0 - 239.255.255.255.");
            } else {
                throw new IllegalArgumentException(message + ", " + "ipv6 multicast address must start with ff, " +
                        "for example: ff01::1");
            }
        }
    }

    private void receive(String ip, String data) {
        log.info("multicast discovery receive data, ip:{}, data:{}", ip, data);
        ConsumerConfig multicastConfig = ConverterUtil.queryStringToProvider(data);
        addInstance(multicastConfig.getName(), multicastConfig);
    }

}
