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

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import io.github.stream.core.Message;
import io.github.stream.core.configuration.ConfigContext;
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

    private MqttStateConfigure stateConfigure;

    @Override
    public void configure(ConfigContext context) throws Exception {
        this.stateConfigure = new MqttStateConfigure();
        this.stateConfigure.configure(context, false);
    }

    @Override
    public void process(List<Message<String>> messages) {
        for (Message<String> message : messages) {
            String topic = message.getHeaders().getString(MqttStateConfigure.OPTIONS_TOPIC);
            String payload = message.getPayload();
            if (StringUtils.isBlank(topic)) {
                continue;
            }

            send(topic, payload);
        }
    }

    public void send(String topic, String payload) {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(stateConfigure.getQos());
        mqttMessage.setPayload(payload.getBytes(StandardCharsets.UTF_8));

        try {
            if (log.isDebugEnabled()) {
                log.debug("Send message {} to mqtt topic {}", payload, topic);
            }
            stateConfigure.getClient().publish(topic, mqttMessage);
        } catch (MqttException e) {
            log.error("Send message to mqtt error", e);
        }
    }

    @Override
    public void stop() {
        stateConfigure.stop();
        super.stop();
    }
}
