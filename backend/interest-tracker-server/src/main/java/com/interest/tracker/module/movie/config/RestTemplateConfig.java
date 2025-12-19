package com.interest.tracker.module.movie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;

/**
 * RestTemplate 配置
 *
 * @author interest-tracker
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        
        // 增加连接超时时间到 15 秒（网络不稳定时可能需要更长时间）
        factory.setConnectTimeout((int) Duration.ofSeconds(15).toMillis());
        // 增加读取超时时间到 30 秒（API 响应可能较慢）
        factory.setReadTimeout((int) Duration.ofSeconds(30).toMillis());
        
        // 尝试使用系统代理（如果 VPN 设置了系统代理，会自动使用）
        // Java 会自动读取系统属性：http.proxyHost, http.proxyPort, https.proxyHost, https.proxyPort
        // 如果 VPN 设置了系统代理，这些属性会被自动设置
        String httpProxyHost = System.getProperty("http.proxyHost");
        String httpProxyPort = System.getProperty("http.proxyPort");
        String httpsProxyHost = System.getProperty("https.proxyHost");
        String httpsProxyPort = System.getProperty("https.proxyPort");
        
        // 如果检测到系统代理，使用系统代理
        if (httpsProxyHost != null && httpsProxyPort != null) {
            try {
                int port = Integer.parseInt(httpsProxyPort);
                factory.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(httpsProxyHost, port)));
            } catch (NumberFormatException e) {
                // 忽略端口解析错误，使用默认配置
            }
        } else if (httpProxyHost != null && httpProxyPort != null) {
            try {
                int port = Integer.parseInt(httpProxyPort);
                factory.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(httpProxyHost, port)));
            } catch (NumberFormatException e) {
                // 忽略端口解析错误，使用默认配置
            }
        }
        
        return new RestTemplate(factory);
    }

}

