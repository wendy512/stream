/**
 * Copyright wendy512@yeah.net
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

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
