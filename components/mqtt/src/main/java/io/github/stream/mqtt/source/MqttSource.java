package io.github.stream.mqtt.source;

import java.nio.charset.StandardCharsets;

import io.github.stream.mqtt.MqttStateConfigure;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import io.github.stream.core.Message;
import io.github.stream.core.StreamException;
import io.github.stream.core.channel.ChannelProcessor;
import io.github.stream.core.message.MessageBuilder;
import io.github.stream.core.properties.AbstractProperties;
import io.github.stream.core.source.AbstractSource;
import lombok.extern.slf4j.Slf4j;

/**
 * mqtt 源，mqtt接受消息
 * @author wendy512@yeah.net
 * @date 2023-05-23 13:46:37
 * @since 1.0.0
 */
@Slf4j
public class MqttSource extends AbstractSource<String> {

    private final MqttStateConfigure stateConfigure = new MqttStateConfigure();

    @Override
    public void configure(AbstractProperties properties) {
        stateConfigure.configure(properties);
    }

    @Override
    public void start() {
        // mqtt订阅
        for (String topic : stateConfigure.getTopics()) {
            try {
                stateConfigure.getClient().subscribe(topic, stateConfigure.getQos(),
                    new MqttMessageListener(getChannelProcessor()));
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
            for (String topic : stateConfigure.getTopics()) {
                try {
                    client.unsubscribe(topic);
                } catch (MqttException e) {
                    throw new StreamException(e);
                }
            }
        }
        stateConfigure.stop();
        super.stop();
    }

    @Slf4j
    public static class MqttMessageListener implements IMqttMessageListener {

        private final ChannelProcessor<String> channelProcessor;

        public MqttMessageListener(ChannelProcessor<String> channelProcessor) {
            this.channelProcessor = channelProcessor;
        }

        @Override
        public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
            String payload = new String(mqttMessage.getPayload(), StandardCharsets.UTF_8);
            Message<String> message = MessageBuilder.<String>withPayload(payload).setHeader(MqttStateConfigure.OPTIONS_TOPIC, topic).build();
            channelProcessor.send(message);
        }
    }
}
