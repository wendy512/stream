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

package io.github.stream.sample;

import io.github.stream.core.Message;
import io.github.stream.core.annotation.Channel;
import io.github.stream.core.channel.ChannelProcessor;
import io.github.stream.core.message.MessageBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 消息推送
 * @author wendy512@yeah.net
 * @date 2023-05-26 21:13:05
 * @since 1.0.0
 */
@Component
public class SamplePusher implements ApplicationListener<ContextRefreshedEvent> {
    
    // 指定哪个通道
    @Channel("localQueue")
    private ChannelProcessor channelProcessor;
    
    public void push(int count) {
        for (int i = 0; i < count; i++) {
            // 组装消息
            String payload = "Here is a sample message, the current index is " + i;
            Message message = MessageBuilder.withPayload(payload).build();
            channelProcessor.send(message);
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        push(100);
    }
}
