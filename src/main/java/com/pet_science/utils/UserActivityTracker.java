package com.pet_science.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 用户活跃信息记录
 */
@Component
public class UserActivityTracker {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String DAILY_ACTIVE_KEY_PREFIX = "active_users:daily:";
    private static final String WEEKLY_ACTIVE_KEY_PREFIX = "active_users:weekly:";
    private static final String MONTHLY_ACTIVE_KEY_PREFIX = "active_users:monthly:";

    /**
     * 记录用户活跃信息
     * @param token JWT token
     */
    public void trackUserActivity(String token) {
        try {
            Integer userId = JWTUtil.getUserId(token);
            if (userId != null) {
                Date now = new Date();
                SimpleDateFormat dailyFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat monthlyFormat = new SimpleDateFormat("yyyy-MM");

                String dailyKey = DAILY_ACTIVE_KEY_PREFIX + dailyFormat.format(now);
                String monthlyKey = MONTHLY_ACTIVE_KEY_PREFIX + monthlyFormat.format(now);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(now);
                // 获取当前年份和周数
                String weeklyKey = WEEKLY_ACTIVE_KEY_PREFIX + calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.WEEK_OF_YEAR);

                // 这里使用了 HyperLogLog 来统计活跃用户数，避免了存储大量用户ID
                // HyperLogLog是Redis提供的一个高效的基数统计算法，主要用于大数据量下的去重计数
                redisTemplate.opsForHyperLogLog().add(dailyKey, userId.toString());
                redisTemplate.opsForHyperLogLog().add(weeklyKey, userId.toString());
                redisTemplate.opsForHyperLogLog().add(monthlyKey, userId.toString());

                // 设置过期时间，日活跃数据保留2天，周活跃保留8天，月活跃保留31天
                redisTemplate.expire(dailyKey, 2, TimeUnit.DAYS);
                redisTemplate.expire(weeklyKey, 8, TimeUnit.DAYS);
                redisTemplate.expire(monthlyKey, 31, TimeUnit.DAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当日活跃用户数
     * @return 活跃用户数
     */
    public long getDailyActiveUserCount() {
        SimpleDateFormat dailyFormat = new SimpleDateFormat("yyyy-MM-dd");
        String key = DAILY_ACTIVE_KEY_PREFIX + dailyFormat.format(new Date());
        Long count = redisTemplate.opsForHyperLogLog().size(key);
        return count == null ? 0L : count;
    }

    /**
     * 获取当周活跃用户数
     * @return 活跃用户数
     */
    public long getWeeklyActiveUserCount() {
        Calendar calendar = Calendar.getInstance();
        String key = WEEKLY_ACTIVE_KEY_PREFIX + calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.WEEK_OF_YEAR);
        Long count = redisTemplate.opsForHyperLogLog().size(key);
        return count == null ? 0L : count;
    }

    /**
     * 获取当月活跃用户数
     * @return 活跃用户数
     */
    public long getMonthlyActiveUserCount() {
        SimpleDateFormat monthlyFormat = new SimpleDateFormat("yyyy-MM");
        String key = MONTHLY_ACTIVE_KEY_PREFIX + monthlyFormat.format(new Date());
        Long count = redisTemplate.opsForHyperLogLog().size(key);
        return count == null ? 0L : count;
    }
}