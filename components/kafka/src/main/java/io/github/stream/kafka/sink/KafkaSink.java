package io.github.stream.kafka.sink;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import io.github.stream.core.Message;
import io.github.stream.core.properties.AbstractProperties;
import io.github.stream.core.sink.AbstractSink;

import lombok.extern.slf4j.Slf4j;

/**
 * kafka 输出
 * @author wendy512@yeah.net
 * @date 2023-05-23 17:01:52
 * @since 1.0.0
 */
@Slf4j
public class KafkaSink extends AbstractSink<Object> {

    private KafkaSender kafkaSender;

    public KafkaSink() {}

    public KafkaSink(int cacheSize) {
        super(cacheSize);
    }

    @Override
    public void configure(AbstractProperties properties) {
        this.kafkaSender = KafkaSender.getInstance(properties);
    }

    @Override
    public void stop() {
        kafkaSender.stop();
        super.stop();
    }

    @Override
    public void startProcess(List<Message<Object>> messages) {
        for (Message<Object> message : messages) {
            String topic = message.getHeaders().getString("topic");
            Object payload = message.getPayload();
            if (StringUtils.isBlank(topic)) {
                log.error("message {} , topic header is empty", payload);
                continue;
            }

            kafkaSender.send(topic, payload);
        }
    }
}
