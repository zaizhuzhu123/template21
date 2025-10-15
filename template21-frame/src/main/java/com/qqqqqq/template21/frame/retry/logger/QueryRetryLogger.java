package com.qqqqqq.template21.frame.retry.logger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Component;

/**
 * @author qmf
 */
@Component
@Slf4j
public class QueryRetryLogger implements RetryListener {

    @Override
    public <T, E extends Throwable> boolean open(RetryContext ctx, RetryCallback<T, E> callback) {
        // 第一次调用前
        return true;
    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext ctx, RetryCallback<T, E> callback, Throwable throwable) {
        int count = ctx.getRetryCount();
        log.info("[Retry] attempt {} due to {}", count, throwable.getClass().getSimpleName());
    }

    @Override
    public <T, E extends Throwable> void close(RetryContext ctx, RetryCallback<T, E> callback, Throwable throwable) {
        if (throwable != null) {
            log.info("[Retry] finished after {} attempts, last error={}", ctx.getRetryCount(), throwable.getClass().getSimpleName());
        }
    }
}
