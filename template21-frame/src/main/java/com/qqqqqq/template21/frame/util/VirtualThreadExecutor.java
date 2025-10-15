package com.qqqqqq.template21.frame.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author qmf
 */
public class VirtualThreadExecutor {


    private static final ExecutorService VIRTUAL_EXECUTOR =
            Executors.newVirtualThreadPerTaskExecutor();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(VIRTUAL_EXECUTOR::shutdown));
    }

    /**
     * 使用虚拟线程并发执行多个任务，阻塞直到全部完成，按顺序返回结果。
     *
     * @param tasks List<Callable<T>> 任务列表
     * @param <T>   返回值类型
     * @return 所有任务的返回结果（与提交顺序一致）
     * @throws ExecutionException   某个任务抛出异常
     * @throws InterruptedException 被中断
     */
    public static <T> List<T> runTasks(List<Callable<T>> tasks) throws ExecutionException, InterruptedException {
        List<Future<T>> futures = VIRTUAL_EXECUTOR.invokeAll(tasks);
        List<T> results = new ArrayList<>();
        for (Future<T> future : futures) {
            try {
                results.add(future.get());
            } catch (ExecutionException e) {
                throw new ExecutionException("某个任务执行失败," + e.getMessage(), e.getCause());
            }
        }

        return results;
    }
}
