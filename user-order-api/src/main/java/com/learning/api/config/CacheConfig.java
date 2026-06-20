package com.learning.api.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * ============================================================
 * INTERVIEW CONCEPT: Spring Cache & Redis Serialization
 * ============================================================
 *
 * @EnableCaching
 * - Activates Spring's caching support, enabling AOP proxies to intercept
 *   methods annotated with @Cacheable, @CachePut, and @CacheEvict.
 *
 * Custom RedisCacheConfiguration:
 * - By default, RedisCacheManager uses standard JDK binary serialization.
 * - Why is binary serialization bad?
 *   1. It is unreadable inside Redis (hard to debug via CLI).
 *   2. It contains Java class metadata, breaking compatibility if the class
 *      definition changes or if non-Java applications read from the cache.
 *
 * - Solution: Configure JSON Serialization using Jackson!
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) // Default time-to-live for cache entries
                .disableCachingNullValues() // Do not cache null results
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }
}
