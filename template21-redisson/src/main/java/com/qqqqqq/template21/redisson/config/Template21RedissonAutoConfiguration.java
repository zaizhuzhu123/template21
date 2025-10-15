package com.qqqqqq.template21.redisson.config;


//import lombok.RequiredArgsConstructor;

import com.alibaba.cola.exception.Assert;
import com.qqqqqq.template21.redisson.config.namemapper.Template21RedissonNameMapper;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.starter.RedissonProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.IOException;


/**
 * @author qmf
 */
@Configuration
@ComponentScan(value = "com.qqqqqq.template21.redisson")
@EnableConfigurationProperties(RedissonProperties.class)
@RequiredArgsConstructor
public class Template21RedissonAutoConfiguration {

    private final Environment environment;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(RedissonProperties redissonProperties) throws IOException {
        Config config = null;
        try {
            config = Config.fromYAML(redissonProperties.getConfig());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 设置全局前缀
        String applicationName = environment.getProperty("spring.application.name");
        Assert.notNull(applicationName, "applicationName cannot be empty!");
        if (config.isClusterConfig()) {
            config.useClusterServers().setNameMapper(new Template21RedissonNameMapper(applicationName));
        } else {
            config.useSingleServer().setNameMapper(new Template21RedissonNameMapper(applicationName));
        }
        return Redisson.create(config);
    }
}
