package autumn.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.apache.hc.core5.util.Timeout;
import org.yaml.snakeyaml.Yaml;

import autumn.core.model.AutumnConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonUtil {
    public static CloseableHttpClient httpClient = null;

    public final static String CONFIG_PATH = "/opt/autumn/autumn-config.yml";

    private static AutumnConfig config;
    private CommonUtil() {

    }

    static {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(3000L))
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(10000L))
                .setResponseTimeout(Timeout.ofMilliseconds(10000L))
                .build();
        try {
            httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(config)
                    .setConnectionManager(getHttpClientConnectionManager())
                    .build();
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            httpClient = HttpClients.createDefault();
            log.warn("http client config exception: {}", e);
        }
    }

    private static HttpClientConnectionManager getHttpClientConnectionManager() throws NoSuchAlgorithmException,
            KeyStoreException,
            KeyManagementException {
        return PoolingHttpClientConnectionManagerBuilder.create()
                .setMaxConnTotal(50)
                .setMaxConnPerRoute(10)
                .setSSLSocketFactory(getSslConnectionSocketFactory())
                .build();
    }

    private static SSLConnectionSocketFactory getSslConnectionSocketFactory() throws NoSuchAlgorithmException,
            KeyStoreException,
            KeyManagementException {
        TrustStrategy acceptingTrustStrategy = (x509Certificates, s) -> true;
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        return new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
    }

    public static String getConnectionPath(String service, String ipPort) {
        String interfacePath = service.concat("/")
                .concat(ipPort);
        return interfacePath;
    }

    public static AutumnConfig getConfig() {
        if(null != config) {
            return config;
        }
        config = readConfig();
        return config;
    }

    public static AutumnConfig readConfig() {
        File file = new File(CONFIG_PATH);
        if(!file.exists()) {
            String path = CommonUtil.class.getClassLoader().getResource("").getPath();
            file = new File(path.concat("/autumn-config.yml"));
        }
        Yaml yaml = new Yaml();
        AutumnConfig config = null;
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            config = yaml.loadAs(new FileInputStream(file), AutumnConfig.class);
        } catch (IOException e) {
            log.warn("config file not exist, default path: /opt/autumn/autumn-config.yml. please check autumn-config.yml!", e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        log.info("read config:{}", config);
        return config;
    }

    public static String getHostIpAddress() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface =  allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                }

                if (!netInterface.getDisplayName().contains("Intel")
                        && !netInterface.getDisplayName().contains("Realtek")
                        && !netInterface.getDisplayName().contains("Atheros")
                        && !netInterface.getDisplayName().contains("Broadcom")) {
                    continue;
                }
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress ip = addresses.nextElement();
                    if (ip != null) {
                        if (ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
                break;
            }
        } catch (SocketException e) {
            e.getMessage();
        }
        return null;
    }

    public static NetworkInterface getNetIf() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface =  allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                }

                if (!netInterface.getDisplayName().contains("Intel")
                        && !netInterface.getDisplayName().contains("Realtek")
                        && !netInterface.getDisplayName().contains("Atheros")
                        && !netInterface.getDisplayName().contains("Broadcom")) {
                    continue;
                }
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress ip = addresses.nextElement();
                    if (ip != null) {
                        if (ip instanceof Inet4Address) {
                            return netInterface;
                        }
                    }
                }
                break;
            }
        } catch (SocketException e) {
            e.getMessage();
        }
        return null;
    }

    public static String doPut(String url, Map<String, String> uriParams, String json) {
        String result = null;
        HttpPut httpPut = new HttpPut(url);
        httpPut.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        if(Objects.nonNull(uriParams) && uriParams.size() > 0) {
            List<NameValuePair> nvps = new ArrayList<>();
            uriParams.forEach((k, v) -> {
                nvps.add(new BasicNameValuePair(k, v));
            });
            URI uri = null;
            try {
                uri = new URIBuilder(new URI(url))
                        .addParameters(nvps)
                        .build();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            httpPut.setUri(uri);
        }

        try {
            CloseableHttpResponse response = httpClient.execute(httpPut);
            result = EntityUtils.toString(response.getEntity());
        } catch (IOException | ParseException e) {
            if(log.isWarnEnabled()) {
                log.warn("http client do-put exception:{}", e);
            }
        }
        return result;
    }


}
