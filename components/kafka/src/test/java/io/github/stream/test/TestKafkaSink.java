package io.github.stream.test;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.github.stream.core.Message;
import io.github.stream.core.annotation.Channel;
import io.github.stream.core.channel.ChannelProcessor;
import io.github.stream.core.message.MessageBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wendy512@yeah.net
 * @date 2023-05-22 15:39:17
 * @since 1.0.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestApplication.class)
@Slf4j
public class TestKafkaSink {

    @Channel("sinkQueue")
    private ChannelProcessor channelProcessor;

    @Test
    public void testSink() throws Exception {
        for (int i = 0; i < 100; i++) {
            Message<String> message = MessageBuilder.withPayload(String.format("this is %s message", i)).setHeader("topic", "test-1").build();
            channelProcessor.send(message);
        }

        TimeUnit.SECONDS.sleep(5);
    }
}
