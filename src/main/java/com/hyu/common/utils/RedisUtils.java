package com.hyu.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 *
 * @author hyu
 */
@Slf4j
@Component
public class RedisUtils {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 默认过期时长，单位：秒
     */
    public static final long DEFAULT_EXPIRE = 60 * 60 * 24;

    /**
     * 不设置过期时长
     */
    public static final long NOT_EXPIRE = -1;

    /**
     * 设置缓存
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, Object value) {
        set(key, value, DEFAULT_EXPIRE);
    }

    /**
     * 设置缓存
     *
     * @param key      键
     * @param value    值
     * @param expire   过期时间（秒）
     */
    public void set(String key, Object value, long expire) {
        try {
            if (expire == NOT_EXPIRE) {
                redisTemplate.opsForValue().set(key, value);
            } else {
                redisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.error("Redis设置缓存失败，key：{}，value：{}，expire：{}", key, value, expire, e);
            throw new RuntimeException("Redis设置缓存失败");
        }
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Redis获取缓存失败，key：{}", key, e);
            return null;
        }
    }

    /**
     * 获取缓存（指定类型）
     *
     * @param key   键
     * @param clazz 类型
     * @param <T>   泛型
     * @return 值
     */
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                return clazz.cast(value);
            }
            return null;
        } catch (Exception e) {
            log.error("Redis获取缓存失败，key：{}，clazz：{}", key, clazz.getName(), e);
            return null;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 键
     */
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Redis删除缓存失败，key：{}", key, e);
            throw new RuntimeException("Redis删除缓存失败");
        }
    }

    /**
     * 批量删除缓存
     *
     * @param keys 键集合
     */
    public void delete(Collection<String> keys) {
        try {
            redisTemplate.delete(keys);
        } catch (Exception e) {
            log.error("Redis批量删除缓存失败，keys：{}", keys, e);
            throw new RuntimeException("Redis批量删除缓存失败");
        }
    }

    /**
     * 是否存在key
     *
     * @param key 键
     * @return 是否存在
     */
    public Boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("Redis判断key是否存在失败，key：{}", key, e);
            return false;
        }
    }

    /**
     * 设置过期时间
     *
     * @param key    键
     * @param expire 过期时间（秒）
     */
    public void expire(String key, long expire) {
        try {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Redis设置过期时间失败，key：{}，expire：{}", key, expire, e);
            throw new RuntimeException("Redis设置过期时间失败");
        }
    }

    /**
     * 获取过期时间
     *
     * @param key 键
     * @return 过期时间（秒）
     */
    public Long getExpire(String key) {
        try {
            return redisTemplate.getExpire(key, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Redis获取过期时间失败，key：{}", key, e);
            return -1L;
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 递增值
     * @return 递增后的值
     */
    public Long increment(String key, long delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            log.error("Redis递增失败，key：{}，delta：{}", key, delta, e);
            throw new RuntimeException("Redis递增失败");
        }
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 递减值
     * @return 递减后的值
     */
    public Long decrement(String key, long delta) {
        try {
            return redisTemplate.opsForValue().increment(key, -delta);
        } catch (Exception e) {
            log.error("Redis递减失败，key：{}，delta：{}", key, delta, e);
            throw new RuntimeException("Redis递减失败");
        }
    }

    /**
     * Hash操作 - 设置
     *
     * @param key     键
     * @param hashKey Hash键
     * @param value   值
     */
    public void hSet(String key, String hashKey, Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
        } catch (Exception e) {
            log.error("Redis Hash设置失败，key：{}，hashKey：{}，value：{}", key, hashKey, value, e);
            throw new RuntimeException("Redis Hash设置失败");
        }
    }

    /**
     * Hash操作 - 获取
     *
     * @param key     键
     * @param hashKey Hash键
     * @return 值
     */
    public Object hGet(String key, String hashKey) {
        try {
            return redisTemplate.opsForHash().get(key, hashKey);
        } catch (Exception e) {
            log.error("Redis Hash获取失败，key：{}，hashKey：{}", key, hashKey, e);
            return null;
        }
    }

    /**
     * Hash操作 - 获取所有
     *
     * @param key 键
     * @return Map
     */
    public Map<Object, Object> hGetAll(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            log.error("Redis Hash获取所有失败，key：{}", key, e);
            return null;
        }
    }

    /**
     * Hash操作 - 删除
     *
     * @param key     键
     * @param hashKeys Hash键集合
     */
    public void hDelete(String key, Object... hashKeys) {
        try {
            redisTemplate.opsForHash().delete(key, hashKeys);
        } catch (Exception e) {
            log.error("Redis Hash删除失败，key：{}，hashKeys：{}", key, hashKeys, e);
            throw new RuntimeException("Redis Hash删除失败");
        }
    }

    /**
     * Set操作 - 添加
     *
     * @param key   键
     * @param value 值
     */
    public void sAdd(String key, Object value) {
        try {
            redisTemplate.opsForSet().add(key, value);
        } catch (Exception e) {
            log.error("Redis Set添加失败，key：{}，value：{}", key, value, e);
            throw new RuntimeException("Redis Set添加失败");
        }
    }

    /**
     * Set操作 - 获取所有成员
     *
     * @param key 键
     * @return Set
     */
    public Set<Object> sMembers(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("Redis Set获取所有成员失败，key：{}", key, e);
            return null;
        }
    }

    /**
     * Set操作 - 判断是否存在
     *
     * @param key   键
     * @param value 值
     * @return 是否存在
     */
    public Boolean sIsMember(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            log.error("Redis Set判断是否存在失败，key：{}，value：{}", key, value, e);
            return false;
        }
    }

    /**
     * Set操作 - 删除
     *
     * @param key   键
     * @param value 值
     */
    public void sRemove(String key, Object value) {
        try {
            redisTemplate.opsForSet().remove(key, value);
        } catch (Exception e) {
            log.error("Redis Set删除失败，key：{}，value：{}", key, value, e);
            throw new RuntimeException("Redis Set删除失败");
        }
    }
}