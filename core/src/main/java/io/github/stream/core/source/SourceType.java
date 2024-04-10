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

package io.github.stream.core.source;

import io.github.stream.core.configuration.ComponentWithClassName;

/**
 * source 类型
 *
 * @author wendy512@yeah.net
 * @date 2023-05-19 16:37:59
 * @since 1.0.0
 */
public enum SourceType implements ComponentWithClassName {
    LOCAL("io.github.stream.core.source.LocalSource"), MQTT("io.github.stream.mqtt.source.MqttSource"),
    KAFKA("io.github.stream.kafka.source.KafkaSource"), RABBITMQ("io.github.stream.rabbitmq.source.RabbitMqSource"),
    REDIS("io.github.stream.redis.source.RedisStreamSource"), PULSAR("io.github.stream.pulsar.source.PulsarSource");

    private final String className;

    SourceType(String className) {
        this.className = className;
    }

    @Override
    public String getClassName() {
        return className;
    }
}
