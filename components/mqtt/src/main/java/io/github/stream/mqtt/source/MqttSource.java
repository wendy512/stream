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

package io.github.stream.mqtt.source;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.util.Assert;

import io.github.stream.core.Message;
import io.github.stream.core.StreamException;
import io.github.stream.core.configuration.ConfigContext;
import io.github.stream.core.message.MessageBuilder;
import io.github.stream.core.source.AbstractSource;
import io.github.stream.mqtt.MqttStateConfigure;
import lombok.extern.slf4j.Slf4j;

/**
 * mqtt 源，mqtt接受消息
 * @author wendy512@yeah.net
 * @date 2023-05-23 13:46:37
 * @since 1.0.0
 */
@Slf4j
public class MqttSource extends AbstractSource<String> {

    private MqttStateConfigure stateConfigure;

    private String[] topics;

    @Override
    public void configure(ConfigContext context) throws Exception {
        this.stateConfigure = MqttStateConfigure.getInstance(context.getInstanceName());
        Object topicValue = context.getConfig().get(MqttStateConfigure.OPTIONS_TOPIC);
        this.topics = resolveTopic(topicValue);
        this.stateConfigure.configure(context);
    }

    @Override
    public void start() {
        // mqtt订阅
        for (String topic : topics) {
            try {
                stateConfigure.getClient().subscribe(topic, stateConfigure.getQos(), new MqttMessageListener());
            } catch (MqttException e) {
                throw new StreamException(e);
            }
        }
        super.start();
    }

    @Override
    public void stop() {
        MqttClient client = stateConfigure.getClient();
        if (null != client && client.isConnected()) {
            try {
                client.unsubscribe(topics);
            } catch (MqttException e) {
                throw new StreamException(e);
            }
        }
        stateConfigure.stop();
        super.stop();
    }

    private class MqttMessageListener implements IMqttMessageListener {

        @Override
        public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
            String payload = new String(mqttMessage.getPayload(), StandardCharsets.UTF_8);
            Message<String> message = MessageBuilder.withPayload(payload)
                    .setHeader(MqttStateConfigure.OPTIONS_TOPIC, topic)
                    .setHeader(MqttStateConfigure.OPTIONS_QOS, mqttMessage.getQos())
                    .setHeader("id", mqttMessage.getId())
                    .build();
            getChannelProcessor().send(message);
        }
    }
    private String[] resolveTopic(Object topicValue) {
        if (topicValue instanceof List) {
            List<String> topicList = (List<String>) topicValue;
            Assert.notEmpty(topicList, "MQTT topic cannot be empty");
            return topicList.toArray(new String[topicList.size()]);
        } else {
            String topic = (String) topicValue;
            Assert.hasText(topic, "MQTT topic cannot be empty");
            return topic.split(",");
        }
    }

}
