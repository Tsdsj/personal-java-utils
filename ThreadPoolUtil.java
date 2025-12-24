package com.tt.picture.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池管理工具类，用于并行处理批量任务。
 *
 * @author tt
 * @date 2025/2/28 9:26
 */
@Slf4j
public class ThreadPoolUtil {

    /**
     * 默认线程池大小, CPU核心数
     */
    private static final int CPU_CORES = Runtime.getRuntime().availableProcessors();

    /**
     * IO密集型任务的线程池大小 = 2 * CPU核心数
     */
    private static final int IO_POOL_SIZE = CPU_CORES * 2;

    /**
     * 计算密集型任务的线程池大小 = CPU核心数 + 1
     */
    private static final int COMPUTE_POOL_SIZE = CPU_CORES + 1;

    /**
     * 批量处理任务的阈值
     */
    private static final int BATCH_THRESHOLD = 1000;

    /**
     * 单批次最大任务数
     */
    private static final int MAX_BATCH_SIZE = 100;

    /**
     * 计算密集型线程池（长期存活）
     */
    private static final ThreadPoolExecutor COMPUTE_EXECUTOR = new ThreadPoolExecutor(
            COMPUTE_POOL_SIZE,
            COMPUTE_POOL_SIZE,
            60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * IO密集型线程池（长期存活）
     */
    private static final ThreadPoolExecutor IO_EXECUTOR = new ThreadPoolExecutor(
            IO_POOL_SIZE,
            IO_POOL_SIZE * 2,
            60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(5000),
            new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * ForkJoin线程池（适合计算密集型递归任务）
     */
    private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool(
            CPU_CORES,
            ForkJoinPool.defaultForkJoinWorkerThreadFactory,
            null, true);

    static {
        // 线程池预热
        warmUpThreadPools();
        // 添加JVM关闭钩子，确保线程池正确关闭
        Runtime.getRuntime().addShutdownHook(new Thread(ThreadPoolUtils::shutdownThreadPools));
    }

    /**
     * 预热线程池
     */
    private static void warmUpThreadPools() {
        // 创建预热任务
        Runnable warmupTask = () -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // 忽略中断
            }
        };

        // 预热计算线程池
        for (int i = 0; i < COMPUTE_POOL_SIZE; i++) {
            COMPUTE_EXECUTOR.submit(warmupTask);
        }

        // 预热IO线程池
        for (int i = 0; i < IO_POOL_SIZE; i++) {
            IO_EXECUTOR.submit(warmupTask);
        }
    }

