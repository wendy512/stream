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

package io.github.stream.mqtt;

import io.github.stream.core.Configurable;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import io.github.stream.core.StreamException;
import io.github.stream.core.properties.AbstractProperties;
import io.github.stream.core.utils.UUID;
import lombok.extern.slf4j.Slf4j;

/**
 * 抽象mqtt管理
 * @author wendy512@yeah.net
 * @date 2023-05-23 15:00:12
 * @since 1.0.0
 */
@Slf4j
public class MqttStateConfigure implements Configurable {

    public static final String OPTIONS_HOST = "host";
    public static final String OPTIONS_CLIENT_ID = "clientId";
    public static final String OPTIONS_TOPIC = "topic";
    public static final String OPTIONS_QOS = "qos";
    public static final String OPTIONS_CONNECT_TIMEOUT = "connectionTimeout";
    public static final String OPTIONS_KEEP_ALIVE_INTERVAL = "keepAlive";
    public static final String OPTIONS_CLEAN_SESSION = "cleanSession";
    public static final String OPTIONS_AUTOMATIC_RECONNECT = "autoReconnect";
    public static final String OPTIONS_USERNAME = "username";
    public static final String OPTIONS_PASSWORD = "password";
    public static final String OPTIONS_TIMETOWAIT = "timeToWait";
    public static final int DEFAULT_TIMETOWAIT = 60000;

    private MqttClient client;

    private String[] topics;

    private int qos;

    public void configure(AbstractProperties properties) {
        this.configure(properties, true);
    }

    public void configure(AbstractProperties properties, boolean resolveTopic) {
        MqttConnectOptions options = createOptions(properties);

        String host = properties.getString(OPTIONS_HOST);
        if (StringUtils.isBlank(host)) {
            throw new IllegalArgumentException("MQTT host cannot be empty");
        }

        if (resolveTopic) {
            String topic = properties.getString(OPTIONS_TOPIC);
            if (StringUtils.isBlank(topic)) {
                throw new IllegalArgumentException("MQTT topic cannot be empty");
            }
            this.topics = topic.split(",");
        }

        String clientId = properties.getString(OPTIONS_CLIENT_ID);
        if (StringUtils.isBlank(clientId)) {
            clientId = UUID.fastUUID().toString(true);
        }

        int timeToWait = properties.getInt(OPTIONS_TIMETOWAIT, DEFAULT_TIMETOWAIT);
        this.qos = properties.getInt(OPTIONS_QOS, 1);
        log.info("Connect to mqtt server {}, client id {}", host, clientId);
        try {
            this.client = new MqttClient(host, clientId, new MemoryPersistence());
            this.client.setTimeToWait(timeToWait);
            this.client.connect(options);
        } catch (MqttException e) {
            throw new StreamException(e);
        }
    }

    private MqttConnectOptions createOptions(AbstractProperties properties) {
        int connectionTimeout =
                properties.getInt(OPTIONS_CONNECT_TIMEOUT, MqttConnectOptions.CONNECTION_TIMEOUT_DEFAULT);
        int keepAlive = properties.getInt( OPTIONS_KEEP_ALIVE_INTERVAL,
                MqttConnectOptions.KEEP_ALIVE_INTERVAL_DEFAULT);
        boolean cleanSession =
                properties.getBooleanValue(OPTIONS_CLEAN_SESSION, MqttConnectOptions.CLEAN_SESSION_DEFAULT);
        boolean autoReconnect = properties.getBooleanValue( OPTIONS_AUTOMATIC_RECONNECT, true);
        String username = properties.getString(OPTIONS_USERNAME);
        String password = properties.getString(OPTIONS_PASSWORD);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setConnectionTimeout(connectionTimeout);
        options.setKeepAliveInterval(keepAlive);
        options.setCleanSession(cleanSession);
        options.setAutomaticReconnect(autoReconnect);
        if (StringUtils.isNotBlank(username)) {
            options.setUserName(username);
        }
        if (StringUtils.isNotBlank(password)) {
            options.setPassword(password.toCharArray());
        }

        return options;
    }

    public void stop() {
        if (null != client && client.isConnected()) {
            try {
                client.disconnect();
                client.close();
            } catch (MqttException e) {
                throw new StreamException(e);
            }
        }
    }

    public MqttClient getClient() {
        return client;
    }

    public String[] getTopics() {
        return topics;
    }

    public int getQos() {
        return qos;
    }
}
