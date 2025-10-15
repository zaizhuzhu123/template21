package com.template21.frame.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * @author qmf
 */
@Configuration
public class RetryConfig {

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        // 每次重试间隔
        backOffPolicy.setBackOffPeriod(5000);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        // 重试 3 次
        retryPolicy.setMaxAttempts(5);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

}
