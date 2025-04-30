package com.github.simaodiazz.schola.backbone.server.cache.redis;

import org.jetbrains.annotations.NotNull;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfiguration {

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .computePrefixWith(
                    CacheKeyPrefix.prefixed("backbone:"))
                .entryTtl(
                        Duration.ofMinutes(15))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()));
    }

    @Bean
    public CacheManager cacheManager(final @NotNull RedisConnectionFactory factory, final @NotNull RedisCacheConfiguration configuration) {
        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(factory)
                .cacheDefaults(configuration)
                .build();
    }

    @Bean
    public MessageListenerAdapter messageListenerAdapter() {
        return new MessageListenerAdapter();
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory, MessageListenerAdapter messageListenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);

        final ChannelTopic topic = new ChannelTopic("notifications:registration:new");
        container.addMessageListener(messageListenerAdapter, topic);

        return container;
    }

    @Bean
    public ChannelTopic notificationsNewRegistrations() {
        return new ChannelTopic("notifications:registrations:new");
    }
}
