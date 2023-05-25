package io.github.stream.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.github.stream.core.Message;
import io.github.stream.core.annotation.Channel;
import io.github.stream.core.channel.ChannelProcessor;
import io.github.stream.core.message.MessageBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 测试输入
 * @author wendy512@yeah.net
 * @date 2023-05-22 15:39:17
 * @since 1.0.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestApplication.class)
@Slf4j
public class TestLocal {

    @Channel("localQueue")
    private ChannelProcessor channelProcessor;

    @Test
    public void testConsumer() throws Exception {
        for (int i = 0; i < 100; i++) {
            Message<String> message = MessageBuilder.withPayload(String.format("this is %s message", i)).build();
            channelProcessor.send(message);
        }

        // 保证消费完
        TimeUnit.SECONDS.sleep(5);
    }
}
