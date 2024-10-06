package autumn.core.discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;

import autumn.core.config.ApplicationConfig;
import autumn.core.config.ConsumerConfig;
import autumn.core.config.ProviderConfig;
import autumn.core.config.ReferenceConfig;
import autumn.core.pool.AutumnPool;
import autumn.core.util.CommonUtil;
import autumn.core.util.ConverterUtil;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static java.net.StandardSocketOptions.IP_MULTICAST_LOOP;

/**
 * @author: baoxin.zhao
 * @date: 2024/9/30
 */
@Slf4j
@Getter
public class MulticastDiscovery {
    private volatile static MulticastDiscovery singleton = null;
    private String ip;
    private Integer port;
    private MulticastSocket socket;
    private ConcurrentHashMap<String, ReferenceConfig> refers = new ConcurrentHashMap<>();
    private AtomicBoolean initStatus = new AtomicBoolean(false);

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
        InetAddress inetAddress = CommonUtil.getInetAddress();
        this.ip = applicationConfig.getMulticastIp();
        this.port = applicationConfig.getMulticastPort();
        SocketAddress socketAddress = new InetSocketAddress(ip, port);
        try {
            socket = new MulticastSocket(port);
            socket.setOption(IP_MULTICAST_LOOP, false);
            socket.setInterface(inetAddress);
            checkMulticastAddress(inetAddress);
            socket.joinGroup(socketAddress, CommonUtil.getNetIf());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        discovery();
        registry(socketAddress);
    }

    public void addRefer(String refer) {
        if(refers.contains(refer)) {
            return;
        }
        ReferenceConfig referenceConfig = new ReferenceConfig();
        referenceConfig.setName(refer);
        referenceConfig.setNamespace("default");
        referenceConfig.setInstances(new ConcurrentSkipListSet<>());
        refers.put(refer, referenceConfig);
    }

    public void addInstance(String name, ConsumerConfig consumerConfig) {
        if(!refers.contains(name)) {
            addRefer(name);
        }
        ReferenceConfig referenceConfig = refers.get(name);
        ConcurrentSkipListSet<ConsumerConfig> consumers = referenceConfig.getInstances();
        if(consumers.contains(consumerConfig)) {
            return;
        }
        consumers.add(consumerConfig);
    }

    private void registry(SocketAddress socketAddress) {
        ProviderConfig config = ProviderConfig.getInstance();
        String info = ConverterUtil.convertToQueryString(config);
        try {
            byte[] buff = info.getBytes();
            DatagramPacket packet = new DatagramPacket(buff, buff.length, socketAddress);
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void discovery() {
        Runnable runnable = () -> {
            try {
                byte[] buff = new byte[2048];
                DatagramPacket packet = new DatagramPacket(buff, buff.length);
                String ip = packet.getAddress().getHostAddress();
                while (!socket.isClosed()) {
                    socket.receive(packet);
                    String data = new String(packet.getData()).trim();
                    int i = data.indexOf('\n');
                    if (i > 0) {
                        data = data.substring(0, i).trim();
                    }
                    receive(ip, data);
                    Arrays.fill(buff, (byte) 0);
                }
            } catch (IOException e) {
                log.warn("multicast discovery registry receive fail: ", e);
            }
        };

        Thread thread = new Thread(runnable, "autumn-multicast-registry-receiver");
        thread.setDaemon(true);
        thread.start();
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
