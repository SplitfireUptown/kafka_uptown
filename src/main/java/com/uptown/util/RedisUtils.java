package com.uptown.util;

import java.util.Set;



import lombok.extern.slf4j.Slf4j;

/**
 *
 * 基于spring和redis的redisTemplate工具类
 */
@Slf4j
public final class RedisUtils {



	// ============================zset=============================
	/**
	 * 根据key获取Set中的所有值
	 *
	 * @param key
	 *            键
	 * @return
	 */
	public static Set<Object> zSGet(String key) {
		try {
			return RedisConfig.getRedisTemplate().opsForSet().members(key);
		} catch (Exception e) {
			log.error(key, e);
			return null;
		}
	}

	/**
	 * 根据value从一个set中查询,是否存在
	 *
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return true 存在 false不存在
	 */
	public static boolean zSHasKey(String key, Object value) {
		try {
			return RedisConfig.getRedisTemplate().opsForSet().isMember(key, value);
		} catch (Exception e) {
			log.error(key, e);
			return false;
		}
	}

	public static Boolean zSSet(String key, Object value, double score) {
		try {
			return RedisConfig.getRedisTemplate().opsForZSet().add(key, value, score);
		} catch (Exception e) {
			log.error(key, e);
			return false;
		}
	}

	public static Set<Object> zRangeByScoreWithScores(String key, Double start, Double end) {
		try {
			return RedisConfig.getRedisTemplate().opsForZSet().rangeByScoreWithScores(key, start, end);
		} catch (Exception e) {
			log.error(key, e);
			return null;
		}
	}

	/**
	 * 获取set缓存的长度
	 *
	 * @param key
	 *            键
	 * @return
	 */
	public static long zSGetSetSize(String key) {
		try {
			return RedisConfig.getRedisTemplate().opsForSet().size(key);
		} catch (Exception e) {
			log.error(key, e);
			return 0;
		}
	}

	/**
	 * 移除值为value的
	 *
	 * @param key
	 *            键
	 * @param values
	 *            值 可以是多个
	 * @return 移除的个数
	 */
	public static long zSetRemove(String key, Object... values) {
		try {
			Long count = RedisConfig.getRedisTemplate().opsForSet().remove(key, values);
			return count;
		} catch (Exception e) {
			log.error(key, e);
			return 0;
		}
	}

}

