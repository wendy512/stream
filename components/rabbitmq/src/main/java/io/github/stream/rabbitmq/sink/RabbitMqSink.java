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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import io.github.stream.core.Message;
import io.github.stream.core.StreamException;
import io.github.stream.core.configuration.ConfigContext;
import io.github.stream.core.message.MessageHeaders;
import io.github.stream.core.sink.AbstractSink;
import io.github.stream.rabbitmq.RabbitMqStateConfigure;

/**
 * rabbitmq 发送
 * @author wendy512@yeah.net
 * @date 2023-05-25 14:19:27
 * @since 1.0.0
 */
public class RabbitMqSink extends AbstractSink<Object> {

    private RabbitMqStateConfigure stateConfigure;

    private Connection connection;

    private Channel channel;

    private final Map<String, Set<String>> exchangeQueueBinds = new ConcurrentHashMap<>();

    private final ReentrantLock lock = new ReentrantLock(true);

    @Override
    public void configure(ConfigContext context) {
        this.stateConfigure = new RabbitMqStateConfigure();
        this.stateConfigure.configure(context);
        try {
            this.connection = stateConfigure.newConnection();
            this.channel = connection.createChannel();
        } catch (Exception e) {
            throw new StreamException(e);
        }
    }

    @Override
    public void process(List<Message<Object>> messages) {
        for (Message<Object> message : messages) {
            if (null != message) {
                send(message);
            }
        }
    }

    public void send(Message message) {
        MessageHeaders headers = message.getHeaders();
        String exchange = headers.getString("exchange");
        if (StringUtils.isBlank(exchange)) {
            throw new IllegalArgumentException("Message header exchange cannot be empty");
        }

        String queue = headers.getString("queue");
        if (StringUtils.isBlank(queue)) {
            throw new IllegalArgumentException("Message header queue cannot be empty");
        }
        Set<String> queues = new ConcurrentSkipListSet<>();
        queues.addAll(Arrays.asList(queue.split(",")));

        String routingKey = headers.getString("routingKey", StringUtils.EMPTY);
        AMQP.BasicProperties props = (AMQP.BasicProperties) headers.get("props");
        Object payload = message.getPayload();
        byte[] body;

        if (payload instanceof String) {
            body = ((String)payload).getBytes(StandardCharsets.UTF_8);
        } else {
            body = (byte[]) payload;
        }

        if (!exchangeQueueBinds.containsKey(exchange)) {
            exchangeBindQueue(exchange, queues, routingKey);
        } else {
            exchangeAddQueue(exchange, queues, routingKey);
        }

        try {
            channel.basicPublish(exchange, routingKey, props, body);
        } catch (IOException e) {
            throw new StreamException(e);
        }
    }

    private void exchangeAddQueue(String exchange, Set<String> queues, String routingKey) {
        Set<String> existsQueues = exchangeQueueBinds.get(exchange);
        try {
            for (String q : queues) {
                if (!existsQueues.contains(q)) {
                    channel.queueDeclare(q, true, false, false, null);
                    channel.queueBind(q, exchange, routingKey);
                    existsQueues.add(q);
                }
            }
        } catch (IOException e) {
            throw new StreamException(e);
        }
    }

    private void exchangeBindQueue(String exchange, Set<String> queues, String routingKey) {
        lock.lock();

        try {
            if (!exchangeQueueBinds.containsKey(exchange)) {
                channel.exchangeDeclare(exchange, BuiltinExchangeType.FANOUT);
                for (String q : queues) {
                    channel.queueDeclare(q, true, false, false, null);
                    channel.queueBind(q, exchange, routingKey);
                }
                exchangeQueueBinds.put(exchange, queues);
            }
        } catch (IOException e) {
            throw new StreamException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void stop() {
        try {
            channel.close();
            connection.close();
        } catch (Exception e) {
            throw new StreamException(e);
        } finally {
            super.stop();
        }
    }
}
