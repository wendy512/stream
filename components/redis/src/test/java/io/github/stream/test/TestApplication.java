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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import io.github.stream.core.Message;
import io.github.stream.core.channel.ChannelContext;
import io.github.stream.core.channel.ChannelProcessor;
import io.github.stream.core.message.MessageBuilder;

/**
 * @author wendy512@yeah.net
 * @date 2023-05-22 15:38:51
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = "io.github.stream")
public class TestApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(TestApplication.class, args);

        ChannelContext channelContext = ctx.getBean(ChannelContext.class);
        ChannelProcessor channelProcessor = channelContext.getChannelProcessor("sinkQueue");
        for (int i = 0; i < 100; i++) {
            Message<String> message = MessageBuilder.withPayload(String.format("this is %s message", i)).setHeader("topic", "test-1").build();
            channelProcessor.send(message);
        }
    }
}
