package autumn.core.discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import autumn.core.config.ProviderConfig;
import autumn.core.pool.AutumnPool;
import autumn.core.util.CommonUtil;
import autumn.core.util.ConverterUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import static java.net.StandardSocketOptions.IP_MULTICAST_LOOP;

/**
 * @author: baoxin.zhao
 * @date: 2024/9/30
 */
@Slf4j
@Data
public class MulticastDiscovery {
    private volatile static MulticastDiscovery singleton = null;
    private String ip;
    private Integer port;
    private Set<String> refers = new HashSet<>();

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

    private void init() {
        this.ip = "224.5.6.7";
        this.port = 30881;
    }

    public void addRefer(String refer) {
        refers.add(refer);
    }

    public void discovery() {
        try {
            SocketAddress socketAddress = new InetSocketAddress(ip, port);
            MulticastSocket socket = new MulticastSocket();
            socket.joinGroup(socketAddress, CommonUtil.getNetIf());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void registry() {
        Runnable runnable = () -> {
            try {
                MulticastSocket socket = new MulticastSocket(this.port);
                socket.setOption(IP_MULTICAST_LOOP, false);
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

    private void receive(String ip, String data) {
        log.info("multicast discovery receive data, ip:{}, data:{}", ip, data);
        ProviderConfig provider = ConverterUtil.queryStringToProvider(data);

    }

}
