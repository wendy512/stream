package io.github.stream.test;

import java.util.List;

import org.springframework.stereotype.Component;

import io.github.stream.core.Consumer;
import io.github.stream.core.Message;
import io.github.stream.core.annotation.Sink;
import lombok.extern.slf4j.Slf4j;

/**
 * 测试consumer
 * @author wendy512@yeah.net
 * @date 2023-05-22 17:17:53
 * @since 1.0.0
 */
@Sink("test1")
@Component
@Slf4j
public class KafkaConsumer implements Consumer<String> {

    @Override
    public void accept(List<Message<String>> messages) {
        String threadName = Thread.currentThread().getName();
        messages.forEach(m -> {
            System.out.println(String.format("[%s] Received kafka message %s", threadName, m.getPayload()));
        });
    }
}
