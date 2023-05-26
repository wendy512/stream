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

package io.github.stream.rabbitmq.source;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.rabbitmq.client.*;

import io.github.stream.core.Message;
import io.github.stream.core.StreamException;
import io.github.stream.core.message.MessageBuilder;
import io.github.stream.core.properties.AbstractProperties;
import io.github.stream.core.source.AbstractSource;
import io.github.stream.rabbitmq.RabbitMqStateConfigure;

/**
 * rabbitMq 输入（消费）
 * @author wendy512@yeah.net
 * @date 2023-05-24 13:56:41
 * @since 1.0.0
 */
public class RabbitMqSource extends AbstractSource {

    private Connection connection;

    private final RabbitMqStateConfigure stateConfigure = new RabbitMqStateConfigure();

    private Map<String, Object> exchangeQueueBind;

    private Channel channel;

    @Override
    public void configure(AbstractProperties properties) {
        stateConfigure.configure(properties);
        // 初始化连接
        try {
            this.connection = stateConfigure.newConnection();
        } catch (Exception e) {
            throw new StreamException(e);
        }

        this.exchangeQueueBind = (Map<String, Object>) properties.get("exchangeQueueBind");
    }

    @Override
    public void start() {
        try {
            this.channel = connection.createChannel();
        } catch (IOException e) {
            throw new StreamException(e);
        }

        // exchange 和 queue进行绑定
        exchangeQueueBind.forEach((exchange,info) -> {

            Map infoMap = (Map) info;
            String queue = MapUtils.getString(infoMap, "queue");
            if (StringUtils.isBlank(queue)) {
                throw new IllegalArgumentException(String.format("Exchange %s queue is empty"));
            }
            String routingKey = MapUtils.getString(infoMap, "routingKey");
            String[] queues = queue.split(",");

            boolean autoAck = MapUtils.getBooleanValue(infoMap, "autoAck", true);

            try {
                channel.exchangeDeclare(exchange, BuiltinExchangeType.FANOUT);


                for (String q : queues) {
                    channel.queueDeclare(q, true, false, false, null);
                    channel.queueBind(q, exchange, routingKey);
                }
            } catch (IOException e) {
                throw new StreamException(e);
            }

            try {
                for (String q : queues) {
                    RabbitMqPollingConsumer consumer = new RabbitMqPollingConsumer(channel, autoAck);
                    channel.basicConsume(q, autoAck, consumer);
                }
            } catch (IOException e) {
                throw new StreamException(e);
            }
        });

        super.start();
    }

    @Override
    public void stop() {
        try {
            channel.close();
            connection.close();
        } catch (Exception e) {
            throw new StreamException(e);
        }
        super.stop();
    }

    private class RabbitMqPollingConsumer extends DefaultConsumer {

        private boolean autoAck;

        public RabbitMqPollingConsumer(Channel channel, boolean autoAck) {
            super(channel);
            this.autoAck = autoAck;
        }

        public RabbitMqPollingConsumer(Channel channel) {
            super(channel);
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            Message message = MessageBuilder.withPayload(body)
                    .setHeader("consumerTag", consumerTag)
                    .setHeader("exchange", envelope.getExchange())
                    .setHeader("deliveryTag", envelope.getDeliveryTag())
                    .setHeader("routingKey", envelope.getRoutingKey())
                    .setHeader("isRedeliver", envelope.isRedeliver())
                    .setHeader("properties", properties)
                    .build();
            try {
                getChannelProcessor().send(message);
            } finally {
                if (!autoAck) {
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }

        }
    }

}