    /**
     * 关闭所有线程池
     */
    private static void shutdownThreadPools() {
        COMPUTE_EXECUTOR.shutdown();
        IO_EXECUTOR.shutdown();
        FORK_JOIN_POOL.shutdown();
        try {
            if (!COMPUTE_EXECUTOR.awaitTermination(5, TimeUnit.SECONDS)) {
                COMPUTE_EXECUTOR.shutdownNow();
            }
            if (!IO_EXECUTOR.awaitTermination(5, TimeUnit.SECONDS)) {
                IO_EXECUTOR.shutdownNow();
            }
            if (!FORK_JOIN_POOL.awaitTermination(5, TimeUnit.SECONDS)) {
                FORK_JOIN_POOL.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 并行处理任务(计算密集型)
     *
     * @param items     任务列表
     * @param processor 任务处理器
     * @param <T>       任务类型
     * @return 成功处理的任务数量
     */
    public static <T> int processTasksInParallel(Collection<T> items, TaskProcessor<T> processor) {
        return processTasksInParallel(items, processor, TaskType.COMPUTE);
    }

    /**
     * 并行处理任务(指定任务类型)
     *
     * @param items     任务列表
     * @param processor 任务处理器
     * @param taskType  任务类型
     * @param <T>       任务类型
     * @return 成功处理的任务数量
     */
    public static <T> int processTasksInParallel(Collection<T> items,
                                                 TaskProcessor<T> processor,
                                                 TaskType taskType) {
        if (items == null || items.isEmpty()) {
            return 0;
        }

        // 根据集合大小选择处理策略
        if (items.size() >= BATCH_THRESHOLD) {
            return processMassiveTasks(items, processor, taskType);
        }

        ExecutorService executor = getExecutorByTaskType(taskType);
        CompletionService<Boolean> completionService = new ExecutorCompletionService<>(executor);
        AtomicInteger successCount = new AtomicInteger(0);

        int total = items.size();

        // 提交所有任务
        items.forEach(item ->
                completionService.submit(() -> {
                    try {
                        boolean result = processor.process(item);
                        if (result) {
                            successCount.incrementAndGet();
                        }
                        return result;
                    } catch (Exception e) {
                        log.error("执行任务出错: {}", e.getMessage(), e);
                        return false;
                    }
                })
        );

        // 等待所有任务完成
        for (int i = 0; i < total; i++) {
            try {
                completionService.take().get();
            } catch (Exception e) {
                log.error("等待任务完成时出错: {}", e.getMessage(), e);
            }
        }

        return successCount.get();
    }

    /**
     * 处理海量任务的优化方法
     */
    private static <T> int processMassiveTasks(Collection<T> items,
                                               TaskProcessor<T> processor,
                                               TaskType taskType) {
        // 将大集合分割成多个小批次
        List<List<T>> batches = splitIntoBatches(items);
        AtomicInteger totalSuccessCount = new AtomicInteger(0);

        // 使用ForkJoin框架处理批次任务
        FORK_JOIN_POOL.submit(() -> {
            batches.parallelStream().forEach(batch -> {
                int batchSuccess = processTaskBatch(batch, processor, taskType);
                totalSuccessCount.addAndGet(batchSuccess);
            });
        }).join();

        return totalSuccessCount.get();
    }

    /**
     * 处理单个任务批次
     */
    private static <T> int processTaskBatch(List<T> batch,
                                            TaskProcessor<T> processor,
                                            TaskType taskType) {
        ExecutorService executor = getExecutorByTaskType(taskType);
        CompletionService<Boolean> completionService = new ExecutorCompletionService<>(executor);
        AtomicInteger batchSuccessCount = new AtomicInteger(0);

        // 提交批次中的所有任务
        batch.forEach(item ->
                completionService.submit(() -> {
                    try {
                        boolean result = processor.process(item);
                        if (result) {
                            batchSuccessCount.incrementAndGet();
                        }
                        return result;
                    } catch (Exception e) {
                        log.error("批处理任务出错: {}", e.getMessage(), e);
                        return false;
                    }
                })
        );

        // 等待批次任务完成
        for (int i = 0; i < batch.size(); i++) {
            try {
                completionService.take().get();
            } catch (Exception e) {
                log.error("等待批处理任务完成时出错: {}", e.getMessage(), e);
            }
        }

        return batchSuccessCount.get();
    }

    /**
     * 将集合分割成固定大小的批次
     */
    private static <T> List<List<T>> splitIntoBatches(Collection<T> items) {
        List<List<T>> batches = new ArrayList<>();
        List<T> currentBatch = new ArrayList<>(ThreadPoolUtils.MAX_BATCH_SIZE);

        for (T item : items) {
            currentBatch.add(item);

            if (currentBatch.size() >= ThreadPoolUtils.MAX_BATCH_SIZE) {
                batches.add(currentBatch);
                currentBatch = new ArrayList<>(ThreadPoolUtils.MAX_BATCH_SIZE);
            }
        }

        // 添加最后一个批次（如果有）
        if (!currentBatch.isEmpty()) {
            batches.add(currentBatch);
        }

        return batches;
    }

    /**
     * 根据任务类型获取对应的线程池
     */
    private static ExecutorService getExecutorByTaskType(TaskType taskType) {
        switch (taskType) {
            case IO:
                return IO_EXECUTOR;
            case COMPUTE:
            default:
                return COMPUTE_EXECUTOR;
        }
    }

    /**
     * 任务类型枚举
     */
    public enum TaskType {
        /**
         * IO密集型任务
         */
        IO,

        /**
         * 计算密集型任务
         */
        COMPUTE
    }

    /**
     * 任务处理器
     *
     * @param <T> 任务类型
     */
    @FunctionalInterface
    public interface TaskProcessor<T> {
        boolean process(T item) throws Exception;
    }
}