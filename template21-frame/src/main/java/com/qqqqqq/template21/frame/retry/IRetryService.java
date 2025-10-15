package com.qqqqqq.template21.frame.retry;

import java.util.function.Supplier;

/**
 * @author qmf
 */
public interface IRetryService {

    <T> T query(Supplier<T> supplier);

}
