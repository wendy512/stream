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
public class TestRedisSink {

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
