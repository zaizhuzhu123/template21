package com.qqqqqq.template21.frame.retry.annotation;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.HttpServerErrorException;

import java.lang.annotation.*;
import java.net.UnknownHostException;

/**
 * 仅用于幂等查询类接口（GET等），自动重试网络层与5xx错误
 *
 * @author qmf
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Retryable(
        retryFor = {
                // 网络闪断、超时
                ResourceAccessException.class,
                // 服务端5xx错误
                HttpServerErrorException.class,
                //dns问题
                UnknownHostException.class
        },
        maxAttempts = 3,
        backoff = @Backoff(delay = 200, multiplier = 2.0),
        listeners = "queryRetryLogger"
)
public @interface QueryRetryable {
}
