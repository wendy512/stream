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

package io.github.stream.kafka.source;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import io.github.stream.core.AbstractAutoRunnable;
import io.github.stream.core.Message;
import io.github.stream.core.message.MessageBuilder;
import io.github.stream.core.properties.AbstractProperties;
import io.github.stream.core.source.AbstractSource;
import lombok.extern.slf4j.Slf4j;

/**
 * kafka源
 * @author wendy512@yeah.net
 * @date 2023-05-23 16:22:49
 * @since 1.0.0
 */
@Slf4j
public class KafkaSource extends AbstractSource {

    private KafkaConsumer kafkaConsumer;

    private Collection<String> topics;

    private KafkaPollingRunner runner;

    private Thread runnerThread;

    private int interval;

    private boolean autoCommit;

    @Override
    public void configure(AbstractProperties properties) {
        String topic = properties.getString("topic");
        if (StringUtils.isBlank(topic)) {
            throw new IllegalArgumentException("Kafka topic is empty");
        }

        Map config = properties.getConfig();
        // 手动提交
        config.putIfAbsent(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        // 自动提交间隔
        config.putIfAbsent(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);
        // 配置序列化
        config.putIfAbsent(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.putIfAbsent(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        this.topics = Arrays.asList(topic.split(","));
        this.interval = properties.getInt("pollInterval", 50);
        this.autoCommit = MapUtils.getBooleanValue(config,  ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG);
        this.kafkaConsumer = new KafkaConsumer(config);
    }

    @Override
    public void start() {
        this.runner = new KafkaPollingRunner();
        this.runnerThread = new Thread(runner, "kafka-source-runner");

        this.runner.startup();
        this.runnerThread.start();
        super.start();
    }

    @Override
    public void stop() {
        if (runnerThread != null) {
            runner.shutdown();
            //runnerThread.interrupt();

            while (runnerThread.isAlive()) {
                try {
                    log.debug("Waiting for runner thread to exit");
                    runnerThread.join(500);
                } catch (InterruptedException e) {
                    log.debug("Interrupted while waiting for runner thread to exit. Exception follows.",
                            e);
                }
            }
        }
        super.stop();
    }

    public void subscribe() {
        kafkaConsumer.subscribe(topics);
    }

    public void unsubscribe() {
        kafkaConsumer.unsubscribe();
        kafkaConsumer.close();
    }

    private class KafkaPollingRunner extends AbstractAutoRunnable {

        @Override
        public void runInternal() {
            // 保证KafkaConsumer在同一线程中，否则close会报线程安全的错误
            subscribe();

            while (isRunning()) {
                ConsumerRecords<String,String> records = kafkaConsumer.poll(Duration.ofSeconds(1));
                Iterator iterator = records.iterator();

                while (iterator.hasNext()) {
                    ConsumerRecord record = (ConsumerRecord) iterator.next();
                    Message message = MessageBuilder.withPayload(record.value())
                            .setHeader("topic", record.topic())
                            .setHeader("timestamp", record.timestamp())
                            .build();

                    try {
                        getChannelProcessor().send(message);
                        // 手动提交offset
                        if (!autoCommit) {
                            kafkaConsumer.commitSync();
                        }
                    } catch (IllegalStateException e) {
                        // 本地队列满了，不能再继续消费了
                        super.shutdown();
                        log.error("", e);
                    }
                }

                if (interval > 0) {
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException ex) {
                        // 有可能调用interrupt会触发sleep interrupted异常
                        return;
                    }
                }
            }

            // 保证KafkaConsumer在同一线程中，否则close会报线程安全的错误
            unsubscribe();
        }
    }
}
