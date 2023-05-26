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

package io.github.stream.mqtt.sink;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import io.github.stream.core.Message;
import io.github.stream.core.properties.AbstractProperties;
import io.github.stream.core.sink.AbstractSink;
import io.github.stream.mqtt.MqttStateConfigure;
import lombok.extern.slf4j.Slf4j;

/**
 * mqtt 发送
 * @author wendy512@yeah.net
 * @date 2023-05-23 14:55:53
 * @since 1.0.0
 */
@Slf4j
public class MqttSink extends AbstractSink<String> {

    private MqttSender mqttSender;

    public MqttSink(int cacheSize) {
        super(cacheSize);
    }

    @Override
    public void configure(AbstractProperties properties) {
        this.mqttSender = MqttSender.getInstance(properties);
    }

    @Override
    public void startProcess(List<Message<String>> messages) {
        for (Message<String> message : messages) {
            String topic = message.getHeaders().getString(MqttStateConfigure.OPTIONS_TOPIC);
            String payload = message.getPayload();
            if (StringUtils.isBlank(topic)) {
                log.error("Message {} , topic header is empty", payload);
                continue;
            }

            mqttSender.send(topic, payload);
        }
    }

    @Override
    public void stop() {
        mqttSender.stop();
        super.stop();
    }
}
