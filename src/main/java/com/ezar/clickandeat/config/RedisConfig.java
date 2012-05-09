package com.ezar.clickandeat.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.pool.impl.GenericObjectPool.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

@Configuration
public class RedisConfig {

	private static Pattern REDIS_URL_PATTERN = Pattern.compile("^redis://([^:]*):([^@]*)@([^:]*):([^/]*)(/)?");
	
	private String redisUrl;
	
	@Value(value="${REDISTOGO_URL}")
	public void setRedisUrl(String redisUrl) {
		this.redisUrl = redisUrl;
	}
	
	@Bean(name="jedisPool")
	public JedisPool getJedisPool() {
		Matcher matcher = REDIS_URL_PATTERN.matcher(redisUrl);
		matcher.matches();
		Config config = new Config();
		config.testOnBorrow = true;
		return new JedisPool(config, matcher.group(3),Integer.parseInt(matcher.group(4)), Protocol.DEFAULT_TIMEOUT, matcher.group(2));
	}
	
}
