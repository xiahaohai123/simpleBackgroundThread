package top.summersea.backgroundthread.infrastructure;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BackgroundThreadPoolExecutor {

    private static final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    private static final Map<Runnable, ScheduledFuture<?>> futureMap;

    static {
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        scheduledThreadPoolExecutor.setKeepAliveTime(30, TimeUnit.SECONDS);
        scheduledThreadPoolExecutor.allowCoreThreadTimeOut(true);
        futureMap = new ConcurrentHashMap<>();
    }


    public static void registerScheduleTask(Runnable runnable, long delay, TimeUnit timeUnit) {
        ScheduledFuture<?> lastScheduledFuture = futureMap.get(runnable);
        if (lastScheduledFuture != null) {
            lastScheduledFuture.cancel(true);
        }
        ScheduledFuture<?> scheduleFuture = scheduledThreadPoolExecutor.schedule(runnable, delay, timeUnit);
        futureMap.put(runnable, scheduleFuture);
    }
}
