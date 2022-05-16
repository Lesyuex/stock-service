package com.jobeth.common.util;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.rmi.ServerException;
import java.time.Clock;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisUtils  {

    private final static RedisTemplate<String, Object> redisTemplate = SpringContextUtils.getBean("redisTemplate");

    /**
     * 保存key - value
     *
     * @param key key
     * @param val val
     * @return boolean
     */
    public static boolean set(String key, Object val) {
        ValueOperations<String, Object> operations = null;
        try {
            operations = redisTemplate.opsForValue();
            operations.set(key, val);
            log.info("【 key => {} 数据存入Redis缓存成功 】", key);
            return true;
        } catch (Exception e) {
            log.error("【 key => {} 数据存入redis缓存发生错误 -】", key, e);
            return false;
        }
    }

    /**
     * 根据key设置有效时间
     *
     * @param key        key
     * @param expireTime time
     */
    public static void setExpire(String key, Object value, long expireTime) {
        ValueOperations<String, Object> operations = null;
        try {
            operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            Date date = new Date(Clock.systemDefaultZone().millis() + expireTime);
            log.info("【 key => {} 数据存入redis缓存成功,失效时间：{} 】", key, date);
        } catch (Exception e) {
            log.error("【 key => {} 数据存入redis缓存发生错误 -】", key, e);
        }
    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key key
     * @return boolean
     */
    public static boolean exists(String key) {
        try {
            if (StringUtils.isNullOrEmpty(key)){
                throw new ServerException("Redis key is null");
            }
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("【 从 Redis 查找 key => {} 缓存数据时发生错误 -】", key, e);
            return false;
        }
    }

    /**
     * 读取缓存
     *
     * @param key key
     * @return Object
     */
    public static Object get(String key) {
        Object value = null;
        ValueOperations<String, Object> operations = null;
        try {
            operations = redisTemplate.opsForValue();
            value = operations.get(key);
        } catch (Exception e) {
            log.error("【 从 Redis 获取 key => {} 缓存发生错误 】", key, e);
        }
        return value;
    }

    /**
     * 删除对应的value
     *
     * @param key key
     */
    public static boolean remove(String key) {
        try {
            return redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("【 从 Redis 删除key => {} 缓存发生错误 】", key, e);
            return false;
        }
    }

    /**
     * redisTemplate删除迷糊匹配的key的缓存
     */
    public static void deleteByPrefix(String prefix) {

        Set<String> keys = null;
        try {
            keys = redisTemplate.keys(prefix);
            if (CollectionUtils.isNotEmpty(keys)) {
                redisTemplate.delete(keys);
                log.info("【 清除Redis keys 数据成功 => {} 】", keys);
            }
        } catch (Exception e) {
            log.error("【 从 Redis 删除 keys => {} 缓存发生错误 】", keys, e);
        }

    }
}
