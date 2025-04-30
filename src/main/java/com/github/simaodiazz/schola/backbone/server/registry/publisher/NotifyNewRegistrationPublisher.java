package com.github.simaodiazz.schola.backbone.server.registry.publisher;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class NotifyNewRegistrationPublisher {

    private final RedisTemplate<String, String> template;
    private final ChannelTopic topic;

    public NotifyNewRegistrationPublisher(RedisTemplate<String, String> template, ChannelTopic topic) {
        this.template = template;
        this.topic = topic;
    }

    public void notification() {
        final String topicName = topic.getTopic();
        template.convertAndSend(topicName, "");
    }
}