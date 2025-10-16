package com.qqqqqq.template21.s3.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;
import java.time.Duration;

/**
 * @author laien
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "s3")
public class Template21S3Config {

    private String endpointUrl;

    private String accessKey;

    private String secretKey;

    private String bucketName;

    private String bucketProxy;

    @Bean
    public S3AsyncClient s3AsyncClient() {
        // 1. 创建S3异步客户端
        // 配置 Netty 异步 HTTP 客户端，设置最大连接数
        SdkAsyncHttpClient asyncHttpClient = NettyNioAsyncHttpClient.builder()
                // 设置最大连接数
                .maxConcurrency(100)
                // 连接获取超时
                .connectionAcquisitionTimeout(Duration.ofSeconds(60))
                // 读取超时
                .readTimeout(Duration.ofSeconds(60)).build();
        // 配置重试策略
        final ClientOverrideConfiguration clientOverrideConfiguration = ClientOverrideConfiguration.builder().retryStrategy(RetryMode.STANDARD).build();
        return S3AsyncClient.builder()
                // R2推荐使用 "auto" 作为region
                .region(Region.of("auto")).endpointOverride(URI.create(endpointUrl)).overrideConfiguration(clientOverrideConfiguration).credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))).httpClient(asyncHttpClient).serviceConfiguration(b -> b.chunkedEncodingEnabled(false).checksumValidationEnabled(false)).build();
    }


    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder().endpointOverride(URI.create(endpointUrl)).region(Region.of("auto")).credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))).build();
    }

}
