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

package io.github.stream.rabbitmq.sink;

import java.util.List;

import io.github.stream.core.Message;
import io.github.stream.core.properties.AbstractProperties;
import io.github.stream.core.sink.AbstractSink;

/**
 * rabbitmq 发送
 * @author wendy512@yeah.net
 * @date 2023-05-25 14:19:27
 * @since 1.0.0
 */
public class RabbitMqSink extends AbstractSink<Object> {

    private RabbitMqSender sender;

    public RabbitMqSink(int cacheSize) {
        super(cacheSize);
    }

    @Override
    public void configure(AbstractProperties properties) {
        this.sender = RabbitMqSender.getInstance(properties);
    }

    @Override
    public void startProcess(List<Message<Object>> messages) {
        for (Message<Object> message : messages) {
            if (null != message) {
                sender.send(message);
            }
        }
    }

    @Override
    public void stop() {
        sender.stop();
        super.stop();
    }
}
