package autumn.core.discovery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import autumn.core.util.CommonUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: baoxin.zhao
 * @date: 2024/9/30
 */
@Slf4j
public class MulticastDiscovery {
    @Setter
    @Getter
    private String ip = "224.5.6.7";
    @Setter
    @Getter
    private Integer port = 30881;
    @Setter
    @Getter
    private Integer networkInterfaceIndex = 0;

    public void discovery() {
        try {
            SocketAddress socketAddress = new InetSocketAddress(ip, port);
            MulticastSocket socket = new MulticastSocket();
            NetworkInterface d;
            socket.joinGroup(socketAddress, CommonUtil.getNetIf());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }


}
