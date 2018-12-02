package org.clever.notification.send.rabbit;

import org.joda.time.Duration;

import java.util.HashMap;
import java.util.Map;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-12-02 12:56 <br/>
 */
@SuppressWarnings({"SameParameterValue", "WeakerAccess", "unused"})
public class RetryStrategy {
    /**
     * 一直保持5秒钟后重试
     */
    public static final RetryStrategy DEFAULT_5_SECOND = new RetryStrategy(new Duration(5 * 1000));

    /**
     * 一直保持15分钟后重试
     */
    public static final RetryStrategy DEFAULT = new RetryStrategy(new Duration(15 * 60 * 1000));

    /**
     * 递增的重试策略（5秒、30秒、1分钟、15分钟、30分钟、30分钟、30分钟、1个小时...）
     */
    public static final RetryStrategy INCREASE = new RetryStrategy(new Duration(60 * 60 * 1000))
            .add(1, new Duration(5 * 1000))
            .add(2, new Duration(30 * 1000))
            .add(3, new Duration(60 * 1000))
            .add(4, new Duration(15 * 60 * 1000))
            .add(5, 7, new Duration(30 * 60 * 1000));

    /**
     * 递增的重试策略（30秒、60秒、120秒、240秒、480秒、...、3个小时、END）
     */
    public static final RetryStrategy DOUBLE = new RetryStrategy(new Duration(128 * 60 * 1000))
            .doubleStrategyInit(15 * 1000, 128 * 60 * 1000, true);

    /**
     * SAME的重试策略（180秒、180秒、180秒、180秒、180秒、...、total[3个小时]、END）
     */
    public static final RetryStrategy SAME = new RetryStrategy(new Duration(128 * 60 * 1000))
            .sameStrategyInit(5 * 60 * 1000, 36, true);

    private final long defaultDuration;
    private final Map<Integer, Long> periodMap = new HashMap<>();

    private RetryStrategy(Duration defaultDuration) {
        this.defaultDuration = defaultDuration.getMillis();
    }

    private RetryStrategy add(int minCount, int maxCount, Duration ttl) {
        for (int i = minCount; i <= maxCount; i++) {
            add(i, ttl);
        }
        return this;
    }

    private RetryStrategy add(int count, Duration ttl) {
        periodMap.put(count, ttl.getMillis());
        return this;
    }

    private RetryStrategy doubleStrategyInit(long startDuration, long maxDuration, boolean isBreak) {
        int i = 1;
        while (startDuration < maxDuration) {
            add(i, new Duration(startDuration));
            startDuration = startDuration * 2;
            i++;
        }
        add(i, new Duration(maxDuration));
        if (isBreak) {
            i++;
            periodMap.put(i, -1L);
        }
        return this;
    }

    private RetryStrategy sameStrategyInit(long perDuration, int times, boolean isBreak) {
        int i = 0;
        while (i < times) {
            add(i, new Duration(perDuration));
            i++;
        }
        if (isBreak) {
            i++;
            periodMap.put(i, -1L);
        }
        return this;
    }

    /**
     * 根据重试次数，获取重试的延迟时间()
     */
    public long getTtl(int retryCount) {
        return periodMap.getOrDefault(retryCount, defaultDuration);
    }
}
