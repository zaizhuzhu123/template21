package com.qqqqqq.template21.runpod.config;

import com.qqqqqq.template21.runpod.api.RunpodApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.*;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;


/**
 * @author qmf
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class Template21RunpodApiConfiguration {

    @Bean
    public RunpodApi userApi() {
        // 使用 JDK 内置 HttpClient（JDK 21 OK），可设置连接/读超时
        HttpClient jdkClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(jdkClient);
        ClientHttpRequestFactory buffering = new BufferingClientHttpRequestFactory(requestFactory);
        requestFactory.setReadTimeout(Duration.ofSeconds(10));
        final RestClient restClient = RestClient
                .builder()
                .requestFactory(buffering)
                .defaultHeader("Accept", "application/json")
                .requestInterceptor(loggingInterceptor())
                .build();
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(RunpodApi.class);
    }

    /**
     * 日志拦截器：记录方法、URL、头、请求/响应体摘要、状态码与耗时
     */
    private ClientHttpRequestInterceptor loggingInterceptor() {
        // 配置：是否打印 body 与最大长度（避免日志过大）
        final boolean logRequestBody = true;
        final boolean logResponseBody = true;
        // 字符数上限
        final int maxBody = 1000;
        return (request, body, execution) -> {
            long start = System.nanoTime();

            // ---- 请求日志 ----
            String method = String.valueOf(request.getMethod());
            String url = request.getURI().toString();
            String reqHeaders = headersToLogString(request.getHeaders());
            String reqBody = (logRequestBody && body != null && body.length > 0)
                    ? trim(new String(body, StandardCharsets.UTF_8), maxBody)
                    : "";

            if (!reqBody.isEmpty()) {
                log.info("→ {} {}\nHeaders: {}\nBody: {}", method, url, reqHeaders, reqBody);
            } else {
                log.info("→ {} {}\nHeaders: {}", method, url, reqHeaders);
            }

            // 执行请求
            ClientHttpResponse response = execution.execute(request, body);

            long tookMs = (System.nanoTime() - start) / 1_000_000;

            // ---- 响应日志 ----
            String status = response.getStatusCode().value() + " " + response.getStatusCode();
            String respHeaders = headersToLogString(response.getHeaders());
            String respBody = "";
            if (logResponseBody) {
                try {
                    respBody = trim(StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8), maxBody);
                } catch (Exception ignore) {
                }
            }
            if (!respBody.isEmpty()) {
                log.info("← {} ({} ms)\nHeaders: {}\nBody: {}", status, tookMs, respHeaders, respBody);
            } else {
                log.info("← {} ({} ms)\nHeaders: {}", status, tookMs, respHeaders);
            }

            return response;
        };
    }

    /**
     * 头部转字符串并**脱敏**敏感头（Authorization、Cookie 等）
     */
    private String headersToLogString(HttpHeaders headers) {
        HttpHeaders copy = new HttpHeaders();
        copy.putAll(headers);
        mask(copy, List.of(HttpHeaders.AUTHORIZATION, HttpHeaders.COOKIE, "Set-Cookie"));
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> e : copy.entrySet()) {
            sb.append(e.getKey()).append(": ").append(e.getValue()).append("; ");
        }
        return sb.toString();
    }

    private void mask(HttpHeaders headers, List<String> keys) {
        for (String k : keys) {
            if (headers.containsKey(k)) {
                headers.put(k, List.of("******"));
            }
        }
    }

    private String trim(String s, int max) {
        if (s == null) {
            return "";
        }
        return s.length() <= max ? s : (s.substring(0, max) + "… (truncated)");
    }
}
