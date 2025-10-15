package com.qqqqqq.template21.frame.retry.impl;

import com.qqqqqq.template21.frame.retry.IRetryService;
import com.qqqqqq.template21.frame.retry.annotation.QueryRetryable;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

/**
 * @author qmf
 */
@Service
public class RetryServiceImpl implements IRetryService {

    @Override
    @QueryRetryable
    public <T> T query(Supplier<T> supplier) {
        return supplier.get();
    }
}
