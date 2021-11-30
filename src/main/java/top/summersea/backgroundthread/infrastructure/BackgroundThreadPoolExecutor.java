package top.summersea.backgroundthread.infrastructure;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BackgroundThreadPoolExecutor {

    /** 单例实例 */
    private static final BackgroundThreadPoolExecutor instance = new BackgroundThreadPoolExecutor();

    /**
     * 获取实例
     * @return 实例
     */
    public static BackgroundThreadPoolExecutor getInstance() {
        return instance;
    }

    /** 最大空转次数 */
    private static final int MAX_LOOP_WITH_IDLE = 3;

    /** 基于时间的线程池 */
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    /** 一次性延时任务追踪容器 */
    private final Map<Runnable, ScheduledFuture<?>> oneShotFutureMap;

    /** 辅助追踪任务 */
    private ScheduledFuture<?> trackFuture;

    /** 剩余空转次数 */
    private int leftLoopToTerminate = MAX_LOOP_WITH_IDLE;

    /** 构造器 */
    private BackgroundThreadPoolExecutor() {
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        scheduledThreadPoolExecutor.setKeepAliveTime(30, TimeUnit.SECONDS);
        scheduledThreadPoolExecutor.allowCoreThreadTimeOut(true);
        oneShotFutureMap = new ConcurrentHashMap<>();
    }

    /**
     * 注册一次性延时任务
     * @param runnable 任务
     * @param delay    延时时间
     * @param timeUnit 延时时间单位
     */
    public void registerScheduleTask(Runnable runnable, long delay, TimeUnit timeUnit) {
        ScheduledFuture<?> lastScheduledFuture = oneShotFutureMap.remove(runnable);
        if (lastScheduledFuture != null) {
            lastScheduledFuture.cancel(true);
        }
        ScheduledFuture<?> scheduleFuture = scheduledThreadPoolExecutor.schedule(runnable, delay, timeUnit);
        oneShotFutureMap.put(runnable, scheduleFuture);
        registerTrackTask();
    }

    /**
     * 注册追踪任务
     * 任务已注册则直接返回
     * 任务为注册则注册追踪任务
     * 追踪任务：
     * 1. 清理追踪容器中已经完成的任务以及被取消的任务，以释放内存。
     * 2. 追踪任务在发现自己连续空转了几轮后需要结束自己。
     */
    private void registerTrackTask() {
        if (trackFuture != null && !trackFuture.isCancelled()) {
            return;
        }
        trackFuture = scheduledThreadPoolExecutor.scheduleWithFixedDelay(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            System.out.println(sdf.format(new Date()));
            System.out.println("left loop: " + leftLoopToTerminate);
            if (oneShotFutureMap.isEmpty()) {
                if (--leftLoopToTerminate <= 0) {
                    System.out.println(trackFuture.cancel(true));
                }
                return;
            }
            leftLoopToTerminate = MAX_LOOP_WITH_IDLE;
            for (Map.Entry<Runnable, ScheduledFuture<?>> entry : oneShotFutureMap.entrySet()) {
                ScheduledFuture<?> future = entry.getValue();
                if (future.isCancelled() || future.isDone()) {
                    oneShotFutureMap.remove(entry.getKey());
                }
            }
        }, 0, 20, TimeUnit.SECONDS);
    }

    public void stopTrack() {
        if (trackFuture != null) {
            System.out.println(trackFuture.cancel(true));
        }
    }
}
