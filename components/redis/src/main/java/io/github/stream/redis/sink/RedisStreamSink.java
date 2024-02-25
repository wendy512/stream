package io.github.stream.redis.sink;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RTopic;

import io.github.stream.core.Message;
import io.github.stream.core.properties.AbstractProperties;
import io.github.stream.core.sink.AbstractSink;
import io.github.stream.redis.RedissonStateConfigure;
import lombok.extern.slf4j.Slf4j;

/**
 * redis stream 队列推送
 * @author taowenwu
 * @date 2023-10-17 16:12:33
 * @since 1.0.0
 */
@Slf4j
public class RedisStreamSink extends AbstractSink<Object> {

    private final RedissonStateConfigure stateConfigure = new RedissonStateConfigure();

    private Map<String, RTopic> rTopics = new ConcurrentHashMap<>();

    @Override
    public void configure(AbstractProperties properties) {
        stateConfigure.configure(properties);
    }

    @Override
    public void process(List<Message<Object>> messages) {
        for (Message message : messages) {
            String topic = message.getHeaders().getString("topic");
            Object payload = message.getPayload();
            if (StringUtils.isBlank(topic)) {
                log.error("Message {} , topic header is empty", payload);
                continue;
            }

            RTopic rTopic;
            if (!rTopics.containsKey(topic)) {
                rTopic = stateConfigure.getClient().getTopic(topic);
                rTopics.put(topic, rTopic);
            } else {
                rTopic = rTopics.get(topic);
            }
            rTopic.publish(payload);
        }
    }

    @Override
    public void stop() {
        stateConfigure.getClient().shutdown();
        super.stop();
    }
}
