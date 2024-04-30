package io.github.stream.redis.source;

import java.util.ArrayList;
import java.util.List;

import org.redisson.api.RTopic;
import org.redisson.api.listener.MessageListener;

import io.github.stream.core.Message;
import io.github.stream.core.configuration.ConfigContext;
import io.github.stream.core.message.MessageBuilder;
import io.github.stream.core.source.AbstractSource;
import io.github.stream.redis.Constants;
import io.github.stream.redis.RedissonStateConfigure;

/**
 * redis stream 队列消费
 * @author taowenwu
 * @date 2023-10-17 16:13:51
 * @since 1.0.0
 */
public class RedisStreamSource extends AbstractSource {

    private final RedissonStateConfigure stateConfigure = new RedissonStateConfigure();

    private String[] topics;

    private List<RTopic> rTopics;

    @Override
    public void configure(ConfigContext context) {
        this.stateConfigure.configure(context);
        this.topics = stateConfigure.resolveTopic(context.getConfig());
    }

    @Override
    public void start() {
        this.rTopics = new ArrayList<>(this.topics.length);
        for (String topic : topics) {
            RTopic rTopic = stateConfigure.getClient().getTopic(topic);
            rTopics.add(rTopic);
            rTopic.addListener(Object.class, new DefaultMessageListener(topic));
        }
        super.start();
    }

    @Override
    public void stop() {
        for (RTopic rTopic : rTopics) {
            rTopic.removeAllListeners();
        }
        stateConfigure.getClient().shutdown();
        super.stop();
    }

    private class DefaultMessageListener implements MessageListener {

        private final String topic;

        private DefaultMessageListener(String topic) {
            this.topic = topic;
        }

        @Override
        public void onMessage(CharSequence channel, Object msg) {
            Message message = MessageBuilder.withPayload(msg).setHeader(Constants.TOPIC_KEY, topic).build();
            getChannelProcessor().send(message);
        }
    }
}
