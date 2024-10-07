package autumn.core.test.discovery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

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
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<InetAddress> inetAddressEnumeration = networkInterface.getInetAddresses();

                log.info("interface name:{}, display-name:{}, addr:{}", networkInterface.getName(), networkInterface.getDisplayName(), networkInterface.getInetAddresses());
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void init() {
        Properties properties = CommonUtil.readClasspath("application.properties");
        ApplicationConfig applicationConfig = ApplicationConfig.getInstance();
        applicationConfig.init(properties);
        ProviderConfig providerConfig = ProviderConfig.getInstance();
        providerConfig.init(properties);
        MulticastDiscovery multicastDiscovery = MulticastDiscovery.getInstance();
        multicastDiscovery.init();

        try {
            System.in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSender() {
        MulticastSocket sendSocket = null;
        try {
            sendSocket = new MulticastSocket();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        new Thread(new Send2(sendSocket)).start();
        try {
            System.in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testReceive() {
        MulticastSocket receSocket = null;
        try {
            receSocket = new MulticastSocket(6789);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        new Thread(new Rece2(receSocket)).start();
        log.info("begin receive listening");
        try {
            System.in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    class Rece2 implements Runnable {
        private MulticastSocket socket;

        public Rece2(MulticastSocket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                byte[] buff = new byte[1024];
                InetAddress group = InetAddress.getByName("228.5.6.7");
                socket.joinGroup(group);
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buff, buff.length);
                    socket.receive(packet);
                    String data = new String(packet.getData(), 0, packet.getLength());
                    String ip = packet.getAddress().getHostAddress();
                    if ("bye".equals(data)) {
                        log.info("ip:{}, away", ip);
                        continue;
                    }
                    log.info("ip: {}, say: {}", ip, data);
                }
            } catch (IOException e) {
                log.warn("receive fail exception: {}", e);
                throw new RuntimeException("receive fail!");
            }
        }
    }

    class Send2 implements Runnable {
        private MulticastSocket socket;

        public Send2(MulticastSocket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                InetAddress group = InetAddress.getByName("228.5.6.7");
                socket.joinGroup(group);
                String line = "Hello: ".concat(UUID.randomUUID().toString());
                while (true) {
                    byte[] buff = line.getBytes();
                    DatagramPacket packet = new DatagramPacket(buff, buff.length, group, 30881);
                    log.info("send message:{}", line);
                    socket.send(packet);
                    Thread.sleep(1000L);
                    if ("bye".equals(line)) {
                        break;
                    }
                }
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException("发送端失败");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    void test3() {
        InetAddress group = null;
        try {
            group = InetAddress.getByName("224.0.0.1");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        int port = 5555;
        MulticastSocket ms = null;

        try {
            ms = new MulticastSocket(port);
            ms.joinGroup(group);//加入到组播组
            while (true) {
                String message = "Hello " + new java.util.Date();
                byte[] buffer = message.getBytes();
                DatagramPacket dp = new DatagramPacket(buffer, buffer.length, group, port);
                ms.send(dp);//发送组播数据报
                System.out.println("发送数据报给" + group + ":" + port);
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ms != null) {
                try {
                    ms.leaveGroup(group);
                    ms.close();
                } catch (IOException e) {
                    log.warn("exception: ", e);
                }

            }
        }
    }

    @Test
    void test4() {
        InetAddress group = null;
        try {
            group = InetAddress.getByName("224.0.0.1");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        int port = 5555;
        MulticastSocket ms = null;
        try {
            ms = new MulticastSocket(port);
            ms.joinGroup(group);
            byte[] buffer = new byte[8192];
            while (true) {
                DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                ms.receive(dp);
                String s = new String(dp.getData(), 0, dp.getLength());
                System.out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ms != null) {
                try {
                    ms.leaveGroup(group);
                    ms.close();
                } catch (IOException e) {
                    log.warn("exception: ", e);
                }
            }
        }
    }

}
