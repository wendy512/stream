package io.github.stream.redis.sink;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RTopic;

import io.github.stream.core.Message;
import io.github.stream.core.configuration.ConfigContext;
import io.github.stream.core.sink.AbstractSink;
import io.github.stream.redis.Constants;
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

    private RedissonStateConfigure stateConfigure;

    private Map<String, RTopic> rTopics = new ConcurrentHashMap<>();

    @Override
    public void configure(ConfigContext context) {
        this.stateConfigure = RedissonStateConfigure.getInstance(context.getInstanceName());
        this.stateConfigure.configure(context);
    }

    @Override
    public void process(List<Message<Object>> messages) {
        for (Message message : messages) {
            String topic = message.getHeaders().getString(Constants.TOPIC_KEY);
            Object payload = message.getPayload();
            if (StringUtils.isBlank(topic)) {
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
